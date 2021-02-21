package com.example.demo.controller;

import com.example.demo.service.AppStreamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@EnableAutoConfiguration
public class AWSController {

    @Autowired
    AppStreamService appStreamService;

    @GetMapping("/aws/imgcreate")
    public String imageCreate(){
        return "";
    }

    @GetMapping("/aws/imgstart")
    public String imageStart(){
        appStreamService.startImageBuilder("test");

        return "";
    }

    @GetMapping("/aws/imgstop")
    public String imageStop(){
        appStreamService.stopImageBuilder("test");

        return "";
    }

    @GetMapping("/aws/createfleet")
    public String createFleet(){
        appStreamService.createFleet("midasArchidesign", "test");

        return "";
    }

    @GetMapping("/aws/deletefleet")
    public String deleteFleet(){
        appStreamService.deleteFleet("test");

        return "";
    }

    @GetMapping("/aws/createstack")
    public String createStack(){
        appStreamService.createStack("test", "test");

        return "";
    }

    @GetMapping("/aws/deletestack")
    public String deleteStack(){
        appStreamService.deleteStack("test", "test");

        return "";
    }

    @GetMapping("/aws/createuser")
    public String createUser(){
        appStreamService.createUser("sonym0326@naver.com", "youngmin2", "son", "test");

        return "";
    }

    @GetMapping("/aws/deleteuser")
    public String deleteUser(){
        appStreamService.deleteUser("sonym0326@naver.com");

        return "";
    }

}
