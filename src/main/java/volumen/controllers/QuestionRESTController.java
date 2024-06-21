package volumen.controllers;

import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import volumen.model.dto.TestQuestionDTO;

@Controller
@RequestMapping("/questionservice")
public class QuestionRESTController {

    @GetMapping(path = "/get/{id}", produces = "application/json")
    public @ResponseBody TestQuestionDTO getBook(@PathVariable("id") Long id) {
        return new TestQuestionDTO();
    }
    // /questionservice/save
    @PostMapping(path= "/save", consumes = "application/json", produces = "application/json")
	public ResponseEntity<TestQuestionDTO> saveQuestion(@RequestBody TestQuestionDTO questionDTO) throws Exception 
	{
    	return new ResponseEntity<>(questionDTO, HttpStatus.CREATED);
	}
}