package volumen.exceptions;

public class CourseNotFoundException extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;

	private Long id;
	
	public CourseNotFoundException() {
		this(-1L);
	}
	
	public CourseNotFoundException(Long id) {
		super();
		this.id = id;
	}
	
	@Override
	public String getMessage() {
		return "Course not found, id=" + id;
	}
}
