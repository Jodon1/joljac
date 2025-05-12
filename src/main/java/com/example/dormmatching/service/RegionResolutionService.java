package com.example.dormmatching.service;

import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class RegionResolutionService {

    // 키워드 순서대로 검사 (우선순위 높은 순)
    private static final Map<String, Integer> KEYWORD_TO_REGION = new LinkedHashMap<>();
    static {
        // 1지역 (10점) → region_id = 1
        KEYWORD_TO_REGION.put("군산시",     1);
        KEYWORD_TO_REGION.put("전주시",     1);
        KEYWORD_TO_REGION.put("익산시",     1);
        KEYWORD_TO_REGION.put("김제시",     1);
        KEYWORD_TO_REGION.put("서천군",     1);
        // 2지역 (20점) → region_id = 2
        KEYWORD_TO_REGION.put("완주군",     2);
        KEYWORD_TO_REGION.put("부안군",     2);
        KEYWORD_TO_REGION.put("정읍시",     2);
        KEYWORD_TO_REGION.put("고창군",     2);
        KEYWORD_TO_REGION.put("임실군",     2);
        KEYWORD_TO_REGION.put("진안군",     2);
        KEYWORD_TO_REGION.put("장수군",     2);
        KEYWORD_TO_REGION.put("무주군",     2);
        KEYWORD_TO_REGION.put("순창군",     2);
        KEYWORD_TO_REGION.put("남원시",     2);
        KEYWORD_TO_REGION.put("부여군",     2);
        KEYWORD_TO_REGION.put("논산시",     2);
        KEYWORD_TO_REGION.put("계룡시",     2);
        KEYWORD_TO_REGION.put("보령시",     2);
        KEYWORD_TO_REGION.put("금산군",     2);
        KEYWORD_TO_REGION.put("청양군",     2);
        KEYWORD_TO_REGION.put("공주시",     2);
        KEYWORD_TO_REGION.put("대전광역시", 2);
        // 3지역 (30점) → region_id = 3 (기타)
        // 기타는 디폴트로 3을 반환
    }

    /**
     * 주소 문자열에서 region_id를 결정합니다.
     * 주소에 매핑된 키워드가 없으면 3 (기타지역) 을 반환합니다.
     */
    public int resolveRegionId(String address) {
        if (address == null) {
            return 3;
        }
        for (Map.Entry<String, Integer> e : KEYWORD_TO_REGION.entrySet()) {
            if (address.contains(e.getKey())) {
                return e.getValue();
            }
        }
        return 3;
    }
}