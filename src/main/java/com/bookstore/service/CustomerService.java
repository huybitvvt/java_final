package com.bookstore.service;

import com.bookstore.dao.CustomerDAO;
import com.bookstore.model.Customer;
import com.bookstore.util.AppLog;

import java.sql.SQLException;
import java.util.List;

/**
 * Customer Service - Business logic layer for Customer
 */
public class CustomerService {

    private final CustomerDAO customerDAO;

    public CustomerService() {
        this.customerDAO = new CustomerDAO();
    }

    /**
     * Lấy tất cả khách hàng
     */
    public List<Customer> getAllCustomers() {
        try {
            return customerDAO.getAll();
        } catch (SQLException e) {
            AppLog.error(CustomerService.class, "Error getting all customers", e);
            return null;
        }
    }

    /**
     * Lấy khách hàng theo ID
     */
    public Customer getCustomerById(int id) {
        try {
            return customerDAO.getById(id);
        } catch (SQLException e) {
            AppLog.error(CustomerService.class, "Error getting customer by id: " + id, e);
            return null;
        }
    }

    /**
     * Thêm khách hàng mới
     */
    public boolean addCustomer(Customer customer) {
        try {
            // Validation
            if (customer.getHoTen() == null || customer.getHoTen().trim().isEmpty()) {
                AppLog.warn(CustomerService.class, "Customer name is required");
                return false;
            }
            return customerDAO.insert(customer);
        } catch (SQLException e) {
            AppLog.error(CustomerService.class, "Error adding customer", e);
            return false;
        }
    }

    /**
     * Cập nhật khách hàng
     */
    public boolean updateCustomer(Customer customer) {
        try {
            // Validation
            if (customer.getHoTen() == null || customer.getHoTen().trim().isEmpty()) {
                AppLog.warn(CustomerService.class, "Customer name is required");
                return false;
            }
            return customerDAO.update(customer);
        } catch (SQLException e) {
            AppLog.error(CustomerService.class, "Error updating customer", e);
            return false;
        }
    }

    /**
     * Xóa khách hàng
     */
    public boolean deleteCustomer(int id) {
        try {
            return customerDAO.delete(id);
        } catch (SQLException e) {
            AppLog.error(CustomerService.class, "Error deleting customer: " + id, e);
            return false;
        }
    }

    /**
     * Tìm kiếm khách hàng
     */
    public List<Customer> searchCustomers(String keyword) {
        try {
            return customerDAO.search(keyword);
        } catch (SQLException e) {
            AppLog.error(CustomerService.class, "Error searching customers", e);
            return null;
        }
    }

    /**
     * Cập nhật điểm tích lũy
     */
    public boolean updatePoints(int customerId, int points) {
        try {
            return customerDAO.updatePoints(customerId, points);
        } catch (SQLException e) {
            AppLog.error(CustomerService.class, "Error updating customer points", e);
            return false;
        }
    }

    /**
     * Tìm khách hàng theo số điện thoại
     */
    public Customer getCustomerByPhone(String phone) {
        try {
            return customerDAO.getByPhone(phone);
        } catch (SQLException e) {
            AppLog.error(CustomerService.class, "Error getting customer by phone", e);
            return null;
        }
    }

    /**
     * Lấy top khách hàng tích lũy nhiều nhất
     */
    public List<Customer> getTopCustomers(int limit) {
        try {
            return customerDAO.getTopCustomers(limit);
        } catch (SQLException e) {
            AppLog.error(CustomerService.class, "Error getting top customers", e);
            return null;
        }
    }

    /**
     * Đếm tổng số khách hàng
     */
    public int countCustomers() {
        try {
            return customerDAO.count();
        } catch (SQLException e) {
            AppLog.error(CustomerService.class, "Error counting customers", e);
            return 0;
        }
    }
}
