package volumen.model.dto;

public class IdNamePair<TId> {
	private TId id;
	private String name;
	
	public IdNamePair(TId id, String name) {
		this.setId(id);
		this.setName(name);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TId getId() {
		return id;
	}

	public void setId(TId id) {
		this.id = id;
	}
}
