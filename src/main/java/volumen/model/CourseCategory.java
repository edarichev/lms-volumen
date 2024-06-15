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
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
public class CourseCategory {
	@Id
	@GeneratedValue
	private Long id;
	
	@NotNull
	private String name;
	
	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST })
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "parent_id")
	private List<CourseCategory> categories = new ArrayList<>();

	
	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST })
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JoinColumn(name = "coursecategory_id")
	private List<Course> courses = new ArrayList<>();
	
}
