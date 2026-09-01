package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // 사용자가 http://localhost:8080/ 에 접속했을 때
    @GetMapping("/")
    public String index() {
        // 접속하자마자 로그인 페이지로 강제 이동(리다이렉트)시킵니다.
        return "redirect:/login";
    }

    // 로그인이 성공한 후 이동할 메인 화면 경로입니다.
    @GetMapping("/main")
    public String mainPage() {
        // 버튼이 수정된 templates/index.html 파일을 화면에 띄웁니다.
        return "index";
    }
}