package volumen.model.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestQuestionDTO implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long id = -1L;
	private String text;
	private String questionType;
	private List<AnswerDTO> answers = new ArrayList<AnswerDTO>(); 
}
