package com.bookstore.controller;

import com.bookstore.model.Book;
import com.bookstore.view.ViewBundle;
import com.bookstore.view.ViewId;
import com.bookstore.service.BookService;
import com.bookstore.util.ActionButtonFactory;
import com.bookstore.util.AnimationUtil;
import com.bookstore.util.AppLog;
import com.bookstore.util.AutoCompleteUtil;
import com.bookstore.util.DialogService;
import com.bookstore.util.ExceptionMessageUtil;
import com.bookstore.util.FormatUtil;
import com.bookstore.util.ExportExcelUtil;
import com.bookstore.util.IconUtil;
import com.bookstore.util.InputFieldUtil;
import com.bookstore.util.NotificationService;
import com.bookstore.util.PaginationUtil;
import com.bookstore.util.SearchUtil;
import com.bookstore.util.ThemeManager;
import com.bookstore.util.TooltipUtil;
import com.bookstore.util.ViewLoaderUtil;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.AutoCompletionBinding;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Book Controller - Xử lý quản lý sách
 */
public class BookController {
    private VBox pageRoot;
    private HBox headerBar;
    private VBox contentCard;
    private TextField txtSearch;
    private ComboBox<String> cboCategory;
    private Button btnSearch;
    private Button btnAdvancedSearch;
    private Button btnAdd;
    private Button btnExport;
    private Button btnRefresh;
    private TableView<Book> tableBooks;
    private TableColumn<Book, Integer> colMaSach;
    private TableColumn<Book, String> colTenSach;
    private TableColumn<Book, String> colTacGia;
    private TableColumn<Book, String> colNXB;
    private TableColumn<Book, String> colTheLoai;
    private TableColumn<Book, Integer> colNamXB;
    private TableColumn<Book, Double> colGiaBia;
    private TableColumn<Book, Integer> colSoLuong;
    private TableColumn<Book, String> colHanhDong;
    private Label lblTotal;
    private ComboBox<Integer> cboPageSize;
    private Pagination pagination;

    private BookService bookService;
    private ObservableList<Book> bookList;
    private PaginationUtil<Book> paginationUtil;
    private List<Book> allBooks;
    private AutoCompletionBinding<String> searchSuggestions;

    public BookController() {
        this.bookService = new BookService();
        this.bookList = FXCollections.observableArrayList();
    }
    public void initialize() {
        runInitStep("setupTableColumns", this::setupTableColumns);
        runInitStep("setupLayout", this::setupLayout);
        runInitStep("decorateToolbar", this::decorateToolbar);
        runInitStep("setupDefaultSort", () -> {
            tableBooks.getSortOrder().clear();
            tableBooks.getSortOrder().add(colMaSach);
            colMaSach.setSortType(TableColumn.SortType.ASCENDING);
        });
        runInitStep("initPagination", this::initPagination);
        runInitStep("loadBooks", this::loadBooks);
        runInitStep("loadCategories", this::loadCategories);
        runInitStep("playAnimations", () -> AnimationUtil.playStagger(List.of(headerBar, contentCard)));
        runInitStep("installInputWorkaround", () -> InputFieldUtil.installCarryOverWorkaround(txtSearch));
        runInitStep("bindSearch", () -> InputFieldUtil.bindKeywordSearch(txtSearch, this::handleSearch));
    }

    private void initPagination() {
        paginationUtil = new PaginationUtil<>(tableBooks, pagination, cboPageSize);
    }

