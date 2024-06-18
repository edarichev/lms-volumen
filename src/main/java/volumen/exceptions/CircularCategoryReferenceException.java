package volumen.exceptions;

public class CircularCategoryReferenceException extends Exception {

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCategoryName() {
		return categoryName;
	}

	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}

	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String categoryName;
	
	public CircularCategoryReferenceException(Long id, String name) {
		this.id = id;
		this.categoryName = name;
	}

	@Override
	public String getMessage() {
		return categoryName + ", id=" + id;
	}

}
