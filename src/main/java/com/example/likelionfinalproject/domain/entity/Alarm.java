package com.example.likelionfinalproject.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Alarm extends BaseEntityForPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Integer fromUserId;
    private Integer targetId;
    private String text;
    private String alarmType;


    @ManyToOne
    private UserEntity userEntity;

}
