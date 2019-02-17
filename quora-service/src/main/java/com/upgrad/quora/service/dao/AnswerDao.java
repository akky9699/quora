package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AnswerDao {

    @PersistenceContext
    private EntityManager entityManager;

    public AnswerEntity createAnswer(AnswerEntity answerEntity){
        entityManager.persist(answerEntity);
        return answerEntity;
    }

    //UserAuthTokenEntity this will return the user_id based on the access token
    public UserAuthTokenEntity getUserAuthToken(final String accesstoken){
        try{
            return entityManager.createNamedQuery("userAuthTokenByAccessToken", UserAuthTokenEntity.class).setParameter("accessToken", accesstoken).getSingleResult();
        } catch (NoResultException nre){
            return null;
        }
    }

    //getAnswerByUUID this will return the answer details based on the answer UUID
    public AnswerEntity getAnswerByUUID(final String answerUUID){
        try{
            return entityManager.createNamedQuery("answerByUuid", AnswerEntity.class).setParameter("uuid", answerUUID).getSingleResult();
        } catch (NoResultException nre){
            return null;
        }
    }

    //updateAnswer this will update the answer details based on the answer UUID
    public AnswerEntity updateAnswer(AnswerEntity answerEntity){
        entityManager.merge(answerEntity);
        return answerEntity;
    }

    //getAllAnswersToQuestion this will return the all answer details based on the question UUID
    public List<AnswerEntity> getAllAnswersToQuestion(Integer questionId){
        try{
            return entityManager.createNamedQuery("answersByQuestion", AnswerEntity.class).setParameter("questionId", questionId).getResultList();
        } catch (NoResultException nre){
            return null;
        }
    }

    //deleteAnswer this will delete  answer based on the answer UUID
    public void deleteAnswer(AnswerEntity answerEntity){
        entityManager.remove(answerEntity);
    }
}
