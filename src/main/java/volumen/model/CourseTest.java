package volumen.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * Entity implementation class for Entity: CourseTest
 *
 * This a test for entire course (exam).
 * 
 * The CourseTest does not contains questions directly: the list of questions
 * builds from lecture tests.
 */
@Entity
@Data
public class CourseTest {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull
	@OneToOne
	private Course course;
	
	@NotNull
	private Long questionCount = 0L;
}
