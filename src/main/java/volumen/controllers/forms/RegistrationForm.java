package volumen.controllers.forms;

import org.springframework.security.crypto.password.PasswordEncoder;

import lombok.Data;
import volumen.User;

@Data
public class RegistrationForm {
	private final String username;
	private final String password;
	private final String firstName;
	private final String middleName;
	private final String surname;

	public User toUser(PasswordEncoder passwordEncoder) {
		return new User(username, passwordEncoder.encode(password), surname, firstName, middleName);
	}

}