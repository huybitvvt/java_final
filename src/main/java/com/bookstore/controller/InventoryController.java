package com.bookstore.controller;

import com.bookstore.model.Book;
import com.bookstore.service.BookService;
import com.bookstore.util.FormatUtil;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.util.List;

/**
 * Inventory Controller
 */
public class InventoryController {
    private TableView<Book> tableInventory;
    private TableColumn<Book, Integer> colMaSach;
    private TableColumn<Book, String> colTenSach;
    private TableColumn<Book, String> colTheLoai;
    private TableColumn<Book, Integer> colSoLuong;
    private TableColumn<Book, Double> colGiaBia;
    private TableColumn<Book, Double> colGiaTri;

    private BookService bookService;
    private ObservableList<Book> bookList;

    public InventoryController() {
        this.bookService = new BookService();
        this.bookList = FXCollections.observableArrayList();
    }
    public void initialize() {
        setupTableColumns();
        setupTableLayout();
        loadInventory();
    }

    private void setupTableColumns() {
        colMaSach.setCellValueFactory(new PropertyValueFactory<>("maSach"));
        colTenSach.setCellValueFactory(new PropertyValueFactory<>("tenSach"));
        colTheLoai.setCellValueFactory(new PropertyValueFactory<>("theLoai"));
        colSoLuong.setCellValueFactory(new PropertyValueFactory<>("soLuongTon"));
        colGiaBia.setCellValueFactory(new PropertyValueFactory<>("giaBia"));
        colGiaTri.setCellValueFactory(cellData ->
                new ReadOnlyObjectWrapper<>(cellData.getValue().getSoLuongTon() * cellData.getValue().getGiaBia()));

        colGiaBia.setCellFactory(column -> new TableCell<Book, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText("");
                else setText(FormatUtil.formatCurrency(item));
            }
        });

        colGiaTri.setCellFactory(column -> new TableCell<Book, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableView().getItems().get(getIndex()) == null) {
                    setText("");
                } else {
                    Book book = getTableView().getItems().get(getIndex());
                    double value = book.getSoLuongTon() * book.getGiaBia();
                    setText(FormatUtil.formatCurrency(value));
                }
            }
        });
    }

    private void setupTableLayout() {
        tableInventory.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableInventory.setFixedCellSize(48);
        tableInventory.setPlaceholder(createPlaceholder("Chưa có dữ liệu tồn kho."));
        colMaSach.prefWidthProperty().bind(tableInventory.widthProperty().subtract(26).multiply(0.08));
        colTenSach.prefWidthProperty().bind(tableInventory.widthProperty().subtract(26).multiply(0.34));
        colTheLoai.prefWidthProperty().bind(tableInventory.widthProperty().subtract(26).multiply(0.14));
        colSoLuong.prefWidthProperty().bind(tableInventory.widthProperty().subtract(26).multiply(0.12));
        colGiaBia.prefWidthProperty().bind(tableInventory.widthProperty().subtract(26).multiply(0.14));
        colGiaTri.prefWidthProperty().bind(tableInventory.widthProperty().subtract(26).multiply(0.18));
    }

    private void loadInventory() {
        List<Book> books = bookService.getAllBooks();
        if (books != null) {
            bookList.clear();
            bookList.addAll(books);
            tableInventory.setItems(bookList);
        }
    }

    private Label createPlaceholder(String message) {
        Label label = new Label(message);
        label.getStyleClass().add("empty-state-label");
        return label;
    }
}

