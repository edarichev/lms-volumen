package volumen.data;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import volumen.model.ContentItem;
import volumen.model.Lecture;

public interface ContentItemRepository extends CrudRepository<ContentItem, Long> {
	List<ContentItem> findAllByLectureEqualsOrderByUploadDateDesc(Lecture lecture);
}
