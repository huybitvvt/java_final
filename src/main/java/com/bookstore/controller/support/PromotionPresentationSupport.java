package com.bookstore.controller.support;

import com.bookstore.model.Promotion;
import com.bookstore.model.Voucher;
import com.bookstore.util.FormatUtil;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

/**
 * Shared presentation and formatting helpers for the promotion center.
 */
public final class PromotionPresentationSupport {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MM/yyyy", new Locale("vi", "VN"));

    private PromotionPresentationSupport() {
    }

    public static boolean matchesPromotionKeyword(Promotion promotion, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return true;
        }
        return containsIgnoreCase(promotion.getName(), keyword)
                || containsIgnoreCase(promotion.getDescription(), keyword)
                || containsIgnoreCase(promotion.getCategory(), keyword)
                || containsIgnoreCase(formatPromotionType(promotion), keyword);
    }

    public static boolean matchesVoucherKeyword(Voucher voucher, String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return true;
        }
        return containsIgnoreCase(voucher.getCode(), keyword)
                || containsIgnoreCase(formatVoucherType(voucher), keyword)
                || containsIgnoreCase(resolveVoucherLifecycle(voucher), keyword);
    }

    public static boolean matchesStatusFilter(String lifecycle, String filter) {
        return filter == null || "Tất cả trạng thái".equals(filter) || filter.equalsIgnoreCase(lifecycle);
    }

    public static String resolveBestOfferMetric(List<Promotion> promotions, List<Voucher> vouchers) {
        double bestPercent = promotions.stream()
                .filter(promotion -> promotion.isActive(LocalDate.now()))
                .filter(promotion -> "PERCENT_ORDER".equalsIgnoreCase(promotion.getType())
                        || "CATEGORY_PERCENT".equalsIgnoreCase(promotion.getType()))
                .mapToDouble(Promotion::getValue)
                .max()
                .orElse(0);

        bestPercent = Math.max(bestPercent, vouchers.stream()
                .filter(voucher -> voucher.isAvailable(LocalDate.now()))
                .filter(voucher -> "PERCENT".equalsIgnoreCase(voucher.getType()))
                .mapToDouble(Voucher::getValue)
                .max()
                .orElse(0));

        if (bestPercent > 0) {
            return trimDouble(bestPercent) + "%";
        }

        double bestFixed = vouchers.stream()
                .filter(voucher -> voucher.isAvailable(LocalDate.now()))
                .filter(voucher -> "FIXED".equalsIgnoreCase(voucher.getType()))
                .mapToDouble(Voucher::getValue)
                .max()
                .orElse(0);

        if (bestFixed > 0) {
            return FormatUtil.formatCurrency(bestFixed);
        }

        return "Mua 2 tặng 1";
    }

    public static String buildEngineSummary(List<Promotion> activePromotions) {
        List<String> promotionNames = activePromotions.stream()
                .map(Promotion::getName)
                .limit(4)
                .toList();
        return promotionNames.isEmpty()
                ? "POS hiện chưa có khuyến mãi tự động nào đang bật."
                : "POS đang tự áp dụng: " + String.join(" • ", promotionNames);
    }

    public static String buildVoucherHighlights(List<Voucher> vouchers) {
        List<String> voucherCodes = vouchers.stream()
                .filter(voucher -> voucher.isAvailable(LocalDate.now()))
                .map(Voucher::getCode)
                .limit(5)
                .toList();
        return voucherCodes.isEmpty()
                ? "Hiện chưa có voucher khả dụng."
                : "Mã đang khả dụng: " + String.join(", ", voucherCodes);
    }

    public static <T> TableCell<T, String> createStatusBadgeCell() {
        return new TableCell<>() {
            private final Label badge = new Label();

            {
                badge.getStyleClass().add("status-badge");
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || item.isBlank()) {
                    setGraphic(null);
                    return;
                }

                badge.setText(item);
                badge.getStyleClass().removeAll(
                        "status-badge-success",
                        "status-badge-warning",
                        "status-badge-danger",
                        "status-badge-info",
                        "status-badge-neutral"
                );
                badge.getStyleClass().add(resolveStatusStyle(item));
                setGraphic(badge);
            }
        };
    }

    public static String resolvePromotionLifecycle(Promotion promotion) {
        LocalDate today = LocalDate.now();
        if (!"ACTIVE".equalsIgnoreCase(promotion.getStatus())) {
            return "Tạm dừng";
        }
        if (promotion.getStartDate() != null && today.isBefore(promotion.getStartDate())) {
            return "Chưa bắt đầu";
        }
        if (promotion.getEndDate() != null && today.isAfter(promotion.getEndDate())) {
            return "Đã kết thúc";
        }
        if (isExpiringSoon(promotion.getEndDate())) {
            return "Sắp hết hạn";
        }
        return "Đang hoạt động";
    }

    public static String resolveVoucherLifecycle(Voucher voucher) {
        LocalDate today = LocalDate.now();
        if (!"ACTIVE".equalsIgnoreCase(voucher.getStatus())) {
            return "Tạm dừng";
        }
        if (voucher.getStartDate() != null && today.isBefore(voucher.getStartDate())) {
            return "Chưa bắt đầu";
        }
        if (voucher.getEndDate() != null && today.isAfter(voucher.getEndDate())) {
            return "Đã kết thúc";
        }
        if (voucher.getUsageLimit() > 0 && voucher.getUsedCount() >= voucher.getUsageLimit()) {
            return "Đã kết thúc";
        }
        if (isExpiringSoon(voucher.getEndDate())) {
            return "Sắp hết hạn";
        }
        return "Đang hoạt động";
    }

    public static String resolvePromotionCondition(Promotion promotion) {
        String type = promotion == null ? null : promotion.getType();
        if (type == null || type.isBlank()) {
            return "Quy tắc tùy chỉnh.";
        }
        return switch (type) {
            case "PERCENT_ORDER" -> "Áp dụng cho toàn đơn đủ điều kiện trong thời gian hiệu lực.";
            case "CATEGORY_PERCENT" -> "Áp dụng cho thể loại " + safeValue(promotion.getCategory()) + ".";
            case "BUY_X_GET_Y" -> "Mua tối thiểu "
                    + (promotion.getMinimumQuantity() == null ? 2 : promotion.getMinimumQuantity())
                    + " cuốn phù hợp để được tặng thêm.";
            default -> "Quy tắc tùy chỉnh.";
        };
    }

    public static String resolvePromotionTypeCode(String label) {
        if (label == null || label.isBlank()) {
            return "PERCENT_ORDER";
        }
        return switch (label) {
            case "Giảm % theo thể loại" -> "CATEGORY_PERCENT";
            case "Mua X tặng Y" -> "BUY_X_GET_Y";
            default -> "PERCENT_ORDER";
        };
    }

    public static String resolvePromotionTypeLabel(String code) {
        if (code == null || code.isBlank()) {
            return "Giảm % toàn đơn";
        }
        return switch (code) {
            case "CATEGORY_PERCENT" -> "Giảm % theo thể loại";
            case "BUY_X_GET_Y" -> "Mua X tặng Y";
            case "PERCENT_ORDER" -> "Giảm % toàn đơn";
            default -> "Giảm % toàn đơn";
        };
    }

    public static String formatPromotionType(Promotion promotion) {
        String type = promotion == null ? null : promotion.getType();
        if (type == null || type.isBlank()) {
            return "Toàn đơn";
        }
        return switch (type) {
            case "PERCENT_ORDER" -> "Toàn đơn";
            case "CATEGORY_PERCENT" -> "Theo thể loại";
            case "BUY_X_GET_Y" -> "Mua X tặng Y";
            default -> safeValue(promotion.getType());
        };
    }

    public static String formatPromotionValue(Promotion promotion) {
        String type = promotion == null ? null : promotion.getType();
        if (type == null || type.isBlank()) {
            return promotion == null ? "0" : trimDouble(promotion.getValue());
        }
        return switch (type) {
            case "PERCENT_ORDER" -> "Giảm " + trimDouble(promotion.getValue()) + "% toàn đơn";
            case "CATEGORY_PERCENT" -> "Giảm " + trimDouble(promotion.getValue()) + "% cho " + safeValue(promotion.getCategory());
            case "BUY_X_GET_Y" -> "Mua " + (promotion.getMinimumQuantity() == null ? 2 : promotion.getMinimumQuantity()) + " tặng 1";
            default -> trimDouble(promotion.getValue());
        };
    }

    public static String formatVoucherType(Voucher voucher) {
        String type = voucher == null ? null : voucher.getType();
        if (type == null || type.isBlank()) {
            return "Phần trăm";
        }
        return switch (type) {
            case "PERCENT" -> "Phần trăm";
            case "FIXED" -> "Trừ trực tiếp";
            default -> safeValue(voucher.getType());
        };
    }

    public static String resolveVoucherTypeCode(String label) {
        return "Trừ trực tiếp".equals(label) ? "FIXED" : "PERCENT";
    }

    public static String resolveVoucherTypeLabel(String code) {
        if (code == null || code.isBlank()) {
            return "Phần trăm";
        }
        return "FIXED".equalsIgnoreCase(code) ? "Trừ trực tiếp" : "Phần trăm";
    }

    public static String formatVoucherValue(Voucher voucher) {
        String type = voucher == null ? null : voucher.getType();
        if (type == null || type.isBlank()) {
            return voucher == null ? "0" : trimDouble(voucher.getValue());
        }
        return switch (type) {
            case "PERCENT" -> "Giảm " + trimDouble(voucher.getValue()) + "%";
            case "FIXED" -> "Giảm " + FormatUtil.formatCurrency(voucher.getValue());
            default -> trimDouble(voucher.getValue());
        };
    }

    public static String formatVoucherUsage(Voucher voucher) {
        if (voucher.getUsageLimit() <= 0) {
            return "Không giới hạn lượt";
        }
        return "Đã dùng " + voucher.getUsedCount() + "/" + voucher.getUsageLimit() + " lượt";
    }

    public static String formatPeriod(LocalDate startDate, LocalDate endDate) {
        String start = startDate == null ? "Bắt đầu ngay" : DATE_FORMATTER.format(startDate);
        String end = endDate == null ? "Không giới hạn" : DATE_FORMATTER.format(endDate);
        return start + " - " + end;
    }

    public static String trimDouble(double value) {
        if (Math.rint(value) == value) {
            return String.valueOf((long) value);
        }
        return String.format(Locale.US, "%.1f", value);
    }

    public static double resolvePromotionValue(String promotionType, String rawValue) {
        if ("BUY_X_GET_Y".equalsIgnoreCase(promotionType)) {
            return 1;
        }
        return parseRequiredDouble(rawValue, "giá trị khuyến mãi");
    }

    public static double parseRequiredDouble(String rawValue, String fieldName) {
        String value = safeText(rawValue).trim();
        if (value.isBlank()) {
            throw new IllegalArgumentException("Vui lòng nhập " + fieldName);
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " phải là số hợp lệ");
        }
    }

    public static Integer parseOptionalInt(String rawValue, String fieldName) {
        String value = safeText(rawValue).trim();
        if (value.isBlank()) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " phải là số nguyên hợp lệ");
        }
    }

    public static int parseRequiredNonNegativeInt(String rawValue, String fieldName) {
        String value = safeText(rawValue).trim();
        if (value.isBlank()) {
            throw new IllegalArgumentException("Vui lòng nhập " + fieldName);
        }
        try {
            int parsed = Integer.parseInt(value);
            if (parsed < 0) {
                throw new IllegalArgumentException(fieldName + " không được âm");
            }
            return parsed;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(fieldName + " phải là số nguyên hợp lệ");
        }
    }

    public static String safeText(String value) {
        return value == null ? "" : value;
    }

    public static String parseOptionalText(String value) {
        String trimmed = safeText(value).trim();
        return trimmed.isBlank() ? null : trimmed;
    }

    public static Promotion copyPromotion(Promotion source) {
        Promotion copy = new Promotion();
        copy.setId(source.getId());
        copy.setName(source.getName());
        copy.setType(source.getType());
        copy.setValue(source.getValue());
        copy.setCategory(source.getCategory());
        copy.setMinimumQuantity(source.getMinimumQuantity());
        copy.setStartDate(source.getStartDate());
        copy.setEndDate(source.getEndDate());
        copy.setStatus(source.getStatus());
        copy.setDescription(source.getDescription());
        return copy;
    }

    public static Voucher copyVoucher(Voucher source) {
        Voucher copy = new Voucher();
        copy.setId(source.getId());
        copy.setCode(source.getCode());
        copy.setType(source.getType());
        copy.setValue(source.getValue());
        copy.setMinimumOrderValue(source.getMinimumOrderValue());
        copy.setStartDate(source.getStartDate());
        copy.setEndDate(source.getEndDate());
        copy.setUsageLimit(source.getUsageLimit());
        copy.setUsedCount(source.getUsedCount());
        copy.setStatus(source.getStatus());
        return copy;
    }

    private static boolean containsIgnoreCase(String source, String keyword) {
        return source != null && source.toLowerCase(Locale.ROOT).contains(keyword);
    }

    private static String safeValue(String value) {
        return value == null || value.isBlank() ? "Mặc định" : value;
    }

    private static boolean isExpiringSoon(LocalDate endDate) {
        LocalDate today = LocalDate.now();
        return endDate != null && !today.isAfter(endDate) && !endDate.isAfter(today.plusDays(7));
    }

    private static String resolveStatusStyle(String status) {
        return switch (status) {
            case "Đang hoạt động" -> "status-badge-success";
            case "Sắp hết hạn" -> "status-badge-warning";
            case "Tạm dừng", "Đã kết thúc" -> "status-badge-danger";
            case "Chưa bắt đầu" -> "status-badge-info";
            default -> "status-badge-neutral";
        };
    }
}
