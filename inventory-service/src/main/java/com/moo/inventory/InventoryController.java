package com.moo.inventory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Random;

@RestController
@RequestMapping("/inventory")
public class InventoryController {

    private static final Logger log = LoggerFactory.getLogger(InventoryController.class);
    private static final Random RANDOM = new Random();

    private final InventoryStore store;

    public InventoryController(InventoryStore store) {
        this.store = store;
    }

    @GetMapping()
    public ResponseEntity<List<InventoryItem>> getInventory() {
        return ResponseEntity.ok(store.findAll());
    }

    @GetMapping("/{productId}")
    public ResponseEntity<InventoryItem> getInventory(@PathVariable String productId) {
        return store.find(productId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{productId}/reserve")
    public ResponseEntity<Map<String, Object>> reserve(
            @PathVariable String productId,
            @RequestBody ReservationRequest request) {

        // Simulate a flaky downstream dependency for the test's resilience scenario
        if ("flaky-item".equals(productId) && RANDOM.nextDouble() < 0.4) {
            log.warn("Simulating transient failure for flaky-item");
            return ResponseEntity.status(503)
                    .body(Map.of("error", "Service temporarily unavailable — please retry"));
        }

        var item = store.find(productId);
        if (item.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        int remaining = store.reserveStock(productId, request.quantity());
        if (remaining < 0) {
            int available = store.find(productId).map(InventoryItem::getQuantity).orElse(0);
            return ResponseEntity.status(409)
                    .body(Map.of(
                            "reserved", false,
                            "reason", "Insufficient stock. Available: " + available
                    ));
        }

        log.info("Reserved {} of {} — {} remaining", request.quantity(), productId, remaining);
        return ResponseEntity.ok(Map.of("reserved", true, "remaining", remaining));
    }
}
