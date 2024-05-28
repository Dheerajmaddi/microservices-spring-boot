package com.dmaddi.quizservice.service;


import com.dmaddi.quizservice.dao.QuizDao;

import com.dmaddi.quizservice.feign.QuizInterface;
import com.dmaddi.quizservice.model.QuestionWrapper;
import com.dmaddi.quizservice.model.Quiz;
import com.dmaddi.quizservice.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class QuizService {

    @Autowired
    private QuizDao quizDao;

    @Autowired
    private QuizInterface quizInterface;

    public ResponseEntity<String> createQuiz(String category, Integer numQ, String title) {
        List<Integer> questions = quizInterface.getQuestionsForQuiz(category,numQ).getBody();

        Quiz quiz = new Quiz();
        quiz.setTitle(title);
        quiz.setQuestionIds(questions);

        quizDao.save(quiz);

        return new ResponseEntity<>("Success", HttpStatus.CREATED);
    }

    public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(Integer id) {
        Optional<Quiz> quiz = quizDao.findById(id);

        List<Integer> questionIds = quiz.get().getQuestionIds();
//        ResponseEntity<List<QuestionWrapper>> questionsForUser = quizInterface.getQuestionsFromId(questionIds);
        return quizInterface.getQuestionsFromId(questionIds);
    }

    public ResponseEntity<Integer> calculateResult(Integer id, List<Response> responses) {
//        ResponseEntity<Integer> score = quizInterface.getScore(responses);
        return quizInterface.getScore(responses);
    }
}
