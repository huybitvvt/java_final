package com.bookstore.model;

/**
 * Aggregated best-seller item.
 */
public record TopBookSale(String bookName, int quantity, double revenue) {
}
