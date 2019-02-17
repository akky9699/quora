package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerBusinessService;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class AnswerController {

    @Autowired
    private AnswerBusinessService answerBusinessService;

    @Autowired
    private QuestionBusinessService questionBusinessService;
    //createAnswer will create answer and  then save answer information in the database and return the "uuid" of the answer
    @RequestMapping(method = RequestMethod.POST,path = "/question/{questionId}/answer/create",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(@PathVariable("questionId") final String questionId, @RequestHeader("authorization") final String authorization, AnswerRequest answerRequest) throws AuthorizationFailedException,InvalidQuestionException {


        UserAuthTokenEntity userAuthToken = answerBusinessService.getUserAuthToken(authorization);
        QuestionEntity questionByUUID = questionBusinessService.getQuestionByUUID(questionId);

        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setUuid(UUID.randomUUID().toString());
        answerEntity.setAnswer(answerRequest.getAnswer());
        answerEntity.setDate(ZonedDateTime.now());
        answerEntity.setUser(userAuthToken.getUser());
        answerEntity.setQuestion(questionByUUID);
        final AnswerEntity createdAnswer = answerBusinessService.createAnswer(answerEntity);

        final AnswerResponse answerResponse = new AnswerResponse().id(createdAnswer.getUuid()).status("ANSWER CREATED");
        return new ResponseEntity<AnswerResponse>(answerResponse,HttpStatus.CREATED);
    }

    //editAnswerContent edit the answer in the database and return "uuid" of the edited answer and message "ANSWER EDITED" Only the answer owner can edit the answer.
    @RequestMapping(method = RequestMethod.PUT,path = "/answer/edit/{answerId}",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswerContent(@PathVariable("answerId") final String answerId, @RequestHeader("authorization") final String authorization, AnswerEditRequest answerEditRequest) throws AuthorizationFailedException,AnswerNotFoundException {

        UserAuthTokenEntity userAuthToken = answerBusinessService.getUserAuthToken(authorization);

        final AnswerEntity answerByUUID = answerBusinessService.getAnswerByUUID(answerId);
        final AnswerEntity editedAnswer = answerBusinessService.editAnswerContent(answerEditRequest.getContent(),answerByUUID,userAuthToken);

        final AnswerEditResponse answerEditResponse = new AnswerEditResponse().id(editedAnswer.getUuid()).status("ANSWER EDITED");
        return new ResponseEntity<AnswerEditResponse>(answerEditResponse,HttpStatus.OK);
    }

    //getAllAnswersToQuestion return "uuid" of the answer, "content" of the question and "content" of all the answers posted for that particular question from the database in the JSON response
    @RequestMapping(method = RequestMethod.GET,path = "answer/all/{questionId}",produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
    public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswersToQuestion(@PathVariable("questionId") String questionId,@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException,InvalidQuestionException {

        UserAuthTokenEntity userAuthToken = answerBusinessService.getUserAuthToken(authorization);

        final QuestionEntity questionByUUID = questionBusinessService.getQuestionByUUID(questionId);
        final List<AnswerEntity> allAnswersToQuestion = answerBusinessService.getAllAnswersToQuestion(questionByUUID.getId());

        List<AnswerDetailsResponse> answers = new ArrayList<>();
        AnswerDetailsResponse answerDetailsResponse = null;
        for (AnswerEntity answer : allAnswersToQuestion) {
            answerDetailsResponse = new AnswerDetailsResponse().id(answer.getUuid()).answerContent(answer.getAnswer()).questionContent(answer.getQuestion().getContent());
            answers.add(answerDetailsResponse);
        }

        return new ResponseEntity<List<AnswerDetailsResponse>>(answers,HttpStatus.OK);
    }

    //deleteAnswer  delete the answer from the database and return "uuid" of the deleted answer and message "ANSWER DELETED" in the JSON response
    @RequestMapping(method = RequestMethod.DELETE,path = "/answer/delete/{answerId}",produces = MediaType.APPLICATION_PROBLEM_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(@PathVariable("answerId") String answerId,@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException,AnswerNotFoundException{

        UserAuthTokenEntity userAuthToken = answerBusinessService.getUserAuthToken(authorization);

        final AnswerEntity answerByUUID = answerBusinessService.getAnswerByUUID(answerId);
        answerBusinessService.deleteAnswer(answerByUUID,userAuthToken);

        final AnswerDeleteResponse answerDeleteResponse = new AnswerDeleteResponse().id(answerByUUID.getUuid()).status("ANSWER DELETED");

        return new ResponseEntity<AnswerDeleteResponse>(answerDeleteResponse,HttpStatus.OK);
    }

}
