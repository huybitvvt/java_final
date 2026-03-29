package com.bookstore.service;

import com.bookstore.dao.BookDAO;
import com.bookstore.exception.BusinessException;
import com.bookstore.model.Book;
import com.bookstore.util.AppLog;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * Book Service - Business logic layer for Book
 */
public class BookService {

    private final BookDAO bookDAO;

    public BookService() {
        this.bookDAO = new BookDAO();
    }

    /**
     * Lấy tất cả sách
     */
    public List<Book> getAllBooks() {
        try {
            return bookDAO.getAll();
        } catch (SQLException e) {
            AppLog.error(BookService.class, "Khong the tai danh sach sach", e);
            return Collections.emptyList();
        }
    }

    /**
     * Lấy sách theo ID
     */
    public Book getBookById(int id) {
        try {
            return bookDAO.getById(id);
        } catch (SQLException e) {
            AppLog.error(BookService.class, "Khong the tai sach theo ID " + id, e);
            return null;
        }
    }

    /**
     * Thêm sách mới
     */
    public boolean addBook(Book book) {
        try {
            // Validation
            if (book.getTenSach() == null || book.getTenSach().trim().isEmpty()) {
                throw new BusinessException("Tên sách không được để trống");
            }
            if (book.getGiaBia() < 0) {
                throw new BusinessException("Giá bìa không được âm");
            }
            if (book.getSoLuongTon() < 0) {
                throw new BusinessException("Số lượng tồn không được âm");
            }
            if (book.getNamXuatBan() < 1900 || book.getNamXuatBan() > 2100) {
                throw new BusinessException("Năm xuất bản không hợp lệ");
            }

            boolean result = bookDAO.insert(book);
            if (result) {
                AppLog.info(BookService.class, "Da them sach moi: " + book.getTenSach());
            }
            return result;
        } catch (SQLException e) {
            AppLog.error(BookService.class, "Khong the them sach", e);
            if (e.getSQLState() != null && e.getSQLState().startsWith("23")) {
                throw new BusinessException("Dữ liệu sách đã tồn tại hoặc vi phạm ràng buộc cơ sở dữ liệu");
            }
            throw new RuntimeException("Không thể thêm sách. Vui lòng thử lại sau.", e);
        } catch (BusinessException e) {
            throw e; // Re-throw business exceptions
        }
    }

    /**
     * Cập nhật sách
     */
    public boolean updateBook(Book book) {
        try {
            // Validation
            if (book.getTenSach() == null || book.getTenSach().trim().isEmpty()) {
                throw new BusinessException("Tên sách không được để trống");
            }
            if (book.getGiaBia() < 0) {
                throw new BusinessException("Giá bìa không được âm");
            }
            if (book.getSoLuongTon() < 0) {
                throw new BusinessException("Số lượng tồn không được âm");
            }

            boolean result = bookDAO.update(book);
            if (result) {
                AppLog.info(BookService.class, "Da cap nhat sach " + book.getMaSach());
            }
            return result;
        } catch (SQLException e) {
            AppLog.error(BookService.class, "Khong the cap nhat sach " + book.getMaSach(), e);
            throw new RuntimeException("Không thể cập nhật sách. Vui lòng thử lại sau.", e);
        } catch (BusinessException e) {
            throw e;
        }
    }

    /**
     * Xóa sách
     */
    public boolean deleteBook(int id) {
        try {
            boolean result = bookDAO.delete(id);
            if (result) {
                AppLog.info(BookService.class, "Da xoa sach " + id);
            }
            return result;
        } catch (SQLException e) {
            AppLog.error(BookService.class, "Khong the xoa sach " + id, e);
            if (e.getSQLState() != null && e.getSQLState().startsWith("23")) {
                throw new BusinessException("Không thể xóa sách vì có dữ liệu liên quan");
            }
            throw new RuntimeException("Không thể xóa sách. Vui lòng thử lại sau.", e);
        }
    }

    /**
     * Tìm Kiếm sách
     */
    public List<Book> searchBooks(String keyword) {
        try {
            return bookDAO.search(keyword);
        } catch (SQLException e) {
            AppLog.error(BookService.class, "Khong the tim sach voi tu khoa " + keyword, e);
            return Collections.emptyList();
        }
    }

    /**
     * Lấy sách theo thể loại
     */
    public List<Book> getBooksByCategory(String category) {
        try {
            return bookDAO.getByCategory(category);
        } catch (SQLException e) {
            AppLog.error(BookService.class, "Khong the tai sach theo the loai " + category, e);
            return Collections.emptyList();
        }
    }

    /**
     * Lấy sách tồn kho thấp
     */
    public List<Book> getLowStockBooks(int threshold) {
        try {
            return bookDAO.getLowStock(threshold);
        } catch (SQLException e) {
            AppLog.error(BookService.class, "Khong the tai sach ton kho thap", e);
            return Collections.emptyList();
        }
    }

    /**
     * Cập nhật số lượng tồn kho
     */
    public boolean updateQuantity(int bookId, int quantity) {
        try {
            boolean result = bookDAO.updateQuantity(bookId, quantity);
            if (result) {
                AppLog.info(BookService.class, "Da cap nhat ton kho sach " + bookId + " voi bien dong " + quantity);
            }
            return result;
        } catch (SQLException e) {
            AppLog.error(BookService.class, "Khong the cap nhat ton kho sach " + bookId, e);
            throw new RuntimeException("Không thể cập nhật số lượng. Vui lòng thử lại sau.", e);
        }
    }

    /**
     * Lấy tất cả thể loại
     */
    public List<String> getAllCategories() {
        try {
            return bookDAO.getAllCategories();
        } catch (SQLException e) {
            AppLog.error(BookService.class, "Khong the tai danh sach the loai", e);
            return Collections.emptyList();
        }
    }

    /**
     * Đếm tổng số sách
     */
    public int countBooks() {
        try {
            return bookDAO.count();
        } catch (SQLException e) {
            AppLog.error(BookService.class, "Khong the dem tong so sach", e);
            return 0;
        }
    }
}
