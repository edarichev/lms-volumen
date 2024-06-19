package volumen.controllers.forms;

import java.io.Serializable;

import lombok.Data;
import volumen.data.ChaptersRepository;
import volumen.exceptions.ChapterNotFoundException;
import volumen.model.Chapter;
import volumen.model.Lecture;

/**
 * Lecture form data to transfer between requests
 */
@Data
public class AddLectureForm implements Serializable {
	private static final long serialVersionUID = 1L;

	private Long chapterId;
	private Long lectureId;
	private String name;
	private String description;
	private Long sequenceNumber = 0L;
	private String content;

	public Lecture toLecture(ChaptersRepository repo) {
		Chapter chapter = repo.findById(chapterId).orElseThrow(() -> new ChapterNotFoundException(chapterId));

		Lecture lecture = new Lecture();
		lecture.setName(name);
		lecture.setChapter(chapter);
		lecture.setDescription(description);
		lecture.setSequenceNumber(sequenceNumber == null ? 0 : sequenceNumber);
		lecture.setContent(content);
		return lecture;
	}

}
