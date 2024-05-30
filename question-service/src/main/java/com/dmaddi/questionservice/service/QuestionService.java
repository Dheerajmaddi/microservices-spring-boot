package com.dmaddi.questionservice.service;

import com.dmaddi.questionservice.dao.QuestionDao;
import com.dmaddi.questionservice.model.Question;
import com.dmaddi.questionservice.model.QuestionWrapper;
import com.dmaddi.questionservice.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class QuestionService {

    @Autowired
    private QuestionDao questionDao;

    public ResponseEntity<List<Question>> getAllQuestions(){
        try {
            return new ResponseEntity<>(questionDao.findAll(), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.BAD_REQUEST);
    }

    public ResponseEntity<List<Question>> getQuestionsByCategory(String category) {
     /* Convert string to title case
        char[] arr = category.toCharArray();
        arr[0] = Character.toTitleCase(arr[0]);
        String titleCaseCategory = new String(arr);
        System.out.println(titleCaseCategory);
     */

        return new ResponseEntity<>(questionDao.findByCategory(category),HttpStatus.OK);
    }

    public ResponseEntity<String> addQuestion(Question question) {
        if(question.getQuestionTitle().isEmpty())
            return new ResponseEntity<>("failure", HttpStatus.NOT_ACCEPTABLE);

        else{
            questionDao.save(question);
            return new ResponseEntity<>("success", HttpStatus.CREATED);
        }
    }

    public ResponseEntity<List<Integer>> getQuestionsForQuiz(String categoryName, Integer numQuestions) {
        List<Integer> questions = questionDao.findRandomQuestionsByCategory(categoryName, numQuestions);

        return new ResponseEntity<>(questions, HttpStatus.OK);
    }

    public ResponseEntity<List<QuestionWrapper>> getQuestionsFromId(List<Integer> questionIds) {
        List<QuestionWrapper> wrappers = new ArrayList<>();
        List<Question> questions = new ArrayList<>();

        if(questionIds.isEmpty())
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);

        for(Integer id: questionIds){
            questions.add(questionDao.findById(id).get());
        }

        for(Question q: questions){
            wrappers.add(new QuestionWrapper(q.getId(), q.getQuestionTitle(), q.getOption1(), q.getOption2(), q.getOption3(), q.getOption4()));
        }

        return new ResponseEntity<>(wrappers, HttpStatus.OK);
    }

    public ResponseEntity<Integer> getScore(List<Response> responses) {
        int rightAnswers = 0;

        for(Response response: responses) {
            if(response.getId() <= 0)
                return new ResponseEntity<>(0, HttpStatus.BAD_REQUEST);
            Question question = questionDao.findById(response.getId()).get();
            if(response.getResponse().equals(question.getRightAnswer()))
                rightAnswers++;
        }

        return new ResponseEntity<>(rightAnswers, HttpStatus.OK);
    }
}
