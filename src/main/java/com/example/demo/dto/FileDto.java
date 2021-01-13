package com.example.demo.dto;

import com.example.demo.entity.FileEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FileDto {
    private int fileno;
    private int boardno;
    private String filename;
    private String fileoriname;
    private String fileurl;

    public FileEntity toEntity(){
        FileEntity fileEntity = FileEntity.builder()
                .fileno(fileno)
                .boardno(boardno)
                .filename(filename)
                .fileoriname(fileoriname)
                .fileurl(fileurl)
                .build();

        return fileEntity;
    }

    @Builder
    public FileDto(int fileno, int boardno, String filename, String fileoriname, String fileurl){
        this.fileno = fileno;
        this.boardno = boardno;
        this.filename = filename;
        this.fileoriname = fileoriname;
        this.fileurl = fileurl;
    }
}
