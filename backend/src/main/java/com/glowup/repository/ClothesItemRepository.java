package com.glowup.repository;

import com.glowup.model.ClothesItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ClothesItemRepository extends JpaRepository<ClothesItem, UUID> {
    List<ClothesItem> findByUserId(UUID userId);
    List<ClothesItem> findByUserIdAndCategory(UUID userId, String category);
}
