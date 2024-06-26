package volumen;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface StorageService {

	void init();

	String store(MultipartFile file);
	
	String store(String prefixPath, MultipartFile file);

	Stream<Path> loadAll();

	Path load(String filename);

	Resource loadAsResource(String filename) throws FileNotFoundException;

	void deleteAll();

	void remove(String relativePath);
}