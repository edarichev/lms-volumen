package volumen.controllers;

import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import volumen.controllers.forms.AddCourseCategoryForm;
import volumen.exceptions.CategoryNotFoundException;
import volumen.model.CourseCategory;
import volumen.web.ui.CategoryNode;
import volumen.web.ui.CategoryTreeBuilder;

@Controller
@RequestMapping("/category")
public class CourseCategoryController extends BaseController {

	private static final String VIEW_CATEGORY_ADD = "category/category_add";
	private static final String VIEW_CATEGORY_HOME = "category/category_home";
	private static final String VIEW_SELECTED_CATEGORY = "category/category_view";
	private static final String VIEW_EDIT_CATEGORY = "category/category_add";

	@GetMapping(value = { "/", "" })
	String index(Model model) {
		ArrayList<CategoryNode> categories = buildCategoriesTreeList();
		model.addAttribute("categories", categories);
		return "redirect:/"; // всё равно одно и то же
	}

	@GetMapping("/{id}")
	ModelAndView getShowCategory(@PathVariable("id") Long id) {
		CourseCategory category = findCategoryOrThrow(id);
		ArrayList<CategoryNode> categories = buildCategoriesList(category);

		ModelAndView model = new ModelAndView(VIEW_SELECTED_CATEGORY);
		model.addObject("category", category);
		model.addObject("categories", categories);
		model.addObject("courses", category.getCourses());
		// path
		model.addObject("categoryPath", CategoryTreeBuilder.buildPathToRoot(getCategoriesList(), category));
		return model;
	}

	@GetMapping("add")
	ModelAndView getAdd() {
		ModelAndView model = new ModelAndView(VIEW_CATEGORY_ADD);
		var form = new AddCourseCategoryForm();
		model.addObject("formData", form);
		model.addObject("categories", buildCategoryListForSelectElement(true, INDENT));
		model.addObject("pageTitle", getMessage("category.page_title_add"));
		return model;
	}

	@PostMapping("add")
	String postAdd(Model model, @ModelAttribute AddCourseCategoryForm formData, Errors errors) {
		model.addAttribute("categories", buildCategoryListForSelectElement(true, INDENT));
		model.addAttribute("formData", formData);
		model.addAttribute("pageTitle", getMessage("category.page_title_add"));
		if (errors.hasErrors()) {
			return VIEW_CATEGORY_ADD;
		}
		if (null == formData.getName() || formData.getName().isBlank()) {
			String requiredError = getMessage("error.category.name_required");
			model.addAttribute("requiredError", requiredError);
			return VIEW_CATEGORY_ADD;
		}
		CourseCategory newCategory = formData.toCategory(categoryRepo);
		categoryRepo.save(newCategory);
		return "redirect:/category";
	}

	@GetMapping("/edit/{id}")
	ModelAndView getEdit(@PathVariable("id") Long id) {
		var category = findCategoryOrThrow(id);

		var form = new AddCourseCategoryForm();
		form.setCategoryId(id);
		form.setName(category.getName());
		form.setDescription(category.getDescription());

		ModelAndView model = new ModelAndView();
		model.addObject("formData", form);
		model.addObject("categories", buildCategoryListForSelectElement(true, INDENT));
		model.addObject("pageTitle", getMessage("category.page_title_edit"));
		model.setViewName(VIEW_EDIT_CATEGORY);
		model.addObject("category", category);
		return model;
	}

	@PostMapping("/edit/{id}")
	String postEdit(Model model, @ModelAttribute AddCourseCategoryForm formData, Errors errors) {
		var category = findCategoryOrThrow(formData.getCategoryId());
		model.addAttribute("categories", buildCategoryListForSelectElement(true, INDENT));
		model.addAttribute("formData", formData);
		model.addAttribute("pageTitle", getMessage("category.page_title_edit"));
		
		if (null == formData.getName() || formData.getName().isBlank()) {
			String requiredError = getMessage("error.category.name_required");
			model.addAttribute("requiredError", requiredError);
			return VIEW_EDIT_CATEGORY;
		}
		category.setName(formData.getName());
		category.setDescription(formData.getDescription());
		var parentCategory = categoryRepo.findById(formData.getParentCategoryId());
		CourseCategory parent = parentCategory.isPresent() ? parentCategory.get() : null;
		if (parent != null) {
			if (CategoryTreeBuilder.isParentOf(parent.getId(), category.getId(), getCategoriesList())) {
				// circular reference
				model.addAttribute("categoryError", getMessage("error.category.circular_reference"));
				return VIEW_EDIT_CATEGORY;
			}
		}
		category.setParent(parent);
		categoryRepo.save(category);
		return "redirect:/category";
	}

	@GetMapping("/delete/{id}")
	public String deleteCategory(@PathVariable("id") long id, Model model) {
		CourseCategory category = categoryRepo.findById(id).orElseThrow(() -> new CategoryNotFoundException(id));
		categoryRepo.delete(category);
		return "redirect:/category";
	}

}
