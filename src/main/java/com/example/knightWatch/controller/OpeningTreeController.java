package com.example.knightWatch.controller;

import com.example.knightWatch.dto.nodetrav.OpeningGamesDTO;
import com.example.knightWatch.dto.nodetrav.OpeningNodeDTO;
import com.example.knightWatch.model.LocalProfile;
import com.example.knightWatch.model.User;
import com.example.knightWatch.repository.LocalProfileRepository;
import com.example.knightWatch.repository.UserRepository;
import com.example.knightWatch.service.OpeningTreeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/openings/tree")
public class OpeningTreeController {

    @Autowired
    private OpeningTreeService openingTreeService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LocalProfileRepository localProfileRepository;

    /**
    * Get root level openings
    */
    @GetMapping("/root/{localProfileId}")
    public ResponseEntity<List<OpeningNodeDTO>> getRootOpenings(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails,  @PathVariable Long localProfileId) {

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalProfile profile = localProfileRepository.findById(localProfileId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        List<OpeningNodeDTO> openings = openingTreeService.getRootOpenings(user.getId(), localProfileId);
        return ResponseEntity.ok(openings);
    }

    /**
    * Navigate deeper into the tree
    */
    @GetMapping("/{openingId}/children/{localProfileId}")
    public ResponseEntity<List<OpeningNodeDTO>> getChildren(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails,
            @PathVariable Long openingId, @PathVariable Long localProfileId) {

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalProfile profile = localProfileRepository.findById(localProfileId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        List<OpeningNodeDTO> children = openingTreeService.getChildOpenings(user.getId(), openingId, profile.getId());
        return ResponseEntity.ok(children);
    }

    /**
     * Navigate back up the tree
     */
    @GetMapping("/{openingId}/parent/{localProfileId}")
    public ResponseEntity<OpeningNodeDTO> getParent(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails,
            @PathVariable Long openingId,
            @PathVariable Long localProfileId) {

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalProfile profile = localProfileRepository.findById(localProfileId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        OpeningNodeDTO parent = openingTreeService.getParentOpening(user.getId(), openingId, profile.getId());
        return ResponseEntity.ok(parent);
    }

    /**
     * Get full details with games for a specific opening
     */
    @GetMapping("/{openingId}/{localProfileId}")
    public ResponseEntity<OpeningGamesDTO> getOpeningWithGames(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User userDetails,
            @PathVariable Long openingId, @PathVariable Long localProfileId) {

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalProfile profile = localProfileRepository.findById(localProfileId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        OpeningGamesDTO data = openingTreeService.getOpeningWithGames(user.getId(), openingId, profile.getId());
        return ResponseEntity.ok(data);
    }
}
