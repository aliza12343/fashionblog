package org.example.capstone2.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.capstone2.dto.ContactDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@Tag(name = "Contact", description = "Studio consultation inquiry submission")
@RestController
@RequestMapping("/api/contact")
public class ContactController {

    @Operation(summary = "Submit a consultation inquiry")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Inquiry received"),
        @ApiResponse(responseCode = "400", description = "Validation error")
    })
    @PostMapping
    public ResponseEntity<Map<String, String>> submitInquiry(@Valid @RequestBody ContactDTO dto) {
        log.info("Consultation inquiry from {} <{}>: {}", dto.getFirstName(), dto.getEmail(), dto.getService());
        return ResponseEntity.ok(Map.of(
                "message", "Thank you, " + dto.getFirstName() + ". Our studio will be in touch within 24 hours."
        ));
    }
}
