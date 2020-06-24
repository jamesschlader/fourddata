package com.brianandjim.fourddata.controllers;

import com.brianandjim.fourddata.entity.dtos.AuthenticationRequest;
import com.brianandjim.fourddata.entity.dtos.AuthenticationResponse;
import com.brianandjim.fourddata.entity.dtos.UserDTO;
import com.brianandjim.fourddata.entity.models.FourDDUser;
import com.brianandjim.fourddata.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    @PreAuthorize("hasAuthority('WRITE_PRIVILEGE')")
    public FourDDUser addUser(@RequestBody UserDTO userDTO){
        return userService.signup(userDTO);
    }

    @PostMapping("/login")
    public AuthenticationResponse createAuthenticationToken(@RequestBody AuthenticationRequest request) throws Exception {
        return userService.login(request);
    }
}
