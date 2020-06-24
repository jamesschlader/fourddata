package com.brianandjim.fourddata.entity.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FourDDataUserPrincipal implements UserDetails {
    private transient FourDDUser user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        user.getRoles()
                .forEach(role -> role.getPrivileges().forEach(authority -> authorities.add(authority.getName())));
        return authorities;
    }

    public Set<Role> getRoles() {
        return new HashSet<>(user.getRoles());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return user.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() { return user.isCredentialsNonExpired(); }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }
}
