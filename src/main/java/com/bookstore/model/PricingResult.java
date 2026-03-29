package com.bookstore.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Aggregated pricing result for POS checkout.
 */
public class PricingResult {
    private double subtotal;
    private double promotionDiscount;
    private double voucherDiscount;
    private double loyaltyDiscount;
    private double totalDiscount;
    private double finalTotal;
    private double effectiveDiscountPercent;
    private Voucher voucher;
    private String loyaltyTier;
    private final List<String> appliedPromotions = new ArrayList<>();

    public double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal = subtotal;
    }

    public double getPromotionDiscount() {
        return promotionDiscount;
    }

    public void setPromotionDiscount(double promotionDiscount) {
        this.promotionDiscount = promotionDiscount;
    }

    public double getVoucherDiscount() {
        return voucherDiscount;
    }

    public void setVoucherDiscount(double voucherDiscount) {
        this.voucherDiscount = voucherDiscount;
    }

    public double getLoyaltyDiscount() {
        return loyaltyDiscount;
    }

    public void setLoyaltyDiscount(double loyaltyDiscount) {
        this.loyaltyDiscount = loyaltyDiscount;
    }

    public double getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(double totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public double getFinalTotal() {
        return finalTotal;
    }

    public void setFinalTotal(double finalTotal) {
        this.finalTotal = finalTotal;
    }

    public double getEffectiveDiscountPercent() {
        return effectiveDiscountPercent;
    }

    public void setEffectiveDiscountPercent(double effectiveDiscountPercent) {
        this.effectiveDiscountPercent = effectiveDiscountPercent;
    }

    public Voucher getVoucher() {
        return voucher;
    }

    public void setVoucher(Voucher voucher) {
        this.voucher = voucher;
    }

    public String getLoyaltyTier() {
        return loyaltyTier;
    }

    public void setLoyaltyTier(String loyaltyTier) {
        this.loyaltyTier = loyaltyTier;
    }

    public List<String> getAppliedPromotions() {
        return Collections.unmodifiableList(appliedPromotions);
    }

    public void addAppliedPromotion(String promotion) {
        if (promotion != null && !promotion.isBlank()) {
            appliedPromotions.add(promotion);
        }
    }
}
