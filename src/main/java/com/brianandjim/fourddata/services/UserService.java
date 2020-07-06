package com.brianandjim.fourddata.services;

import com.brianandjim.fourddata.entity.dao.FourDDataUserDAO;
import com.brianandjim.fourddata.entity.dao.RoleDAO;
import com.brianandjim.fourddata.entity.dtos.AuthenticationRequest;
import com.brianandjim.fourddata.entity.dtos.AuthenticationResponse;
import com.brianandjim.fourddata.entity.dtos.FourDDataUserDTO;
import com.brianandjim.fourddata.entity.dtos.UserDTO;
import com.brianandjim.fourddata.entity.models.FourDDUser;
import com.brianandjim.fourddata.entity.models.FourDDataUserPrincipal;
import com.brianandjim.fourddata.entity.models.Role;
import com.brianandjim.fourddata.utils.JwtUtil;
import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLMutation;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
@GraphQLApi
public class UserService {

    private final FourDDataUserDAO fourDDataUserDAO;
    private final AuthenticationManager authenticationManager;
    private final FourDDataUserDetailsService fourDDataUserDetailsService;
    private final JwtUtil jwtUtil;
    private final RoleDAO roleDAO;

    public UserService(FourDDataUserDAO fourDDataUserDAO, AuthenticationManager authenticationManager, FourDDataUserDetailsService fourDDataUserDetailsService, JwtUtil jwtUtil, RoleDAO roleDAO) {
        this.fourDDataUserDAO = fourDDataUserDAO;
        this.authenticationManager = authenticationManager;
        this.fourDDataUserDetailsService = fourDDataUserDetailsService;
        this.jwtUtil = jwtUtil;
        this.roleDAO = roleDAO;
    }

    @GraphQLQuery(name = "login")
    @PreAuthorize(value = "permitAll()")
    public AuthenticationResponse login(@GraphQLArgument(name = "request") AuthenticationRequest request) {
        try {
            log.info("incoming request from " + request.getUsername());
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(),
                    request.getPassword()));
        } catch (BadCredentialsException e) {
            throw new UsernameNotFoundException("Incorrect username or password");
        }
        return new AuthenticationResponse(getJwtByUsername(request.getUsername()));
    }

    @GraphQLMutation(name = "signup")
    @PreAuthorize(value = "permitAll()")
    public AuthenticationResponse signup(@GraphQLArgument(name = "userDTO") UserDTO userDTO) {
        String salt = BCrypt.gensalt();
        FourDDUser user = new FourDDUser(userDTO.getUsername(), BCrypt.hashpw(userDTO.getPassword(), salt));
        Set<Role> roles = new HashSet<>();
        roles.add(roleDAO.findByName("ROLE_User"));
        user.setRoles(roles);
        FourDDUser newUser = fourDDataUserDAO.saveAndFlush(user);
        return new AuthenticationResponse(getJwtByUsername(newUser.getUsername()));
    }

    @GraphQLQuery(name = "getUserDetailsByUsername")
    @PreAuthorize(value = "hasAnyAuthority('WRITE_PRIVILEGE')")
    public FourDDUser getUserDetailsByUsername(String username){
        return fourDDataUserDAO.findFirstByUsername(username);
    }

    private String getJwtByUsername(String username){
        final FourDDataUserPrincipal userDetails = fourDDataUserDetailsService.loadUserByUsername(username);
        FourDDataUserDTO fourDDataUserDTO = new FourDDataUserDTO(userDetails);
        return jwtUtil.generateToken(fourDDataUserDTO);
    }
}
