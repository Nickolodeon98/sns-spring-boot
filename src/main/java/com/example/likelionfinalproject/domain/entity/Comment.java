package com.example.likelionfinalproject.domain.entity;

import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLDeleteAll;

import javax.persistence.*;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SQLDelete(sql = "UPDATE likes SET deleted_at = current_timestamp WHERE id = ?")
@SQLDeleteAll(sql = "UPDATE likes SET deleted_at = current_timestamp WHERE id = ?")
public class Comment extends BaseEntityForPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String comment;

    @ManyToOne
    private Post post;

    @ManyToOne
    @JoinColumn(referencedColumnName = "userName")
    private User author;
}
