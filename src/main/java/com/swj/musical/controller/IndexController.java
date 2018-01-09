package com.swj.musical.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {

    @RequestMapping(path = {"/"})
    public String Home() {
        return "index";
    }

    @RequestMapping(path = {"/home"})
    @ResponseBody
    public String Index() {
        return "hello, world!";
    }

}
