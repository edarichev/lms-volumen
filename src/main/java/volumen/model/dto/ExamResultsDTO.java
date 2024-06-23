package volumen.model.dto;

import java.io.Serializable;
import java.util.ArrayList;

import lombok.Data;

@Data
public class ExamResultsDTO implements Serializable {
	
	private static final long serialVersionUID = 1L;

	private Long lectureId;
	private Long lectureTestId;
	private Long chapterId;
	private Long courseId;
	
	private ArrayList<QuestionResultDTO> questions = new ArrayList<QuestionResultDTO>();
}
