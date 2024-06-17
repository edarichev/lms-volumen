package volumen.data;

import org.springframework.data.repository.CrudRepository;

import volumen.model.CourseCategory;

public interface CourseCategoryRepository extends CrudRepository<CourseCategory, Long> {

	
}
