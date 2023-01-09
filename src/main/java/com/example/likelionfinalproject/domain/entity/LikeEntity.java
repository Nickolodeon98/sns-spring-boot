package com.example.likelionfinalproject.domain.entity;

import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLDeleteAll;
import org.hibernate.annotations.Where;

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
@Where(clause = "deleted_at IS NULL")
public class LikeEntity extends BaseEntityForPost{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    private Post post;

    @ManyToOne
    private UserEntity userEntity;


}
