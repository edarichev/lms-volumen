package volumen.controllers.forms;

import java.io.Serializable;

import lombok.Data;

@Data
public class TestRunForm implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long testId;
	private Long lectureId;
	private Long chapterId;
	private Long courseId;
}
