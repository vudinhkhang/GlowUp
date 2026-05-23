package com.glowup.repository;

import com.glowup.model.OutfitCalendar;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OutfitCalendarRepository extends JpaRepository<OutfitCalendar, UUID> {
    List<OutfitCalendar> findByUserId(UUID userId);
    Optional<OutfitCalendar> findByUserIdAndDate(UUID userId, LocalDate date);
}
