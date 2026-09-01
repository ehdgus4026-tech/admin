package com.example.demo.repository;

import com.example.demo.domain.EarthquakeShelter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// JpaRepository의 두 번째 제네릭 타입을 Long에서 String으로 변경
public interface EarthquakeShelterRepository extends JpaRepository<EarthquakeShelter, String> {
    Page<EarthquakeShelter> findByFcltNmContainingOrDaddrContaining(String fcltNm, String daddr, Pageable pageable);
    Optional<EarthquakeShelter> findByShltId(String shltId);
    void deleteByShltId(String shltId);
}