package com.example.knightWatch.dto;

import java.util.Set;

public record UserDTO(Long id, String username, Set<String> roles, String email) {
}
