package volumen.data;

import org.springframework.data.repository.CrudRepository;
import volumen.model.Course;

public interface CourseRepository extends CrudRepository<Course, Long> {

}
