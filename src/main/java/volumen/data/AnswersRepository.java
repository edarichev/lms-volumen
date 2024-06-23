package volumen.data;

import org.springframework.data.repository.CrudRepository;

import volumen.model.Answer;

public interface AnswersRepository extends CrudRepository<Answer, Long> {
	//@Query("DELETE FROM Answer a WHERE a.test_question_id = ?1")
	void deleteAnswerByTestQuestionIdEquals(Long questionId);
}
