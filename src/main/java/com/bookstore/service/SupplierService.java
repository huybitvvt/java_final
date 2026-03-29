package com.bookstore.service;

import com.bookstore.dao.SupplierDAO;
import com.bookstore.model.Supplier;
import com.bookstore.util.AppLog;

import java.sql.SQLException;
import java.util.List;

/**
 * Supplier Service - Business logic layer for Supplier
 */
public class SupplierService {

    private final SupplierDAO supplierDAO;

    public SupplierService() {
        this.supplierDAO = new SupplierDAO();
    }

    public List<Supplier> getAllSuppliers() {
        try {
            return supplierDAO.getAll();
        } catch (SQLException e) {
            AppLog.error(SupplierService.class, "Error getting all suppliers", e);
            return null;
        }
    }

    public Supplier getSupplierById(int id) {
        try {
            return supplierDAO.getById(id);
        } catch (SQLException e) {
            AppLog.error(SupplierService.class, "Error getting supplier by id: " + id, e);
            return null;
        }
    }

    public boolean addSupplier(Supplier supplier) {
        try {
            if (supplier.getTenNhaCungCap() == null || supplier.getTenNhaCungCap().trim().isEmpty()) {
                AppLog.warn(SupplierService.class, "Supplier name is required");
                return false;
            }
            return supplierDAO.insert(supplier);
        } catch (SQLException e) {
            AppLog.error(SupplierService.class, "Error adding supplier", e);
            return false;
        }
    }

    public boolean updateSupplier(Supplier supplier) {
        try {
            if (supplier.getTenNhaCungCap() == null || supplier.getTenNhaCungCap().trim().isEmpty()) {
                AppLog.warn(SupplierService.class, "Supplier name is required");
                return false;
            }
            return supplierDAO.update(supplier);
        } catch (SQLException e) {
            AppLog.error(SupplierService.class, "Error updating supplier", e);
            return false;
        }
    }

    public boolean deleteSupplier(int id) {
        try {
            return supplierDAO.delete(id);
        } catch (SQLException e) {
            AppLog.error(SupplierService.class, "Error deleting supplier: " + id, e);
            return false;
        }
    }

    public List<Supplier> searchSuppliers(String keyword) {
        try {
            return supplierDAO.search(keyword);
        } catch (SQLException e) {
            AppLog.error(SupplierService.class, "Error searching suppliers", e);
            return null;
        }
    }

    public int countSuppliers() {
        try {
            return supplierDAO.count();
        } catch (SQLException e) {
            AppLog.error(SupplierService.class, "Error counting suppliers", e);
            return 0;
        }
    }
}
