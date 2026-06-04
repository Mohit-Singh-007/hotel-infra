package com.project.user.services;

import com.project.user.dto.UserReq;
import com.project.user.dto.UserRes;
import com.project.user.model.Role;
import com.project.user.model.User;
import com.project.user.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserImpl {

    private final UserRepo userRepo;

    @Override
    public UserRes registerUser(UserReq req) {
        User user = User.builder()
                .name(req.name())
                .email(req.email())
                .password(req.password()) // can encode later
                .role(Role.USER)
                .build();

        User saved = userRepo.save(user);
        return mapToUserRes(saved);
    }

    @Override
    public UserRes getUserById(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(()-> new RuntimeException("User not found..."));

        return mapToUserRes(user);
    }

    @Override
    public UserRes getUserByEmail(String email) {
       User user = userRepo.findByEmail(email)
               .orElseThrow(() -> new RuntimeException("User not found...."));

       return mapToUserRes(user);
    }

    private UserRes mapToUserRes(User u){
        return new UserRes(
                u.getId(),
                u.getName(),
                u.getEmail(),
               u.getRole().name()
        );
    }
}
