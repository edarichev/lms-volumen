package volumen.controllers.forms;

import java.io.Serializable;

import lombok.Data;
import volumen.User;
import volumen.data.CourseCategoryRepository;
import volumen.exceptions.CategoryNotFoundException;
import volumen.model.Course;
import volumen.model.CourseCategory;

@Data
public class AddCourseForm implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long categoryId;
	private Long courseId;
	private String name;
	private String description;
	
	public Course toCourse(CourseCategoryRepository categoryRepo) {
		User createdBy = null;
		CourseCategory category = categoryRepo.findById(categoryId)
				.orElseThrow(() -> new CategoryNotFoundException(categoryId));
		Course course = new Course();
		course.setName(name);
		course.setDescription(description);
		course.setCategory(category);
		course.setCreatedBy(createdBy);
		return course;
	}
}
