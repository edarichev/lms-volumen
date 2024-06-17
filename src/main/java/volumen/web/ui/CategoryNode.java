package volumen.web.ui;

import java.util.ArrayList;

import volumen.model.CourseCategory;

public class CategoryNode {
	public CourseCategory getValue() {
		return value;
	}
	public void setValue(CourseCategory value) {
		this.value = value;
	}
	public ArrayList<CategoryNode> getItems() {
		return items;
	}
	public void setItems(ArrayList<CategoryNode> items) {
		this.items = items;
	}
	private CourseCategory value = null;
	private ArrayList<CategoryNode> items = new ArrayList<>();
	
	public CategoryNode() {
		
	}
	
	public CategoryNode(CourseCategory value) {
		this.value = value;
	}
}
