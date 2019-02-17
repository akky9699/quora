package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;


@Service
public class QuestionBusinessService {

    @Autowired
    QuestionDao questionDao;

    @Autowired
    UserDao userDao;

    // Function to create a question
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(QuestionEntity questionEntity, final String authorizationToken) throws AuthorizationFailedException {
        UserAuthTokenEntity userAuth =  questionDao.getUserAuthToken(authorizationToken);


        if(userAuth == null)
        {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
        final ZonedDateTime signOutUserTime = userAuth.getLogoutAt();

        if(signOutUserTime!=null && userAuth != null)
        {
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get user details");
        }

        questionEntity.setUser_id(userAuth.getUser());
        return questionDao.createQuestion(questionEntity);

    }

    // Function to edit question by UUID
    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity editQuestionByUUID(final String questionUUID, final String questionContent , final String authorizationToken) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthTokenEntity userAuthEdit =  questionDao.getUserAuthToken(authorizationToken);

        if(userAuthEdit == null)
        {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
        final ZonedDateTime signOutUserTime = userAuthEdit.getLogoutAt();

        if(signOutUserTime!=null && userAuthEdit != null)
        {
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get user details");
        }

        QuestionEntity editQuestionEntity = questionDao.getQuestionByUUID(questionUUID);

        if(editQuestionEntity == null){
            throw new InvalidQuestionException("QUES-001","Entered question uuid does not exist");
        }

        if(userAuthEdit.getUser().getId() != editQuestionEntity.getUser_id().getId()){
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
        }

        editQuestionEntity.setContent(questionContent);

        return questionDao.editQuestion(editQuestionEntity);
    }

    // Function to get all questions from database
    public List<QuestionEntity> getAllQuestions(final String authorizationToken) throws AuthorizationFailedException{
        final UserAuthTokenEntity userAuthAll =  questionDao.getUserAuthToken(authorizationToken);

        if(userAuthAll == null)
        {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
        final ZonedDateTime signOutUserTime = userAuthAll.getLogoutAt();

        if(signOutUserTime!=null && userAuthAll != null)
        {
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get all questions");
        }

        return questionDao.getAllQuestions();

    }

    // Function to delete a question by using the UUID of User
    @Transactional(propagation = Propagation.REQUIRED)
    public String deleteQuestionByUuId(final String questionUUID, final String authorizationToken) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthTokenEntity userAuthDel =  questionDao.getUserAuthToken(authorizationToken);

        QuestionEntity deleteQuestionEntity = questionDao.getQuestionByUUID(questionUUID);

        if(deleteQuestionEntity == null){
            throw new InvalidQuestionException("QUES-001","Entered question uuid does not exist");
        }

        if(userAuthDel == null)
        {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
        final ZonedDateTime signOutUserTime = userAuthDel.getLogoutAt();

        if(signOutUserTime!=null && userAuthDel != null)
        {
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get user details");
        }

        if((userAuthDel.getUser().getId() == deleteQuestionEntity.getUser_id().getId()) || userAuthDel.getUser().getRole().equals("admin")) {
            return questionDao.deleteQuestion(questionUUID);
        }else{
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
        }

    }

    // Function to get a question by using the UUID of user
    public QuestionEntity getQuestionByUUID(final String questionUUID) throws InvalidQuestionException{
        QuestionEntity questionEntity = questionDao.getQuestionByUUID(questionUUID);
        if(questionEntity == null){
            throw new InvalidQuestionException("QUES-001","The question entered is invalid");
        }
        return questionEntity;
    }

    // Function to get all questions by UUID
    public List<QuestionEntity> getAllQuestionsByUUUID(final String userUUID, final String authorizationToken)
            throws UserNotFoundException,AuthorizationFailedException {
        final UserAuthTokenEntity userAuthQues =  questionDao.getUserAuthToken(authorizationToken);
        final UserEntity userEntity = userDao.getUserByUuid(userUUID);

        if(userAuthQues == null)
        {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
        final ZonedDateTime signOutUserTime = userAuthQues.getLogoutAt();

        if(signOutUserTime!=null && userAuthQues != null)
        {
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get all questions posted by a specific user");
        }

        if(userEntity == null){
            throw new UserNotFoundException("USR-001", "User with entered uuid whose question details are to be seen does not exist");
        }

        return questionDao.getAllQuestionsByUUID(userEntity.getId());

    }
}