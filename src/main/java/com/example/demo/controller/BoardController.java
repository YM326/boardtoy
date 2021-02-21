package com.example.demo.controller;

import com.example.demo.dto.BoardDto;
import com.example.demo.dto.FileDto;
import com.example.demo.entity.BoardEntity;
import com.example.demo.entity.FileEntity;
import com.example.demo.repository.BoardRepository;
import com.example.demo.repository.FileRepository;
import com.example.demo.service.BoardService;
import com.example.demo.service.FileService;
import com.example.demo.service.S3Service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import java.io.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Controller
@EnableAutoConfiguration
public class BoardController {
    private static final String UPLOAD_PATH = "D:\\Upload";

    @Autowired
    private S3Service s3Service;

    @Autowired
    private BoardService boardService;

    @Autowired
    private FileService fileService;

    @Autowired
    BoardRepository boardRepository;

    @Autowired
    FileRepository fileRepository;

    @GetMapping("/board")
    public String boardlist(Model model, HttpSession session){
//        String id = getSessionId(session);
//        if(id == null)
//            return "login1";

        List<BoardEntity> boardList = boardRepository.findAll();
        model.addAttribute("boardList", boardList);

        return "board_list";
    }

    @GetMapping("/board/write")
    public String boardwrite(Model model, HttpSession session){
        String id = getSessionId(session);
        if(!id.equals(""))
            model.addAttribute("id", id);

        return "board_write";
    }

    @PostMapping("/board/write")
    public String boardwrite(String id, String title, String content, MultipartFile attachfile, HttpSession session) throws IOException{
        if(title == "" || id == "")
            return "redirect:/board";

        BoardEntity boardEntity = BoardEntity.builder().title(title).content(content).id(id).date(LocalDateTime.now()).path(attachfile.getOriginalFilename()).build();
        boardRepository.save(boardEntity);

        List <BoardEntity> boardList = boardRepository.findAll();
        int listSize = boardList.size();
        BoardEntity lastBoard = boardList.get(listSize - 1);

        String fileName = attachfile.getName();
        String fileOriName = attachfile.getOriginalFilename();
        if(!fileOriName.equals("")) {
            UUID uuid = UUID.randomUUID();
            String saveName = "test/" + uuid + "_" + attachfile.getOriginalFilename();
            String sseKey = (String)session.getAttribute("sseKey");
            String fileUrl = s3Service.upload(attachfile, saveName, sseKey);

            if (saveName != null && !saveName.equals("")) {
                FileEntity fileEntity = FileEntity.builder().boardno(lastBoard.getNumber()).filename(saveName)
                        .fileoriname(attachfile.getOriginalFilename()).fileurl(fileUrl).build();
                fileRepository.save(fileEntity);
            }
        }

        return "redirect:/board";
    }

    @GetMapping("/board/read/{number}")
    public String boardread(@PathVariable(value="number") int number, Model model){
        log.info("테스트");
        BoardEntity boardEntity = boardRepository.findById(number).orElse(new BoardEntity());
        FileEntity fileEntity = fileRepository.findByBoardno(number);

        int hit = boardEntity.getHit();
        boardEntity.setHit(hit + 1);
        boardRepository.save(boardEntity);

        model.addAttribute("board", boardEntity);
        model.addAttribute("file", fileEntity);

        return "board_read";
    }

    @GetMapping("/board/filedown/{bno}")
    public ResponseEntity<byte[]> filedown(@PathVariable int bno, HttpSession session) throws Exception{
        FileEntity fileEntity = fileRepository.findByBoardno(bno);

        String sseKey = (String)session.getAttribute("sseKey");
        ResponseEntity<byte[]> responseEntity = s3Service.download(fileEntity.getFilename(), fileEntity.getFileoriname(), sseKey);
        if(responseEntity.getStatusCode() == HttpStatus.OK){
            return responseEntity;
        }else{
            System.out.println("접근 권한이 없습니다.");
            return responseEntity;
        }
    }

    @GetMapping("/board/delete/{bno}")
    public String deletePost(@PathVariable(value="bno") int bno) throws Exception{
        FileEntity fileEntity = fileRepository.findByBoardno(bno);

        //파일 삭제
        s3Service.deleteObject(fileEntity.getFilename());

        //DB 삭제
        boardRepository.deleteById(bno);
        fileRepository.deleteById(fileEntity.getFileno());

        return "redirect:/board";
    }

    @GetMapping("/board/edit/{bno}")
    public String editPost(@PathVariable(value="bno") int bno, Model model) throws Exception{
        BoardDto boardDto = boardService.getPost(bno);
        FileDto fileDto = fileService.getBnoPost(bno);

        model.addAttribute("boardDto", boardDto);
        model.addAttribute("file", fileDto);

        return "board_edit";
    }

    @PostMapping("/board/edit/{bno}")
    public String editPost(BoardDto boardDto, FileDto fileDto) throws Exception{
        //DB 수정
        String title = boardDto.getTitle();
        String content = boardDto.getContent();

        boardRepository.save(boardDto.toEntity());

        return "redirect:/board";
    }

    @GetMapping("/admin")
    public String admin(){
        return "admin";
    }

    private String saveFile(MultipartFile file){
        // 파일 이름 변경
        UUID uuid = UUID.randomUUID();
        String saveName = uuid + "_" + file.getOriginalFilename();

        File saveFile = new File(UPLOAD_PATH, saveName);

        try {
            file.transferTo(saveFile);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return saveName;
    } // end saveFile(

    private String getSessionId(HttpSession session){
        return (String)session.getAttribute("id");
    }
}
