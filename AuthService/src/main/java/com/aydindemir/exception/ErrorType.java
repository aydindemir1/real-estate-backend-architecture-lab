package com.aydindemir.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum ErrorType {

    REGISTER_PASSWORD_MISMATCH(1004, "Girilen parolalar uyuşmadı.", HttpStatus.BAD_REQUEST),
    REGISTER_USERNAME_EXISTS(1005, "Bu kullanıcı adı sistemde alınmıştır.", HttpStatus.BAD_REQUEST),

    LOGIN_USERNAME_OR_PASSWORD_MISMATCH(3001, "Kullanıcı adı veya parola hatalı.", HttpStatus.BAD_REQUEST),

    INVALID_TOKEN(5001, "Geçersiz token.", HttpStatus.BAD_REQUEST),

    BAD_REQUEST(6001, "Geçersiz istek yaptınız.", HttpStatus.BAD_REQUEST),
    VALIDATION_ERROR(6002, "İstek doğrulama kurallarını sağlamıyor.", HttpStatus.BAD_REQUEST),
    MALFORMED_REQUEST_BODY(6003, "İstek gövdesi okunamadı veya geçersiz JSON içeriyor.", HttpStatus.BAD_REQUEST),
    MISSING_REQUEST_PARAMETER(6004, "Zorunlu istek parametresi eksik.", HttpStatus.BAD_REQUEST),
    MISSING_REQUEST_HEADER(6005, "Zorunlu HTTP header bilgisi eksik.", HttpStatus.BAD_REQUEST),
    TYPE_MISMATCH(6006, "İstek parametresi beklenen veri tipinde değil.", HttpStatus.BAD_REQUEST),
    METHOD_NOT_ALLOWED(6007, "Bu endpoint için kullanılan HTTP metodu desteklenmiyor.", HttpStatus.METHOD_NOT_ALLOWED),
    UNSUPPORTED_MEDIA_TYPE(6008, "Gönderilen Content-Type desteklenmiyor.", HttpStatus.UNSUPPORTED_MEDIA_TYPE),
    NOT_ACCEPTABLE(6009, "İstenen response formatı desteklenmiyor.", HttpStatus.NOT_ACCEPTABLE),
    RESOURCE_NOT_FOUND(6010, "İstenen kaynak bulunamadı.", HttpStatus.NOT_FOUND),

    DATA_INTEGRITY_VIOLATION(7001, "Veri bütünlüğü kuralı ihlal edildi.", HttpStatus.CONFLICT),
    DATABASE_ERROR(7002, "Veritabanı işlemi sırasında beklenmeyen bir hata oluştu.", HttpStatus.INTERNAL_SERVER_ERROR),
    INTERNAL_SERVER_ERROR(7000, "Beklenmeyen bir sunucu hatası oluştu.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;
}
