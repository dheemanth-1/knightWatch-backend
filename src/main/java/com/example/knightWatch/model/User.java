package com.example.knightWatch.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles;

    private String email;

    @OneToMany(mappedBy = "user",  cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<LocalProfile> profiles;

    @OneToMany(mappedBy = "user",  cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<LichessSyncStatus> lichessSyncStatuses;

    @OneToMany(mappedBy = "user",  cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ChesscomSyncStatus> chesscomSyncStatuses;

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<LocalProfile> getProfiles() {
        return profiles;
    }

    public void setProfiles(List<LocalProfile> profiles) {
        this.profiles = profiles;
    }

    public List<LichessSyncStatus> getLichessSyncStatuses() {
        return lichessSyncStatuses;
    }

    public void setLichessSyncStatuses(List<LichessSyncStatus> lichessSyncStatuses) {
        this.lichessSyncStatuses = lichessSyncStatuses;
    }

    public List<ChesscomSyncStatus> getChesscomSyncStatuses() {
        return chesscomSyncStatuses;
    }

    public void setChesscomSyncStatuses(List<ChesscomSyncStatus> chesscomSyncStatuses) {
        this.chesscomSyncStatuses = chesscomSyncStatuses;
    }
}
