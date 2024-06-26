package volumen;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentItemDTO {
	private Long id;
	private String name;
	private String uri;
	private String type;
	private long size;
}