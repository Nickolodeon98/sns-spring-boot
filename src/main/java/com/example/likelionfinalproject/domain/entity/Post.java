package com.example.likelionfinalproject.domain.entity;

import com.example.likelionfinalproject.domain.dto.request.PostRequest;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;

@Entity
@AllArgsConstructor
@Getter
@NoArgsConstructor
@Setter
@Builder
@ToString(callSuper = true)
@SQLDelete(sql = "UPDATE post SET deleted_at = current_timestamp WHERE id = ?")
@Where(clause = "deleted_at IS NULL")
public class Post extends BaseEntityForPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;
    private String body;

    @ManyToOne
    @JoinColumn(referencedColumnName = "userName")
    // 현재에는 POST 엔티티에서 고유 sql DB pk 로서의 row 의 아이디를 가져와서 저장하는데
    // 어차피 중복 체크해서 로그인 시 아이디는 단 하나의 고유한 정보니까 pk row 아이디 말고
    // 사용자 아이디 흔히 말하는 아이디 패스워드 할 때 아이디를 Post 테이블에서 저장하고 있도록 만드려면 어떻게 해요?
    private UserEntity author;

    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    private List<Comment> comments;

    @OneToMany(mappedBy="post", cascade = CascadeType.REMOVE)
    private List<LikeEntity> likeEntities;

    public PostRequest toRequest() {
        return PostRequest.builder()
                .title(title)
                .body(body)
                .build();
    }
}
