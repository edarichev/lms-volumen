package volumen.model.dto;

import java.io.Serializable;
import java.util.ArrayList;

import lombok.Data;

/**
 * A set of question for test/exam that sends to client to test 
 * and back to server to check answers.
 * 
 * The answers of questions.answers represents the answers of user
 * and depends on type of question: if TEXT - one entered answer with "answer" field,
 * if "SINGLE" or "MULTIPLE" - the "id" fields accepts as true answers only.
 */
@Data
public class ExamSetDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long lectureId;
	private Long lectureTestId;
	private Long chapterId;
	private Long courseId;

	private ArrayList<TestQuestionDTO> questions = new ArrayList<TestQuestionDTO>();
}
