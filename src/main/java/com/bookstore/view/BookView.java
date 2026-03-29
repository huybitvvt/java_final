package com.bookstore.view;

import com.bookstore.controller.BookController;
import com.bookstore.model.Book;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Handcrafted JavaFX book management view to avoid generated builder issues.
 */
public final class BookView {

    private BookView() {
    }

    public static ViewBundle create() {
        BookController controller = new BookController();

        AnchorPane root = new AnchorPane();

        VBox pageRoot = new VBox(18);
        pageRoot.getStyleClass().add("page-root");
        AnchorPane.setTopAnchor(pageRoot, 0.0);
        AnchorPane.setRightAnchor(pageRoot, 0.0);
        AnchorPane.setBottomAnchor(pageRoot, 0.0);
        AnchorPane.setLeftAnchor(pageRoot, 0.0);
        GeneratedViewSupport.bind(controller, "pageRoot", pageRoot);

        HBox headerBar = new HBox();
        headerBar.setAlignment(Pos.CENTER_LEFT);
        headerBar.getStyleClass().add("page-header");
        GeneratedViewSupport.bind(controller, "headerBar", headerBar);

        VBox titleBox = new VBox(4);
        Label title = label("Quản lý sách", "section-title");
        Label subtitle = label(
                "Quản lý danh mục sách, tồn kho, giá bán và xuất dữ liệu theo một không gian làm việc thống nhất.",
                "section-subtitle"
        );
        subtitle.setWrapText(true);
        titleBox.getChildren().addAll(title, subtitle);

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        Label lblTotal = label("Tổng số: 0", "dashboard-badge");
        GeneratedViewSupport.bind(controller, "lblTotal", lblTotal);

        headerBar.getChildren().addAll(titleBox, headerSpacer, lblTotal);

        VBox contentCard = new VBox(16);
        contentCard.getStyleClass().add("chart-panel");
        VBox.setVgrow(contentCard, Priority.ALWAYS);
        GeneratedViewSupport.bind(controller, "contentCard", contentCard);

        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.getStyleClass().add("screen-toolbar");

        TextField txtSearch = new TextField();
        txtSearch.setPromptText("Tìm theo tên sách, tác giả hoặc thể loại...");
        HBox.setHgrow(txtSearch, Priority.ALWAYS);
        GeneratedViewSupport.bind(controller, "txtSearch", txtSearch);

        ComboBox<String> cboCategory = new ComboBox<>();
        cboCategory.setPrefWidth(190);
        cboCategory.setPromptText("Tất cả thể loại");
        GeneratedViewSupport.bind(controller, "cboCategory", cboCategory);

        Button btnSearch = button("Tìm kiếm", "chip-button");
        GeneratedViewSupport.bindAction(btnSearch, controller, "handleSearch");
        GeneratedViewSupport.bind(controller, "btnSearch", btnSearch);

        Button btnAdvancedSearch = button("Lọc nâng cao", "chip-button");
        GeneratedViewSupport.bindAction(btnAdvancedSearch, controller, "handleAdvancedSearch");
        GeneratedViewSupport.bind(controller, "btnAdvancedSearch", btnAdvancedSearch);

        Button btnAdd = button("Thêm mới", "primary-button");
        GeneratedViewSupport.bindAction(btnAdd, controller, "handleAdd");
        GeneratedViewSupport.bind(controller, "btnAdd", btnAdd);

        Button btnExport = button("Xuất Excel", "chip-button");
        GeneratedViewSupport.bindAction(btnExport, controller, "handleExport");
        GeneratedViewSupport.bind(controller, "btnExport", btnExport);

        Button btnRefresh = button("Làm mới", "chip-button");
        GeneratedViewSupport.bindAction(btnRefresh, controller, "handleRefresh");
        GeneratedViewSupport.bind(controller, "btnRefresh", btnRefresh);

        toolbar.getChildren().addAll(
                txtSearch,
                cboCategory,
                btnSearch,
                btnAdvancedSearch,
                btnAdd,
                btnExport,
                btnRefresh
        );

        TableView<Book> tableBooks = new TableView<>();
        VBox.setVgrow(tableBooks, Priority.ALWAYS);
        GeneratedViewSupport.bind(controller, "tableBooks", tableBooks);

        TableColumn<Book, Integer> colMaSach = column("Mã", 70);
        GeneratedViewSupport.bind(controller, "colMaSach", colMaSach);

        TableColumn<Book, String> colTenSach = column("Tên sách", 260);
        GeneratedViewSupport.bind(controller, "colTenSach", colTenSach);

        TableColumn<Book, String> colTacGia = column("Tác giả", 170);
        GeneratedViewSupport.bind(controller, "colTacGia", colTacGia);

        TableColumn<Book, String> colNXB = column("Nhà xuất bản", 150);
        GeneratedViewSupport.bind(controller, "colNXB", colNXB);

        TableColumn<Book, String> colTheLoai = column("Thể loại", 120);
        GeneratedViewSupport.bind(controller, "colTheLoai", colTheLoai);

        TableColumn<Book, Integer> colNamXB = column("Năm XB", 100);
        GeneratedViewSupport.bind(controller, "colNamXB", colNamXB);

        TableColumn<Book, Double> colGiaBia = column("Giá bìa", 120);
        GeneratedViewSupport.bind(controller, "colGiaBia", colGiaBia);

        TableColumn<Book, Integer> colSoLuong = column("Tồn kho", 100);
        GeneratedViewSupport.bind(controller, "colSoLuong", colSoLuong);

        TableColumn<Book, String> colHanhDong = column("Hành động", 170);
        GeneratedViewSupport.bind(controller, "colHanhDong", colHanhDong);

        tableBooks.getColumns().addAll(
                colMaSach,
                colTenSach,
                colTacGia,
                colNXB,
                colTheLoai,
                colNamXB,
                colGiaBia,
                colSoLuong,
                colHanhDong
        );

        HBox footer = new HBox(10);
        footer.setAlignment(Pos.CENTER_LEFT);
        footer.getStyleClass().add("screen-footer");

        Label footerHint = label(
                "Hiển thị danh sách sách theo phân trang để dễ thao tác hơn trên kho dữ liệu lớn.",
                "summary-inline-text"
        );
        footerHint.setWrapText(true);

        Region footerSpacer = new Region();
        HBox.setHgrow(footerSpacer, Priority.ALWAYS);

        Label pageSizeLabel = label("Số dòng/trang", "summary-inline-text");

        ComboBox<Integer> cboPageSize = new ComboBox<>();
        cboPageSize.setPrefWidth(88);
        GeneratedViewSupport.bind(controller, "cboPageSize", cboPageSize);

        footer.getChildren().addAll(footerHint, footerSpacer, pageSizeLabel, cboPageSize);

        Pagination pagination = new Pagination();
        GeneratedViewSupport.bind(controller, "pagination", pagination);

        contentCard.getChildren().addAll(toolbar, tableBooks, footer, pagination);
        pageRoot.getChildren().addAll(headerBar, contentCard);
        root.getChildren().add(pageRoot);

        GeneratedViewSupport.initialize(controller);
        return new ViewBundle(root, controller);
    }

    private static Label label(String text, String styleClass) {
        Label label = new Label(text);
        label.getStyleClass().add(styleClass);
        return label;
    }

    private static Button button(String text, String styleClass) {
        Button button = new Button(text);
        button.getStyleClass().add(styleClass);
        button.setMnemonicParsing(false);
        return button;
    }

    private static <S, T> TableColumn<S, T> column(String text, double prefWidth) {
        TableColumn<S, T> column = new TableColumn<>(text);
        column.setPrefWidth(prefWidth);
        return column;
    }
}
