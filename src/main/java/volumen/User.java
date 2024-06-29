package volumen;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "Users")
@NoArgsConstructor
public class User {

	@Id
	@GeneratedValue
	private Long id;

	@NotNull
	@Column(unique = true)
	private String username;
	
	@NotNull
	private String password;
	
	@NotNull
	private Date createdAt = new Date();
	
	// фамилия
	private String surname;
	// имя
	private String firstName;
	// отчество или среднее имя
	private String middleName;

	@ElementCollection(fetch = FetchType.EAGER)
	@CollectionTable(name = "user_roles")
	@Column(name = "role_name")
	private List<String> roles = new ArrayList<>();
	
	public String[] getRolesArray() {
		int n = roles.size();
		if (n == 0)
			return null;
		String arr[] = new String[getRoles().size()];
		for (int i = 0; i < arr.length; ++i)
			arr[i] = getRoles().get(i);
		return arr;
	}
	
	public boolean hasAnyRole(String[] testRoles) {
		if (testRoles == null)
			return false;
		for (int i = 0; i < testRoles.length; ++i) {
			if (roles.contains(testRoles[i]))
				return true;
		}
		return false;
	}

	public User(String userName, String encodedPassword, 
			String surname, String firstName, String middleName) {
		this.username = userName;
		this.password = encodedPassword;
		this.firstName = firstName;
		this.surname = surname;
		this.middleName = middleName;
	}

}
