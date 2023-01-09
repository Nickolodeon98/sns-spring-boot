package com.example.likelionfinalproject.domain.entity;

import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLDeleteAll;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Builder
@Table(name = "likes")
@SQLDelete(sql="UPDATE likes SET deleted_at = current_timestamp WHERE id = ?")
@SQLDeleteAll(sql = "UPDATE likes SET deleted_at = current_timestamp WHERE id = ?")
public class LikeEntity extends BaseEntityForPost{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private Post post;

    @ManyToOne
    private UserEntity userEntity;


}
