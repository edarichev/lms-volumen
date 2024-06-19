package volumen.data;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import jakarta.transaction.Transactional;
import volumen.model.Lecture;

public interface LecturesRepository extends CrudRepository<Lecture, Long> {
	@Query("SELECT MAX(p.sequenceNumber) FROM Lecture p WHERE p.chapter.id = ?1")
	Long findMaxSequenceNumber(Long chapterId);
	
	default long getNextSequenceNumber(Long chapterId) {
		Long maxNumber = findMaxSequenceNumber(chapterId);
		if (maxNumber == null)
			maxNumber = 0L;
		maxNumber++;
		return maxNumber;
	}
	
	// antics and jumping and workaround for PSQL+lazy load blob
	@Transactional
	@Query("SELECT p.content FROM Lecture p WHERE p.id=?1")
	String getLectureContent(Long id);
}
