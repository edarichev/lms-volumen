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

import volumen.controllers.forms.AddLectureForm;
import volumen.data.ChaptersRepository;
import volumen.data.LecturesRepository;
import volumen.exceptions.ChapterNotFoundException;
import volumen.exceptions.LectureNotFoundException;
import volumen.model.Chapter;
import volumen.model.Course;
import volumen.model.CourseCategory;
import volumen.model.Lecture;
import volumen.web.ui.CategoryTreeBuilder;

@Controller
@RequestMapping("/lecture")
public class LectureController extends BaseController {
	
	private static final String VIEW_LECTURE_ADD = "lecture/lecture_add";
	private static final String VIEW_SELECTED_LECTURE = "lecture/lecture_view";
	private static final String VIEW_EDIT_LECTURE = "lecture/lecture_add";

	@Autowired
	ChaptersRepository chapterRepo;
	
	@Autowired
	LecturesRepository lectureRepo;

	
	@GetMapping(path = {"", "/"})
	String index() {
		return "redirect:/category";
	}
	
	@GetMapping("/{id}")
	public ModelAndView getLecture(@PathVariable("id") Long id) {
		Lecture lecture = findLectureOrThrow(id);
		Chapter chapter = getChapterOrThrow(lecture);
		Course course = getCourseOrThrow(chapter);
		CourseCategory category = getCategoryOrThrow(course);
		
		ModelAndView model = new ModelAndView(VIEW_SELECTED_LECTURE);
		model.addObject("course", course);
		model.addObject("chapter", chapter);
		model.addObject("lecture", lecture);
		model.addObject("lectureContent", lectureRepo.getLectureContent(id));
		// path
		model.addObject("categoryPath", CategoryTreeBuilder.buildPathToRoot(getCategoriesList(), category));
		return model;
	}

	private Lecture findLectureOrThrow(Long id) {
		return lectureRepo.findById(id).orElseThrow(() -> new LectureNotFoundException(id));
	}
	
	
	@GetMapping("/add/{chapterId}")
	public ModelAndView getAdd(@PathVariable("chapterId") Long chapterId) {
		Chapter chapter = findChapterOrThrow(chapterId);
		Course course = getCourseOrThrow(chapter);

		var formData = new AddLectureForm();
		formData.setChapterId(chapter.getId());
		formData.setSequenceNumber(lectureRepo.getNextSequenceNumber(chapterId));
		
		ModelAndView model = new ModelAndView(VIEW_LECTURE_ADD);
		model.addObject("chapters", course.getChapters());
		model.addObject("chapter", chapter);
		model.addObject("formData", formData);
		model.addObject("pageTitle", getMessage("lecture.page_title_add"));
		model.addObject("course", course);
		return model;
	}

	private Chapter findChapterOrThrow(Long chapterId) {
		return chapterRepo.findById(chapterId).orElseThrow(() -> new ChapterNotFoundException(chapterId));
	}
	
	@PostMapping("/add/{chapterId}")
	public String getAdd(Model model, @ModelAttribute AddLectureForm formData, Errors errors) {
		var chapterId = formData.getChapterId();
		Chapter chapter = findChapterOrThrow(chapterId);
		Course course = getCourseOrThrow(chapter);
		if (null == formData.getName() || formData.getName().isBlank()) {
			model.addAttribute("chapters", course.getChapters());
			model.addAttribute("chapter", chapter);
			model.addAttribute("formData", formData);
			model.addAttribute("pageTitle", getMessage("lecture.page_title_add"));
			model.addAttribute("course", course);
			model.addAttribute("requiredError", getMessage("error.lecture.name_required"));
			return VIEW_LECTURE_ADD;
		}
		Lecture newLecture = formData.toLecture(chapterRepo);
		lectureRepo.save(newLecture);
		return "redirect:/unit/" + chapterId;
	}
	
	@GetMapping("/edit/{id}")
	public ModelAndView getEdit(@PathVariable("id") Long id) {
		Lecture lecture = findLectureOrThrow(id);
		Chapter chapter = getChapterOrThrow(lecture);
		Course course = getCourseOrThrow(chapter);
		
		var formData = new AddLectureForm();
		formData.setChapterId(chapter.getId());
		formData.setLectureId(id);
		formData.setName(lecture.getName());
		formData.setDescription(lecture.getDescription());
		formData.setSequenceNumber(lecture.getSequenceNumber());
		formData.setContent(lectureRepo.getLectureContent(id));

		ModelAndView model = new ModelAndView(VIEW_EDIT_LECTURE);
		model.addObject("pageTitle", getMessage("lecture.page_title_edit"));
		model.addObject("formData", formData);
		model.addObject("chapter", chapter);
		model.addObject("chapters", course.getChapters());
		model.addObject("course", course);
		return model;
	}

	@PostMapping("/edit/{id}")
	public String postEdit(Model model, @ModelAttribute AddLectureForm formData, Errors errors) {
		Long id = formData.getLectureId();
		Lecture lecture = findLectureOrThrow(id);
		Chapter chapter = getChapterOrThrow(lecture);
		Course course = getCourseOrThrow(chapter);
		
		if (null == formData.getName() || formData.getName().isBlank()) {
			model.addAttribute("pageTitle", getMessage("unit.page_title_edit"));
			model.addAttribute("formData", formData);
			model.addAttribute("chapters", course.getChapters());
			model.addAttribute("chapter", chapter);
			model.addAttribute("course", course);
			model.addAttribute("requiredError", getMessage("error.unit.name_required"));
			return VIEW_LECTURE_ADD;
		}
		if (formData.getSequenceNumber() == null)
			formData.setSequenceNumber(lectureRepo.getNextSequenceNumber(chapter.getId()));
		lecture.setName(formData.getName());
		lecture.setDescription(formData.getDescription());
		lecture.setSequenceNumber(formData.getSequenceNumber());
		lecture.setContent(formData.getContent());
		lecture.setChapter(chapter);
		lectureRepo.save(lecture);
		return "redirect:/unit/" + chapter.getId();
	}
	
	@GetMapping("/delete/{id}")
	public String getDelete(@PathVariable("id") Long id) {
		Lecture lecture = findLectureOrThrow(id);
		Chapter chapter = getChapterOrThrow(lecture);
		lectureRepo.delete(lecture);
		return "redirect:/unit/" + chapter.getId();
	}
}
