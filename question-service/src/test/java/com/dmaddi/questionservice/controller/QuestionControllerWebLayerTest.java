package com.dmaddi.questionservice.controller;


import com.dmaddi.questionservice.model.Question;
import com.dmaddi.questionservice.service.QuestionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.mockito.stubbing.OngoingStubbing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@WebMvcTest(controllers = QuestionController.class)
//@MockBean({QuestionService.class, QuestionDao.class})
public class QuestionControllerWebLayerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QuestionService questionService;

    private Question question;


    @BeforeEach
    void fillQuestion(){
        question = new Question();
//        question.setQuestionTitle("What is your age?");
        question.setCategory("Coding Language");
        question.setOption1("1");
        question.setOption2("2");
        question.setOption3("3");
        question.setOption4("4");
        question.setRightAnswer("3");
        question.setDifficultyLevel("Easy");
    }

    @Test
    @DisplayName("Add a new question")
    void testGetAllQuestions_whenValidDetailsProvided_shouldReturnAllQuestions() throws Exception {
        // Arrange
        question.setId(1);

        when(questionService.addQuestion(any(Question.class))).thenReturn(new ResponseEntity<>("success", HttpStatus.CREATED));

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/question/add")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(question));

        // Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String responseBodyAsString = mvcResult.getResponse().getContentAsString();

        // Assert
        Assertions.assertEquals("success", responseBodyAsString, "Should return a success message");
    }

    @Test
    @DisplayName("Question title is empty")
    void testAddQuestion_whenQuestionTitleFieldIsEmpty_returns406HttpCode() throws Exception {
        // Arrange
        question.setQuestionTitle("");
        question.setId(2);

        ResponseEntity<String> re = new ResponseEntity<>("failure", HttpStatus.NOT_ACCEPTABLE);

        when(questionService.addQuestion(any(Question.class)))
                .thenReturn(re);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/question/add")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(question));

        // Act
        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        String responseMessage = mvcResult.getResponse().getContentAsString();

        // Assert
        Assertions.assertEquals(re.getBody(), responseMessage, "Http message should be failure for an empty title");
    }
}
