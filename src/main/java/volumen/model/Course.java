package volumen.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import volumen.User;

/**
 * Represent a learning course with lectures, tests, exams etc.
 */
@Entity
@Data
public class Course {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@ManyToOne
	private User createdBy;
	
	/**
	 * The name (title) of the course.
	 */
	@NotNull
	@Size(min = 1, max = 1024)
	private String name;

	/**
	 * The description (annotation) of the course. Optional.
	 */
	@Size(max = 4096)
	private String description;
	
	@NotNull
	@Temporal(TemporalType.TIMESTAMP)
	private Date createdAt = new Date();
	
	@ManyToOne
	@NotNull
	private CourseCategory category;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST })
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "course_id")
	//@OrderColumn(name = "sequenceNumber")
	private List<Chapter> chapters = new ArrayList<Chapter>();
	
}
