package volumen.controllers.forms;

import java.io.Serializable;

import lombok.Data;
import volumen.data.CourseCategoryRepository;
import volumen.model.CourseCategory;

@Data
public class AddCourseCategoryForm implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long parentCategoryId;
	private Long categoryId;
	private String name;
	private String description;

	public CourseCategory toCategory(CourseCategoryRepository repo) {
		CourseCategory cat = new CourseCategory();
		cat.setName(name);
		cat.setParent(parentCategoryId == null || parentCategoryId == 0 ? null 
				: repo.findById(parentCategoryId).orElse(null));
		cat.setDescription(description);
		return cat;
	}
}
