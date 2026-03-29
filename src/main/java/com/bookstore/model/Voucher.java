package com.bookstore.model;

import java.time.LocalDate;

/**
 * Voucher domain model.
 */
public class Voucher {
    private int id;
    private String code;
    private String type;
    private double value;
    private double minimumOrderValue;
    private LocalDate startDate;
    private LocalDate endDate;
    private int usageLimit;
    private int usedCount;
    private String status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getMinimumOrderValue() {
        return minimumOrderValue;
    }

    public void setMinimumOrderValue(double minimumOrderValue) {
        this.minimumOrderValue = minimumOrderValue;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public int getUsageLimit() {
        return usageLimit;
    }

    public void setUsageLimit(int usageLimit) {
        this.usageLimit = usageLimit;
    }

    public int getUsedCount() {
        return usedCount;
    }

    public void setUsedCount(int usedCount) {
        this.usedCount = usedCount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isAvailable(LocalDate date) {
        return "ACTIVE".equalsIgnoreCase(status)
                && (startDate == null || !date.isBefore(startDate))
                && (endDate == null || !date.isAfter(endDate))
                && (usageLimit <= 0 || usedCount < usageLimit);
    }
}
