package volumen.model;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Represents a question of test.
 */
@Entity
@Data
public class TestQuestion {
	@Id
	@GeneratedValue
	private Long id;

	/**
	 * Text of the question. 
	 */
	@NotNull
	@Size(min = 1, max = 512)
	private String text;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	private QuestionType questionType = QuestionType.SINGLE;
	
	/**
	 * Passing score. Default == 1.
	 */
	@NotNull
	private Long score = 1L;
	
	/**
	 * Answers: can not be null or empty
	 */
	@Size(min = 1)
	@NotNull
	@NotEmpty
	@OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "question_id")
	@OnDelete(action = OnDeleteAction.CASCADE)
	private List<Answer> answers = new ArrayList<Answer>();
}
