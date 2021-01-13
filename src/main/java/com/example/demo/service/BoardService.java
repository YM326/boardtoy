package com.example.demo.service;

import com.example.demo.dto.BoardDto;
import com.example.demo.entity.BoardEntity;
import com.example.demo.repository.BoardRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@AllArgsConstructor
@Service
public class BoardService {
    private BoardRepository boardRepository;

    @Transactional
    public BoardDto getPost(int id) {
        Optional<BoardEntity> boardEntityWrapper = boardRepository.findById(id);
        BoardEntity boardEntity = boardEntityWrapper.get();

        BoardDto boardDTO = BoardDto.builder()
                .number(boardEntity.getNumber())
                .title(boardEntity.getTitle())
                .content(boardEntity.getContent())
                .id(boardEntity.getId())
                .date(boardEntity.getDate())
                .path(boardEntity.getPath())
                .hit(boardEntity.getHit())
                .build();

        return boardDTO;
    }

    @Transactional
    public int savePost(BoardDto boardDto) {
        return boardRepository.save(boardDto.toEntity()).getNumber();
    }

    @Transactional
    public void deletePost(int id) {
        boardRepository.deleteById(id);
    }
}
