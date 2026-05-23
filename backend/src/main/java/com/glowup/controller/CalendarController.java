package com.glowup.controller;

import com.glowup.model.Outfit;
import com.glowup.model.OutfitCalendar;
import com.glowup.repository.OutfitCalendarRepository;
import com.glowup.repository.OutfitRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/calendar")
public class CalendarController {

    private final OutfitCalendarRepository calendarRepository;
    private final OutfitRepository outfitRepository;

    public CalendarController(OutfitCalendarRepository calendarRepository,
                              OutfitRepository outfitRepository) {
        this.calendarRepository = calendarRepository;
        this.outfitRepository = outfitRepository;
    }

    /**
     * Logs an outfit in the calendar for a specific date.
     */
    @PostMapping("/log")
    public ResponseEntity<?> logOutfit(@RequestBody Map<String, String> payload) {
        try {
            String userIdStr = payload.get("userId");
            String dateStr = payload.get("date");
            String outfitIdStr = payload.get("outfitId");

            if (userIdStr == null || dateStr == null || outfitIdStr == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "UserId, date, and outfitId are required."));
            }

            UUID userId = UUID.fromString(userIdStr);
            LocalDate date = LocalDate.parse(dateStr);
            UUID outfitId = UUID.fromString(outfitIdStr);

            Optional<Outfit> outfitOpt = outfitRepository.findById(outfitId);
            if (outfitOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Outfit not found."));
            }

            // Check if there is already a log for this date, delete or update it
            Optional<OutfitCalendar> existingLog = calendarRepository.findByUserIdAndDate(userId, date);
            existingLog.ifPresent(calendarRepository::delete);

            OutfitCalendar log = new OutfitCalendar(userId, date, outfitOpt.get());
            OutfitCalendar savedLog = calendarRepository.save(log);
            return ResponseEntity.ok(savedLog);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to log outfit: " + e.getMessage()));
        }
    }

    /**
     * Gets all calendar logs for a user.
     */
    @GetMapping("/logs")
    public ResponseEntity<?> getLogs(@RequestParam("userId") UUID userId) {
        List<OutfitCalendar> logs = calendarRepository.findByUserId(userId);
        return ResponseEntity.ok(logs);
    }

    /**
     * Deletes a calendar log.
     */
    @DeleteMapping("/log")
    public ResponseEntity<?> deleteLog(@RequestParam("userId") UUID userId, @RequestParam("date") String dateStr) {
        try {
            LocalDate date = LocalDate.parse(dateStr);
            Optional<OutfitCalendar> existingLog = calendarRepository.findByUserIdAndDate(userId, date);
            if (existingLog.isPresent()) {
                calendarRepository.delete(existingLog.get());
                return ResponseEntity.ok(Map.of("message", "Log deleted successfully."));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Log not found."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to delete log: " + e.getMessage()));
        }
    }
}
