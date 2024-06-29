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

import volumen.controllers.forms.EditTestForm;
import volumen.data.LectureTestRepository;
import volumen.data.LecturesRepository;
import volumen.exceptions.LectureNotFoundException;
import volumen.exceptions.TestNotFoundException;
import volumen.model.Chapter;
import volumen.model.Course;
import volumen.model.CourseCategory;
import volumen.model.Lecture;
import volumen.model.LectureTest;
import volumen.model.TestQuestion;
import volumen.web.ui.CategoryTreeBuilder;

/**
 * Test controller
 * 
 * /test/add/lectureId
 * /test/edit/id
 * /test/run/id
 * /test/delete/id
 */
@Controller
@RequestMapping("/test")
public class TestController extends BaseController {
	
	private static final String VIEW_EDIT_TEST = "test/test_edit";
	
	@Autowired
	LectureTestRepository testRepo;
	
	@Autowired
	LecturesRepository lectureRepo;

	@GetMapping(path = {"", "/"})
	String index() {
		return "redirect:/category";
	}
	
	/**
	 * Add the test. No parameters of test required, after adding a test go to edit page directly.
	 * 
	 * @param lectureId lecture of this test
	 * @return redirect to edit page
	 */
	@GetMapping("/add/{lectureId}")
	String getAdd(@PathVariable("lectureId") Long lectureId) {
		Lecture lecture = findLectureOrThrow(lectureId);
		LectureTest test = getLectureTestOrThrow(lecture);
		if (test == null) {
			// check possible dangling reference
			test = testRepo.findTestByLectureId(lectureId);
			if (test != null) {
				// dangling reference found
				lecture.setLectureTest(test);
				lectureRepo.save(lecture);
			}
			else {
				// create a new test
				test = createTest(lecture);
			}
		}
		return "redirect:/test/edit/" + test.getId();
	}

	private Lecture findLectureOrThrow(Long lectureId) {
		Lecture lecture = lectureRepo.findById(lectureId).orElseThrow(() -> new LectureNotFoundException(lectureId));
		return lecture;
	}

	private LectureTest createTest(Lecture lecture) {
		LectureTest test;
		test = new LectureTest();		
		test.setLecture(lecture);
		testRepo.save(test);
		lecture.setLectureTest(test);
		lectureRepo.save(lecture);
		return test;
	}
	
	@GetMapping("/edit/{id}")
	ModelAndView getEdit(@PathVariable("id") Long id) {
		// find and load main data of test
		LectureTest test = findTestOrThrow(id);
		Lecture lecture = getLectureOrThrow(test);
		Chapter chapter = getChapterOrThrow(lecture);
		Course course = getCourseOrThrow(chapter);
		CourseCategory category = course.getCategory();
		
		EditTestForm formData = new EditTestForm();
		formData.setTestId(test.getId());
		formData.setLectureId(lecture.getId());

		ModelAndView model = new ModelAndView(VIEW_EDIT_TEST);
		model.addObject("formData", formData);
		model.addObject("lecture", lecture);
		model.addObject("chapter", chapter);
		model.addObject("course", course);
		model.addObject("questions", getTestQuestions(test));
		// path
		model.addObject("categoryPath", CategoryTreeBuilder.buildPathToRoot(getCategoriesList(), category));
		this.addRoleAttributes(model);
		return model;
	}
	
	@PostMapping("/edit/{id}")
	String postEdit(Model model, @ModelAttribute EditTestForm formData, Errors errors) {
		// find and load main data of test
		LectureTest test = findTestOrThrow(formData.getTestId());
		Lecture lecture = getLectureOrThrow(test);
		Chapter chapter = getChapterOrThrow(lecture);
		Course course = getCourseOrThrow(chapter);
		CourseCategory category = course.getCategory();
		model.addAttribute("formData", formData);
		model.addAttribute("lecture", lecture);
		model.addAttribute("chapter", chapter);
		model.addAttribute("course", course);
		this.addRoleAttributes(model);
		// path
		model.addAttribute("categoryPath", CategoryTreeBuilder.buildPathToRoot(getCategoriesList(), category));
		// move test
		Long lectureId = formData.getLectureId();
		Lecture moveTestToLecture = lectureRepo.findById(lectureId).orElseThrow(() -> new LectureNotFoundException(lectureId));
		if (moveTestToLecture.getLectureTest() != null) {
			// target found, check if lecture already contains a test
			if (lecture.getId() != moveTestToLecture.getId()) {
				model.addAttribute("alreadyContainsTest", getMessage("lecture.already_contains_test"));
				return VIEW_EDIT_TEST;
			}
		}
		if (!moveTestToLecture.getId().equals(lecture.getId())) {
			// move test
			moveTestToLecture.setLectureTest(test);
			lecture.setLectureTest(null);
			test.setLecture(moveTestToLecture);
			testRepo.save(test);
			lectureRepo.save(moveTestToLecture);
			lectureRepo.save(lecture);
			// and redirect to lecture
			return "redirect:/lecture/" + lecture.getId();
		}
		// questions
		return VIEW_EDIT_TEST;
	}

	@GetMapping("/delete/{id}")
	String getDelete(@PathVariable("id") Long id) {
		LectureTest test = findTestOrThrow(id);
		Lecture lecture = getLectureOrThrow(test);
		// в таком порядке:
		// сначала над отвязать тест от лекции
		lecture.setLectureTest(null);
		lectureRepo.save(lecture);
		// теперь можно удалить
		testRepo.delete(test);
		return "redirect:/lecture/" + lecture.getId();
	}

	protected LectureTest findTestOrThrow(Long id) {
		return testRepo.findById(id).orElseThrow(() -> new TestNotFoundException(id));
	}
	
	protected ArrayList<TestQuestion> getTestQuestions(LectureTest test) {
		return test.getQuestions().stream().collect(Collectors.toCollection(ArrayList::new));
	}
}
