package com.example.demo.service;

import com.example.demo.domain.AirRaidShelter;
import com.example.demo.repository.AirRaidShelterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 읽기 전용 트랜잭션을 기본으로 설정하여 성능 최적화
public class AirRaidShelterService {

    private final AirRaidShelterRepository airRaidShelterRepository;

    public Page<AirRaidShelter> getShelters(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return airRaidShelterRepository.findAll(pageable);
        }
        return airRaidShelterRepository.findByFcltNmContainingOrDaddrContaining(keyword, keyword, pageable);
    }

    public List<AirRaidShelter> getAllShelters() {
        return airRaidShelterRepository.findAll();
    }

    @Transactional // 쓰기 작업에만 별도 선언
    public void save(AirRaidShelter shelter) {
        if (shelter.getShltId() == null || shelter.getShltId().trim().isEmpty()) {
            shelter.setShltId("USER_" + UUID.randomUUID().toString());
        }
        airRaidShelterRepository.save(shelter);
    }

    @Transactional
    public void update(String shltId, AirRaidShelter shelterDto) {
        AirRaidShelter shelter = airRaidShelterRepository.findByShltId(shltId)
                .orElseThrow(() -> new IllegalArgumentException("해당 대피소가 존재하지 않습니다. shltId=" + shltId));

        shelter.setCtpvNm(shelterDto.getCtpvNm());
        shelter.setSggNm(shelterDto.getSggNm());
        shelter.setFcltNm(shelterDto.getFcltNm());
        shelter.setDaddr(shelterDto.getDaddr());
        shelter.setLat(shelterDto.getLat());
        shelter.setLot(shelterDto.getLot());
        shelter.setMngDeptNm(shelterDto.getMngDeptNm());
    }

    @Transactional
    public void delete(String shltId) {
        airRaidShelterRepository.deleteByShltId(shltId);
    }

    public void syncData() {
        try {
            System.out.println(">>> 공습 대피소 파이썬 DB 연동 실행");
            ProcessBuilder processBuilder = new ProcessBuilder("python", "scripts/airstrike.py");
            processBuilder.inheritIO();
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("파이썬 스크립트 실행 실패 (Exit Code: " + exitCode + ")");
            }
            System.out.println(">>> 공습 대피소 파이썬 DB 연동 완료");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("공습 DB 연동 중 오류 발생: " + e.getMessage());
        }
    }
}