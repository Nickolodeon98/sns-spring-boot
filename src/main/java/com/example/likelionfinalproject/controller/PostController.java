package com.example.likelionfinalproject.controller;

import com.example.likelionfinalproject.domain.Response;
import com.example.likelionfinalproject.domain.dto.EditPostRequest;
import com.example.likelionfinalproject.domain.dto.PostRequest;
import com.example.likelionfinalproject.domain.dto.PostResponse;
import com.example.likelionfinalproject.domain.dto.SelectedPostResponse;
import com.example.likelionfinalproject.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;

    @PostMapping("")
    @ResponseBody
    public Response<PostResponse> uploadPost(Authentication authentication, @RequestBody(required = false) PostRequest postRequest) {
        PostResponse postResponse = postService.createPost(postRequest, authentication.getName());

        return Response.success(postResponse);
    }

    /* id, 제목, 내용, 작성자, 작성날짜, 수정날짜 조회 */
    @GetMapping("/{postId}")
    @ResponseBody
    public Response<SelectedPostResponse> getSinglePost(@PathVariable Integer postId) {
        SelectedPostResponse selectedPostResponse = postService.acquirePost(postId);
        return Response.success(selectedPostResponse);
    }

    @GetMapping("")
    @ResponseBody
    public Response<Page<SelectedPostResponse>> getEveryPost(@PageableDefault(size=20, sort="id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<SelectedPostResponse> responses = postService.listAllPosts(pageable);
        log.info("Responses:{}", responses);
        return Response.success(responses);
    }

    @PutMapping("/{postId}")
    @ResponseBody
    public Response<PostResponse> update(@RequestBody EditPostRequest editPostRequest,
                                              @PathVariable Integer postId,
                                              Authentication authentication) {

        PostResponse response = postService.editPost(editPostRequest, postId, authentication.getName());

        return Response.success(response);
    }

    @DeleteMapping("/{postId}")
    @ResponseBody
    public Response<PostResponse> delete(@PathVariable Integer postId, Authentication authentication) {
        PostResponse postResponse = postService.removePost(postId, authentication.getName());

        return Response.success(postResponse);
    }

}
