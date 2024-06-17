package volumen.data;

import org.springframework.data.repository.CrudRepository;

import volumen.model.TestQuestion;

public interface TestQuestionRepository extends CrudRepository<TestQuestion, Long> {

}
