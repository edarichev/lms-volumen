package volumen.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Represents an answer of the test question. 
 */
@Entity
@Data
public class Answer {
	@Id
	@GeneratedValue
	private Long id;

	/**
	 * Text of the answer. 
	 */
	@NotNull
	@Size(min = 1, max = 256)
	private String text;

	/**
	 * True if this variant is valid. Required.
	 */
	private boolean isValid = false;
}
