package com.example.demo.handler; // Kendi paket adınızı kullanın

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@ControllerAdvice // Bu anotasyon, sınıfın tüm controller'ları dinlemesini sağlar.
public class GlobalExceptionHandler {

    /**
     * ResponseStatusException tipindeki hataları yakalar (Örn: 403 Forbidden, 404 Not Found).
     * @param ex Servis katmanından fırlatılan hata.
     * @return Hatanın durum kodunu ve mesajını içeren bir ResponseEntity.
     */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleResponseStatusException(ResponseStatusException ex) {
        return ResponseEntity
                .status(ex.getStatusCode())
                .body(Map.of("error", ex.getReason()));
    }

    /**
     * Diğer tüm genel RuntimeException hatalarını yakalar.
     * @param ex Fırlatılan hata.
     * @return 400 Bad Request durum kodu ve hata mesajı içeren bir ResponseEntity.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException ex) {
        // Bu, daha genel bir hata yakalayıcıdır.
        return ResponseEntity
                .badRequest()
                .body(Map.of("error", ex.getMessage()));
    }
}