package volumen.model;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.Basic;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Represents a lecture of the learning course.
 */
@Entity
@Data
public class Lecture {

	@Id
	@GeneratedValue
	private Long id;

	/**
	 * Sequence number.
	 */
	@NotNull
	private Long sequenceNumber = 0L;

	/**
	 * The name (title) of this lecture.
	 */
	@NotNull
	@Size(min = 1, max = 1024)
	private String name;
	/**
	 * The description (annotation) of this lecture. Optional.
	 */
	@Size(max = 4096)
	private String description;

	/**
	 * The text of this lecture.
	 */
	@Lob
	@Basic(fetch=FetchType.LAZY)
	private String content;

	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@OnDelete(action = OnDeleteAction.CASCADE)
	private LectureTest lectureTest;

	@ManyToOne
	private Chapter chapter;
}
