package volumen.controllers.forms;

import java.io.Serializable;

import lombok.Data;
import volumen.data.CourseRepository;
import volumen.exceptions.CourseNotFoundException;
import volumen.model.Chapter;
import volumen.model.Course;

@Data
public class AddChapterForm implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long courseId;
	private Long chapterId;
	private String name;
	private String description;
	private Long sequenceNumber = 0L;

	public Chapter toChapter(CourseRepository repo) {
		Course course = repo.findById(courseId)
				.orElseThrow(() -> new CourseNotFoundException(courseId));
		Chapter chapter = new Chapter();
		chapter.setCourse(course);
		chapter.setName(name);
		chapter.setDescription(description);
		chapter.setSequenceNumber(sequenceNumber == null ? 0 : sequenceNumber); 
		return chapter;
	}
}
