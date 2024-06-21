package volumen.controllers;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import volumen.controllers.forms.EditQuestionForm;
import volumen.data.LectureTestRepository;
import volumen.exceptions.TestNotFoundException;
import volumen.model.Chapter;
import volumen.model.Course;
import volumen.model.Lecture;
import volumen.model.LectureTest;
import volumen.model.QuestionType;
import volumen.model.dto.AnswerDTO;
import volumen.model.dto.IdNamePair;
import volumen.model.dto.TestQuestionDTO;

@Controller
@RequestMapping("/question")
public class QuestionController extends BaseController {

	private static final String VIEW_QUESTION_ADD = "question/question_edit";
	private static final String VIEW_EDIT_QUESTION = "question/question_edit";
	
	@Autowired
	LectureTestRepository testRepo;
	
	@GetMapping(path = {"", "/"})
	String index() {
		return "redirect:/category";
	}
	
	@GetMapping("/add/{testId}")
	ModelAndView getAdd(@PathVariable("testId") Long testId) {
		LectureTest test = findTest(testId); 
		Lecture lecture = getLecture(test);
		Chapter chapter = getChapter(lecture);
		Course course = getCourse(chapter);
		ModelAndView model = new ModelAndView(VIEW_QUESTION_ADD);
		EditQuestionForm formData = new EditQuestionForm();
		formData.setQuestionTypes(buildQuestionTypes());
		formData.setTestId(testId);
		TestQuestionDTO questionDTO = new TestQuestionDTO(testId, null, QuestionType.SINGLE.name(), new ArrayList<AnswerDTO>());
		formData.setQuestion(questionDTO);
		
		model.addObject("course", course);
		model.addObject("chapter", chapter);
		model.addObject("lecture", lecture);
		model.addObject("test", test);
		model.addObject("formData", formData);
		model.addObject("testId", testId);
		model.addObject("questionId", -1L);
		model.addObject("lectureId", lecture.getId());
		return model;
	}
	
	protected ArrayList<IdNamePair<String>> buildQuestionTypes() {
		ArrayList<IdNamePair<String>> types = new ArrayList<>();
		types.add(new IdNamePair<String>(QuestionType.TEXT.name(), getMessage("question.type_name_text")));
		types.add(new IdNamePair<String>(QuestionType.SINGLE.name(), getMessage("question.type_name_single")));
		types.add(new IdNamePair<String>(QuestionType.MULTIPLE.name(), getMessage("question.type_name_multiple")));
		return types;
	}
	
	protected LectureTest findTest(Long id) {
		LectureTest test = testRepo.findById(id).orElseThrow(() -> new TestNotFoundException(id));
		return test;
	}
}
