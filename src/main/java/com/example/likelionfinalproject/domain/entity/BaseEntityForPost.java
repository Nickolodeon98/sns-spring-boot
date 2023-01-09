package com.example.likelionfinalproject.domain.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@ToString
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Slf4j
public class BaseEntityForPost {

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column
    private LocalDateTime lastModifiedAt;

    private LocalDateTime deletedAt;

    @PrePersist
    public void beforeCreation() {
        LocalDateTime localDateTime = LocalDateTime
                .of(LocalDate.now().getYear(), LocalDate.now().getMonth(), LocalDate.now().getDayOfMonth(),
                        LocalTime.now().getHour(), LocalTime.now().getMinute(), LocalTime.now().getSecond());

        log.info("localDateTime:{}", localDateTime);

        this.createdAt = localDateTime;
        this.lastModifiedAt = localDateTime;
    }

    @PreUpdate
    public void beforeUpdate() {
        this.lastModifiedAt = LocalDateTime
                .of(LocalDate.now().getYear(), LocalDate.now().getMonth(), LocalDate.now().getDayOfMonth(),
                        LocalTime.now().getHour(), LocalTime.now().getMinute(), LocalTime.now().getSecond());
    }

}
