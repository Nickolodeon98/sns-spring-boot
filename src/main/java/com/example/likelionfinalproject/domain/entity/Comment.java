package com.example.likelionfinalproject.domain.entity;

import javax.persistence.*;

@Entity
public class Comment extends BaseEntityForPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;

    String comment;

    @ManyToOne
    Post postId;

    @ManyToOne
    User userId;
}
