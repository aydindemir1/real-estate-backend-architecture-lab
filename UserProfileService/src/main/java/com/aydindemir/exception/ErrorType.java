package com.aydindemir.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorType {

    BAD_REQUEST(6001, "Geçersiz istek yaptınız.", HttpStatus.BAD_REQUEST),
    MALFORMED_REQUEST_BODY(6003, "İstek gövdesi okunamadı veya geçersiz JSON içeriyor.", HttpStatus.BAD_REQUEST),
    METHOD_NOT_ALLOWED(6007, "Bu endpoint için kullanılan HTTP metodu desteklenmiyor.", HttpStatus.METHOD_NOT_ALLOWED),
    RESOURCE_NOT_FOUND(6010, "İstenen kaynak bulunamadı.", HttpStatus.NOT_FOUND),
    DATA_INTEGRITY_VIOLATION(7001, "Veri bütünlüğü kuralı ihlal edildi.", HttpStatus.CONFLICT),
    DATABASE_ERROR(7002, "Veritabanı işlemi sırasında beklenmeyen bir hata oluştu.", HttpStatus.INTERNAL_SERVER_ERROR),
    INTERNAL_SERVER_ERROR(7000, "Beklenmeyen bir sunucu hatası oluştu.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
}
