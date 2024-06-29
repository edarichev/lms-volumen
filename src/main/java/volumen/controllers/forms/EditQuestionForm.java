package volumen.controllers.forms;

import java.util.ArrayList;

import lombok.Data;
import volumen.model.dto.IdNamePair;
import volumen.model.dto.TestQuestionDTO;

@Data
public class EditQuestionForm {
	private Long testId;
	private Long questionId;
	private Long lectureId;
	
	private TestQuestionDTO question;
	
	private ArrayList<IdNamePair<String>> questionTypes = new ArrayList<IdNamePair<String>>();
	
	
}
