package volumen.exceptions;

public class ChapterNotFoundException  extends IllegalArgumentException {

	private static final long serialVersionUID = 1L;

	private Long id;
	
	public ChapterNotFoundException(Long id) {
		super();
		this.id = id;
	}
	
	public ChapterNotFoundException() {
		this(-1L);
	}
	
	@Override
	public String getMessage() {
		return "Chapter not found, id=" + id;
	}
}
