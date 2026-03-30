package com.moo.inventory;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public class InventoryStore {

    private final InventoryItemRepository repository;

    public InventoryStore(InventoryItemRepository repository) {
        this.repository = repository;
    }

    public Optional<InventoryItem> find(String productId) {
        return repository.findById(productId);
    }

    public List<InventoryItem> findAll() {
        return repository.findAll();
    }

    /**
     * Atomically reserves the requested quantity.
     *
     * @return remaining stock on success, or -1 if insufficient
     */
    @Transactional
    public int reserveStock(String productId, int quantity) {
        Optional<InventoryItem> opt = repository.findById(productId);
        if (opt.isEmpty()) return -1;

        InventoryItem item = opt.get();
        if (item.getQuantity() < quantity) return -1;

        int remaining = item.getQuantity() - quantity;
        item.setQuantity(remaining);
        repository.save(item);
        return remaining;
    }
}