package com.example.demo.entity;

import javax.persistence.*;

import com.sun.istack.NotNull;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Entity
@Table(name = "login2")
@Data
@Getter
@Setter
public class Login2Entity {

    @Id
    @NotNull
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @NotNull
    private String pw;

    @NotNull
    private String salt;

    @NotNull
    private String ssekey;

    @NotNull
    private String role;

    @Builder
    public Login2Entity(String id, String pw, String salt, String ssekey, String role){
        this.id = id;
        this.pw = pw;
        this.salt = salt;
        this.ssekey = ssekey;
        this.role = role;
    }

    public List<GrantedAuthority> getAuth(){
        List<GrantedAuthority> roles = new ArrayList<>();
        roles.add(new SimpleGrantedAuthority(this.role));

        return roles;
    }
}
