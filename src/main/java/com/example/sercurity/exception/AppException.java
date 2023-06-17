package com.example.sercurity.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.function.Supplier;

@AllArgsConstructor
@Getter
public class AppException extends RuntimeException {
    private ErrorCode errodCode;
    private String message;
}
