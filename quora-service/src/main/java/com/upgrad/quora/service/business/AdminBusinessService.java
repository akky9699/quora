package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthTokenEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminBusinessService {


    @Autowired
    UserDao userDao;

    //CommonBussinessService will contains business logic for performing checks 1)is user logged in 2)is user signed in 3)is user is a authorized user
    @Autowired
    private UserBusinessService userBussinessService;

    @Transactional(propagation = Propagation.REQUIRED)

    //deleteUser required arguments (userid , accessToken) these functions check if the user exists or not based on the user_id and if it exist then the user is deleted
    public String deleteUser(final String userid , final String accessToken) throws UserNotFoundException,AuthorizationFailedException  {

        UserEntity userEntity = userBussinessService.getUser(userid,accessToken);
        UserAuthTokenEntity userAuthTokenEntity = userDao.checkToken(accessToken);

        if(userAuthTokenEntity.getUser().getRole().equals("admin")){
            return userDao.deleteUser(userid);
        }
        else
        {
            throw new AuthorizationFailedException("ATHR-003","Unauthorized Access, Logged in user is not an admin");
        }
    }
}
