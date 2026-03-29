package com.bookstore.service;

import com.bookstore.dao.PromotionDAO;
import com.bookstore.model.Book;
import com.bookstore.model.InvoiceDetail;
import com.bookstore.model.Promotion;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Promotion business rules and fallback data.
 */
public class PromotionService {

    private static final List<Promotion> FALLBACK_PROMOTIONS = new ArrayList<>();
    private static int fallbackSequence = 1000;

    private final PromotionDAO promotionDAO;

    public PromotionService() {
        this.promotionDAO = new PromotionDAO();
        initializeFallbackIfNeeded();
    }

    public List<Promotion> getAllPromotions() {
        try {
            return promotionDAO.getAll();
        } catch (RuntimeException ignored) {
        }
        return new ArrayList<>(FALLBACK_PROMOTIONS);
    }

    public List<Promotion> getActivePromotions(LocalDate date) {
        return getAllPromotions().stream()
                .filter(promotion -> promotion.isActive(date))
                .sorted(Comparator.comparing(Promotion::getName))
                .toList();
    }

    public double calculatePromotionDiscount(List<InvoiceDetail> cart, LocalDate date, List<String> appliedLabels) {
        double discount = 0;
        for (Promotion promotion : getActivePromotions(date)) {
            switch (promotion.getType()) {
                case "PERCENT_ORDER" -> {
                    double subtotal = cart.stream().mapToDouble(InvoiceDetail::getThanhTien).sum();
                    double amount = subtotal * promotion.getValue() / 100;
                    if (amount > 0) {
                        discount += amount;
                        appliedLabels.add(promotion.getName());
                    }
                }
                case "CATEGORY_PERCENT" -> {
                    double categoryTotal = cart.stream()
                            .filter(detail -> detail.getSach() != null)
                            .filter(detail -> promotion.getCategory() != null
                                    && promotion.getCategory().equalsIgnoreCase(detail.getSach().getTheLoai()))
                            .mapToDouble(InvoiceDetail::getThanhTien)
                            .sum();
                    if (categoryTotal > 0) {
                        discount += categoryTotal * promotion.getValue() / 100;
                        appliedLabels.add(promotion.getName());
                    }
                }
                case "BUY_X_GET_Y" -> {
                    for (InvoiceDetail detail : cart) {
                        Book book = detail.getSach();
                        if (book == null) {
                            continue;
                        }
                        if (promotion.getCategory() != null && !promotion.getCategory().isBlank()
                                && !promotion.getCategory().equalsIgnoreCase(book.getTheLoai())) {
                            continue;
                        }
                        int minimumQuantity = promotion.getMinimumQuantity() == null ? 2 : promotion.getMinimumQuantity();
                        if (detail.getSoLuong() >= minimumQuantity + 1) {
                            int freeUnits = detail.getSoLuong() / (minimumQuantity + 1);
                            discount += freeUnits * detail.getDonGia();
                            appliedLabels.add(promotion.getName() + " - " + book.getTenSach());
                        }
                    }
                }
                default -> {
                    // ignore unknown types
                }
            }
        }
        return discount;
    }

    public boolean addPromotion(Promotion promotion) {
        validatePromotion(promotion);
        try {
            return promotionDAO.insert(promotion);
        } catch (RuntimeException ignored) {
            promotion.setId(++fallbackSequence);
            FALLBACK_PROMOTIONS.add(promotion);
            return true;
        }
    }

    public boolean updatePromotion(Promotion promotion) {
        validatePromotion(promotion);
        try {
            return promotionDAO.update(promotion);
        } catch (RuntimeException ignored) {
            for (int i = 0; i < FALLBACK_PROMOTIONS.size(); i++) {
                if (FALLBACK_PROMOTIONS.get(i).getId() == promotion.getId()) {
                    FALLBACK_PROMOTIONS.set(i, promotion);
                    return true;
                }
            }
            return false;
        }
    }

    public boolean deletePromotion(int id) {
        try {
            return promotionDAO.delete(id);
        } catch (RuntimeException ignored) {
            return FALLBACK_PROMOTIONS.removeIf(promotion -> promotion.getId() == id);
        }
    }

