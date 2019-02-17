package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDeleteResponse;
import com.upgrad.quora.service.business.AdminBusinessService;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class AdminController {

    //adminBusinessService contains the buissness logic for deleting the  user and also the logged in user's role is admin
    @Autowired
    private AdminBusinessService adminBusinessService;

    //deleteUser function required arguments (authorization and user id)
    @RequestMapping(method = RequestMethod.DELETE , path = "/admin/user/{userId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity <UserDeleteResponse> deleteUser(@PathVariable("userId") final String userid,
                                                          @RequestHeader("authorization") final String authorization) throws AuthenticationFailedException,AuthorizationFailedException, UserNotFoundException {

        String [] bearerToken = authorization.split("Bearer ");

        String uuid = adminBusinessService.deleteUser(userid ,authorization);
        UserDeleteResponse userDeleteResponse=new UserDeleteResponse().id(uuid).status("USER SUCCESSFULLY DELETED");

        return new ResponseEntity<UserDeleteResponse>(userDeleteResponse,HttpStatus.OK);
    }
}
