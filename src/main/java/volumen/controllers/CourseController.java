package volumen.controllers;

import java.util.ArrayList;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import volumen.controllers.forms.AddCourseForm;
import volumen.model.Chapter;
import volumen.model.Course;
import volumen.model.CourseCategory;
import volumen.web.ui.CategoryTreeBuilder;


@Controller
@RequestMapping("/course")
public class CourseController extends BaseController {

	private static final String VIEW_COURSE_ADD = "course/course_add";
	private static final String VIEW_SELECTED_COURSE = "course/course_view";

	@GetMapping(path = {"", "/"})
	String index() {
		return "redirect:/category"; // course id or category must be selected
	}
	
	@GetMapping("/{id}")
	public ModelAndView getCourse(@PathVariable("id") Long id) {
		var course = findCourseOrThrow(id);
		var category = getCategoryOrThrow(course);
		ArrayList<Chapter> chapters = course.getChapters().stream()
				.sorted((k1, k2) -> {
					Long n1 = k1.getSequenceNumber() == null ? 0 : k1.getSequenceNumber();
					Long n2 = k2.getSequenceNumber() == null ? 0 : k2.getSequenceNumber();
					return (int)(n1 - n2);
				})
				.collect(Collectors.toCollection(ArrayList<Chapter>::new));

		ModelAndView model = new ModelAndView(VIEW_SELECTED_COURSE);
		model.addObject("categories", buildCategoryListForSelectElement(false, INDENT));
		model.addObject("course", course);
		model.addObject("chapters", chapters);
		// path
		model.addObject("categoryPath", CategoryTreeBuilder.buildPathToRoot(getCategoriesList(), category));
		return model;
	}

	@GetMapping("/add/{categoryId}")
	public ModelAndView getAdd(@PathVariable("categoryId") Long categoryId) {
		var formData = new AddCourseForm();
		formData.setCategoryId(categoryId);

		ModelAndView model = new ModelAndView(VIEW_COURSE_ADD);
		model.addObject("categories", buildCategoryListForSelectElement(false, INDENT));
		model.addObject("formData", formData);
		model.addObject("pageTitle", getMessage("course.page_title_add"));
		return model;
	}
	
	@PostMapping("/add/{categoryId}")
	public String getAdd(Model model, @ModelAttribute AddCourseForm formData, Errors errors) {
		Long categoryId = formData.getCategoryId();
		// check for category
		findCategoryOrThrow(categoryId);
		if (null == formData.getName() || formData.getName().isBlank()) {
			String requiredError = getMessage("error.course.name_required");
            model.addAttribute("requiredError", requiredError);
    		model.addAttribute("categories", buildCategoryListForSelectElement(false, INDENT));
    		model.addAttribute("formData", formData);
    		model.addAttribute("pageTitle", getMessage("course.page_title_add"));
            return VIEW_COURSE_ADD;
        }
		Course newCourse = formData.toCourse(categoryRepo);
		courseRepo.save(newCourse);
		return "redirect:/course/" + newCourse.getId();
	}

	@GetMapping("/edit/{id}")
	public ModelAndView getEdit(@PathVariable("id") Long id) {
		Course course = findCourseOrThrow(id);
		CourseCategory category = getCategoryOrThrow(course);
		
		var formData = new AddCourseForm();
		formData.setCategoryId(category.getId());
		formData.setCourseId(id);
		formData.setName(course.getName());
		formData.setDescription(course.getDescription());
		
		ModelAndView model = new ModelAndView(VIEW_COURSE_ADD);
		model.addObject("categories", buildCategoryListForSelectElement(false, INDENT));
		model.addObject("formData", formData);
		model.addObject("course", course);
		model.addObject("pageTitle", getMessage("course.page_title_edit"));
		// path
		model.addObject("categoryPath", CategoryTreeBuilder.buildPathToRoot(getCategoriesList(), category));
		return model;
	}
	
	@PostMapping("/edit/{id}")
	public String postEdit(Model model, @ModelAttribute AddCourseForm formData, Errors errors) {
		var course = findCourseOrThrow(formData.getCourseId());
		var category = getCategoryOrThrow(course);
		if (null == formData.getName() || formData.getName().isBlank()) {
			String requiredError = getMessage("error.course.name_required");
            model.addAttribute("requiredError", requiredError);
            return VIEW_COURSE_ADD;
        }
		course.setName(formData.getName());
		course.setDescription(formData.getDescription());
		course.setCategory(category);
		courseRepo.save(course);
		return "redirect:/course/" + course.getId();
	}
	
	@GetMapping("/delete/{id}")
	public String deleteCourse(@PathVariable("id") long id, Model model) {
	    Course course = findCourseOrThrow(id);
	    courseRepo.delete(course);
	    return "redirect:/category";
	}

}
