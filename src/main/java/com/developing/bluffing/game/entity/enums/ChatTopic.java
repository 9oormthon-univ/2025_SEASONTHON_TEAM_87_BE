package com.developing.bluffing.game.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatTopic {
    // 세대별 추억/문화
    CHILDHOOD_PLAY("어릴 때 놀이/추억"),
    SCHOOL_LIFE("학창시절 문화"),
    EXAM_EDU("입시/공부 방식 변화"),
    TV_MEDIA("TV/웹 콘텐츠 변화"),
    INTERNET_CULTURE("인터넷/커뮤니티 문화"),
    MUSIC_IDOLS("음악/아이돌 세대 차이"),
    FASHION_TRENDS("패션/스타일 트렌드"),
    GAMES_RETRO_VS_MOBILE("레트로 게임 vs 모바일 게임"),
    TECH_FIRST_DEVICES("첫 휴대폰/디바이스"),
    SOCIAL_APPS("메신저/소셜앱 세대 교체"),
    FOOD_DELIVERY("먹거리/배달 문화"),
    DATING_MANNERS("연애/만남 문화의 변화"),
    WORK_VALUES("일/커리어 가치관"),
    MONEY_SENSE("물가/돈 감각(용돈·월급 체감)"),
    HOUSING_LIFE("거주/자취 문화"),
    TRANSPORT_COMMUTE("교통/등하교·출퇴근 경험"),
    HOBBIES_COLLECTIONS("취미/굿즈·수집 문화"),
    FESTIVALS_EVENTS("축제/행사 경험 차이"),
    SPORTS_MEMORY("스포츠/국민스타 기억"),
    NEWS_CONSUMPTION("뉴스 소비 방식(신문→SNS)");

    private final String labelKo;
}
