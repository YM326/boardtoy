package com.example.demo.entity;

import javax.persistence.*;

import com.sun.istack.NotNull;
import lombok.*;

@NoArgsConstructor
@Entity
@Table(name = "login")
@Data
@Getter
@Setter
public class LoginEntity {
    @Id
    @NotNull
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private String id;

    @NotNull
    private String pw;

    @Builder
    public LoginEntity(String id, String pw){
        this.id = id;
        this.pw = pw;
    }
}
