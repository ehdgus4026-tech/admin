package com.example.demo.config;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import java.io.BufferedReader;
import java.io.InputStreamReader;

@Component
public class PythonScriptRunner {

    @PostConstruct
    public void runPythonScripts() {
        // 순서대로 실행할 파이썬 스크립트 목록
        String[] scripts = {
                "scripts/excel_to_mysql.py", // 1. 홍수 대피소 (엑셀 적재)
                "scripts/airstrike.py",      // 2. 공습 대피소 (API 수집 및 적재)
                "scripts/earthquake.py"      // 3. 지진 대피소 (API 수집 및 적재)
        };

        for (String scriptPath : scripts) {
            runSingleScript(scriptPath);
        }
    }

    private void runSingleScript(String scriptPath) {
        try {
            System.out.println("\n>>> [PythonRunner] 실행 시작: " + scriptPath);

            // 리눅스 환경에서는 "python3", 윈도우에서는 "python"일 수 있으니 서버 환경에 맞춰 조정 가능
            ProcessBuilder pb = new ProcessBuilder("python3", scriptPath);

            pb.redirectErrorStream(true);
            Process process = pb.start();

            // 파이썬 실행 결과 로그 출력 확인용 (리눅스/윈도우 호환을 위해 MS949 또는 UTF-8)
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "MS949"))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println("[Python Log][" + scriptPath + "] " + line);
                }
            }

            int exitCode = process.waitFor();
            System.out.println(">>> [PythonRunner] 실행 종료 (" + scriptPath + ", 종료 코드: " + exitCode + ")");

        } catch (Exception e) {
            System.out.println("[ERROR] 파이썬 스크립트 실행 중 예외 발생 (" + scriptPath + ")");
            e.printStackTrace();
        }
    }
}