    private void setupTableColumns() {
        colMaSach.setCellValueFactory(new PropertyValueFactory<>("maSach"));
        colTenSach.setCellValueFactory(new PropertyValueFactory<>("tenSach"));
        colTacGia.setCellValueFactory(new PropertyValueFactory<>("tacGia"));
        colNXB.setCellValueFactory(new PropertyValueFactory<>("nhaXuatBan"));
        colTheLoai.setCellValueFactory(new PropertyValueFactory<>("theLoai"));
        colNamXB.setCellValueFactory(new PropertyValueFactory<>("namXuatBan"));
        colGiaBia.setCellValueFactory(new PropertyValueFactory<>("giaBia"));
        colSoLuong.setCellValueFactory(new PropertyValueFactory<>("soLuongTon"));
        colHanhDong.setCellValueFactory(cellData -> new SimpleStringProperty("actions"));

        // Custom cell for price
        colGiaBia.setCellFactory(column -> new TableCell<Book, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("");
                } else {
                    setText(FormatUtil.formatCurrency(item));
                }
            }
        });

        colSoLuong.setCellFactory(column -> new TableCell<Book, Integer>() {
            private final Label badge = new Label();

            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                    return;
                }

                badge.setText(item + " cuốn");
                badge.getStyleClass().removeAll("status-badge-warning", "status-badge-success");
                badge.getStyleClass().addAll("status-badge",
                        item < 10 ? "status-badge-warning" : "status-badge-success");
                setGraphic(badge);
            }
        });

        // Action column
        colHanhDong.setSortable(false);
        colHanhDong.setCellFactory(column -> new TableCell<Book, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Book book = getTableView().getItems().get(getIndex());
                    Button btnEdit = ActionButtonFactory.createEditButton(e -> handleEdit(book));
                    Button btnDelete = ActionButtonFactory.createDeleteButton(e -> handleDelete(book));
                    HBox actions = ActionButtonFactory.createGroup(btnEdit, btnDelete);
                    setGraphic(actions);
                }
            }
        });
    }

    private void setupLayout() {
        tableBooks.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableBooks.setFixedCellSize(48);
        tableBooks.setPlaceholder(createPlaceholder("Chưa có sách nào hoặc chưa có kết quả phù hợp."));
        configureActionColumn();
    }

    private void configureActionColumn() {
        colHanhDong.setMinWidth(170);
        colHanhDong.setPrefWidth(170);
        colHanhDong.setMaxWidth(170);
        colHanhDong.setResizable(false);
    }

    private Label createPlaceholder(String message) {
        Label placeholder = new Label(message);
        placeholder.getStyleClass().add("empty-state-label");
        return placeholder;
    }

    private void decorateToolbar() {
        IconUtil.apply(btnSearch, "fas-search", "Tìm kiếm nhanh sách");
        IconUtil.apply(btnAdvancedSearch, "fas-sliders-h", "Tìm kiếm nâng cao theo nhiều điều kiện");
        IconUtil.apply(btnAdd, "fas-plus", "Thêm sách mới");
        IconUtil.apply(btnExport, "fas-file-export", "Xuất danh sách sách ra Excel");
        IconUtil.apply(btnRefresh, "fas-sync-alt", "Nạp lại danh sách sách");
        TooltipUtil.install(txtSearch, "Nhập tên sách, tác giả hoặc thể loại để tìm nhanh");
        TooltipUtil.install(cboCategory, "Lọc nhanh danh sách theo thể loại");
    }

    private void loadBooks() {
        List<Book> loadedBooks = bookService.getAllBooks();
        allBooks = loadedBooks == null ? new ArrayList<>() : new ArrayList<>(loadedBooks);

        if (allBooks.isEmpty()) {
            tableBooks.setPlaceholder(createPlaceholder("Không tải được dữ liệu sách. Kiểm tra kết nối MySQL hoặc dữ liệu hiện đang trống."));
        } else {
            tableBooks.setPlaceholder(createPlaceholder("Chưa có sách nào hoặc chưa có kết quả phù hợp."));
        }
        allBooks.sort(Comparator.comparingInt(Book::getMaSach));
        if (paginationUtil != null) {
            paginationUtil.setData(allBooks);
        }
        lblTotal.setText("Tổng số: " + allBooks.size());
        refreshSearchSuggestions();
    }

    private void loadCategories() {
        List<String> categories = bookService.getAllCategories();
        if (categories != null) {
            cboCategory.getItems().clear();
            cboCategory.getItems().add("Tất cả");
            cboCategory.getItems().addAll(categories);
            cboCategory.getSelectionModel().selectFirst();
        }
    }
    private void handleSearch() {
        String keyword = txtSearch.getText().trim();
        String category = cboCategory.getValue();

        List<Book> books;
        if (!keyword.isEmpty()) {
            books = bookService.searchBooks(keyword);
        } else if (category != null && !category.equals("Tất cả")) {
            books = bookService.getBooksByCategory(category);
        } else {
            books = bookService.getAllBooks();
        }

        if (books != null) {
            books.sort(Comparator.comparingInt(Book::getMaSach));
            allBooks = books;
            paginationUtil.setData(books);
            tableBooks.sort();
            lblTotal.setText("Tổng số: " + books.size());
        }
    }
    private void handleAdd() {
        openBookDialog(null);
    }
    private void handleEdit(Book book) {
        openBookDialog(book);
    }
    private void handleDelete(Book book) {
        if (DialogService.confirm(tableBooks, "Xóa sách",
                "Bạn có chắc chắn muốn xóa sách: " + book.getTenSach() + "?", "Xóa")) {
            try {
                if (bookService.deleteBook(book.getMaSach())) {
                    NotificationService.showSuccess(tableBooks, "Đã xóa sách thành công");
                    loadBooks();
                } else {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa sách!");
                }
            } catch (RuntimeException e) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", e.getMessage());
            }
        }
    }
    private void handleRefresh() {
        txtSearch.clear();
        cboCategory.getSelectionModel().selectFirst();
        loadBooks();
        pagination.setCurrentPageIndex(0);
    }
    private void handleExport() {
        exportToExcel();
    }

    private void exportToExcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu file Excel");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files", "*.xlsx"));
        fileChooser.setInitialFileName("DanhSachSach_" + System.currentTimeMillis() + ".xlsx");

        File file = fileChooser.showSaveDialog(tableBooks.getScene().getWindow());
        if (file != null) {
            try {
                List<Book> books = bookService.getAllBooks();
                if (books != null && !books.isEmpty()) {
                    boolean success = ExportExcelUtil.exportBooks(books, file.getAbsolutePath());
                    if (success) {
                        NotificationService.showSuccess(tableBooks, "Xuất Excel thành công");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Lỗi", "Xuất Excel thất bại!");
                    }
                } else {
                    showAlert(Alert.AlertType.WARNING, "Thông báo", "Không có dữ liệu để xuất!");
                }
            } catch (RuntimeException e) {
                AppLog.error(BookController.class, "Khong the xuat danh sach sach ra Excel", e);
                showAlert(Alert.AlertType.ERROR, "Lỗi",
                        "Lỗi khi xuất Excel: " + ExceptionMessageUtil.resolve(e, "Lỗi không xác định"));
            }
        }
    }
    private void handleAdvancedSearch() {
        // Advanced search with multiple criteria
        String keyword = txtSearch.getText().trim();
        String category = cboCategory.getValue();

        List<Book> results;
        if (!keyword.isEmpty()) {
            results = SearchUtil.searchBooks(keyword, category);
        } else if (category != null && !category.equals("Tất cả")) {
            results = bookService.getBooksByCategory(category);
        } else {
            results = bookService.getAllBooks();
        }

        if (results != null) {
            results.sort(Comparator.comparingInt(Book::getMaSach));
            allBooks = results;
            paginationUtil.setData(results);
            lblTotal.setText("Tổng số: " + results.size());
        }
    }

    private void openBookDialog(Book book) {
        try {
            ViewBundle view = ViewLoaderUtil.loadView(ViewId.BOOK_DIALOG);
            Parent root = view.getRoot();

            BookDialogController controller = view.getController(BookDialogController.class);
            controller.setBook(book);

            Stage stage = new Stage();
            DialogService.attachToOwner(stage, tableBooks);
            stage.setTitle(book == null ? "Thêm sách mới" : "Sửa thông tin sách");
            Scene scene = new Scene(root);
            ThemeManager.applyTheme(scene);
            stage.setScene(scene);
            stage.setMinWidth(760);
            stage.setMinHeight(560);
            stage.showAndWait();

            loadBooks();
        } catch (RuntimeException e) {
            AppLog.error(BookController.class, "Khong the mo dialog sach", e);
            showAlert(Alert.AlertType.ERROR, "Lỗi",
                    "Không thể mở dialog: " + ExceptionMessageUtil.resolve(e, "Lỗi không xác định"));
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        switch (type) {
            case ERROR -> DialogService.showError(tableBooks, title, message);
            case WARNING -> DialogService.showWarning(tableBooks, title, message);
            default -> DialogService.showInfo(tableBooks, title, message);
        }
    }

    private void refreshSearchSuggestions() {
        if (searchSuggestions != null) {
            searchSuggestions.dispose();
        }
        searchSuggestions = AutoCompleteUtil.bindBookSuggestions(txtSearch, () -> allBooks, this::handleSearch);
    }

    private void runInitStep(String step, Runnable action) {
        try {
            action.run();
        } catch (RuntimeException e) {
            AppLog.error(BookController.class, "Khong the khoi tao buoc quan ly sach: " + step, e);
            if (tableBooks != null && tableBooks.getPlaceholder() == null) {
                tableBooks.setPlaceholder(createPlaceholder("Không thể khởi tạo đầy đủ màn hình sách. Một số tính năng đã được tắt để màn hình vẫn mở."));
            }
        }
    }
}

