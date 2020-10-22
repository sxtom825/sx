package com.songxiang.sx1.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/9")
public class con1 {


    @RequestMapping("/123")
    public String test1(){
        return "1";
    }

}
