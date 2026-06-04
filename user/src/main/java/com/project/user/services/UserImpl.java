package com.project.user.services;

import com.project.user.dto.UserReq;
import com.project.user.dto.UserRes;

public interface UserImpl {

   UserRes registerUser(UserReq req);
   UserRes getUserById(Long id);
   UserRes getUserByEmail(String email);
}
