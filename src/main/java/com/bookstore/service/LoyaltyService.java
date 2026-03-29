package com.bookstore.service;

import com.bookstore.dao.InvoiceDAO;
import com.bookstore.model.Customer;

import java.sql.SQLException;

/**
 * Customer loyalty tier calculation.
 */
public class LoyaltyService {

    private final InvoiceDAO invoiceDAO;

    public LoyaltyService() {
        this.invoiceDAO = new InvoiceDAO();
    }

    public String resolveTier(Customer customer) {
        if (customer == null) {
            return "Khách lẻ";
        }

        double totalSpent = customer.getTongChiTieu();
        if (totalSpent <= 0) {
            try {
                totalSpent = invoiceDAO.getCustomerTotalSpent(customer.getMaKhachHang());
                customer.setTongChiTieu(totalSpent);
            } catch (SQLException ignored) {
                totalSpent = customer.getDiemTichLuy() * 1000.0;
            }
        }

        if (totalSpent >= 15000000) {
            customer.setHangThanhVien("Diamond");
            return "Diamond";
        }
        if (totalSpent >= 7000000) {
            customer.setHangThanhVien("Gold");
            return "Gold";
        }
        if (totalSpent >= 2000000) {
            customer.setHangThanhVien("Silver");
            return "Silver";
        }
        customer.setHangThanhVien("Standard");
        return "Standard";
    }

    public double getDiscountPercent(Customer customer) {
        return switch (resolveTier(customer)) {
            case "Gold" -> 5;
            case "Diamond" -> 10;
            default -> 0;
        };
    }
}
