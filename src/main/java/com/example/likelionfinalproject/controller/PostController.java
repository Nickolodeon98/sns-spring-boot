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
    public Response<PostResponse> newPost(Authentication authentication, @RequestBody PostRequest postRequest) {
        PostResponse postResponse = postService.createNewPost(postRequest, authentication.getName());

        return Response.success(postResponse);
    }

    /* id, 제목, 내용, 작성자, 작성날짜, 수정날짜 조회 */
    @GetMapping("/{postId}")
    @ResponseBody
    public Response<SelectedPostResponse> postInfoDetails(@PathVariable Integer postId) {
        SelectedPostResponse selectedPostResponse = postService.acquireSinglePost(postId);
        return Response.success(selectedPostResponse);
    }

    @GetMapping("")
    @ResponseBody
    public Response<Page<SelectedPostResponse>> everyPostAsList(@PageableDefault(size=20, sort="id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<SelectedPostResponse> responses = postService.listAllPosts(pageable);
        log.info("Responses:{}", responses);
        return Response.success(responses);
    }

    @PutMapping("/{postId}")
    @ResponseBody
    public Response<PostResponse> updateAPost(@RequestBody EditPostRequest editPostRequest, @PathVariable Integer postId) {
        PostResponse response = postService.editPost(editPostRequest, postId);

        return Response.success(response);
    }
}
