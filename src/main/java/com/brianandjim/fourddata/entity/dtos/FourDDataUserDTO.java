package com.brianandjim.fourddata.entity.dtos;

import com.brianandjim.fourddata.entity.models.FourDDataUserPrincipal;
import com.brianandjim.fourddata.entity.models.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FourDDataUserDTO {

    private String username;
    private boolean isAccountNonExpired;
    private boolean isAccountNonLocked;
    private boolean isCredentialsNonExpired;
    private boolean isEnabled;
    private Set<GrantedAuthority> authorities;
    private Set<Role> roles;

    public FourDDataUserDTO(FourDDataUserPrincipal myUserPrincipal){
        this.username = myUserPrincipal.getUsername();
        this.isAccountNonExpired = myUserPrincipal.isAccountNonExpired();
        this.isAccountNonLocked = myUserPrincipal.isAccountNonLocked();
        this.isCredentialsNonExpired = myUserPrincipal.isCredentialsNonExpired();
        this.isEnabled = myUserPrincipal.isEnabled();
        this.authorities = new HashSet<>(myUserPrincipal.getAuthorities());
        this.roles = myUserPrincipal.getRoles();
    }
}
