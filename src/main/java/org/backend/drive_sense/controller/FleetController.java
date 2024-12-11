package org.backend.drive_sense.controller;

import org.backend.drive_sense.dto.FleetDTO;
import org.backend.drive_sense.service.FleetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v0/fleets")
public class FleetController {

    private final FleetService fleetService;

    public FleetController(FleetService fleetService) {
        this.fleetService = fleetService;
    }

    @PostMapping
    public ResponseEntity<FleetDTO> createFleet(@RequestBody FleetDTO fleetDTO) {
        FleetDTO createdFleet = fleetService.createFleet(fleetDTO);
        return ResponseEntity.ok(createdFleet);
    }

    @PostMapping("/{fleetId}/vehicles/{vehicleId}")
    public ResponseEntity<FleetDTO> assignVehicleToFleet(@PathVariable UUID fleetId, @PathVariable UUID vehicleId) {
        FleetDTO updatedFleet = fleetService.assignVehicleToFleet(fleetId, vehicleId);
        return ResponseEntity.ok(updatedFleet);
    }

    @GetMapping("/manager/{managerId}")
    public ResponseEntity<FleetDTO> getFleetsByManager(@PathVariable UUID managerId) {
        FleetDTO fleet = fleetService.getFleetByManager(managerId);
        return ResponseEntity.ok(fleet);
    }
}
