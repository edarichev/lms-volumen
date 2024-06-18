package volumen.exceptions;

public class CourseNotFoundException extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;

	private Long id;
	
	public CourseNotFoundException(Long id) {
		this.id = id;
	}
	
	@Override
	public String getMessage() {
		return "Course not found, id=" + id;
	}
}
