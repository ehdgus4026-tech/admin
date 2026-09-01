package com.example.demo.service;

import com.example.demo.domain.FloodShelter;
import com.example.demo.repository.FloodShelterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FloodShelterService {

    private final FloodShelterRepository floodShelterRepository;

    public Page<FloodShelter> getShelters(String keyword, Pageable pageable) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return floodShelterRepository.findAll(pageable);
        }
        return floodShelterRepository.findByFcltNmContainingOrDaddrContaining(keyword, keyword, pageable);
    }

    @Transactional
    public void save(FloodShelter shelter) {
        if (shelter.getShltId() == null || shelter.getShltId().trim().isEmpty()) {
            shelter.setShltId("FL_" + UUID.randomUUID().toString());
        }
        floodShelterRepository.save(shelter);
    }

    @Transactional
    public void update(String shltId, FloodShelter shelterDto) {
        FloodShelter shelter = floodShelterRepository.findByShltId(shltId)
                .orElseThrow(() -> new IllegalArgumentException("해당 홍수 대피소가 존재하지 않습니다. shltId=" + shltId));

        shelter.setCtpvNm(shelterDto.getCtpvNm());
        shelter.setSggNm(shelterDto.getSggNm());
        shelter.setFcltNm(shelterDto.getFcltNm());
        shelter.setDaddr(shelterDto.getDaddr());
        shelter.setLat(shelterDto.getLat()); // 👈 위도 반영
        shelter.setLot(shelterDto.getLot()); // 👈 경도 반영
        shelter.setMngDeptNm(shelterDto.getMngDeptNm());
    }

    @Transactional
    public void delete(String shltId) {
        floodShelterRepository.deleteByShltId(shltId);
    }

    @Transactional
    public void syncData() {
        try {
            System.out.println(">>> 홍수 대피소 파이썬 DB 연동 실행");
            ProcessBuilder processBuilder = new ProcessBuilder("python", "scripts/excel_to_mysql.py");
            processBuilder.inheritIO();
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("홍수 파이썬 스크립트 실행 실패 (Exit Code: " + exitCode + ")");
            }
            System.out.println(">>> 홍수 대피소 파이썬 DB 연동 완료");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("홍수 DB 연동 중 오류 발생: " + e.getMessage());
        }
    }
}