package volumen.exceptions;

public class TestNotFoundException extends IllegalArgumentException {
	private static final long serialVersionUID = 1L;
	private Long id;
	
	public TestNotFoundException(Long id) {
		this.id = id;
	}
	
	@Override
	public String getMessage() {
		return "Test not found: " + this.id;
	}

}