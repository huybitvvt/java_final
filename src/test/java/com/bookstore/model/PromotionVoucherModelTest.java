package com.bookstore.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PromotionVoucherModelTest {

    @Test
    void promotionIsActiveOnlyInsideEffectiveWindow() {
        Promotion promotion = new Promotion();
        promotion.setStatus("ACTIVE");
        promotion.setStartDate(LocalDate.of(2026, 3, 1));
        promotion.setEndDate(LocalDate.of(2026, 3, 31));

        assertFalse(promotion.isActive(LocalDate.of(2026, 2, 28)));
        assertTrue(promotion.isActive(LocalDate.of(2026, 3, 20)));
        assertFalse(promotion.isActive(LocalDate.of(2026, 4, 1)));
    }

    @Test
    void voucherAvailabilityRespectsUsageLimitAndDateWindow() {
        Voucher voucher = new Voucher();
        voucher.setStatus("ACTIVE");
        voucher.setStartDate(LocalDate.of(2026, 3, 1));
        voucher.setEndDate(LocalDate.of(2026, 3, 31));
        voucher.setUsageLimit(5);
        voucher.setUsedCount(5);

        assertFalse(voucher.isAvailable(LocalDate.of(2026, 3, 20)));

        voucher.setUsedCount(4);
        assertTrue(voucher.isAvailable(LocalDate.of(2026, 3, 20)));
        assertFalse(voucher.isAvailable(LocalDate.of(2026, 4, 1)));
    }
}
