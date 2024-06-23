package volumen.exceptions;

public class CategoryNotFoundException extends IllegalArgumentException {
	private static final long serialVersionUID = 1L;
	private Long id;
	
	public CategoryNotFoundException(Long catId) {
		this.id = catId;
	}
	
	public CategoryNotFoundException() {
		this(-1L);
	}
	
	@Override
	public String getMessage() {
		return "Category not found: " + this.id;
	}
}
