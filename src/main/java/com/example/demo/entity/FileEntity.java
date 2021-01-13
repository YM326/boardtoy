package com.example.demo.entity;


import com.sun.istack.NotNull;
import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@Entity
@Table(name = "file")
@Data
@Getter
@Setter
public class FileEntity {
    @Id
    @NotNull
    private int fileno;
    private int boardno;
    private String filename;
    private String fileoriname;
    private String fileurl;

    @Builder
    public FileEntity(int fileno, int boardno, String filename, String fileoriname, String fileurl){
        this.fileno = fileno;
        this.boardno = boardno;
        this.filename = filename;
        this.fileoriname = fileoriname;
        this.fileurl = fileurl;
    }
}
