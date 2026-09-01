package com.example.demo.service;

import com.example.demo.domain.User;
import com.example.demo.dto.UserSignupDto;
import com.example.demo.repository.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본 읽기 전용 트랜잭션 적용
public class UserService {

    private final UserRepository userRepository;

    // 1. 회원가입 로직
    @Transactional // 쓰기 작업
    public void registerUser(UserSignupDto dto) {
        // 🚨 최고 관리자 아이디 예약어 차단 (super, SUPER, Super 등 대소문자 무관 차단)
        if (dto.getUsername() != null && "super".equalsIgnoreCase(dto.getUsername().trim())) {
            throw new IllegalArgumentException("사용할 수 없는 관리자 아이디입니다.");
        }

        // 아이디 중복 확인
        if (userRepository.existsByUsername(dto.getUsername().trim())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        // 새 회원 정보 저장
        User user = new User();
        user.setUsername(dto.getUsername().trim());
        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());
        user.setRealname(dto.getRealname());

        userRepository.save(user);
    }

    // 2. 로그인 검증 로직
    public User login(String username, String password) {
        // 1. DB에서 입력받은 아이디로 유저 조회
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));

        // 2. 비밀번호 일치 여부 확인
        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 3. 검증 통과 시 유저 객체 반환
        return user;
    }
}