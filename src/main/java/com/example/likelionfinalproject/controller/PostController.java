package com.example.likelionfinalproject.controller;

import com.example.likelionfinalproject.domain.Response;
import com.example.likelionfinalproject.domain.dto.request.CommentRequest;
import com.example.likelionfinalproject.domain.dto.request.PostRequest;
import com.example.likelionfinalproject.domain.dto.response.CommentDeleteResponse;
import com.example.likelionfinalproject.domain.dto.response.CommentResponse;
import com.example.likelionfinalproject.domain.dto.response.PostResponse;
import com.example.likelionfinalproject.domain.dto.response.SelectedPostResponse;
import com.example.likelionfinalproject.service.CommentService;
import com.example.likelionfinalproject.service.LikeService;
import com.example.likelionfinalproject.service.PostService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Controller
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "POST Endpoints")
@ApiOperation(value = "포스트 관련 API 의 컨트롤러")
public class PostController {

    private final PostService postService;
    private final CommentService commentService;
    private final LikeService likeService;

    @Operation(summary = "포스트 작성", description = "로그인 한 사용자는 포스트 제목과 내용을 작성하여 등록할 수 있다.")
    @PostMapping
    @ResponseBody
    public Response<PostResponse> uploadPost(@ApiIgnore Authentication authentication, @RequestBody(required = false) PostRequest postRequest) {
        PostResponse postResponse = postService.createPost(postRequest, authentication.getName());

        return Response.success(postResponse);
    }

    /* id, 제목, 내용, 작성자, 작성날짜, 수정날짜 조회 */
    @Operation(summary = "포스트 조회", description = "포스트 고유 아이디를 입력하면 포스트 정보를 조회할 수 있다.")
    @GetMapping("/{postId}")
    @ResponseBody
    public Response<SelectedPostResponse> getSinglePost(@PathVariable Integer postId) {
        SelectedPostResponse selectedPostResponse = postService.acquirePost(postId);
        return Response.success(selectedPostResponse);
    }

    @Operation(summary = "전체 포스트 조회", description = "현재까지 작성된 모든 포스트를 조회할 수 있다.")
    @GetMapping
    @ResponseBody
    public Response<Page<SelectedPostResponse>> getEveryPost(@PageableDefault(size=20, sort="lastModifiedAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<SelectedPostResponse> responses = postService.listAllPosts(pageable);
        log.info("Responses:{}", responses);
        return Response.success(responses);
    }
    @Operation(summary = "포스트 수정", description = "로그인되어 있을 시 자신이 작성한 포스트의 고유 아이디를 입력하면 수정할 수 있다.")
    @PutMapping("/{postId}")
    @ResponseBody
    public Response<PostResponse> update(@RequestBody PostRequest editPostRequest,
                                              @PathVariable Integer postId,
                                              @ApiIgnore Authentication authentication) {

        PostResponse response = postService.editPost(editPostRequest, postId, authentication.getName());

        return Response.success(response);
    }
    @Operation(summary = "포스트 삭제", description = "로그인되어 있을 시 자신이 작성한 포스트의 고유 아이디를 입력하면 삭제할 수 있다.")
    @DeleteMapping("/{postId}")
    @ResponseBody
    public Response<PostResponse> delete(@PathVariable Integer postId, @ApiIgnore Authentication authentication) {
        PostResponse postResponse = postService.removePost(postId, authentication.getName());

        return Response.success(postResponse);
    }
    
    @Operation(summary = "댓글 작성", description = "로그인 되어 있을 시 특정 포스트의 고유 아이디를 입력하여 댓글을 작성할 수 있다.")
    @ResponseBody
    @PostMapping("/{postId}/comments")
    public Response<CommentResponse> addComment(@PathVariable Integer postId, @ApiIgnore Authentication authentication,
                                                @RequestBody(required = false) CommentRequest commentRequest) {
        CommentResponse commentResponse = commentService.uploadComment(commentRequest, authentication.getName(), postId);

        return Response.success(commentResponse);
    }

    @Operation(summary = "특정 포스트 댓글 모두 조회", description = "모든 사용자는 특정 포스트의 댓글을 모두 조회할 수 있다.")
    @ResponseBody
    @GetMapping("/{postId}/comments")
    public Response<Page<CommentResponse>> showComments(@PathVariable Integer postId,
                                                        @PageableDefault(direction=Sort.Direction.DESC, sort="createdAt")
                                                        Pageable pageable) {

        Page<CommentResponse> comments = commentService.fetchComments(pageable, postId);

        return Response.success(comments);
    }

    @Operation(summary = "댓글 수정", description = "인증된 사용자는 자신이 작성한 댓글을 수정할 수 있다.")
    @ResponseBody
    @PutMapping("/{postId}/comments/{id}")
    public ResponseEntity<Response<CommentResponse>> editComment(@PathVariable Integer postId, @PathVariable Integer id,
                                                 @RequestBody(required=false) CommentRequest commentRequest,
                                                 @ApiIgnore Authentication authentication) {

        CommentResponse commentResponse = commentService.modifyComment(commentRequest, id, authentication.getName());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Response.success(commentResponse));
    }

    @Operation(summary = "댓글 삭제", description = "인증된 사용자는 자신이 작성한 댓글을 삭제할 수 있다.")
    @ResponseBody
    @DeleteMapping("/{postId}/comments/{id}")
    public ResponseEntity<Response<CommentDeleteResponse>> deleteComment(@PathVariable Integer postId, @PathVariable Integer id,
                                                         @ApiIgnore Authentication authentication) {
        CommentDeleteResponse response = commentService.removeComment(id, authentication.getName());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Response.success(response));
    }

    @Operation(summary = "마이 피드", description = "인증된 사용자는 자신이 작성한 포스트를 모아서 볼 수 있다.")
    @ResponseBody
    @GetMapping("/my")
    public ResponseEntity<Response<Page<SelectedPostResponse>>> retrievePast(@ApiIgnore Authentication authentication,
                                                                             @PageableDefault(size = 20, direction = Sort.Direction.DESC, sort = "lastModifiedAt")
                                                             Pageable pageable) {
        Page<SelectedPostResponse> recalled = postService.showMyPosts(authentication.getName(), pageable);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Response.success(recalled));
    }

    @Operation(summary = "좋아요 누르는 기능", description = "인증된 사용자는 포스트에 좋아요를 달 수 있다.")
    @ResponseBody
    @PostMapping("/{postId}/likes")
    public ResponseEntity<Response<String>> toggleOnLike(@PathVariable Integer postId, @ApiIgnore Authentication authentication) {
        String response = likeService.pushThumbsUp(postId, authentication.getName());

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Response.success(response));
    }

    @Operation(summary = "좋아요 개수 세는 기능", description = "모든 사용자는 특정 포스트에 총 몇 개의 좋아요가 눌렸는지 볼 수 있다.")
    @ResponseBody
    @GetMapping("/{postId}/likes")
    public Response<Long> showLikesVolume(@PathVariable Integer postId) {
        Long likesVolume = likeService.countLikes(postId);
        return Response.success(likesVolume);
    }
}
