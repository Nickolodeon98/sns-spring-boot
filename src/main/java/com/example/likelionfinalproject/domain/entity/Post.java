package com.example.likelionfinalproject.domain.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@Getter
@NoArgsConstructor
@Setter
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String body;

    @ManyToOne
    @JoinColumn(referencedColumnName = "userId")
    // 현재에는 POST 엔티티에서 고유 sql DB pk 로서의 row 의 아이디를 가져와서 저장하는데
    // 어차피 중복 체크해서 로그인 시 아이디는 단 하나의 고유한 정보니까 pk row 아이디 말고
    // 사용자 아이디 흔히 말하는 아이디 패스워드 할 때 아이디를 Post 테이블에서 저장하고 있도록 만드려면 어떻게 해요?
    private User author;

    private LocalDateTime createdAt;
}
