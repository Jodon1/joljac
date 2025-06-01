package com.example.dormmatching.service;

import com.example.dormmatching.dto.RegisterRequest;
import com.example.dormmatching.service.auth.AuthService;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class CsvUserImporter implements CommandLineRunner {

    private final AuthService authService;

    // application.properties 에서 읽어올 프로퍼티
    @Value("${csv.user-import-path}")
    private String csvPath;

    @Override
    public void run(String... args) throws Exception {
        // CSV 경로가 "classpath:…" 로 시작하면 리소스에서, 아니면 파일 시스템에서 불러옵니다.
        InputStream is;
        if (csvPath.startsWith("classpath:")) {
            String pathInResources = csvPath.substring("classpath:".length());
            is = new ClassPathResource(pathInResources).getInputStream();
        } else {
            is = new FileInputStream(csvPath);
        }

        try (CSVReader reader = new CSVReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String[] row;
            reader.readNext(); // 헤더 라인 건너뛰기

            while ((row = reader.readNext()) != null) {

                RegisterRequest req = new RegisterRequest();
                req.setStudentNumber(row[0]);
                req.setPassword(row[1]);
                req.setName(row[2]);
                req.setStatusId(Integer.valueOf(row[3]));
                req.setRoleId(Integer.valueOf(row[4]));
                req.setBirthDate(LocalDate.parse(row[5]));
                req.setGender(row[6]);
                req.setAddress(StringUtils.hasText(row[7]) ? row[7] : "");
                req.setDepartmentId(Integer.valueOf(row[8]));
                req.setInternational(Boolean.parseBoolean(row[9]));
                req.setPhoneNumber(StringUtils.hasText(row[10]) ? row[10] : "");
                req.setGrade(Integer.valueOf(row[11]));

                try {
                    authService.register(req);
                    System.out.println("Created user " + req.getStudentNumber());
                } catch (Exception e) {
                    System.err.println("Failed to create " + req.getStudentNumber() + ": " + e.getMessage());
                }
            }
        } catch (CsvValidationException e) {
            System.err.println("CSV validation error: " + e.getMessage());
        }
    }
}
