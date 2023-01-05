package com.example.likelionfinalproject.domain.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
@Table(name = "\"likes\"")
public class Like extends BaseEntityForPost{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private Post post;

    @ManyToOne
    private User user;


}
