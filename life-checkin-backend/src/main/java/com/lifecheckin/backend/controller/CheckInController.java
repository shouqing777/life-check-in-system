package com.lifecheckin.backend.controller;

import com.lifecheckin.backend.exception.ResourceNotFoundException;
import com.lifecheckin.backend.model.CheckIn;
import com.lifecheckin.backend.service.CheckInService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/checkins")
@Tag(name = "CheckIn API", description = "API for managing check-in records")
public class CheckInController {

    @Autowired
    private CheckInService checkInService;

    @GetMapping
    @Operation(summary = "Get all check-ins", description = "Retrieve a list of all check-in records")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CheckIn.class)))
    public ResponseEntity<List<CheckIn>> getAllCheckIns() {
        return ResponseEntity.ok(checkInService.getAllCheckIns());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a check-in by ID", description = "Retrieve a single check-in record by its ID")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved check-in",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CheckIn.class)))
    @ApiResponse(responseCode = "404", description = "CheckIn not found", content = @Content)
    public ResponseEntity<CheckIn> getCheckInById(@PathVariable Long id) {
        CheckIn checkIn = checkInService.getCheckInById(id)
                .orElseThrow(() -> new ResourceNotFoundException("CheckIn not found with id: " + id));
        return ResponseEntity.ok(checkIn);
    }

    @PostMapping
    @Operation(summary = "Create a new check-in", description = "Create a new check-in record")
    @ApiResponse(responseCode = "201", description = "CheckIn created successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CheckIn.class)))
    public ResponseEntity<CheckIn> createCheckIn(@RequestBody CheckIn checkIn) {
        CheckIn savedCheckIn = checkInService.createCheckIn(checkIn);
        return ResponseEntity.status(201).body(savedCheckIn);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing check-in", description = "Update an existing check-in record by ID")
    @ApiResponse(responseCode = "200", description = "CheckIn updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = CheckIn.class)))
    @ApiResponse(responseCode = "404", description = "CheckIn not found", content = @Content)
    public ResponseEntity<CheckIn> updateCheckIn(@PathVariable Long id, @RequestBody CheckIn updatedCheckIn) {
        CheckIn checkIn = checkInService.updateCheckIn(id, updatedCheckIn);
        return ResponseEntity.ok(checkIn);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a check-in", description = "Delete a check-in record by its ID")
    @ApiResponse(responseCode = "204", description = "CheckIn deleted successfully", content = @Content)
    @ApiResponse(responseCode = "404", description = "CheckIn not found", content = @Content)
    public ResponseEntity<Void> deleteCheckIn(@PathVariable Long id) {
        checkInService.deleteCheckIn(id);
        return ResponseEntity.noContent().build();
    }
}
