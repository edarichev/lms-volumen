package volumen.controllers;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import volumen.FileResponse;
import volumen.StorageService;
import volumen.model.dto.ImageItemDTO;

@Controller
@RequestMapping("/uploadservice")
public class UploadFileRESTController {

	@Autowired
	private StorageService storageService;

	@GetMapping("/")
	public String listAllFiles(Model model) {

		model.addAttribute("files",
				storageService.loadAll().map(path -> ServletUriComponentsBuilder.fromCurrentContextPath()
						.path("/download/").path(path.getFileName().toString()).toUriString())
						.collect(Collectors.toList()));

		return "listFiles";
	}

	@GetMapping("/download/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {

		Resource resource;
		try {
			resource = storageService.loadAsResource(filename);
			return ResponseEntity.ok()
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
					.body(resource);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().build();
		}

	}

	@PostMapping(path =  "/uploadimage", consumes = {"*/*"}, produces = "application/json")
	@ResponseBody
	public FileResponse uploadFile(@RequestParam(name = "file", required = true) MultipartFile file) {
		String name = storageService.store(file);

		String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/download/").path(name).toUriString();

		return new FileResponse(name, uri, file.getContentType(), file.getSize());
	}

	@PostMapping(path =  "/uploadimage/lecture", consumes = {"*/*"}, produces = "application/json")
	@ResponseBody
	public FileResponse uploadFileForLecture(@RequestParam(name = "file", required = true) MultipartFile file,
			@RequestParam(name = "lectureId", required = true) Long lectureId) {
		String prefix = "lecture/" + lectureId;
		String name = storageService.store(prefix, file);

		String uri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/download/").path(name).toUriString();

		return new FileResponse(name, uri, file.getContentType(), file.getSize());
	}

	@PostMapping("/upload-multiple-files")
	@ResponseBody
	public List<FileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
		return Arrays.stream(files).map(file -> uploadFile(file)).collect(Collectors.toList());
	}
}
