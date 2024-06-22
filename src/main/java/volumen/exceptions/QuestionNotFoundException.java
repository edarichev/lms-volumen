package volumen.exceptions;

public class QuestionNotFoundException extends IllegalArgumentException {
	private static final long serialVersionUID = 1L;
	private Long id;
	
	public QuestionNotFoundException(Long id) {
		this.id = id;
	}
	
	public QuestionNotFoundException() {
		this(-1L);
	}
	
	@Override
	public String getMessage() {
		return "Question not found: " + this.id;
	}

}