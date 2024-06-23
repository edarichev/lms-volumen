package volumen.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
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
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	/**
	 * Text of the answer. 
	 */
	@NotNull
	@Size(min = 1, max = 256)
	private String text;
	
	@NotNull
	@ManyToOne
	private TestQuestion testQuestion;

	/**
	 * True if this variant is valid. Required.
	 */
	private boolean isValid = false;
	
	private Long sequenceNumber;
}
