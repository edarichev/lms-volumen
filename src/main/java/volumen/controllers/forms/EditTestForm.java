package volumen.controllers.forms;

import java.io.Serializable;

import lombok.Data;

@Data
public class EditTestForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long lectureId;
	private Long testId;
	
	
}
