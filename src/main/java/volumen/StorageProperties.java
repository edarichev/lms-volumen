package volumen;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.stereotype.Service;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "storage")
public class StorageProperties {

	private String location;

}