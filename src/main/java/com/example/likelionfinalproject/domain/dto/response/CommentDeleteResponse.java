package com.example.likelionfinalproject.domain.dto.response;

import com.example.likelionfinalproject.domain.entity.Comment;
import lombok.*;

@Getter
@AllArgsConstructor
@Setter
@NoArgsConstructor
@Builder
public class CommentDeleteResponse extends ResponseObject implements ResponseDto {
    Integer id;

    public static CommentDeleteResponse of(Comment comment) {
        CommentDeleteResponse response = CommentDeleteResponse.builder()
                .id(comment.getId())
                .build();
        response.setMessage("댓글 삭제 완료");
        return response;
    }

    @Override
    public void setMessage(String message) {
        super.message = message;
    }
}
