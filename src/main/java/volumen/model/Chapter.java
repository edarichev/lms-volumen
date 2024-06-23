package volumen.model;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Represent a logical part of the learning course (a group of lectures). 
 */
@Entity
@Data
public class Chapter {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	/**
	 * The name (title) of this chapter.
	 */
	@NotNull
	@Size(min = 1, max = 1024)
	private String name;
	/**
	 * The description (annotation) of this chapter. Optional.
	 */
	@Size(max = 4096)
	private String description;
	
	/**
	 * Sequence number. May be non-unique and negative if needed.
	 */
	@NotNull
	@Column(name = "sequence_number")
	private Long sequenceNumber = 0L;
	
	@NotNull
	@ManyToOne
	private Course course;

	/**
	 * Lectures contained in this chapter.
	 */
	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST })
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "chapter_id")
	//@OrderColumn(name = "sequenceNumber")
	private List<Lecture> lectures = new ArrayList<Lecture>();
}
