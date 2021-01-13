package com.example.demo.controller;

import com.example.demo.entity.Login2Entity;
import com.example.demo.repository.Login2Repository;
import com.example.demo.util.ARIA256Util;
import com.example.demo.util.SHA256Util;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.List;

@Controller
@EnableAutoConfiguration
public class LoginController {

    @Autowired
    Login2Repository loginRepository;

    @GetMapping("/login")
    public String loginpage(){
        return "login";
    }

    @PostMapping("/login")
    public String login(String id, String pw, HttpSession session){
        Login2Entity loginEntity = checkuser(id, pw);
        if(loginEntity == null) {
            System.out.println("<script language='javascript'>alert('Fail Login');history.back();</script>");
            return "redirect:/login";
        } else {
            session.setAttribute("id", id);
            return "redirect:/board";
        }
    }

    @GetMapping("/login1")
    public String login1page(HttpSession session){
        String id = (String)session.getAttribute("id");
        if(id == null)
            return "login1";
        else
            return "redirect:/board";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session){
        session.removeAttribute("id");
        session.removeAttribute("sseKey");

        return "login1";
    }

    @PostMapping("/login1")
    public String login1page(String id, String pw, HttpSession session){
        Login2Entity loginEntity = checkuser(id, pw);
        if(loginEntity == null) {
            System.out.println("<script language='javascript'>alert('Login Fail');history.back();</script>");
            return "redirect:/login1";
        }
        else {
            session.setAttribute("id", id);
            session.setAttribute("sseKey", loginEntity.getSsekey());
            return "redirect:/board";
        }
    }

    @GetMapping("/signup")
    public String signup(){
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(String id, String pw) throws IOException {
        if(id == "" || pw == "")
            return "redirect:/login1";

        try {
            Login2Entity loginEntityCk = checkuserId(id);
            if (loginEntityCk != null) {
                System.out.println("<script language='javascript'>alert('Id exists');history.back();</script>");
                return "redirect:/signup";
            }

            String salt = SHA256Util.generateSalt();
            String encryptPw = SHA256Util.getEncrypt(pw, salt);
            String encSalt = ARIA256Util.encrypted(salt, ARIA256Util.LOGIN_KEY);

            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(256, new SecureRandom());
            SecretKey tempKey = keyGenerator.generateKey();
            String sseKey = tempKey.toString();
            String encrypted = Base64.encodeBase64String(tempKey.getEncoded());

            String role = "ROLE_MEMBER";
            if(id.equals("sym0417")){
                role = "ROLE_ADMIN";
            }

            Login2Entity loginEntity = Login2Entity.builder().id(id).pw(encryptPw).salt(encSalt).ssekey(encrypted).role(role).build();
            loginRepository.save(loginEntity);
        } catch(Exception e){

        }

        return "redirect:/login1";
    }

    @GetMapping("/member")
    public @ResponseBody List<Login2Entity> createLogin(){
        return loginRepository.findAll();
    }

    @GetMapping("/test")
    public @ResponseBody String hello(){
        String test = "나는 영민";

        String enc = ARIA256Util.encrypted(test, ARIA256Util.LOGIN_KEY);
        String dec = ARIA256Util.decrypted(enc, ARIA256Util.LOGIN_KEY);

        return "원본 : " + test + "ENC = " + enc + ", DNC = " + dec;
//        return "Hello";
    }

    public Login2Entity checkuser(String id, String pw){
        Login2Entity loginEntity = checkuserId(id);
        if(loginEntity == null)
            return loginEntity;

        String decSalt = ARIA256Util.decrypted(loginEntity.getSalt(), ARIA256Util.LOGIN_KEY);

        String encryptPw = SHA256Util.getEncrypt(pw, decSalt);

        return loginRepository.findByIdAndPw(id, encryptPw);
    }

    public Login2Entity checkuserId(String id){
        return loginRepository.findById(id);
    }
}
