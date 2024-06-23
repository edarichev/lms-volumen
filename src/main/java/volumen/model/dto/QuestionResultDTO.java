package volumen.model.dto;

import java.io.Serializable;

import lombok.Data;

@Data
public class QuestionResultDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	
	private String text;
	
	private boolean valid;
	
	public QuestionResultDTO() {
		
	}
	
	public QuestionResultDTO(Long id, String text, boolean valid) {
		this.id = id;
		this.text = text;
		this.valid = valid;
	}
}
