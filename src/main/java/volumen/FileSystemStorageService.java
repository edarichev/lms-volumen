package volumen;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import volumen.exceptions.StorageException;

@Service
public class FileSystemStorageService implements StorageService {

	private Path rootLocation;
	
	private final Path getRootLocation() {
		if (this.rootLocation == null)
			this.rootLocation = Paths.get(properties.getLocation());
		return this.rootLocation;
	}
	
	@Autowired
	private StorageProperties properties;

	@Override
	@PostConstruct
	public void init() {
		try {
			Files.createDirectories(getRootLocation());
		} catch (IOException e) {
			throw new StorageException("Could not initialize storage location", e);
		}
	}
	
	@Override
	public String store(String prefixPath, MultipartFile file) {
		// only file name without path is here
		String filename = StringUtils.cleanPath(file.getOriginalFilename());
		try {
			if (file.isEmpty()) {
				throw new StorageException("Failed to store empty file " + filename);
			}
			if (filename.contains("..")) {
				// This is a security check
				throw new StorageException(
						"Cannot store file with relative path outside current directory " + filename);
			}
			try (InputStream inputStream = file.getInputStream()) {
				Path targetPath = (prefixPath == null || prefixPath.isBlank()) ?
						this.getRootLocation() :
							Paths.get(getRootLocation().toString(), prefixPath);
				Files.createDirectories(targetPath);
				Files.copy(inputStream, targetPath.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException e) {
			throw new StorageException("Failed to store file " + filename, e);
		}

		return filename;
	}

	@Override
	public String store(MultipartFile file) {
		return store(null, file);
	}

	@Override
	public Stream<Path> loadAll() {
		try {
			return Files.walk(this.getRootLocation(), 1).filter(path -> !path.equals(this.getRootLocation()))
					.map(this.getRootLocation()::relativize);
		} catch (IOException e) {
			throw new StorageException("Failed to read stored files", e);
		}

	}

	@Override
	public Path load(String filename) {
		return getRootLocation().resolve(filename);
	}

	@Override
	public Resource loadAsResource(String filename) throws FileNotFoundException {
		try {
			Path file = load(filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new FileNotFoundException("Could not read file: " + filename);
			}
		} catch (MalformedURLException e) {
			throw new FileNotFoundException("Could not read file: " + filename);
		}
	}

	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(getRootLocation().toFile());
	}

	@Override
	public void remove(String relativePath) {
		Path file = load(relativePath);
		try {
			Files.deleteIfExists(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}