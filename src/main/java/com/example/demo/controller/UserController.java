package com.example.demo.controller;

import com.example.demo.domain.User;
import com.example.demo.dto.UserSignupDto;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    @GetMapping("/write")
    public String writePage(HttpSession session) {
        Object loginUser = session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/login";
        }
        return "write";
    }

    // 로그인 처리 창구
    @PostMapping("/api/users/login")
    public String login(String username, String password, HttpSession session, Model model) {
        try {
            User user = userService.login(username, password);
            session.setAttribute("loginUser", user.getUsername());

            // 로그인 성공 시 메인 화면으로 이동
            return "redirect:/main";

        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", "아이디 또는 비밀번호가 잘못되었습니다.");
            return "login";
        }
    }

    // 회원가입 처리 창구
    @PostMapping("/api/users/signup")
    public String signup(UserSignupDto dto, Model model, RedirectAttributes redirectAttributes) {
        // 💡 [보안] SUPER 아이디(대소문자 무관) 가입 차단
        if (dto.getUsername() != null && "super".equalsIgnoreCase(dto.getUsername().trim())) {
            model.addAttribute("errorMessage", "해당 아이디(SUPER)는 최고 관리자 전용이므로 사용할 수 없습니다.");
            return "signup";
        }

        try {
            userService.registerUser(dto);
            // 💡 회원가입 성공 메시지를 로그인 페이지로 전달
            redirectAttributes.addFlashAttribute("successMessage", "회원가입이 완료되었습니다! 로그인해 주세요.");
            return "redirect:/login";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "signup";
        }
    }

    // 로그아웃 처리 창구
    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirectAttributes) {
        session.invalidate();
        redirectAttributes.addFlashAttribute("successMessage", "로그아웃되었습니다.");
        return "redirect:/login";
    }
}