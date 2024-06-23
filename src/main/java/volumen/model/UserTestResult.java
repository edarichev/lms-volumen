package volumen.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import volumen.User;

/**
 * Test results ()
 */
@Entity
@Data
public class UserTestResult {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@NotNull
	private User user;
	
	@ManyToOne
	@Nullable
	private CourseTest course;
	
	@ManyToOne
	@Nullable
	private ChapterTest chapter;
	
	@ManyToOne
	@Nullable
	private LectureTest lecture;
	
	@NotNull
	private TestScope testScope;
	
	private int totalQuestions = 0;
	
	private int trueQuestions = 0;
	
	public final double totalRate() {
		if (totalQuestions == 0)
			return 0;
		return (double)trueQuestions / (double)totalQuestions;
	}
}
