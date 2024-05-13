package com.example.applicationcongess.services;
import com.example.applicationcongess.models.Personnel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

    public class UserDetailsImpl implements UserDetails {
        private static final long serialVersionUID = 1L;
        Long cin;
        String  refreshtoken;
        String jwt;
        String username;
        String e_mail;
        @JsonIgnore
        String password;
        Collection<? extends GrantedAuthority> authorities;
        public UserDetailsImpl(Long cin, String username, String e_mail, String password,
                              Collection<? extends GrantedAuthority> authorities) {
            this.cin = cin;
            this.username = username;
            this.e_mail = e_mail;
            this.password = password;

            this.authorities = authorities;
        }

        public static UserDetailsImpl build(Personnel personnel ) {
            List<GrantedAuthority> authorities = personnel.getRoles().stream()
                    .map(role -> new SimpleGrantedAuthority(role.getName().name()))
                    .collect(Collectors.toList());

            return new UserDetailsImpl(
                    personnel.getCin(),
                    personnel.getUsername(),
                    personnel.getEmail(),
                    personnel.getPassword(),
                    authorities) ;
        }
        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return authorities;
        }
        public Long getCin(){
            return cin;
        }
        public String getE_mail() {
            return  e_mail;
        }
        @Override
        public String getPassword() {
            return password;
        }
        @Override
        public String getUsername() {
            return username ;
        }


        public String getJwt(){
            return jwt;
        }
        @Override
        public boolean isAccountNonExpired() {
            return true;
        }
        @Override
        public boolean isAccountNonLocked() {
            return true;
        }
        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }
        @Override
        public boolean isEnabled() {
            return true;
        }
        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            UserDetailsImpl user = (UserDetailsImpl) o;
            return Objects.equals(cin, user.cin);
        }
    }

