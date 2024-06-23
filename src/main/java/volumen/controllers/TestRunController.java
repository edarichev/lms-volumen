package volumen.controllers;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import volumen.controllers.forms.TestRunForm;
import volumen.data.LectureTestRepository;
import volumen.data.LecturesRepository;
import volumen.exceptions.LectureNotFoundException;
import volumen.exceptions.TestNotFoundException;
import volumen.model.Chapter;
import volumen.model.Course;
import volumen.model.Lecture;
import volumen.model.LectureTest;
import volumen.model.dto.AnswerDTO;
import volumen.model.dto.ExamResultsDTO;
import volumen.model.dto.ExamSetDTO;
import volumen.model.dto.QuestionResultDTO;
import volumen.model.dto.TestQuestionDTO;

@RequestMapping("/runtest")
@Controller
public class TestRunController extends BaseController {
	
	private static final String VIEW_RUN_FULL_TEST = "test/test_run_full";
	
	@Autowired
	LecturesRepository lectureRepo;
	
	@Autowired
	LectureTestRepository testRepo;
	
	// /runtest/11/22/33
	@GetMapping("/lecture/{lectureId}")
	ModelAndView getRunTest(@PathVariable("lectureId") Long lectureId) {
		Lecture lecture = findLecture(lectureId);
		LectureTest test = getLectureTestOrThrow(lecture);
		Chapter chapter = getChapterOrThrow(lecture);
		Course course = getCourseOrThrow(chapter);
		
		ExamSetDTO exam = makeExamSetDTO(lecture, test, chapter, course);
		
		TestRunForm formData = new TestRunForm();
		formData.setChapterId(chapter.getId());
		formData.setCourseId(course.getId());
		formData.setLectureId(lectureId);
		formData.setTestId(test.getId());
		
		ModelAndView model = new ModelAndView();
		model.setViewName(VIEW_RUN_FULL_TEST);
		model.addObject("formData", formData);
		model.addObject("lecture", lecture);
		model.addObject("chapter", chapter);
		model.addObject("course", course);
		model.addObject("exam", exam);
		model.addObject("isPostBack", false);
		model.addObject("pageTitle", getMessage("test.running") + ": " + lecture.getName());
		return model;
	}

	private ExamSetDTO makeExamSetDTO(Lecture lecture, LectureTest test, Chapter chapter, Course course) {
		ExamSetDTO exam = new ExamSetDTO();
		exam.setChapterId(chapter.getId());
		exam.setCourseId(course.getId());
		exam.setLectureTestId(test.getId());
		exam.setLectureId(lecture.getId());
		
		ArrayList<TestQuestionDTO> questions = new ArrayList<TestQuestionDTO>();
		for (var question : test.getQuestions()) {
			ArrayList<AnswerDTO> answersList = new ArrayList<AnswerDTO>();
			for (var answer : question.getAnswers()) {
				// set 'valid' to false
				answersList.add(new AnswerDTO(answer.getId(), answer.getSequenceNumber(), answer.getText(), false));
			}
			TestQuestionDTO questionDTO = new TestQuestionDTO(test.getId(), question.getId(), question.getText(), 
					question.getQuestionType().name(), answersList);
			questions.add(questionDTO);
		}
		exam.setQuestions(questions);
		return exam;
	}

	@PostMapping("/lecture/{lectureId}")
	ModelAndView postRunTest(ModelAndView model, HttpServletRequest request, @ModelAttribute TestRunForm formData) {
		model.setViewName(VIEW_RUN_FULL_TEST);
		Long testId = formData.getTestId();
		var test = findLectureTest(testId);
		var lecture = getLectureOrThrow(test);
		var chapter = getChapterOrThrow(lecture);
		var course = getCourseOrThrow(chapter);
		
		ExamResultsDTO result = checkUserAnswers(request, test);
		model.addObject("result", result);
		model.addObject("formData", formData);
		model.addObject("lecture", lecture);
		model.addObject("chapter", chapter);
		model.addObject("course", course);
		model.addObject("isPostBack", true);
		model.addObject("pageTitle", getMessage("test.running") + ": " + lecture.getName());
		return model;
	}

	private ExamResultsDTO checkUserAnswers(HttpServletRequest request, LectureTest test) {
		// restore DTO from fields and build exam results
		ExamResultsDTO result = new ExamResultsDTO();
		for (var question : test.getQuestions()) {
			boolean isValidAnswer = false;
			switch (question.getQuestionType()) {
			case SINGLE: {
				// radio: in 'value' stored the selected answer 
				String strValue = request.getParameter("dto_question_single_" + question.getId());
				for (var answer : question.getAnswers()) {
					if (answer.getId().toString().equals(strValue) && answer.isValid()) {
						isValidAnswer = true;
						break;
					}
				}
				break;
			}
			case MULTIPLE: {
				// checkboxes: answer id as selected value
				isValidAnswer = true;
				for (var answer : question.getAnswers()) {
					String strValue = request.getParameter("answer_" + answer.getId());
					if ((strValue == null || strValue.isBlank()) && !answer.isValid()) {
						// ok, false answer is not set
						continue;
					}
					if ((strValue == null || strValue.isBlank()) && answer.isValid()) {
						// true answer is not set, stop
						isValidAnswer = false;
						break;
					}
					if (strValue.equalsIgnoreCase("on") && answer.isValid()) {
						// ok, true answer is set
						continue;
					}
					// false answer, stop
					isValidAnswer = false;
					break;
				}
				break;
			}
			case TEXT: {
				// input=text, value is answer
				String strValue = request.getParameter("dto_question_text_answer_" + question.getId());
				if (strValue == null)
					break;
				strValue = strValue.strip();
				for (var answer : question.getAnswers()) { // must be only one answer, but for any case
					if (strValue.equalsIgnoreCase(answer.getText())) {
						isValidAnswer = true;
						break;
					}
				}
			}
			break;
			default:
				break;
			}
			result.getQuestions().add(new QuestionResultDTO(question.getId(), question.getText(), isValidAnswer));
		}
		return result;
	}

	private Lecture findLecture(Long lectureId) {
		return lectureRepo.findById(lectureId).orElseThrow(() -> new LectureNotFoundException(lectureId));
	}
	
	private LectureTest findLectureTest(Long testId) {
		return testRepo.findById(testId).orElseThrow(() -> new TestNotFoundException(testId));
	}
}
