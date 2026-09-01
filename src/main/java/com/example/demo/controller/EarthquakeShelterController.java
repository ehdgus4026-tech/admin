package com.example.demo.controller;

import com.example.demo.domain.EarthquakeShelter;
import com.example.demo.service.EarthquakeShelterService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Controller
@RequiredArgsConstructor
public class EarthquakeShelterController {

    private final EarthquakeShelterService earthquakeShelterService;

    @GetMapping("/shelters/earthquake")
    public String getEarthquakeShelters(
            @RequestParam(value = "keyword", required = false) String keyword,
            @PageableDefault(page = 0, size = 10) Pageable pageable,
            Model model) {
        Page<EarthquakeShelter> shelters = earthquakeShelterService.getShelters(keyword, pageable);
        model.addAttribute("shelters", shelters);
        model.addAttribute("keyword", keyword);
        return "earthquake";
    }

    @PostMapping("/earthquake/write")
    public String writeEarthquakeShelter(
            @RequestParam(value = "lat", required = false) String latStr,
            @RequestParam(value = "lot", required = false) String lotStr,
            EarthquakeShelter shelter,
            RedirectAttributes redirectAttributes) {

        // 위도/경도 숫자 유효성 검증
        try {
            if (latStr != null && !latStr.trim().isEmpty()) {
                Double.parseDouble(latStr); // 숫자인지 검증만 수행
                shelter.setLat(latStr);     // String 타입 필드에 대입
            }
            if (lotStr != null && !lotStr.trim().isEmpty()) {
                Double.parseDouble(lotStr); // 숫자인지 검증만 수행
                shelter.setLot(lotStr);     // String 타입 필드에 대입
            }
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "위도와 경도에는 숫자만 입력해야 합니다!");
            return "redirect:/shelters/earthquake";
        }

        earthquakeShelterService.save(shelter);
        redirectAttributes.addFlashAttribute("successMessage", "지진 대피소 등록이 완료되었습니다!");
        return "redirect:/shelters/earthquake";
    }

    @PostMapping("/earthquake/edit/{id}")
    public String editEarthquakeShelter(
            @PathVariable("id") String id,
            @RequestParam(value = "lat", required = false) String latStr,
            @RequestParam(value = "lot", required = false) String lotStr,
            EarthquakeShelter shelterDto,
            @RequestParam(value = "keyword", required = false) String keyword,
            RedirectAttributes redirectAttributes) {

        // 위도/경도 숫자 유효성 검증
        try {
            if (latStr != null && !latStr.trim().isEmpty()) {
                Double.parseDouble(latStr); // 숫자인지 검증만 수행
                shelterDto.setLat(latStr);  // String 타입 필드에 대입
            }
            if (lotStr != null && !lotStr.trim().isEmpty()) {
                Double.parseDouble(lotStr); // 숫자인지 검증만 수행
                shelterDto.setLot(lotStr);  // String 타입 필드에 대입
            }
        } catch (NumberFormatException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "위도와 경도에는 숫자만 입력해야 합니다!");
            String redirectUrl = "redirect:/shelters/earthquake";
            if (keyword != null && !keyword.trim().isEmpty()) {
                redirectUrl += "?keyword=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8);
            }
            return redirectUrl;
        }

        earthquakeShelterService.update(id, shelterDto);
        redirectAttributes.addFlashAttribute("successMessage", "지진 대피소 수정이 완료되었습니다!");

        if (keyword != null && !keyword.trim().isEmpty()) {
            return "redirect:/shelters/earthquake?keyword=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8);
        }
        return "redirect:/shelters/earthquake";
    }

    // 삭제 시에도 검색어(keyword) 유지 처리
    @GetMapping("/earthquake/delete/{id}")
    public String deleteEarthquakeShelter(
            @PathVariable("id") String id,
            @RequestParam(value = "keyword", required = false) String keyword,
            RedirectAttributes redirectAttributes) {
        earthquakeShelterService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "지진 대피소가 삭제되었습니다.");

        if (keyword != null && !keyword.trim().isEmpty()) {
            return "redirect:/shelters/earthquake?keyword=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8);
        }
        return "redirect:/shelters/earthquake";
    }

    @PostMapping("/shelters/sync/earthquake")
    public String syncEarthquakeShelters(RedirectAttributes redirectAttributes) {
        earthquakeShelterService.syncData();
        redirectAttributes.addFlashAttribute("successMessage", "데이터 동기화가 완료되었습니다.");
        return "redirect:/shelters/earthquake";
    }
}