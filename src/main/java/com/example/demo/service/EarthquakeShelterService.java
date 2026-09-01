package com.example.demo.service;

import com.example.demo.domain.EarthquakeShelter;
import com.example.demo.repository.EarthquakeShelterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true) // 기본 읽기 성능 최적화
public class EarthquakeShelterService {

    private final EarthquakeShelterRepository earthquakeShelterRepository;

    public Page<EarthquakeShelter> getShelters(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return earthquakeShelterRepository.findAll(pageable);
        }
        return earthquakeShelterRepository.findByFcltNmContainingOrDaddrContaining(keyword, keyword, pageable);
    }

    @Transactional // 저장 트랜잭션
    public void save(EarthquakeShelter shelter) {
        if (shelter.getShltId() == null || shelter.getShltId().trim().isEmpty()) {
            shelter.setShltId("EQ_" + UUID.randomUUID().toString());
        }
        earthquakeShelterRepository.save(shelter);
    }

    @Transactional // 수정 트랜잭션 (Dirty Checking)
    public void update(String shltId, EarthquakeShelter shelterDto) {
        EarthquakeShelter shelter = earthquakeShelterRepository.findByShltId(shltId)
                .orElseThrow(() -> new IllegalArgumentException("해당 지진 대피소가 존재하지 않습니다. shltId=" + shltId));

        shelter.setCtpvNm(shelterDto.getCtpvNm());
        shelter.setSggNm(shelterDto.getSggNm());
        shelter.setFcltNm(shelterDto.getFcltNm());
        shelter.setDaddr(shelterDto.getDaddr());
        shelter.setLat(shelterDto.getLat());
        shelter.setLot(shelterDto.getLot());
        shelter.setMngDeptNm(shelterDto.getMngDeptNm());
    }

    @Transactional // 삭제 트랜잭션
    public void delete(String shltId) {
        earthquakeShelterRepository.deleteByShltId(shltId);
    }

    public void syncData() {
        try {
            System.out.println(">>> 지진 대피소 파이썬 DB 연동 실행");
            ProcessBuilder processBuilder = new ProcessBuilder("python", "scripts/earthquake.py");
            processBuilder.inheritIO();
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("지진 파이썬 스크립트 실행 실패 (Exit Code: " + exitCode + ")");
            }
            System.out.println(">>> 지진 대피소 파이썬 DB 연동 완료");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("지진 DB 연동 중 오류 발생: " + e.getMessage());
        }
    }
}