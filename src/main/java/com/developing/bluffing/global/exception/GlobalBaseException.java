package com.developing.bluffing.global.exception;

public abstract  class GlobalBaseException extends RuntimeException {
    public abstract GlobalException getErrorCode();
}
