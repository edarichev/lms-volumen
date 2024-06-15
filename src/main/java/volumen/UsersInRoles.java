package volumen;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
public class UsersInRoles {
	@Id
	@GeneratedValue
	private Long id;

	@NotNull
	@OneToOne
	private User user;
	
	@NotNull
	@OneToOne
	private UserRole role;
}
