package volumen.model.dto;

import java.io.Serializable;

public class IdNamePair<TId> implements Serializable {
	private static final long serialVersionUID = 1L;
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
