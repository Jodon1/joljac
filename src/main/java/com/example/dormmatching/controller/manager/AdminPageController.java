package com.example.dormmatching.controller.manager;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminPageController {

    // GET /admin/login → templates/admin/login.html 렌더링
    @GetMapping("/admin/login")
    public String showLoginForm() {
        return "admin/login";
    }

    // GET /admin/dashboard → templates/admin/dashboard.html 렌더링
    @GetMapping("/admin/dashboard")
    public String showDashboard() {
        return "admin/dashboard";
    }

    // 기타 /admin/** 페이지를 추가로 만든다면 이곳에 @GetMapping을 추가합니다.
}
