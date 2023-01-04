package com.example.likelionfinalproject.domain.dto;

import lombok.*;


public interface ResponseDto {
    void setMessage(String message);
    Integer getId();
    String getMessage();
}
