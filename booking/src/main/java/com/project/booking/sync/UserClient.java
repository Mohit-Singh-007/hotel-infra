package com.project.booking.sync;

import com.project.booking.dto.UserRes;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "USER",path = "/users")
public interface UserClient {

    @GetMapping("/{id}")
    UserRes getUserById(@PathVariable Long id);
}
