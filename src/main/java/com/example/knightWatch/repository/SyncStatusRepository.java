package com.example.knightWatch.repository;

import com.example.knightWatch.model.SyncStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SyncStatusRepository extends JpaRepository<SyncStatus, Long> {
}
