package volumen.model;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderColumn;
import lombok.Data;

/**
 * Represents a test of a lecture. This is a container of questions.
 */
@Entity
@Data
public class LectureTest {

	@Id
	@GeneratedValue
	private Long id;

	@OneToOne
	private Lecture lecture;

	@JoinColumn(name = "lecture_id")
	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST })
	@OnDelete(action = OnDeleteAction.CASCADE)
	private List<TestQuestion> questions = new ArrayList<TestQuestion>();
	
	/**
	 * Returns the full score of this test.
	 * @return 0 if no questions
	 */
	public long getFullScore() {
		long sum = 0;
		for (var q : questions)
			sum += q.getScore();
		return sum;
	}
}
