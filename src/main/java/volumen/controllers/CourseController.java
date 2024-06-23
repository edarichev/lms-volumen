package volumen.controllers;

import java.util.ArrayList;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import lombok.extern.slf4j.Slf4j;
import volumen.controllers.forms.AddCourseForm;
import volumen.data.CourseRepository;
import volumen.data.CourseTestRepository;
import volumen.exceptions.CategoryNotFoundException;
import volumen.exceptions.CourseNotFoundException;
import volumen.model.Chapter;
import volumen.model.Course;
import volumen.model.CourseCategory;
import volumen.web.ui.CategoryTreeBuilder;


@Slf4j
@Controller
@RequestMapping("/course")
public class CourseController extends BaseController {

	protected static final String INDENT = "\u00A0\u00A0";
	private static final String VIEW_COURSE_ADD = "course/course_add";
	private static final String VIEW_SELECTED_COURSE = "course/course_view";
	private static final String VIEW_COURSE_NOT_FOUND = "course/course_not_found";
	private static final String VIEW_EDIT_COURSE = "course/course_add";

	@Autowired
	private CourseRepository courseRepo;
	
	@Autowired
	private CourseTestRepository courseTestRepo;
	
	@GetMapping(path = {"", "/"})
	String index() {
		return "redirect:/category"; // course id or category must be selected
	}
	
	@GetMapping("/{id}")
	public ModelAndView getCourse(@PathVariable("id") Long id) {
		ModelAndView model = new ModelAndView(VIEW_SELECTED_COURSE);
		var course = courseRepo.findById(id).orElseThrow(() -> new CourseNotFoundException(id));
		model.addObject("categories", categories(false, INDENT));
		var category = getCategoryOrThrow(course);
		model.addObject("course", course);
		ArrayList<Chapter> chapters = course.getChapters().stream()
				.sorted((k1, k2) -> (int)(k1.getSequenceNumber() - k2.getSequenceNumber()))
				.collect(Collectors.toCollection(ArrayList<Chapter>::new));
		model.addObject("chapters", chapters);
		// path
		model.addObject("categoryPath", CategoryTreeBuilder.buildPathToRoot(getCategoriesList(), category));
		return model;
	}

	@GetMapping("/add/{categoryId}")
	public ModelAndView getAdd(@PathVariable("categoryId") Long categoryId) {
		ModelAndView model = new ModelAndView(VIEW_COURSE_ADD);
		var formData = new AddCourseForm();
		formData.setCategoryId(categoryId);
		model.addObject("categories", categories(false, INDENT));
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
    		model.addAttribute("categories", categories(false, INDENT));
    		model.addAttribute("formData", formData);
    		model.addAttribute("pageTitle", getMessage("course.page_title_add"));
            return VIEW_COURSE_ADD;
        }
		Course newCourse = formData.toCourse(categoryRepo);
		courseRepo.save(newCourse);
		return "redirect:/course/" + newCourse.getId();
	}

	private CourseCategory findCategoryOrThrow(Long categoryId) {
		return categoryRepo.findById(categoryId).orElseThrow(() -> new CategoryNotFoundException(categoryId));
	}

	@GetMapping("/edit/{id}")
	public ModelAndView getEdit(@PathVariable("id") Long id) {
		ModelAndView model = new ModelAndView(VIEW_COURSE_ADD);
		var selectedCourse = courseRepo.findById(id);
		if (selectedCourse.isEmpty()) {
			model.setViewName(VIEW_COURSE_NOT_FOUND);
			log.error("Course not found");
			return model;
		}
		Course course = selectedCourse.get();
		var category = categoryRepo.findById(course.getCategory().getId());
		if (category.isEmpty()) {
			model.setViewName(VIEW_COURSE_NOT_FOUND);
			log.error("Category not found");
			return model;
		}
		model.setViewName(VIEW_EDIT_COURSE);
		var formData = new AddCourseForm();
		formData.setCategoryId(category.get().getId());
		formData.setCourseId(id);
		formData.setName(course.getName());
		formData.setDescription(course.getDescription());
		model.addObject("categories", categories(false, INDENT));
		model.addObject("formData", formData);
		model.addObject("course", course);
		model.addObject("pageTitle", getMessage("course.page_title_edit"));
		// path
		model.addObject("categoryPath", CategoryTreeBuilder.buildPathToRoot(getCategoriesList(), category.get()));
		return model;
	}
	
	@PostMapping("/edit/{id}")
	public String postEdit(Model model, @ModelAttribute AddCourseForm formData, Errors errors) {
		var existingCategory = categoryRepo.findById(formData.getCategoryId());
		if (existingCategory.isEmpty()) {
			return VIEW_COURSE_NOT_FOUND;
		}
		if (existingCategory.isEmpty()) {
			return VIEW_COURSE_NOT_FOUND;
		}
		var existingCourse = courseRepo.findById(formData.getCourseId());
		if (existingCourse.isEmpty()) {
			return VIEW_COURSE_NOT_FOUND;
		}
		var course = existingCourse.get();
		if (null == formData.getName() || formData.getName().isBlank()) {
			String requiredError = getMessage("error.course.name_required");
            model.addAttribute("requiredError", requiredError);
            return VIEW_COURSE_ADD;
        }
		course.setName(formData.getName());
		course.setDescription(formData.getDescription());
		course.setCategory(existingCategory.get());
		courseRepo.save(course);
		return "redirect:/course/" + course.getId();
	}
	
	@GetMapping("/delete/{id}")
	public String deleteCourse(@PathVariable("id") long id, Model model) {
	    Course course = courseRepo.findById(id).orElseThrow(
	    		() -> new CourseNotFoundException(id)
	    		);
	    courseRepo.delete(course);
	    return "redirect:/category";
	}
}
