package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

@Repository
public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;

    public QuestionEntity createQuestion(QuestionEntity questionEntity) {
        entityManager.persist(questionEntity);

        return questionEntity;
    }

    public QuestionEntity getQuestionByUUID(final String questionUUID) {
        try {
            return entityManager.createNamedQuery("questionByUuid", QuestionEntity.class).setParameter("uuid", questionUUID).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public QuestionEntity editQuestion(QuestionEntity questionEntity) {
        try {
            entityManager.merge(questionEntity);

            return questionEntity;
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<QuestionEntity> getAllQuestions() {
        try {
            Query query = entityManager.createQuery("select qs from QuestionEntity qs");
            return new ArrayList<QuestionEntity>(query.getResultList());
        } catch (NoResultException nre) {
            return null;
        }
    }

    public String deleteQuestion(final String questionUUID) {
        try {
            QuestionEntity questionEntity = getQuestionByUUID(questionUUID);
            entityManager.remove(questionEntity);
            return questionUUID;
        } catch (NoResultException nre) {
            return null;
        }
    }

    public UserAuthTokenEntity getUserAuthToken(final String accesstoken) {
        try {
            return entityManager.createNamedQuery("userAuthTokenByAccessToken", UserAuthTokenEntity.class).setParameter("accessToken", accesstoken).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    public List<QuestionEntity> getAllQuestionsByUUID(final Integer UserID) {
        try {
            return entityManager.createNamedQuery("getAllQuestionByUUID", QuestionEntity.class).setParameter("user_id", UserID).getResultList();
        } catch (NoResultException nre) {
            return null;
        }
    }


}