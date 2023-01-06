package com.example.likelionfinalproject.database;

import com.example.likelionfinalproject.domain.entity.Like;
import com.example.likelionfinalproject.domain.entity.Post;
import com.example.likelionfinalproject.domain.entity.User;
import com.example.likelionfinalproject.fixture.PostFixture;
import com.example.likelionfinalproject.fixture.UserFixture;
import com.example.likelionfinalproject.repository.LikeRepository;
import com.example.likelionfinalproject.repository.PostRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
public class SoftDeleteTest {

    Post post;
    Like like;
    User user;
    final Integer postId = 1;

    @BeforeEach
    void setUp() {
        post = PostFixture.get(postId);
        user = UserFixture.get();
        like = LikeFixture.get(post, user);
    }

    @Test
    @DisplayName("Soft Delete 로 데이터를 삭제하는 대신 업데이트한다.")
    void success_soft_delete() {
        postRepository.save(post);

        likeRepository.save(like);

        Assertions.assertEquals(1, likeRepository.countByPost(post));

        /* cascade 를 사용하여 영속성 객체 Post 와 Like 의 생명주기를 같게 하면 두 번 삭제해주지 않아도 된다. */
        postRepository.delete(post);

        Assertions.assertEquals(1, likeRepository.countByPost(post));
        Assertions.assertNotNull(like.getDeletedAt());
    }

}
