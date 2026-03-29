package com.bookstore.controller.support;

import com.bookstore.model.Customer;
import com.bookstore.model.InvoiceDetail;
import com.bookstore.model.PricingResult;
import com.bookstore.model.Voucher;
import com.bookstore.service.VoucherService;
import com.bookstore.util.FormatUtil;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.util.List;

/**
 * View-state helpers for the POS pricing summary.
 */
public final class PosPricingViewSupport {

    private PosPricingViewSupport() {
    }

    public static void applyPricingState(
            ObservableList<InvoiceDetail> cartList,
            PricingResult pricingResult,
            Customer selectedCustomer,
            String appliedVoucherCode,
            TextField txtVoucherCode,
            VoucherService voucherService,
            Label lblCartCount,
            Label lblTongTien,
            Label lblPromotionDiscount,
            Label lblVoucherDiscount,
            Label lblLoyaltyDiscount,
            Label lblThanhToan,
            Label lblVoucherStatus,
            Label lblAppliedPromotions,
            Label lblCustomerTier,
            Label lblVoucherCodeInfo
    ) {
        lblTongTien.setText(FormatUtil.formatCurrency(pricingResult.getSubtotal()));
        lblPromotionDiscount.setText("- " + FormatUtil.formatCurrency(pricingResult.getPromotionDiscount()));
        lblVoucherDiscount.setText("- " + FormatUtil.formatCurrency(pricingResult.getVoucherDiscount()));
        lblLoyaltyDiscount.setText("- " + FormatUtil.formatCurrency(pricingResult.getLoyaltyDiscount()));
        lblThanhToan.setText(FormatUtil.formatCurrency(pricingResult.getFinalTotal()));

        int quantity = cartList.stream().mapToInt(InvoiceDetail::getSoLuong).sum();
        lblCartCount.setText(quantity + " cuốn trong giỏ");

        lblCustomerTier.setText(selectedCustomer == null
                ? "Khách lẻ"
                : formatTierLabel(pricingResult.getLoyaltyTier()));

        if (pricingResult.getAppliedPromotions().isEmpty()) {
            lblAppliedPromotions.setText("Khuyến mãi áp dụng: Chưa có");
        } else {
            lblAppliedPromotions.setText("Khuyến mãi áp dụng: " + String.join(", ", pricingResult.getAppliedPromotions()));
        }

        updateVoucherStatusLabel(lblVoucherStatus, appliedVoucherCode, pricingResult.getVoucher());
        updateVoucherCodeInfo(lblVoucherCodeInfo, pricingResult.getVoucher(), txtVoucherCode, voucherService);
    }

    public static String formatTierLabel(String tier) {
        if (tier == null || tier.isBlank() || "Standard".equalsIgnoreCase(tier)) {
            return "Thành viên tiêu chuẩn";
        }
        return switch (tier) {
            case "Silver" -> "Hạng Bạc";
            case "Gold" -> "Hạng Vàng";
            case "Diamond" -> "Hạng Kim cương";
            default -> tier;
        };
    }

    private static void updateVoucherStatusLabel(Label lblVoucherStatus, String appliedVoucherCode, Voucher voucher) {
        lblVoucherStatus.getStyleClass().removeAll("status-text-success", "status-text-error", "status-text-muted");
        if (appliedVoucherCode == null || appliedVoucherCode.isBlank()) {
            lblVoucherStatus.setText("Chưa áp dụng mã giảm giá.");
            lblVoucherStatus.getStyleClass().add("status-text-muted");
            return;
        }

        if (voucher != null) {
            lblVoucherStatus.setText("Đã áp dụng mã " + voucher.getCode() + " thành công.");
            lblVoucherStatus.getStyleClass().add("status-text-success");
        } else {
            lblVoucherStatus.setText("Mã giảm giá không hợp lệ hoặc chưa đủ điều kiện đơn hàng.");
            lblVoucherStatus.getStyleClass().add("status-text-error");
        }
    }

    private static void updateVoucherCodeInfo(
            Label lblVoucherCodeInfo,
            Voucher appliedVoucher,
            TextField txtVoucherCode,
            VoucherService voucherService
    ) {
        if (lblVoucherCodeInfo == null) {
            return;
        }

        lblVoucherCodeInfo.getStyleClass().removeAll("status-text-success", "status-text-error", "status-text-muted");
        if (appliedVoucher != null && appliedVoucher.getCode() != null && !appliedVoucher.getCode().isBlank()) {
            lblVoucherCodeInfo.setText("Mã đang áp dụng: " + appliedVoucher.getCode());
            lblVoucherCodeInfo.getStyleClass().add("status-text-success");
            return;
        }

        String pendingCode = txtVoucherCode != null && txtVoucherCode.getText() != null
                ? txtVoucherCode.getText().trim().toUpperCase()
                : "";
        if (!pendingCode.isBlank()) {
            lblVoucherCodeInfo.setText("Mã đang nhập: " + pendingCode);
            lblVoucherCodeInfo.getStyleClass().add("status-text-muted");
            return;
        }

        List<String> availableCodes = voucherService.getAllVouchers().stream()
                .filter(voucher -> voucher != null && voucher.isAvailable(LocalDate.now()))
                .map(Voucher::getCode)
                .filter(code -> code != null && !code.isBlank())
                .limit(4)
                .toList();
        lblVoucherCodeInfo.setText(availableCodes.isEmpty()
                ? "Hiện chưa có mã giảm giá khả dụng."
                : "Mã khả dụng: " + String.join(", ", availableCodes));
        lblVoucherCodeInfo.getStyleClass().add("status-text-muted");
    }
}
