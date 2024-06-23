package volumen.model;

import java.util.ArrayList;
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
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
public class CourseCategory {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@NotNull
	private String name;
	
	private String description;
	
	@ManyToOne
	@JoinColumn(name = "parent_id")
	private CourseCategory parent;
	
	@OneToMany(mappedBy = "parent", fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST })
	@OnDelete(action = OnDeleteAction.RESTRICT)
	private List<CourseCategory> categories = new ArrayList<>();

	
	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST })
	@OnDelete(action = OnDeleteAction.RESTRICT)
	@JoinColumn(name = "category_id")
	private List<Course> courses = new ArrayList<>();
	
}
