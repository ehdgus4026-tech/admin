package com.example.demo.repository;

import com.example.demo.domain.AirRaidShelter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// JpaRepository의 ID 타입을 Long -> String으로 수정
public interface AirRaidShelterRepository extends JpaRepository<AirRaidShelter, String> {

    // 대피소 이름 또는 주소 키워드 페이징 검색
    Page<AirRaidShelter> findByFcltNmContainingOrDaddrContaining(String fcltNm, String daddr, Pageable pageable);

    // 기본 findById(String id)와 deleteById(String id)로 대체 가능하지만, 명시적 활용도 가능합니다.
    Optional<AirRaidShelter> findByShltId(String shltId);

    void deleteByShltId(String shltId);
}