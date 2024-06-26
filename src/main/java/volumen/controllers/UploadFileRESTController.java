package volumen.controllers;

import java.nio.file.Path;
import java.nio.file.Paths;
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

import volumen.ContentItemResponse;
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

	@ResponseBody
	@GetMapping("/deleteimage/{imageId}")
	public ResponseEntity<Void> deleteFile(@PathVariable("imageId") Long imageId) {
		ContentItem item = contentItemRepository.findById(imageId).orElse(null);
		if (item != null) {
			Lecture lecture = item.getLecture();
			Long lectureId = lecture.getId();
			contentItemRepository.delete(item);
			String prefix = "files/lecture/" + lectureId;
			storageService.remove(item.getRelativePath());
			System.out.println("deleted");
		}
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}

	@PostMapping(path =  "/uploadimage/lecture", consumes = {"multipart/form-data"}, produces = "application/json")
	@ResponseBody
	public ContentItemResponse uploadFileForLecture(@RequestParam(name = "file", required = true) MultipartFile file,
			@RequestParam(name = "lectureId", required = true) Long lectureId) {
		String prefix = "files/lecture/" + lectureId;
		String name = storageService.store(prefix, file);
		Path relativePath = Paths.get(prefix, name);
		String upperString = relativePath.toString().toUpperCase();
		boolean allowExtension = (upperString.endsWith(".JPG") || 
				upperString.endsWith(".JPEG") ||
				upperString.endsWith(".PNG") ||
				upperString.endsWith(".WEBP") ||
				upperString.endsWith(".BMP") ||
				upperString.endsWith(".GIF"));
		if (!allowExtension)
			throw new IllegalArgumentException("The uploaded file type is not supported, expected image");
		
		Lecture lecture = findLectureOrThrow(lectureId);
		Chapter chapter = getChapterOrThrow(lecture);
		Course course = getCourseOrThrow(chapter);
		ContentItem contentItem = new ContentItem(relativePath.toString(), course, lecture, ContentItemType.IMAGE);
		contentItemRepository.save(contentItem);
		// see application.yml: static-locations
		String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path(relativePath.toString()).toUriString();

		return new ContentItemResponse(contentItem.getId(), name, uri, file.getContentType(), file.getSize());
	}

	@PostMapping("/upload-multiple-files/lecture")
	@ResponseBody
	public List<ContentItemResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files,
			 Long lectureId) {
		return Arrays.stream(files).map(file -> uploadFileForLecture(file, lectureId)).collect(Collectors.toList());
	}
	
	private Lecture findLectureOrThrow(Long lectureId) {
		return lectureRepo.findById(lectureId).orElseThrow(() -> new LectureNotFoundException(lectureId));
	}
}
