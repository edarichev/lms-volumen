package volumen;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Data
public class UserRole {
	@Id
	@GeneratedValue
	private Long id;

	@NotNull
	@Size(min = 1, max = 64)
	private String name;
}
