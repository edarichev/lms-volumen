package volumen.web.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.springframework.util.Assert;

import volumen.exceptions.CircularCategoryReferenceException;
import volumen.model.CourseCategory;
import volumen.model.dto.IdNamePair;

public class CategoryTreeBuilder {

	public static CategoryNode buildTree(List<CourseCategory> collection) throws CircularCategoryReferenceException {
		if (collection == null || collection.isEmpty())
			return null;
		List<CourseCategory> list = collection.stream().sorted((k1, k2) -> {
			// now we sort by parent id			
			Long a1 = k1.getParent() == null ? 0 : k1.getParent().getId();
			Long a2 = k2.getParent() == null ? 0 : k2.getParent().getId();
			int result = (int)(a1 - a2);
			// and then by name
			if (result == 0) {
				result = k1.getName().compareToIgnoreCase(k2.getName());
			}
			return result;
			}).toList();
		// build the tree
		// the root node always has no value - it is a container
		CategoryNode root = new CategoryNode();
		// for quick search
		HashMap<Long, CategoryNode> map = new HashMap<>();
		for (var item : list) {
			map.put(item.getId(), new CategoryNode(item));
		}
		// items in this list are sorted alphabetically
		HashSet<CategoryNode> usedItems = new HashSet<CategoryNode>();
		for (var item : list) {
			CategoryNode nodeOfThisItem = map.get(item.getId());
			if (usedItems.contains(nodeOfThisItem))
				throw new CircularCategoryReferenceException(item.getId(), item.getName());
			if (item.getParent() == null) {
				root.getItems().add(nodeOfThisItem);
			} else {
				Long parentId = item.getParent().getId();
				CategoryNode parentOfThisItem = map.get(parentId);
				if (parentId != null && isParentOf(parentId, item.getId(), map))
					throw new CircularCategoryReferenceException(item.getId(), item.getName());
				parentOfThisItem.getItems().add(nodeOfThisItem);
			}
		}
		return root;
	}
	
	public static boolean isParentOf(long itemId, long parentId, List<CourseCategory> list) {
		// for quick search
		HashMap<Long, CategoryNode> map = new HashMap<>();
		for (var item : list) {
			map.put(item.getId(), new CategoryNode(item));
		}
		return isParentOf(itemId, parentId, map);
	}
	
	public static boolean isParentOf(long itemId, long parentId, HashMap<Long, CategoryNode> container) {
		var itemNode = container.get(itemId);
		if (itemNode == null)
			return false;
		var item = itemNode.getValue();
		while (true) {
			var parent = item.getParent();
			if (parent == null || parent.getId() == null)
				break; // дошли до верха, это не родительский
			if (parent.getId().longValue() == parentId)
				return true;
			item = parent;
		}
		return false;
	}
	
	public static List<IdNamePair<Long>> buildStringItems(List<CourseCategory> collection, String indentWith) 
			throws CircularCategoryReferenceException {
		CategoryNode root = buildTree(collection);
		List<IdNamePair<Long>> resultList = new ArrayList<>();
		doWorkTreeLevel(root, collection, resultList, indentWith, 0);
		return resultList;
	}
	
	private static void doWorkTreeLevel(CategoryNode parent, List<CourseCategory> collection, 
			List<IdNamePair<Long>> resultList,
			String indentWith, int level) {
		if (parent.getValue() != null) {
			String name = indentWith.repeat(level - 1) + parent.getValue().getName();
			resultList.add(new IdNamePair<Long>(parent.getValue().getId(), name));
		}
		for (var item : parent.getItems()) {
			doWorkTreeLevel(item, collection, resultList, indentWith, level + 1);
		}
	}
}
