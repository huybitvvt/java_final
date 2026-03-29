package com.bookstore.controller.support;

import com.bookstore.model.Promotion;
import com.bookstore.model.Voucher;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PromotionPresentationSupportTest {

    @Test
    void resolvesPromotionLifecycleForFutureAndExpiringPromotions() {
        Promotion futurePromotion = new Promotion();
        futurePromotion.setStatus("ACTIVE");
        futurePromotion.setStartDate(LocalDate.now().plusDays(2));
        futurePromotion.setEndDate(LocalDate.now().plusDays(10));

        Promotion expiringPromotion = new Promotion();
        expiringPromotion.setStatus("ACTIVE");
        expiringPromotion.setStartDate(LocalDate.now().minusDays(2));
        expiringPromotion.setEndDate(LocalDate.now().plusDays(3));

        assertEquals("Chưa bắt đầu", PromotionPresentationSupport.resolvePromotionLifecycle(futurePromotion));
        assertEquals("Sắp hết hạn", PromotionPresentationSupport.resolvePromotionLifecycle(expiringPromotion));
    }

    @Test
    void resolvesVoucherLifecycleWhenUsageLimitReached() {
        Voucher voucher = new Voucher();
        voucher.setStatus("ACTIVE");
        voucher.setStartDate(LocalDate.now().minusDays(1));
        voucher.setEndDate(LocalDate.now().plusDays(10));
        voucher.setUsageLimit(100);
        voucher.setUsedCount(100);

        assertEquals("Đã kết thúc", PromotionPresentationSupport.resolveVoucherLifecycle(voucher));
    }

    @Test
    void formatsAndValidatesNumericPromotionInputs() {
        assertEquals("12", PromotionPresentationSupport.trimDouble(12.0));
        assertEquals("12.5", PromotionPresentationSupport.trimDouble(12.5));
        assertEquals(1.0, PromotionPresentationSupport.resolvePromotionValue("BUY_X_GET_Y", ""), 0.0001);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> PromotionPresentationSupport.parseRequiredDouble("abc", "giá trị khuyến mãi")
        );

        assertEquals("giá trị khuyến mãi phải là số hợp lệ", exception.getMessage());
    }
}