    private void validatePromotion(Promotion promotion) {
        if (promotion == null) {
            throw new IllegalArgumentException("Khuyến mãi không hợp lệ");
        }
        String promotionType = promotion.getType() == null ? "" : promotion.getType().trim();
        if (promotion.getName() == null || promotion.getName().isBlank()) {
            throw new IllegalArgumentException("Tên khuyến mãi không được để trống");
        }
        if (promotionType.isBlank()) {
            throw new IllegalArgumentException("Loại khuyến mãi không được để trống");
        }
        if (!"BUY_X_GET_Y".equalsIgnoreCase(promotionType) && promotion.getValue() <= 0) {
            throw new IllegalArgumentException("Giá trị khuyến mãi phải lớn hơn 0");
        }
        if ("BUY_X_GET_Y".equalsIgnoreCase(promotionType) && promotion.getValue() <= 0) {
            promotion.setValue(1);
        }
        if (promotion.getStartDate() != null && promotion.getEndDate() != null
                && promotion.getEndDate().isBefore(promotion.getStartDate())) {
            throw new IllegalArgumentException("Ngày kết thúc không được trước ngày bắt đầu");
        }
        if ("CATEGORY_PERCENT".equalsIgnoreCase(promotionType)
                && (promotion.getCategory() == null || promotion.getCategory().isBlank())) {
            throw new IllegalArgumentException("Khuyến mãi theo thể loại cần chọn thể loại");
        }
        if ("BUY_X_GET_Y".equalsIgnoreCase(promotionType)
                && (promotion.getMinimumQuantity() == null || promotion.getMinimumQuantity() < 1)) {
            throw new IllegalArgumentException("Mua X tặng Y cần số lượng mua tối thiểu hợp lệ");
        }
    }

    private void initializeFallbackIfNeeded() {
        if (!FALLBACK_PROMOTIONS.isEmpty()) {
            return;
        }
        FALLBACK_PROMOTIONS.addAll(createFallbackPromotions());
        FALLBACK_PROMOTIONS.stream()
                .mapToInt(Promotion::getId)
                .max()
                .ifPresent(maxId -> fallbackSequence = Math.max(fallbackSequence, maxId));
    }

    private List<Promotion> createFallbackPromotions() {
        List<Promotion> promotions = new ArrayList<>();

        Promotion order = new Promotion();
        order.setId(1);
        order.setName("Giảm 10% cuối tuần");
        order.setType("PERCENT_ORDER");
        order.setValue(10);
        order.setStartDate(LocalDate.now().minusDays(30));
        order.setEndDate(LocalDate.now().plusDays(30));
        order.setStatus("ACTIVE");
        order.setDescription("Tự động áp dụng cho đơn hàng hợp lệ.");
        promotions.add(order);

        Promotion tech = new Promotion();
        tech.setId(2);
        tech.setName("Khuyến mãi sách Công nghệ");
        tech.setType("CATEGORY_PERCENT");
        tech.setValue(12);
        tech.setCategory("Công nghệ");
        tech.setStartDate(LocalDate.now().minusDays(15));
        tech.setEndDate(LocalDate.now().plusDays(45));
        tech.setStatus("ACTIVE");
        tech.setDescription("Áp dụng cho toàn bộ sách công nghệ.");
        promotions.add(tech);

        Promotion buyTwo = new Promotion();
        buyTwo.setId(3);
        buyTwo.setName("Mua 2 tặng 1 sách thiếu nhi");
        buyTwo.setType("BUY_X_GET_Y");
        buyTwo.setCategory("Thiếu nhi");
        buyTwo.setMinimumQuantity(2);
        buyTwo.setValue(1);
        buyTwo.setStartDate(LocalDate.now().minusDays(7));
        buyTwo.setEndDate(LocalDate.now().plusDays(60));
        buyTwo.setStatus("ACTIVE");
        buyTwo.setDescription("Mua 2 tặng 1 cùng dòng phù hợp.");
        promotions.add(buyTwo);

        return promotions;
    }
}
