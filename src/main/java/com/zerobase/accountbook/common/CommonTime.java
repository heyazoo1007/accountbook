package com.zerobase.accountbook.common;

import java.time.LocalDateTime;
import java.time.YearMonth;

public class CommonTime {

    // 2024-04-01 형태의 매월 시작일 반환
    public static String getStartDate(LocalDateTime now) {
        return getYearMonthString(now) + "-01";
    }

    // 2024-04-30 형태의 매월 마지막일 반환
    public static String getEndDate(LocalDateTime now) {
        return getYearMonthString(now) + "-" + getYearMonth(now).lengthOfMonth();
    }

    // 2024-04 형태의 날짜 문자열 반환
    public static String getYearMonthString(LocalDateTime now) {
        return now.getYear() + "-" + String.format("%02d", now.getMonthValue());
    }

    // 마지막 날 각각 다르므로 YearMonth 반환
    private static YearMonth getYearMonth(LocalDateTime now) {
        return YearMonth.of(now.getYear(), now.getMonth());
    }
}
