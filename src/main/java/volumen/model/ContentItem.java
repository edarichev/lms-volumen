package volumen.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Storage record for uploaded image or other content resource.
 * 
 * The image or other content item can be stored only for specified course and removed with it
 */
@Data
@Entity
@NoArgsConstructor
public class ContentItem {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotNull
	private String relativePath;
	
	@Nullable
	@ManyToOne
	@JoinColumn(name = "course_id")
	private Course course;
	
	@Nullable
	@ManyToOne
	@JoinColumn(name = "lecture_id")
	private Lecture lecture;
	
	@NotNull
	@Enumerated(EnumType.STRING)
	private ContentItemType itemType;
	
	public ContentItem(String path, Course course, Lecture lecture, ContentItemType type) {
		this.relativePath = path;
		this.course = course;
		this.lecture = lecture;
		this.itemType = type;
	}
}
