package com.bookstore.service;

import com.bookstore.dao.CustomerDAO;
import com.bookstore.dao.InvoiceDAO;
import com.bookstore.model.Book;
import com.bookstore.model.Customer;
import com.bookstore.model.DashboardPoint;
import com.bookstore.model.TopBookSale;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Aggregates dashboard analytics.
 */
public class DashboardService {

    private final BookService bookService;
    private final CustomerService customerService;
    private final InvoiceService invoiceService;
    private final InvoiceDAO invoiceDAO;
    private final CustomerDAO customerDAO;

    public DashboardService() {
        this.bookService = new BookService();
        this.customerService = new CustomerService();
        this.invoiceService = new InvoiceService();
        this.invoiceDAO = new InvoiceDAO();
        this.customerDAO = new CustomerDAO();
    }

    public int getTotalBooks() {
        return bookService.countBooks();
    }

    public int getTotalCustomers() {
        return customerService.countCustomers();
    }

    public int getTodayInvoices() {
        LocalDate anchorDate = getLatestInvoiceDate();
        Date start = Date.from(anchorDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(anchorDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).minusSeconds(1).toInstant());
        List<com.bookstore.model.Invoice> invoices = invoiceService.getInvoicesByDateRange(start, end);
        return invoices == null ? 0 : invoices.size();
    }

    public double getTodayRevenue() {
        LocalDate anchorDate = getLatestInvoiceDate();
        Date start = Date.from(anchorDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(anchorDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).minusSeconds(1).toInstant());
        return invoiceService.getRevenueByDateRange(start, end);
    }

    public List<Book> getLowStockBooks() {
        try {
            return bookService.getLowStockBooks(10);
        } catch (RuntimeException ex) {
            return Collections.emptyList();
        }
    }

    public List<DashboardPoint> getRevenueByDay(int days) {
        LocalDate anchorDate = getLatestInvoiceDate();
        LocalDate startDate = anchorDate.minusDays(days - 1L);
        Date start = Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date end = Date.from(anchorDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).minusSeconds(1).toInstant());
        List<com.bookstore.model.Invoice> invoices = invoiceService.getInvoicesByDateRange(start, end);
        if (invoices == null) {
            return Collections.emptyList();
        }

        Map<LocalDate, Double> revenueByDate = new LinkedHashMap<>();
        for (int i = 0; i < days; i++) {
            LocalDate date = startDate.plusDays(i);
            revenueByDate.put(date, 0.0);
        }

        invoices.stream()
                .filter(invoice -> invoice.getNgayLap() != null)
                .forEach(invoice -> {
                    LocalDate date = invoice.getNgayLap().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                    revenueByDate.computeIfPresent(date, (key, value) -> value + invoice.getThanhToan());
                });

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        return revenueByDate.entrySet().stream()
                .map(entry -> new DashboardPoint(entry.getKey().format(formatter), entry.getValue()))
                .toList();
    }

    public List<DashboardPoint> getRevenueByMonth(int months) {
        try {
            LocalDate anchorDate = getLatestInvoiceDate();
            YearMonth anchorMonth = YearMonth.from(anchorDate);
            Map<YearMonth, Double> revenueByMonth = new LinkedHashMap<>();
            for (int i = months - 1; i >= 0; i--) {
                revenueByMonth.put(anchorMonth.minusMonths(i), 0.0);
            }

            Date start = Date.from(anchorMonth.minusMonths(months - 1L).atDay(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
            Date end = Date.from(anchorMonth.plusMonths(1).atDay(1).atStartOfDay(ZoneId.systemDefault()).minusSeconds(1).toInstant());
            List<com.bookstore.model.Invoice> invoices = invoiceService.getInvoicesByDateRange(start, end);
            if (invoices == null) {
                return Collections.emptyList();
            }

            invoices.stream()
                    .filter(invoice -> invoice.getNgayLap() != null)
                    .forEach(invoice -> {
                        YearMonth month = YearMonth.from(invoice.getNgayLap().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
                        revenueByMonth.computeIfPresent(month, (key, value) -> value + invoice.getThanhToan());
                    });

            return revenueByMonth.entrySet().stream()
                    .map(entry -> new DashboardPoint("Thg " + entry.getKey().getMonthValue(), entry.getValue()))
                    .toList();
        } catch (RuntimeException e) {
            return Collections.emptyList();
        }
    }

    public List<TopBookSale> getTopSellingBooks(int limit) {
        try {
            return invoiceDAO.getTopSellingBooks(limit);
        } catch (SQLException e) {
            return Collections.emptyList();
        }
    }

    public Map<String, Double> getRevenueByCategory() {
        try {
            return invoiceDAO.getRevenueByCategory();
        } catch (SQLException e) {
            return Collections.emptyMap();
        }
    }

    public List<Customer> getTopCustomers(int limit) {
        try {
            List<Customer> customers = customerDAO.getTopCustomers(limit);
            LoyaltyService loyaltyService = new LoyaltyService();
            customers.forEach(loyaltyService::resolveTier);
            return customers;
        } catch (SQLException e) {
            return Collections.emptyList();
        }
    }

    public String getCurrentMonthLabel() {
        YearMonth month = YearMonth.from(getLatestInvoiceDate());
        return "Tháng " + month.getMonthValue() + "/" + month.getYear();
    }

    public String getLatestInvoiceDateLabel() {
        LocalDate latest = getLatestInvoiceDate();
        return latest.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private LocalDate getLatestInvoiceDate() {
        List<com.bookstore.model.Invoice> invoices = invoiceService.getAllInvoices();
        if (invoices == null || invoices.isEmpty()) {
            return LocalDate.now();
        }
        return invoices.stream()
                .map(com.bookstore.model.Invoice::getNgayLap)
                .filter(Objects::nonNull)
                .max(Date::compareTo)
                .map(date -> date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                .orElse(LocalDate.now());
    }
}
