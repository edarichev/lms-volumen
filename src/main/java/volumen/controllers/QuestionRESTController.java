package volumen.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import volumen.data.AnswersRepository;
import volumen.data.LectureTestRepository;
import volumen.data.TestQuestionRepository;
import volumen.exceptions.QuestionNotFoundException;
import volumen.exceptions.TestNotFoundException;
import volumen.model.Answer;
import volumen.model.LectureTest;
import volumen.model.QuestionType;
import volumen.model.TestQuestion;
import volumen.model.dto.AnswerDTO;
import volumen.model.dto.TestQuestionDTO;

@Controller
@RequestMapping("/questionservice")
public class QuestionRESTController {
	
	@Autowired
	TestQuestionRepository questionRepo;
	
	@Autowired
	LectureTestRepository testRepo;
	
	@Autowired
	AnswersRepository answersRepo;

    @GetMapping(path = "/get/{id}", produces = "application/json")
    public @ResponseBody TestQuestionDTO getBook(@PathVariable("id") Long id) {
        return new TestQuestionDTO();
    }
    
	@GetMapping("/delete/{id}")
	String getDelete(@PathVariable("id") Long id) {
		TestQuestion question = findQuestion(id);
		LectureTest test = question.getLectureTest();
		questionRepo.deleteById(id);
		return "redirect:/test/edit/" + test.getId();
	}

    private TestQuestion findQuestion(Long id) {
		return questionRepo.findById(id).orElseThrow(() -> new QuestionNotFoundException(id));
	}

	// /questionservice/save
    @PostMapping(path= "/save", consumes = "application/json", produces = "application/json")
	public ResponseEntity<TestQuestionDTO> saveQuestion(@RequestBody TestQuestionDTO questionDTO) throws Exception 
	{
    	TestQuestionDTO savedDTO = saveTestQuestion(questionDTO);
    	return new ResponseEntity<>(savedDTO, HttpStatus.CREATED);
	}
    
	private TestQuestionDTO saveTestQuestion(TestQuestionDTO questionDTO) {
		TestQuestion question = createOrFindQuestion(questionDTO);
		// reorder and change sequence numbers [1..n]
		ArrayList<AnswerDTO> orderedAnswers = reorderAnswersBySequenceNumber(questionDTO);
		questionDTO.setAnswers(orderedAnswers);
		// answers
		replaceAnswers(questionDTO, question, orderedAnswers);
		questionDTO.setId(question.getId());
		return questionDTO;
	}
	
	private void replaceAnswers(TestQuestionDTO questionDTO, TestQuestion question,
			ArrayList<AnswerDTO> orderedAnswers) {
		if (orderedAnswers.isEmpty()) {
			answersRepo.deleteAnswerByTestQuestionIdEquals(questionDTO.getId());
			return;
		}
		if (question.getAnswers().isEmpty()) {
			// no answers presents in the DB, simple add all answers
			for (var answerDTO : questionDTO.getAnswers()) {
				var answer = new Answer();
				answer.setTestQuestion(question);
				answer.setText(answerDTO.getAnswer());
				answer.setValid(answerDTO.getValid());
				question.getAnswers().add(answer);
				answersRepo.save(answer);
				answerDTO.setId(answer.getId());
			}
		} else {
			// modify existing, remove if not present in DTO, add new answers
			// make a map to quick access and make relations
			// key - dto answer
			// value - db answer
			// build this map by id==id
			HashMap<AnswerDTO, Answer> amap = new HashMap<AnswerDTO, Answer>();
			HashSet<Long> keysFromRequest = new HashSet<Long>();
			var existingAnswers = question.getAnswers();
			for (var a : orderedAnswers) {
				if (a.getId() == -1) {
					// new answer
					amap.put(a, null);
					continue;
				}
				// find and add by id
				int i = 0;
				for (i = 0; i < existingAnswers.size(); ++i) {
					var answer = existingAnswers.get(i);
					if (a.getId().equals(answer.getId())) {
						amap.put(a, answer);
						keysFromRequest.add(a.getId());
						break;
					}
				}
				if (i == existingAnswers.size()) {
					// id > 0 here,
					// not found, but id was present in previous request, 
					// this answer was deleted between requests, set id to -1
					a.setId(-1L);
					amap.put(a, null);
				}
			}
			
			// 1. remove, if not in a request set
			for (int i = 0; i < existingAnswers.size(); ++i) {
				var answer = existingAnswers.get(i);
				if (!keysFromRequest.contains(answer.getId())) {
					existingAnswers.remove(i);
					i--;
					answersRepo.delete(answer);
					continue;
				}
			}
			// 2. add with id==-1 (new answers) and update order numbers of all
			for (var a : amap.entrySet()) {
				var dto = a.getKey();
				var answer = a.getValue();
				if (answer == null) {
					answer = new Answer();
					amap.replace(dto, answer);
				}
				answer.setSequenceNumber(dto.getSequenceNumber());
				answer.setTestQuestion(question);
				answer.setText(dto.getAnswer());
				answer.setValid(dto.getValid());
				answersRepo.save(answer);
			}
		}
	}
	
	private ArrayList<AnswerDTO> reorderAnswersBySequenceNumber(TestQuestionDTO questionDTO) {
		ArrayList<AnswerDTO> orderedAnswers = questionDTO.getAnswers().stream()
				.filter((k)->k.getAnswer() != null && !k.getAnswer().isBlank())
				.sorted((k1, k2) -> (int)(k1.getSequenceNumber() - k2.getSequenceNumber()))
				.collect(Collectors.toCollection(ArrayList::new));
		for (int i = 0; i < orderedAnswers.size(); ++i)
			orderedAnswers.get(i).setSequenceNumber((long)(i + 1));
		return orderedAnswers;
	}
	
	private TestQuestion createOrFindQuestion(TestQuestionDTO questionDTO) {
		TestQuestion question = null;
		if (questionDTO.getId() > 0) {
			var existingQuestion = questionRepo.findById(questionDTO.getId());
			if (existingQuestion.isPresent())
				question = existingQuestion.get();
		}
		var questionType = QuestionType.valueOf(questionDTO.getQuestionType());
		if (question == null) {
			// may be not found, create a new question
			question = new TestQuestion();
			LectureTest test = testRepo.findById(questionDTO.getTestId()).orElseThrow(() -> new TestNotFoundException());
			question.setLectureTest(test);
		}
		question.setText(questionDTO.getText());
		question.setQuestionType(questionType);
		// save the question to get an id
		questionRepo.save(question);
		return question;
	}
}