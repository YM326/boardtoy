package com.example.demo.dto;

import com.example.demo.entity.BoardEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Getter
@Setter
public class BoardDto {
    private int number;
    private String title;
    private String content;
    private String id;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime date;
    private String path;
    private int hit;

    public BoardEntity toEntity(){
        BoardEntity boardEntity = BoardEntity.builder()
                .number(number)
                .title(title)
                .content(content)
                .id(id)
                .date(date)
                .path(path)
                .hit(hit)
                .build();

        return boardEntity;
    }

    @Builder
    public BoardDto(int number, String title, String content, String id, LocalDateTime date, String path, int hit){
        this.number = number;
        this.title = title;
        this.content = content;
        this.id = id;
        this.date = date;
        this.path = path;
        this.hit = hit;
    }
}
