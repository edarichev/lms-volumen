package volumen.data;

import org.springframework.data.repository.CrudRepository;

import volumen.model.Lecture;

public interface LecturesRepository extends CrudRepository<Lecture, Long> {

}
