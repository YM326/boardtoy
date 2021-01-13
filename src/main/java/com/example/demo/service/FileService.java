package com.example.demo.service;

import com.example.demo.dto.FileDto;
import com.example.demo.entity.FileEntity;
import com.example.demo.repository.FileRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@AllArgsConstructor
@Service
public class FileService {
    private FileRepository fileRepository;

    @Transactional
    public FileDto getPost(int id) {
        Optional<FileEntity> fileEntityWrapper = fileRepository.findById(id);
        FileEntity fileEntity = fileEntityWrapper.get();

        FileDto fileDTO = FileDto.builder()
                .fileno(fileEntity.getFileno())
                .boardno(fileEntity.getBoardno())
                .filename(fileEntity.getFilename())
                .fileoriname(fileEntity.getFileoriname())
                .fileurl(fileEntity.getFileurl())
                .build();

        return fileDTO;
    }

    @Transactional
    public FileDto getBnoPost(int bno) {
        Optional<FileEntity> fileEntityWrapper = Optional.ofNullable(fileRepository.findByBoardno(bno));
        FileEntity fileEntity = fileEntityWrapper.get();

        FileDto fileDTO = FileDto.builder()
                .fileno(fileEntity.getFileno())
                .boardno(fileEntity.getBoardno())
                .filename(fileEntity.getFilename())
                .fileoriname(fileEntity.getFileoriname())
                .fileurl(fileEntity.getFileurl())
                .build();

        return fileDTO;
    }

    @Transactional
    public int savePost(FileDto fileDto) {
        return fileRepository.save(fileDto.toEntity()).getFileno();
    }

    @Transactional
    public void deletePost(int id) {
        fileRepository.deleteById(id);
    }
}
