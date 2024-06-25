package volumen.controllers;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
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

	@PostMapping(path =  "/uploadimage", consumes = {"*/*"}, produces = "application/json")
	@ResponseBody
	public ContentItemResponse uploadFile(@RequestParam(name = "file", required = true) MultipartFile file) {
		String name = storageService.store(file);

		String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/download/").path(name).toUriString();

		return new ContentItemResponse(name, uri, file.getContentType(), file.getSize());
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

		return new ContentItemResponse(name, uri, file.getContentType(), file.getSize());
	}

	@PostMapping("/upload-multiple-files")
	@ResponseBody
	public List<ContentItemResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
		return Arrays.stream(files).map(file -> uploadFile(file)).collect(Collectors.toList());
	}
	
	private Lecture findLectureOrThrow(Long lectureId) {
		return lectureRepo.findById(lectureId).orElseThrow(() -> new LectureNotFoundException(lectureId));
	}
}
