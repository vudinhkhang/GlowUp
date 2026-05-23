package com.glowup.repository;

import com.glowup.model.Outfit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface OutfitRepository extends JpaRepository<Outfit, UUID> {
    List<Outfit> findByUserId(UUID userId);
}
