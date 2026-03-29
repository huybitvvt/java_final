package com.bookstore.service;

import com.bookstore.model.Customer;
import com.bookstore.model.InvoiceDetail;
import com.bookstore.model.PricingResult;
import com.bookstore.model.Voucher;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Centralized checkout pricing pipeline.
 */
public class PricingService {

    private final PromotionService promotionService;
    private final VoucherService voucherService;
    private final LoyaltyService loyaltyService;

    public PricingService() {
        this.promotionService = new PromotionService();
        this.voucherService = new VoucherService();
        this.loyaltyService = new LoyaltyService();
    }

    public PricingResult calculate(List<InvoiceDetail> cart, Customer customer, String voucherCode) {
        PricingResult result = new PricingResult();
        double subtotal = cart.stream().mapToDouble(InvoiceDetail::getThanhTien).sum();
        result.setSubtotal(subtotal);

        if (subtotal <= 0) {
            result.setLoyaltyTier(loyaltyService.resolveTier(customer));
            return result;
        }

        List<String> appliedPromotions = new ArrayList<>();
        double promotionDiscount = promotionService.calculatePromotionDiscount(cart, LocalDate.now(), appliedPromotions);
        result.setPromotionDiscount(Math.min(subtotal, promotionDiscount));
        appliedPromotions.forEach(result::addAppliedPromotion);

        double afterPromotion = Math.max(0, subtotal - result.getPromotionDiscount());
        Voucher voucher = voucherService.validateVoucher(voucherCode, afterPromotion);
        double voucherDiscount = voucherService.calculateDiscount(voucher, afterPromotion);
        result.setVoucher(voucher);
        result.setVoucherDiscount(Math.min(afterPromotion, voucherDiscount));

        double afterVoucher = Math.max(0, afterPromotion - result.getVoucherDiscount());
        double loyaltyPercent = loyaltyService.getDiscountPercent(customer);
        result.setLoyaltyTier(loyaltyService.resolveTier(customer));
        result.setLoyaltyDiscount(afterVoucher * loyaltyPercent / 100);

        double totalDiscount = result.getPromotionDiscount() + result.getVoucherDiscount() + result.getLoyaltyDiscount();
        result.setTotalDiscount(totalDiscount);
        result.setFinalTotal(Math.max(0, subtotal - totalDiscount));
        result.setEffectiveDiscountPercent(subtotal == 0 ? 0 : totalDiscount * 100 / subtotal);

        return result;
    }
}
