package com.example.demo.repository;

import com.example.demo.domain.FloodShelter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FloodShelterRepository extends JpaRepository<FloodShelter, String> {

    // 대피소명 또는 상세주소 키워드 포함 페이징 검색
    Page<FloodShelter> findByFcltNmContainingOrDaddrContaining(String fcltNm, String daddr, Pageable pageable);

    // ID 기반 단건 조회 (기본 findById와 동일하게 동작)
    Optional<FloodShelter> findByShltId(String shltId);

    // ID 기반 삭제 (서비스 레이어에서 @Transactional 필요)
    void deleteByShltId(String shltId);
}