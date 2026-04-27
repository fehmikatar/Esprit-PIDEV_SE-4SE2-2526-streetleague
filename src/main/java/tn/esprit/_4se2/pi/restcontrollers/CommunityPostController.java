package tn.esprit._4se2.pi.restcontrollers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.dto.Community.CommunityPostDTOs;
import tn.esprit._4se2.pi.services.Community.CommunityPostService;

import java.util.List;

@RestController
@RequestMapping("/api/communities/{communityId}/posts")
@RequiredArgsConstructor
public class CommunityPostController {

    private final CommunityPostService communityPostService;

    @GetMapping
    public ResponseEntity<List<CommunityPostDTOs.PostResponse>> getPosts(@PathVariable Long communityId) {
        return ResponseEntity.ok(communityPostService.getPostsForCommunity(communityId));
    }

    @PostMapping
    public ResponseEntity<CommunityPostDTOs.PostResponse> createPost(
            @PathVariable Long communityId,
            @Valid @RequestBody CommunityPostDTOs.CreatePostRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(communityPostService.createPost(communityId, request));
    }
}