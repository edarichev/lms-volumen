package volumen.controllers;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.annotation.security.RolesAllowed;
import volumen.ContentItemDTO;
import volumen.StorageService;
import volumen.data.ContentItemRepository;
import volumen.data.LecturesRepository;
import volumen.exceptions.LectureNotFoundException;
import volumen.model.Chapter;
import volumen.model.ContentItem;
import volumen.model.ContentItemType;
import volumen.model.Course;
import volumen.model.Lecture;

@Controller
@RequestMapping("/uploadservice")
public class UploadFileRESTController extends BaseController {

	@Autowired
	private StorageService storageService;
	
	@Autowired
	private ContentItemRepository contentItemRepository;
	
	@Autowired
	private LecturesRepository lectureRepo;

	@RolesAllowed({"ADMIN", "TEACHER"})
	@ResponseBody
	@GetMapping("/deleteimage/{imageId}")
	public ResponseEntity<Void> deleteFile(@PathVariable("imageId") Long imageId) {
		ContentItem item = contentItemRepository.findById(imageId).orElse(null);
		if (item != null) {
			contentItemRepository.deleteById(imageId);
			storageService.remove(item.getRelativePath());
			System.out.println("deleted");
		}
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

	@RolesAllowed({"ADMIN", "TEACHER"})
	@GetMapping(path = "/getimages/lecture/{lectureId}", produces = "application/json")
	@ResponseBody
	public List<ContentItemDTO> getFilesForLecture(@PathVariable("lectureId") Long lectureId) {
		Lecture lecture = findLectureOrThrow(lectureId);
		var items = contentItemRepository.findAllByLectureEqualsOrderByUploadDateDesc(lecture);
		ArrayList<ContentItemDTO> list = new ArrayList<>();
		for (var i : items) {
			String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path(i.getRelativePath()).toUriString();
			ContentItemDTO dto = new ContentItemDTO(i.getId(), 
					Paths.get(i.getRelativePath()).getFileName().toString(),
					uri, 
					i.getItemType().name(), 
					0L);
			list.add(dto);
		}
		return list;
	}
	
	@RolesAllowed({"ADMIN", "TEACHER"})
	@PostMapping(path =  "/uploadimage/lecture", consumes = {"multipart/form-data"}, produces = "application/json")
	@ResponseBody
	public ContentItemDTO uploadFileForLecture(@RequestParam(name = "file", required = true) MultipartFile file,
			@RequestParam(name = "lectureId", required = true) Long lectureId) {
		String prefix = "files/lecture/" + lectureId;
		String name = storageService.store(prefix, file);
		Path relativePath = Paths.get(prefix, name);
		String extension = getFileExtension(relativePath.toString()).toUpperCase();
		
		boolean allowExtension = false;
		switch (extension) {
		case ".JPG":
		case ".JPEG":
		case ".PNG":
		case ".WEBP":
		case ".BMP":
		case ".GIF":
			allowExtension = true;
			break;
		}
		if (!allowExtension)
			throw new IllegalArgumentException("The uploaded file type is not supported, image expected");
		
		Lecture lecture = findLectureOrThrow(lectureId);
		Chapter chapter = getChapterOrThrow(lecture);
		Course course = getCourseOrThrow(chapter);
		ContentItem contentItem = new ContentItem(relativePath.toString(), course, lecture, ContentItemType.IMAGE);
		contentItem.setSize(file.getSize());
		contentItemRepository.save(contentItem);
		// see application.yml: static-locations
		String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path(relativePath.toString()).toUriString();

		return new ContentItemDTO(contentItem.getId(), name, uri, file.getContentType(), file.getSize());
	}

	/**
	 * Returns the file extension with dot or empty string
	 * 
	 * @param fileName
	 * @return
	 */
	private String getFileExtension(String fileName) {
		int i = fileName.lastIndexOf('.');
		if (i < 0)
			return "";
		return fileName.substring(i);
	}

	@RolesAllowed({"ADMIN", "TEACHER"})
	@PostMapping("/upload-multiple-files/lecture")
	@ResponseBody
	public List<ContentItemDTO> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files,
			@RequestParam(name = "lectureId", required = true) Long lectureId) {
		return Arrays.stream(files).map(file -> uploadFileForLecture(file, lectureId)).collect(Collectors.toList());
	}
	
	private Lecture findLectureOrThrow(Long lectureId) {
		return lectureRepo.findById(lectureId).orElseThrow(() -> new LectureNotFoundException(lectureId));
	}
}
