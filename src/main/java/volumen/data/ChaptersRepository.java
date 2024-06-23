package volumen.data;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import volumen.model.Chapter;

public interface ChaptersRepository extends CrudRepository<Chapter, Long> {

	@Query("SELECT MAX(p.sequenceNumber) FROM Chapter p WHERE p.course.id = ?1")
	Long findMaxSequenceNumber(Long chapterId);
	
	default long getNextSequenceNumber(Long courseId) {
		Long maxNumber = findMaxSequenceNumber(courseId);
		if (maxNumber == null)
			maxNumber = 0L;
		maxNumber++;
		return maxNumber;
	}
}
