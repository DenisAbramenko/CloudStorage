//package com.project.cloudstorage.security;
//
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import lombok.Getter;
//import lombok.NonNull;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.userdetails.User;
//
//import java.util.Collection;
//
//@Getter
//public class ApplicationUser extends User {
//    private final int id;
//
//    public ApplicationUser(Integer id, String username, String password, Collection<? extends GrantedAuthority> authorities) {
//        super(username, password, authorities);
//        this.id = id;
//    }
//}
