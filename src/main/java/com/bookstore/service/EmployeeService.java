package com.bookstore.service;

import com.bookstore.dao.EmployeeDAO;
import com.bookstore.model.Employee;
import com.bookstore.util.AppLog;

import java.sql.SQLException;
import java.util.List;

/**
 * Employee Service - Business logic layer for Employee
 */
public class EmployeeService {

    private final EmployeeDAO employeeDAO;

    public EmployeeService() {
        this.employeeDAO = new EmployeeDAO();
    }

    public List<Employee> getAllEmployees() {
        try {
            return employeeDAO.getAll();
        } catch (SQLException e) {
            AppLog.error(EmployeeService.class, "Error getting all employees", e);
            return null;
        }
    }

    public Employee getEmployeeById(int id) {
        try {
            return employeeDAO.getById(id);
        } catch (SQLException e) {
            AppLog.error(EmployeeService.class, "Error getting employee by id: " + id, e);
            return null;
        }
    }

    public boolean addEmployee(Employee employee) {
        try {
            if (employee.getHoTen() == null || employee.getHoTen().trim().isEmpty()) {
                AppLog.warn(EmployeeService.class, "Employee name is required");
                return false;
            }
            return employeeDAO.insert(employee);
        } catch (SQLException e) {
            AppLog.error(EmployeeService.class, "Error adding employee", e);
            return false;
        }
    }

    public boolean updateEmployee(Employee employee) {
        try {
            if (employee.getHoTen() == null || employee.getHoTen().trim().isEmpty()) {
                AppLog.warn(EmployeeService.class, "Employee name is required");
                return false;
            }
            return employeeDAO.update(employee);
        } catch (SQLException e) {
            AppLog.error(EmployeeService.class, "Error updating employee", e);
            return false;
        }
    }

    public boolean deleteEmployee(int id) {
        try {
            return employeeDAO.delete(id);
        } catch (SQLException e) {
            AppLog.error(EmployeeService.class, "Error deleting employee: " + id, e);
            return false;
        }
    }

    public List<Employee> searchEmployees(String keyword) {
        try {
            return employeeDAO.search(keyword);
        } catch (SQLException e) {
            AppLog.error(EmployeeService.class, "Error searching employees", e);
            return null;
        }
    }

    public List<Employee> getEmployeesByPosition(String position) {
        try {
            return employeeDAO.getByPosition(position);
        } catch (SQLException e) {
            AppLog.error(EmployeeService.class, "Error getting employees by position", e);
            return null;
        }
    }

    public int countEmployees() {
        try {
            return employeeDAO.count();
        } catch (SQLException e) {
            AppLog.error(EmployeeService.class, "Error counting employees", e);
            return 0;
        }
    }
}
