package com.developing.bluffing.game.entity.enums;

public enum AgeGroup {
    TEENS(10, 19, "10대"),
    TWENTIES(20, 29, "20대"),
    THIRTIES(30, 39, "30대"),
    FORTIES(40, 49, "40대"),
    FIFTIES(50, 59, "50대"),
    SIXTIES(60, 69, "60대"),
    SEVENTIES(70, 79, "70대"),
    EIGHTIES(80, 89, "80대"),
    NINETIES(90, 99, "90대"),
    HUNDREDS(100, 109, "100대"),
    OVER_HUNDRED(110, 200, "100세 이상");

    private final int min;
    private final int max;
    private final String label;

    AgeGroup(int min, int max, String label) {
        this.min = min;
        this.max = max;
        this.label = label;
    }

    public String getAge() {        // ← 너가 원한 "10대" 같은 문자열
        return label;
    }

    public static AgeGroup fromAge(int age) {
        for (AgeGroup g : values()) {
            if (age >= g.min && age <= g.max) return g;
        }
        throw new IllegalArgumentException("Unsupported age: " + age);
    }

    // 편의 메서드: 바로 "10대" 얻고 싶을 때
    public static String labelOf(int age) {
        return fromAge(age).getAge();
    }

    @Override
    public String toString() {
        return label;
    }
}
