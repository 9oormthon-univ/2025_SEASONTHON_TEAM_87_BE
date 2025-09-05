package com.developing.bluffing.global.exception;

public interface GlobalException {
    int getStatus(); // 상태값
    int getErrorCode(); // 에러코드
    String getMessage(); // 메시지
}
