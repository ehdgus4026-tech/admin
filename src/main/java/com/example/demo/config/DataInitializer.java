package com.example.demo.config;

import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) {
        // DB에 'super' 계정이 없으면 자동 생성
        if (!userRepository.existsByUsername("super")) {
            User superUser = new User();
            superUser.setUsername("super");
            superUser.setPassword("1234"); // 기본 비밀번호 (필요에 맞게 변경)
            superUser.setRealname("최고관리자");
            superUser.setEmail("super@admin.com");



            userRepository.save(superUser);
            System.out.println(">>> [시스템] 최고 관리자(super) 계정이 자동으로 생성되었습니다.");
        }
    }
}