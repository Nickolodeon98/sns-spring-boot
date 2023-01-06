package com.example.likelionfinalproject.database;

import com.example.likelionfinalproject.domain.entity.Like;
import com.example.likelionfinalproject.domain.entity.Post;
import com.example.likelionfinalproject.domain.entity.User;
import com.example.likelionfinalproject.fixture.LikeFixture;
import com.example.likelionfinalproject.fixture.PostFixture;
import com.example.likelionfinalproject.fixture.UserFixture;
import com.example.likelionfinalproject.repository.LikeRepository;
import com.example.likelionfinalproject.repository.PostRepository;
import com.example.likelionfinalproject.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Slf4j
public class SoftDeleteTest {

    Post post;
    Like like;
    User user;
    final Integer postId = 2;
    final Integer userId = 14;
    final String userName = "string";
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private LikeRepository likeRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        user = UserFixture.get(userId, userName);
        userRepository.save(user);
        post = PostFixture.get(postId, user);
        postRepository.save(post);
        like = LikeFixture.get(post, user);
    }

    @Test
    @DisplayName("Soft Delete 로 데이터를 삭제하는 대신 업데이트한다.")
    void success_soft_delete() {
        likeRepository.save(like);

        Assertions.assertEquals(1, likeRepository.countByPost(post));

        /* cascade 를 사용하여 영속성 객체 Post 와 Like 의 생명주기를 같게 하면 두 번 삭제해주지 않아도 된다. */
        postRepository.delete(post);

        Assertions.assertEquals(1, likeRepository.countByPost(post));

        Optional<Like> likeAfterDeletion = likeRepository.findByPostIdAndUserId(postId, userId);

        Assertions.assertNotNull(likeAfterDeletion.get().getDeletedAt());
    }

}
