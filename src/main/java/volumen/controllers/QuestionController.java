package volumen.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import volumen.controllers.forms.EditQuestionForm;
import volumen.data.LectureTestRepository;
import volumen.data.TestQuestionRepository;
import volumen.exceptions.QuestionNotFoundException;
import volumen.exceptions.TestNotFoundException;
import volumen.model.Chapter;
import volumen.model.Course;
import volumen.model.CourseCategory;
import volumen.model.Lecture;
import volumen.model.LectureTest;
import volumen.model.QuestionType;
import volumen.model.TestQuestion;
import volumen.model.dto.AnswerDTO;
import volumen.model.dto.IdNamePair;
import volumen.model.dto.TestQuestionDTO;
import volumen.web.ui.CategoryTreeBuilder;

/**
 * Controller for editing questions
 * 
 * SAVE method placed separately into QuestionRESTController because
 * it works with javascript-based form
 */
@Controller
@RequestMapping("/question")
public class QuestionController extends BaseController {

	private static final String VIEW_QUESTION_ADD = "question/question_edit";
	private static final String VIEW_EDIT_QUESTION = "question/question_edit";

	@Autowired
	LectureTestRepository testRepo;

	@Autowired
	TestQuestionRepository questionRepo;

	@GetMapping(path = { "", "/" })
	String index() {
		return "redirect:/category";
	}

	@GetMapping("/add/{testId}")
	ModelAndView getAdd(@PathVariable("testId") Long testId) {
		LectureTest test = findTestOrThrow(testId);
		Lecture lecture = getLectureOrThrow(test);
		Chapter chapter = getChapterOrThrow(lecture);
		Course course = getCourseOrThrow(chapter);
		CourseCategory category = getCategoryOrThrow(course);

		EditQuestionForm formData = new EditQuestionForm();
		formData.setQuestionTypes(buildQuestionTypesList());
		formData.setTestId(testId);
		TestQuestionDTO questionDTO = new TestQuestionDTO(testId, null, null, QuestionType.SINGLE.name(),
				new ArrayList<AnswerDTO>());
		formData.setQuestion(questionDTO);

		ModelAndView model = new ModelAndView(VIEW_QUESTION_ADD);
		model.addObject("course", course);
		model.addObject("chapter", chapter);
		model.addObject("lecture", lecture);
		model.addObject("test", test);
		model.addObject("formData", formData);
		model.addObject("testId", testId);
		model.addObject("questionId", -1L);
		model.addObject("lectureId", lecture.getId());
		model.addObject("categoryPath", CategoryTreeBuilder.buildPathToRoot(getCategoriesList(), category));
		return model;
	}

	@GetMapping("/edit/{id}")
	ModelAndView getEdit(@PathVariable("id") Long id) {
		TestQuestion question = findQuestionOrThrow(id);
		LectureTest test = getLectureTestOrThrow(question);
		Long testId = test.getId();
		Lecture lecture = getLectureOrThrow(test);
		Chapter chapter = getChapterOrThrow(lecture);
		Course course = getCourseOrThrow(chapter);
		CourseCategory category = getCategoryOrThrow(course);
		
		EditQuestionForm formData = new EditQuestionForm();
		formData.setQuestionTypes(buildQuestionTypesList());
		formData.setTestId(testId);
		
		TestQuestionDTO questionDTO = createTestQuestionDTO(id, question, testId);
		formData.setQuestion(questionDTO);

		ModelAndView model = new ModelAndView(VIEW_EDIT_QUESTION);
		model.addObject("course", course);
		model.addObject("chapter", chapter);
		model.addObject("lecture", lecture);
		model.addObject("test", test);
		model.addObject("formData", formData);
		model.addObject("testId", testId);
		model.addObject("questionId", id);
		model.addObject("lectureId", lecture.getId());
		model.addObject("categoryPath", CategoryTreeBuilder.buildPathToRoot(getCategoriesList(), category));
		return model;
	}

	private TestQuestionDTO createTestQuestionDTO(Long questionId, TestQuestion question, Long testId) {
		var answers = new ArrayList<AnswerDTO>();
		for (var a : question.getAnswers()) {
			answers.add(new AnswerDTO(a));
		}
		Collections.sort(answers, new Comparator<AnswerDTO>() {
			@Override
			public int compare(AnswerDTO arg0, AnswerDTO arg1) {
				long s0 = arg0.getSequenceNumber() == null ? 0 : arg0.getSequenceNumber();
				long s1 = arg1.getSequenceNumber() == null ? 0 : arg1.getSequenceNumber();
				return (int) (s0 - s1);
			}
		});
		TestQuestionDTO questionDTO = new TestQuestionDTO(testId, questionId, question.getText(),
				question.getQuestionType().name(), answers);
		return questionDTO;
	}

	protected ArrayList<IdNamePair<String>> buildQuestionTypesList() {
		ArrayList<IdNamePair<String>> types = new ArrayList<>();
		types.add(new IdNamePair<String>(QuestionType.TEXT.name(), getMessage("question.type_name_text")));
		types.add(new IdNamePair<String>(QuestionType.SINGLE.name(), getMessage("question.type_name_single")));
		types.add(new IdNamePair<String>(QuestionType.MULTIPLE.name(), getMessage("question.type_name_multiple")));
		return types;
	}

	private TestQuestion findQuestionOrThrow(Long id) {
		return questionRepo.findById(id).orElseThrow(() -> new QuestionNotFoundException(id));
	}

	protected LectureTest findTestOrThrow(Long id) {
		LectureTest test = testRepo.findById(id).orElseThrow(() -> new TestNotFoundException(id));
		return test;
	}
}
