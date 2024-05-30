package com.dmaddi.questionservice.controller;


import com.dmaddi.questionservice.model.Question;
import com.dmaddi.questionservice.model.QuestionWrapper;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class QuestionControllerIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    private JSONObject questionDetails;

    private List<Question> allQuestionsFromDb;

    @BeforeEach
    void setupQuestion() throws JSONException {
        questionDetails = new JSONObject();
        questionDetails.put("difficultyLevel", "Easy");
        questionDetails.put("category", "Test");
        questionDetails.put("questionTitle", "What is your age?");
        questionDetails.put("option1", "20");
        questionDetails.put("option2", "23");
        questionDetails.put("option3", "25");
        questionDetails.put("option4", "26");
        questionDetails.put("rightAnswer", "25");
    }

    @Test
    @DisplayName("Create a new question")
    @Order(4)
//    @Disabled
    void testAddQuestion_WhenAllDetailsAreProvided_returnSuccess() {
        // Arrange
        // @BeforeEach

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));


        HttpEntity<String> request = new HttpEntity<>(questionDetails.toString(), headers);

        // Act
        ResponseEntity<String> createdQuestionDetails =
                testRestTemplate.postForEntity("/question/add", request, String.class);

        String responseMessage = createdQuestionDetails.getBody();

        // Assert
        Assertions.assertEquals("success", responseMessage, "Should return a success message");
        Assertions.assertEquals(HttpStatus.CREATED, createdQuestionDetails.getStatusCode(), "Should return 201 Created");
    }

    @Test
    @Order(3)
//    @Disabled
    @DisplayName("Returns a failure when creating a new question")
    void testAddQuestion_whenQuestionTitleIsEmpty_returnsBadRequest() throws JSONException {
        // Arrange
        // @BeforeEach
        questionDetails.put("questionTitle", "");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));


        HttpEntity<String> request = new HttpEntity<>(questionDetails.toString(), headers);

        // Act
        ResponseEntity<String> createdQuestionDetails =
                testRestTemplate.postForEntity("/question/add", request, String.class);

        String responseMessage = createdQuestionDetails.getBody();

        // Assert
        Assertions.assertEquals("failure", responseMessage, "Should return a failure message");
        Assertions.assertEquals(HttpStatus.NOT_ACCEPTABLE, createdQuestionDetails.getStatusCode(), "Should return 406 Not acceptable");
    }

    @Test
    @Order(5)
//    @Disabled
    @DisplayName("Get score for responses")
    void testGetScore_whenResponsesAreSent_returnsAnInteger() throws JSONException {
        // Arrange
        List<JSONObject> responses = new ArrayList<>();

        // Object 1
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("id", 7);
        jsonObject1.put("response", "32 and 64");
        responses.add(jsonObject1);

        // Object 2
        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("id", 13);
        jsonObject2.put("response", "Both A and C");
        responses.add(jsonObject2);

        // Object 3
        JSONObject jsonObject3 = new JSONObject();
        jsonObject3.put("id", 14);
        jsonObject3.put("response", "JDB");
        responses.add(jsonObject3);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));


        HttpEntity<String> request = new HttpEntity<>(responses.toString(), headers);

        // Act
        ResponseEntity<Integer> generatedScoreEntity =
                testRestTemplate.postForEntity("/question/getScore", request, Integer.class);

        Integer responseScore = generatedScoreEntity.getBody();

        // Assert
        Assertions.assertEquals(3, responseScore, "All answers are correct so score should be 3");
    }

    @Test
    @Order(2)
