package org.example.eksamenbandbackend.security;

import org.example.eksamenbandbackend.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class SecurityUser implements UserDetails {

    private final User user;

    public SecurityUser(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Normalize the stored role so both "ADMIN" and "ROLE_ADMIN" work with
        // Spring Security role checks such as hasRole("ADMIN").
        return List.of(new SimpleGrantedAuthority(normalizeRole(user.getRole())));
    }

    private String normalizeRole(String role) {
        String normalizedRole = role.trim();
        return normalizedRole.startsWith("ROLE_") ? normalizedRole : "ROLE_" + normalizedRole;
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
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // Locked users stay authenticated in the database but are rejected by Spring
        // Security.
        return !user.isLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        // Inactive users cannot authenticate even if their credentials are valid.
        return user.isActive();
    }
}
