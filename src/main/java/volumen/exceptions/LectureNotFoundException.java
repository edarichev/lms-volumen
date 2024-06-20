package volumen.exceptions;

public class LectureNotFoundException extends IllegalArgumentException {
	private static final long serialVersionUID = 1L;
	private Long id;
	
	public LectureNotFoundException(Long id) {
		this.id = id;
	}
	
	public LectureNotFoundException() {
		this(-1L);
	}

	@Override
	public String getMessage() {
		return "Lecture not found: " + this.id;
	}

}
