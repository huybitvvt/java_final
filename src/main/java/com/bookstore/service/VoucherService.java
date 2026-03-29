package com.bookstore.service;

import com.bookstore.dao.VoucherDAO;
import com.bookstore.model.Voucher;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Voucher validation and fallback seed data.
 */
public class VoucherService {

    private static final List<Voucher> FALLBACK_VOUCHERS = new ArrayList<>();
    private static int fallbackSequence = 1000;

    private final VoucherDAO voucherDAO;

    public VoucherService() {
        this.voucherDAO = new VoucherDAO();
        initializeFallbackIfNeeded();
    }

    public List<Voucher> getAllVouchers() {
        try {
            return voucherDAO.getAll();
        } catch (RuntimeException ignored) {
        }
        return new ArrayList<>(FALLBACK_VOUCHERS);
    }

    public Voucher validateVoucher(String code, double subtotal) {
        if (code == null || code.isBlank()) {
            return null;
        }

        LocalDate today = LocalDate.now();
        return getAllVouchers().stream()
                .filter(voucher -> voucher.getCode().equalsIgnoreCase(code.trim()))
                .filter(voucher -> voucher.isAvailable(today))
                .filter(voucher -> subtotal >= voucher.getMinimumOrderValue())
                .findFirst()
                .orElse(null);
    }

    public double calculateDiscount(Voucher voucher, double subtotal) {
        if (voucher == null) {
            return 0;
        }
        return switch (voucher.getType()) {
            case "PERCENT" -> subtotal * voucher.getValue() / 100;
            case "FIXED" -> Math.min(subtotal, voucher.getValue());
            default -> 0;
        };
    }

    public boolean addVoucher(Voucher voucher) {
        validateVoucher(voucher);
        try {
            return voucherDAO.insert(voucher);
        } catch (RuntimeException ignored) {
            voucher.setId(++fallbackSequence);
            FALLBACK_VOUCHERS.add(voucher);
            return true;
        }
    }

    public boolean updateVoucher(Voucher voucher) {
        validateVoucher(voucher);
        try {
            return voucherDAO.update(voucher);
        } catch (RuntimeException ignored) {
            for (int i = 0; i < FALLBACK_VOUCHERS.size(); i++) {
                if (FALLBACK_VOUCHERS.get(i).getId() == voucher.getId()) {
                    FALLBACK_VOUCHERS.set(i, voucher);
                    return true;
                }
            }
            return false;
        }
    }

    public boolean deleteVoucher(int id) {
        try {
            return voucherDAO.delete(id);
        } catch (RuntimeException ignored) {
            return FALLBACK_VOUCHERS.removeIf(voucher -> voucher.getId() == id);
        }
    }

    private void validateVoucher(Voucher voucher) {
        if (voucher == null) {
            throw new IllegalArgumentException("Voucher không hợp lệ");
        }
        if (voucher.getCode() == null || voucher.getCode().isBlank()) {
            throw new IllegalArgumentException("Mã giảm giá không được để trống");
        }
        if (voucher.getType() == null || voucher.getType().isBlank()) {
            throw new IllegalArgumentException("Loại voucher không được để trống");
        }
        if (voucher.getValue() <= 0) {
            throw new IllegalArgumentException("Giá trị voucher phải lớn hơn 0");
        }
        if (voucher.getStartDate() != null && voucher.getEndDate() != null
                && voucher.getEndDate().isBefore(voucher.getStartDate())) {
            throw new IllegalArgumentException("Ngày kết thúc không được trước ngày bắt đầu");
        }
        if (voucher.getMinimumOrderValue() < 0) {
            throw new IllegalArgumentException("Đơn tối thiểu không hợp lệ");
        }
        if (voucher.getUsageLimit() < 0 || voucher.getUsedCount() < 0) {
            throw new IllegalArgumentException("Số lượt dùng không hợp lệ");
        }
        boolean duplicateCode = getAllVouchers().stream()
                .anyMatch(existing -> existing.getCode() != null
                        && existing.getCode().equalsIgnoreCase(voucher.getCode())
                        && existing.getId() != voucher.getId());
        if (duplicateCode) {
            throw new IllegalArgumentException("Mã giảm giá đã tồn tại");
        }
    }

    private void initializeFallbackIfNeeded() {
        if (!FALLBACK_VOUCHERS.isEmpty()) {
            return;
        }
        FALLBACK_VOUCHERS.addAll(createFallbackVouchers());
        FALLBACK_VOUCHERS.stream()
                .mapToInt(Voucher::getId)
                .max()
                .ifPresent(maxId -> fallbackSequence = Math.max(fallbackSequence, maxId));
    }

    private List<Voucher> createFallbackVouchers() {
        List<Voucher> vouchers = new ArrayList<>();

        Voucher welcome = new Voucher();
        welcome.setId(1);
        welcome.setCode("WELCOME10");
        welcome.setType("PERCENT");
        welcome.setValue(10);
        welcome.setMinimumOrderValue(100000);
        welcome.setStartDate(LocalDate.now().minusDays(60));
        welcome.setEndDate(LocalDate.now().plusDays(180));
        welcome.setUsageLimit(0);
        welcome.setUsedCount(0);
        welcome.setStatus("ACTIVE");
        vouchers.add(welcome);

        Voucher student = new Voucher();
        student.setId(2);
        student.setCode("STUDENT20");
        student.setType("PERCENT");
        student.setValue(20);
        student.setMinimumOrderValue(200000);
        student.setStartDate(LocalDate.now().minusDays(30));
        student.setEndDate(LocalDate.now().plusDays(90));
        student.setUsageLimit(500);
        student.setUsedCount(32);
        student.setStatus("ACTIVE");
        vouchers.add(student);

        Voucher fixed = new Voucher();
        fixed.setId(3);
        fixed.setCode("SAVE50K");
        fixed.setType("FIXED");
        fixed.setValue(50000);
        fixed.setMinimumOrderValue(350000);
        fixed.setStartDate(LocalDate.now().minusDays(15));
        fixed.setEndDate(LocalDate.now().plusDays(60));
        fixed.setUsageLimit(0);
        fixed.setUsedCount(0);
        fixed.setStatus("ACTIVE");
        vouchers.add(fixed);

        return vouchers;
    }
}