//    @Disabled
    @DisplayName("Get score for responses")
    void testGetScore_whenIdsAreEmpty_returnsZeroAndBadRequest() throws JSONException {
        // Arrange
        List<JSONObject> responses = new ArrayList<>();

        // Object 1
        JSONObject jsonObject1 = new JSONObject();
        jsonObject1.put("id", 0);
        jsonObject1.put("response", "32 and 64");
        responses.add(jsonObject1);

        // Object 2
        JSONObject jsonObject2 = new JSONObject();
        jsonObject2.put("id", 13);
        jsonObject2.put("response", "Both A and C");
        responses.add(jsonObject2);

        // Object 3
        JSONObject jsonObject3 = new JSONObject();
        jsonObject3.put("id", 14);
        jsonObject3.put("response", "JDB");
        responses.add(jsonObject3);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));


        HttpEntity<String> request = new HttpEntity<>(responses.toString(), headers);

        // Act
        ResponseEntity<Integer> generatedScoreEntity =
                testRestTemplate.postForEntity("/question/getScore", request, Integer.class);

        Integer responseScore = generatedScoreEntity.getBody();

        // Assert
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, generatedScoreEntity.getStatusCode(), "Should return a Bad request");
        Assertions.assertEquals(0, responseScore, "Empty or Zero Ids should return 0");
    }

    @Test
    @Order(1)
    @DisplayName("Returns all questions from database")
    void testGetAllQuestions_whenGetRequestSent_returnsHttpStatusOk(){
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");

        HttpEntity requestEntity = new HttpEntity(null,headers);

        // Act
        ResponseEntity<List<Question>> responseEntity = testRestTemplate.exchange(
                "/question/allQuestions",
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<List<Question>>() {});

        allQuestionsFromDb = responseEntity.getBody();

        // Assert
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "Should return HttpStatus.OK code 200");
    }


    @Test
    @Order(6)
    @DisplayName("List of questions based on category are returned")
    void testGetQuestionsByCategory_whenCategoryProvided_returnsListOfQuestionsByCategory(){
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");

        HttpEntity requestEntity = new HttpEntity(headers);

        // Act
        ResponseEntity<List<Question>> responseEntity = testRestTemplate.exchange(
                "/question/category/Java",
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<List<Question>>() {});


        List<Question> receivedQuestionsByCategory = responseEntity.getBody();


        boolean containsQuestions = false;
        if (allQuestionsFromDb != null && receivedQuestionsByCategory != null) {
            containsQuestions = allQuestionsFromDb.containsAll(receivedQuestionsByCategory);
        }

        // Assert
        Assertions.assertEquals(true, containsQuestions, "Should return true if questions are matched.");
    }

    @Test
    @DisplayName("Gets a list of question ids by a category")
    @Order(7)
    void testGetQuestionsForQuiz_whenCategoryAndLimitProvided_returnsListOfQuestionIds(){
        // Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");

        HttpEntity requestEntity = new HttpEntity(headers);

        // Act
        ResponseEntity<List<Integer>> responseEntity = testRestTemplate.exchange(
                "/question/generate?categoryName=Java&numQuestions=3",
                HttpMethod.GET,
                requestEntity,
                new ParameterizedTypeReference<List<Integer>>() {});

        List<Integer> questionIds = responseEntity.getBody();
        System.out.println(questionIds);

        // Assert
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "Should return Ok code");

    }

    @Test
    @DisplayName("Gets a list of question wrappers")
    @Order(8)
    void testGetQuestionsFromId_whenQuestionIdsProvided_returnsQuestionWrappers(){
        //  Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");

        List<Integer> questionIntegerIds = new ArrayList<>(Arrays.asList(7, 14, 8));
        HttpEntity requestEntity = new HttpEntity(questionIntegerIds, headers);

        // Act
        ResponseEntity<List<QuestionWrapper>> responseEntity = testRestTemplate.exchange(
                "/question/getQuestions",
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<List<QuestionWrapper>>(){});

        List<QuestionWrapper> questionsByIds = responseEntity.getBody();

        // Assert
        Assertions.assertEquals(HttpStatus.OK, responseEntity.getStatusCode(), "Should return Ok 200 code");
    }

    @Test
    @DisplayName("Gets a Forbidden response when empty list of Q ids provided")
    @Order(9)
    void testGetQuestionsFromId_whenQuestionIdsIsEmpty_returnsHttpForbidden(){
        //  Arrange
        HttpHeaders headers = new HttpHeaders();
        headers.set("Accept", "application/json");

        List<Integer> questionIntegerIds = new ArrayList<>();
        HttpEntity requestEntity = new HttpEntity(questionIntegerIds, headers);

        // Act
        ResponseEntity<List<QuestionWrapper>> responseEntity = testRestTemplate.exchange(
                "/question/getQuestions",
                HttpMethod.POST,
                requestEntity,
                new ParameterizedTypeReference<List<QuestionWrapper>>(){});



        // Assert
        Assertions.assertEquals(HttpStatus.FORBIDDEN, responseEntity.getStatusCode(), "Should return Ok 200 code");
        Assertions.assertNull(responseEntity.getBody(), "Should return null");
    }
}
