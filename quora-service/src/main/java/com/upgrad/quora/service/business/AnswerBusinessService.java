package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class AnswerBusinessService {

    @Autowired
    private AnswerDao answerDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(AnswerEntity answerEntity){
        AnswerEntity answer = answerDao.createAnswer(answerEntity);
        return answer;
    }

    public UserAuthTokenEntity getUserAuthToken(final String accesstoken) throws AuthorizationFailedException{
        UserAuthTokenEntity userAuthToken = answerDao.getUserAuthToken(accesstoken);
        if(userAuthToken == null)
        {
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }
        final ZonedDateTime signOutUserTime = userAuthToken.getLogoutAt();

        if(signOutUserTime!=null && userAuthToken!=null)
        {
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get user details");
        }

        return userAuthToken;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity editAnswerContent(String changedAnswerContent, AnswerEntity answerEntity,UserAuthTokenEntity userAuthTokenEntity) throws AuthorizationFailedException {
        AnswerEntity updatedAnswer;
        if (answerEntity.getUser().getId() == userAuthTokenEntity.getUser().getId()) {
            answerEntity.setAnswer(changedAnswerContent);
            updatedAnswer = answerDao.updateAnswer(answerEntity);
        } else {
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the answer");
        }

        return updatedAnswer;
    }

    public AnswerEntity getAnswerByUUID(final String answerId) throws AnswerNotFoundException {
        AnswerEntity answerEntity = answerDao.getAnswerByUUID(answerId);
        if(answerEntity == null){
            throw new AnswerNotFoundException("ANS-001","Entered answer uuid does not exist");
        }
        return answerEntity;
    }

    public List<AnswerEntity> getAllAnswersToQuestion(Integer questionId) throws InvalidQuestionException{
        List<AnswerEntity> allAnswersToQuestion = answerDao.getAllAnswersToQuestion(questionId);
        if(allAnswersToQuestion == null){
            throw new InvalidQuestionException("QUES-001", "The question with entered uuid whose details are to be seen does not exist");
        }

        return allAnswersToQuestion;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteAnswer(AnswerEntity answerEntity,UserAuthTokenEntity userAuthTokenEntity) throws AuthorizationFailedException,AnswerNotFoundException{
        if ((answerEntity.getUser().getId() == userAuthTokenEntity.getUser().getId()) || userAuthTokenEntity.getUser().getRole().equals("admin") ) {
            answerDao.deleteAnswer(answerEntity);
        }else {
            throw new AuthorizationFailedException("ATHR-003","Only the answer owner or admin can delete the answer");
        }
    }
}
