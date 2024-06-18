package volumen.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;

import volumen.data.CourseCategoryRepository;
import volumen.exceptions.CircularCategoryReferenceException;
import volumen.model.CourseCategory;
import volumen.model.dto.IdNamePair;
import volumen.web.ui.CategoryTreeBuilder;

public class BaseController {
	
	@Autowired
	protected CourseCategoryRepository categoryRepo;
	
	@Autowired
    protected MessageSource messageSource;
	
	protected ArrayList<CourseCategory> getCategoriesList() {
		var categoriesList = new ArrayList<CourseCategory>();
		for (var cat : categoryRepo.findAll())
			categoriesList.add(cat);
		return categoriesList;
	}

	protected List<IdNamePair<Long>> categories(boolean includeEmptyRoot, String indent) {
		ArrayList<CourseCategory> list = getCategoriesList();
		List<IdNamePair<Long>> items;
		try {
			items = CategoryTreeBuilder.buildStringItems(list, indent);
			// add empty item (NULL)
			if (includeEmptyRoot)
				items.add(0, new IdNamePair<Long>(0L, getMessage("category.root")));
		} catch (CircularCategoryReferenceException e) {
			e.printStackTrace();
			items = new ArrayList<IdNamePair<Long>>();//return an empty list
		}
		return items;
	}
	
	protected String getMessage(String key) {
		return messageSource.getMessage(key, null, null);
	}
}
