package volumen.controllers;

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

import jakarta.annotation.security.RolesAllowed;
import volumen.controllers.forms.AddChapterForm;
import volumen.data.ChaptersRepository;
import volumen.exceptions.ChapterNotFoundException;
import volumen.exceptions.CourseNotFoundException;
import volumen.model.Chapter;
import volumen.model.Course;
import volumen.model.CourseCategory;
import volumen.web.ui.CategoryTreeBuilder;

@Controller
@RequestMapping("/unit")
public class ChapterController extends BaseController {

	private static final String VIEW_CHAPTER_ADD = "chapter/chapter_add";
	private static final String VIEW_SELECTED_CHAPTER = "chapter/chapter_view";
	private static final String VIEW_EDIT_CHAPTER = "chapter/chapter_add";

	@Autowired
	ChaptersRepository chapterRepo;

	@GetMapping(path = { "", "/" })
	String index() {
		return "redirect:/category";
	}

	@GetMapping("/{id}")
	public ModelAndView getChapter(@PathVariable("id") Long id) {
		Chapter chapter = findChapterOrThrow(id);
		Course course = getCourseOrThrow(chapter);
		CourseCategory category = getCategoryOrThrow(course);

		ModelAndView model = new ModelAndView();
		model.setViewName(VIEW_SELECTED_CHAPTER);
		model.addObject("course", course);
		model.addObject("chapter", chapter);
		// path
		model.addObject("categoryPath", CategoryTreeBuilder.buildPathToRoot(getCategoriesList(), category));
		this.addRoleAttributes(model);
		return model;
	}

	private Chapter findChapterOrThrow(Long id) {
		return chapterRepo.findById(id).orElseThrow(() -> new ChapterNotFoundException(id));
	}

	@RolesAllowed({"ADMIN", "TEACHER"})
	@GetMapping("/add/{courseId}")
	public ModelAndView getAdd(@PathVariable("courseId") Long courseId) {
		Course course = courseRepo.findById(courseId).orElseThrow(() -> new CourseNotFoundException(courseId));
		var formData = new AddChapterForm();
		formData.setCourseId(courseId);
		formData.setSequenceNumber(chapterRepo.getNextSequenceNumber(courseId));

		ModelAndView model = new ModelAndView(VIEW_CHAPTER_ADD);
		model.addObject("categories", buildCategoryListForSelectElement(false, INDENT));
		model.addObject("formData", formData);
		model.addObject("pageTitle", getMessage("unit.page_title_add"));
		model.addObject("course", course);
		this.addRoleAttributes(model);
		return model;
	}

	@RolesAllowed({"ADMIN", "TEACHER"})
	@PostMapping("/add/{courseId}")
	public String getAdd(Model model, @ModelAttribute AddChapterForm formData, Errors errors) {
		var courseId = formData.getCourseId();
		Course course = courseRepo.findById(courseId).orElseThrow(() -> new CourseNotFoundException(courseId));
		this.addRoleAttributes(model);
		if (null == formData.getName() || formData.getName().isBlank()) {

			model.addAttribute("categories", buildCategoryListForSelectElement(false, INDENT));
			model.addAttribute("formData", formData);
			model.addAttribute("pageTitle", getMessage("unit.page_title_add"));
			model.addAttribute("course", course);
			String requiredError = getMessage("error.unit.name_required");
			model.addAttribute("requiredError", requiredError);
			return VIEW_CHAPTER_ADD;
		}
		Chapter newChapter = formData.toChapter(courseRepo);
		chapterRepo.save(newChapter);
		return "redirect:/course/" + formData.getCourseId();
	}

	@RolesAllowed({"ADMIN", "TEACHER"})
	@GetMapping("/edit/{id}")
	public ModelAndView getEdit(@PathVariable("id") Long id) {
		Chapter chapter = findChapterOrThrow(id);
		Course course = getCourseOrThrow(chapter);

		var formData = new AddChapterForm();
		formData.setChapterId(id);
		formData.setName(chapter.getName());
		formData.setDescription(chapter.getDescription());
		formData.setCourseId(course.getId());
		formData.setSequenceNumber(chapter.getSequenceNumber());

		ModelAndView model = new ModelAndView(VIEW_EDIT_CHAPTER);
		model.addObject("formData", formData);
		model.addObject("pageTitle", getMessage("unit.page_title_edit"));
		model.addObject("course", course);
		this.addRoleAttributes(model);
		return model;
	}

	@RolesAllowed({"ADMIN", "TEACHER"})
	@PostMapping("/edit/{id}")
	public String postEdit(Model model, @ModelAttribute AddChapterForm formData, Errors errors) {
		Long id = formData.getChapterId();
		Chapter chapter = findChapterOrThrow(id);
		Course course = getCourseOrThrow(chapter);
		this.addRoleAttributes(model);
		if (null == formData.getName() || formData.getName().isBlank()) {
			model.addAttribute("formData", formData);
			model.addAttribute("pageTitle", getMessage("unit.page_title_edit"));
			model.addAttribute("course", course);
			String requiredError = getMessage("error.unit.name_required");
			model.addAttribute("requiredError", requiredError);
			return VIEW_CHAPTER_ADD;
		}
		if (formData.getSequenceNumber() == null)
			formData.setSequenceNumber(0L);
		chapter.setName(formData.getName());
		chapter.setDescription(formData.getDescription());
		chapter.setSequenceNumber(formData.getSequenceNumber());
		chapterRepo.save(chapter);
		return "redirect:/unit/" + chapter.getId();
	}
	
	@RolesAllowed({"ADMIN", "TEACHER"})
	@GetMapping("/delete/{id}")
	public String getDelete(@PathVariable("id") Long id) {
		Chapter chapter = findChapterOrThrow(id);
		Course course = getCourseOrThrow(chapter);
		chapterRepo.delete(chapter);
		return "redirect:/course/" + course.getId();
	}
}
