package volumen.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.validation.SimpleErrors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;
import volumen.controllers.forms.AddCourseCategoryForm;
import volumen.data.CourseCategoryRepository;
import volumen.exceptions.CircularCategoryReferenceException;
import volumen.model.CourseCategory;
import volumen.model.dto.IdNamePair;
import volumen.web.ui.CategoryNode;
import volumen.web.ui.CategoryTreeBuilder;

@Slf4j
@Controller
@RequestMapping("/category")
public class CourseCategoryController {
	
	private static final String VIEW_CATEGORY_ADD = "category/category_add";
	private static final String VIEW_CATEGORY_HOME = "category/category_home";
	private static final String VIEW_SELECTED_CATEGORY = "category/category_view";
	private static final String VIEW_CATEGORY_NOT_FOUND = "category/category_not_found";
	private static final String VIEW_EDIT_CATEGORY = "category/category_add";
	
	@Autowired
	private CourseCategoryRepository categoryRepo;
	
	@Autowired
    private MessageSource messageSource;
	
	@GetMapping(value = {"/", ""})
	String index(Model model) {
		List<CourseCategory> target = new ArrayList<>();
		categoryRepo.findAll().forEach(target::add);
		CategoryNode rootCat;
		try {
			rootCat = CategoryTreeBuilder.buildTree(target);
			model.addAttribute("categories", rootCat.getItems());
		} catch (CircularCategoryReferenceException e) {
			e.printStackTrace();
			model.addAttribute("categories", new ArrayList<CategoryNode>());
		}
		return VIEW_CATEGORY_HOME;
	}
	
	@GetMapping("/{id}")
	ModelAndView getShowCategory(@PathVariable("id") Long id) {
		ModelAndView model = new ModelAndView();
		var category = categoryRepo.findById(id);
		if (category.isEmpty()) {
			model.setViewName(VIEW_CATEGORY_NOT_FOUND);
		} else {
			model.setViewName(VIEW_SELECTED_CATEGORY);
			model.addObject("category", category.get());
		}
		return model;
	}
	
	@GetMapping("add")
	ModelAndView getAdd() {
		ModelAndView model = new ModelAndView(VIEW_CATEGORY_ADD);
		var form = new AddCourseCategoryForm();
		model.addObject("formData", form);
		model.addObject("categories", categories(true, "\u00A0\u00A0"));
		model.addObject("pageTitle", messageSource.getMessage("category.page_title_add", null, null));
		return model;
	}
	
	@PostMapping("add")
	String postAdd(Model model, @ModelAttribute AddCourseCategoryForm formData, Errors errors) {
		model.addAttribute("categories", categories(true, "\u00A0\u00A0"));
		model.addAttribute("formData", formData);
		model.addAttribute("pageTitle", messageSource.getMessage("category.page_title_add", null, null));
		if (errors.hasErrors()) {
			log.error("Errors add category: " + errors.toString());
			return VIEW_CATEGORY_ADD;
		}
		if (null == formData.getName() || formData.getName().isBlank()) {
			String requiredError = messageSource.getMessage("error.category.name_required", null, null);
            model.addAttribute("requiredError", requiredError);
            return VIEW_CATEGORY_ADD;
        }
		CourseCategory newCategory = formData.toCategory(categoryRepo);
		categoryRepo.save(newCategory);
		return "redirect:/category";
	}
	
	@GetMapping("/edit/{id}")
	ModelAndView getEdit(@PathVariable("id") Long id) {
		ModelAndView model = new ModelAndView();
		var form = new AddCourseCategoryForm();
		model.addObject("formData", form);
		model.addObject("categories", categories(true, "\u00A0\u00A0"));
		model.addObject("pageTitle", messageSource.getMessage("category.page_title_edit", null, null));
		var category = categoryRepo.findById(id);
		if (category.isEmpty()) {
			model.setViewName(VIEW_CATEGORY_NOT_FOUND);
		} else {
			model.setViewName(VIEW_EDIT_CATEGORY);
			model.addObject("category", category.get());
			form.setCategoryId(id);
			form.setName(category.get().getName());
			form.setDescription(category.get().getDescription());
		}
		return model;
	}
	
	@PostMapping("/edit/{id}")
	String postEdit(Model model, @ModelAttribute AddCourseCategoryForm formData, Errors errors) {
		log.info("SAVE THE Category: " + formData.getCategoryId());
		var existingCategory = categoryRepo.findById(formData.getCategoryId());
		if (existingCategory.isEmpty()) {
			return "redirect:/" + VIEW_CATEGORY_NOT_FOUND;
		}
		model.addAttribute("categories", categories(true, "\u00A0\u00A0"));
		model.addAttribute("formData", formData);
		model.addAttribute("pageTitle", messageSource.getMessage("category.page_title_add", null, null));
		if (errors.hasErrors()) {
			log.error("Errors add category: " + errors.toString());
			return VIEW_EDIT_CATEGORY;
		}
		if (null == formData.getName() || formData.getName().isBlank()) {
			String requiredError = messageSource.getMessage("error.category.name_required", null, null);
            model.addAttribute("requiredError", requiredError);
            return VIEW_EDIT_CATEGORY;
        }
		CourseCategory category = existingCategory.get();
		category.setName(formData.getName());
		category.setDescription(formData.getDescription());
		var parentCategory = categoryRepo.findById(formData.getParentCategoryId());
		CourseCategory parent = parentCategory.isPresent() ? parentCategory.get() : null;
		category.setParent(parent);
		categoryRepo.save(category);
		return "redirect:/category";
	}
	
	private List<IdNamePair<Long>> categories(boolean includeEmptyRoot, String indent) {
		ArrayList<CourseCategory> list = new ArrayList<CourseCategory>();
		for (var cat : categoryRepo.findAll())
			list.add(cat);
		List<IdNamePair<Long>> items;
		try {
			items = CategoryTreeBuilder.buildStringItems(list, indent);
			// add empty item (NULL)
			if (includeEmptyRoot)
				items.add(0, new IdNamePair<Long>(0L, messageSource.getMessage("category.root", null, null)));
		} catch (CircularCategoryReferenceException e) {
			e.printStackTrace();
			items = new ArrayList<IdNamePair<Long>>();//return an empty list
		}
		return items;
	}
	
}
