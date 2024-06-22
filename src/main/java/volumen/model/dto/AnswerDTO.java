package volumen.model.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import volumen.model.Answer;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnswerDTO implements Serializable {
	public AnswerDTO(Answer a) {
		id = a.getId();
		sequenceNumber = a.getSequenceNumber();
		answer = a.getText();
		valid = a.isValid();
	}
	private static final long serialVersionUID = 1L;
	private Long id = -1L;
	private Long sequenceNumber = 0L;
	private String answer;
	private Boolean valid;
	
	 
}
