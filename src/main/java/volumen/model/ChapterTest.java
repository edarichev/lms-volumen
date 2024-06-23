package volumen.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Entity implementation class for Entity: ChapterTest
 *
 * The ChapterTest does not contains questions directly: the list of questions
 * builds from lecture tests.
 */
@Entity
@Data
public class ChapterTest {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull
	@OneToOne
	private Chapter chapter;
	
	@NotNull
	private Long questionCount = 0L;
}
