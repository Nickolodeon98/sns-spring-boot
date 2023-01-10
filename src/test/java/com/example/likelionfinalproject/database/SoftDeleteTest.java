package com.example.likelionfinalproject.database;

import com.example.likelionfinalproject.domain.entity.LikeEntity;
import com.example.likelionfinalproject.domain.entity.Post;
import com.example.likelionfinalproject.domain.entity.UserEntity;
import com.example.likelionfinalproject.repository.LikeRepository;
import com.example.likelionfinalproject.repository.PostRepository;
import com.example.likelionfinalproject.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
//@SpringBootTest
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Slf4j
public class SoftDeleteTest {

    Post post;
    Post savedPost;
    LikeEntity likeEntity;
    UserEntity userEntity;
    final Integer postId = 2;
    final Integer userId = 14;
    final String userName = "string";
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private UserRepository userRepository;
    final LocalDateTime timeInfo = LocalDateTime.of(2022, 12, 26, 18, 03, 14);

    @BeforeEach
    void setUp() {
//        like = LikeFixture.get(post);
        likeEntity = LikeEntity.builder().build();
        post = Post.builder()
                .id(postId)
                .body("body")
                .title("title")
                .build();

        savedPost = postRepository.save(post);

        likeEntity.setPost(savedPost);
        savedPost.setLikeEntities(List.of(likeEntity));

    }

    @Test
    @DisplayName("Soft Delete 로 데이터를 삭제하는 대신 업데이트한다.")
    void success_soft_delete() {
        LikeEntity savedLike = likeRepository.save(likeEntity);
        log.info("foundLike:{}", likeRepository.findByPost(savedPost));

        Assertions.assertEquals(1, likeRepository.countByPostId(savedPost.getId()));

        /* cascade 를 사용하여 영속성 객체 Post 와 Like 의 생명주기를 같게 하면 두 번 삭제해주지 않아도 된다. */
        Optional<LikeEntity> likeBeforeDeletion = likeRepository.findByPost(savedPost);
        log.info("likeBeforeDeletion:{}", likeBeforeDeletion);

        Assertions.assertNull(likeBeforeDeletion.get().getDeletedAt());
        Optional<Post> foundPost = postRepository.findById(savedPost.getId());
        log.info("post:{}", foundPost);

        postRepository.deleteById(savedPost.getId());

        log.info("countByPost:{}", likeRepository.countByPostId(savedPost.getId()));
        Assertions.assertEquals(0, likeRepository.countByPostId(savedPost.getId()));

        Optional<Post> postAfterDeletion = postRepository.findById(savedPost.getId());
        log.info("postAfterDeletion:{}", postAfterDeletion);

        /* 더 이상에는 테스트 내에서 저장하고 지운 포스트가 soft-delete 되어서 find 로 조회가 되지 않으며, 존재하지 않는 포스트로 취급받게 되었다.
         * 그래서 더이상 Post 객체로 Like row 를 찾을 수 없다. (Post 객체가 사라졌기 때문에)
         * 그래서 아래의 방식은 사용하지 않고, 대신에 Like 저장 시 반환된 likes 테이블의 row 에 대한 참조 엔티티 객체를 사용해 LikeEntity 객체를 찾는다. */
//        Optional<LikeEntity> likeAfterDeletion = likeRepository.findByPost(savedPost);
        Optional<LikeEntity> likeAfterDeletion = likeRepository.findById(savedLike.getId());
        log.info("likeAfterDeletion:{}", likeAfterDeletion);

        Assertions.assertTrue(likeAfterDeletion.isEmpty());
    }

}
