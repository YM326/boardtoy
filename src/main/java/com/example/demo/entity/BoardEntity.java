package com.example.demo.entity;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Slf4j
@NoArgsConstructor
@Entity
@Table(name = "board")
@Data
@Getter
@Setter
public class BoardEntity {
    @Id
    private int number;

    private String title;

    private String content;

    private String id;

    private LocalDateTime date;

    private String path;

    private int hit;

    @Builder
    public BoardEntity(int number, String title, String content, String id, LocalDateTime date, String path, int hit){
        this.number = number;
        this.title = title;
        this.content = content;
        this.id = id;
        this.date = date;
        this.path = path;
        this.hit = hit;
    }
}
