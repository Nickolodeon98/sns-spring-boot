package com.example.likelionfinalproject.domain.dto.response;

import lombok.*;


public interface ResponseDto {
    void setMessage(String message);
    Integer getId();
    String getMessage();
}
