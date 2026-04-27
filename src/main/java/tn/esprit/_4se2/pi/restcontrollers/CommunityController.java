package tn.esprit._4se2.pi.restcontrollers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.esprit._4se2.pi.dto.Community.CommunityDTO;
import tn.esprit._4se2.pi.services.Community.CommunityService;

import java.util.List;

@RestController
@RequestMapping("/api/communities")
@RequiredArgsConstructor
public class CommunityController {

    private final CommunityService communityService;

    @GetMapping
    public ResponseEntity<List<CommunityDTO>> getCommunities() {
        return ResponseEntity.ok(communityService.getVisibleCommunitiesForCurrentUser());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CommunityDTO> getCommunityById(@PathVariable Long id) {
        return communityService.getVisibleCommunityById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/insights")
    public ResponseEntity<List<CommunityDTO>> getCommunityInsights(
            @RequestParam(defaultValue = "") String categoryKeyword,
            @RequestParam(defaultValue = "0") Long minMembers
    ) {
        return ResponseEntity.ok(communityService.getCommunityInsightsByCategory(categoryKeyword, minMembers));
    }
}