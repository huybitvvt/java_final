package com.bookstore.view;

import com.bookstore.controller.POSController;
import com.bookstore.model.Book;
import com.bookstore.model.Customer;
import com.bookstore.model.InvoiceDetail;
import com.bookstore.util.ResponsiveLayoutUtil;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Handcrafted JavaFX POS view to replace the generated builder on the checkout screen.
 */
public final class PosView {

    private PosView() {
    }

    public static ViewBundle create() {
        POSController controller = new POSController();
        boolean compactMode = ResponsiveLayoutUtil.isCompactScreen();

        ScrollPane root = new ScrollPane();
        root.setFitToWidth(true);
        root.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        root.getStyleClass().add("page-scroll");

        VBox pageRoot = new VBox(18);
        pageRoot.getStyleClass().add("page-root");
        GeneratedViewSupport.bind(controller, "pageRoot", pageRoot);

        HBox headerBar = new HBox();
        headerBar.setAlignment(Pos.CENTER_LEFT);
        headerBar.getStyleClass().add("page-header");
        GeneratedViewSupport.bind(controller, "headerBar", headerBar);

        VBox titleBox = new VBox(4);
        titleBox.getChildren().addAll(
                label("Quầy thanh toán", "section-title"),
                wrappingLabel("Bán hàng trực tiếp với giỏ hàng realtime, voucher và ưu đãi thành viên.", "section-subtitle")
        );

        Label lblCustomerTier = label("Khách lẻ", "dashboard-badge");
        GeneratedViewSupport.bind(controller, "lblCustomerTier", lblCustomerTier);

        headerBar.getChildren().addAll(titleBox, spacer(), lblCustomerTier);

        Pane contentContainer = compactMode ? new VBox(16) : new HBox(16);
        VBox.setVgrow(contentContainer, Priority.ALWAYS);

        VBox booksPanel = new VBox(16);
        booksPanel.setPrefWidth(660);
        booksPanel.getStyleClass().add("pos-panel");
        booksPanel.setMaxWidth(Double.MAX_VALUE);
        if (!compactMode) {
            HBox.setHgrow(booksPanel, Priority.ALWAYS);
        }
        GeneratedViewSupport.bind(controller, "booksPanel", booksPanel);

        VBox booksTitle = new VBox(4);
        booksTitle.getChildren().addAll(
                label("Danh sách sách", "side-panel-title"),
                wrappingLabel("Tìm kiếm nhanh theo tên, tác giả và lọc theo thể loại để thêm vào giỏ.", "side-panel-subtitle")
        );

        HBox bookToolbar = new HBox(10);
        TextField txtSearchBook = new TextField();
        txtSearchBook.setPromptText("Tìm sách theo tên hoặc tác giả");
        HBox.setHgrow(txtSearchBook, Priority.ALWAYS);
        GeneratedViewSupport.bind(controller, "txtSearchBook", txtSearchBook);

        ComboBox<String> cboCategoryFilter = new ComboBox<>();
        cboCategoryFilter.setPrefWidth(180);
        cboCategoryFilter.setPromptText("Tất cả thể loại");
        GeneratedViewSupport.bind(controller, "cboCategoryFilter", cboCategoryFilter);

        Button btnAddToCart = button("Thêm vào giỏ", "primary-button");
        GeneratedViewSupport.bindAction(btnAddToCart, controller, "handleAddToCart");
        GeneratedViewSupport.bind(controller, "btnAddToCart", btnAddToCart);
        bookToolbar.getChildren().addAll(txtSearchBook, cboCategoryFilter, btnAddToCart);

        TableView<Book> tableBooks = new TableView<>();
        VBox.setVgrow(tableBooks, Priority.ALWAYS);
        GeneratedViewSupport.bind(controller, "tableBooks", tableBooks);
        tableBooks.getColumns().addAll(
                bindColumn(controller, "colBookMa", column("Mã", 60)),
                bindColumn(controller, "colBookTen", column("Tên sách", 220)),
                bindColumn(controller, "colBookTacGia", column("Tác giả", 150)),
                bindColumn(controller, "colBookTheLoai", column("Thể loại", 120)),
                bindColumn(controller, "colBookGia", column("Giá bán", 120)),
                bindColumn(controller, "colBookTon", column("Tồn kho", 90))
        );

        VBox promotionCard = new VBox(8);
        promotionCard.getStyleClass().add("side-panel-card");
        Label lblActivePromotions = wrappingLabel("Đang tải danh sách khuyến mãi...", "side-panel-subtitle");
        GeneratedViewSupport.bind(controller, "lblActivePromotions", lblActivePromotions);
        promotionCard.getChildren().addAll(
                label("Khuyến mãi đang bật", "side-panel-title"),
                lblActivePromotions
        );

        booksPanel.getChildren().addAll(booksTitle, bookToolbar, tableBooks, promotionCard);

        VBox cartPanel = new VBox(16);
        cartPanel.setMinWidth(560);
        cartPanel.setPrefWidth(560);
        cartPanel.getStyleClass().add("pos-panel");
        cartPanel.setMaxWidth(Double.MAX_VALUE);
        if (!compactMode) {
            HBox.setHgrow(cartPanel, Priority.ALWAYS);
        }
        GeneratedViewSupport.bind(controller, "cartPanel", cartPanel);

        HBox cartHeader = new HBox();
        cartHeader.setAlignment(Pos.CENTER_LEFT);
        VBox cartTitle = new VBox(4);
        Label lblCartCount = label("0 cuốn trong giỏ", "side-panel-subtitle");
        GeneratedViewSupport.bind(controller, "lblCartCount", lblCartCount);
        cartTitle.getChildren().addAll(label("Giỏ hàng", "side-panel-title"), lblCartCount);
        cartHeader.getChildren().add(cartTitle);

        HBox customerRow = new HBox(10);
        ComboBox<Customer> cboCustomer = new ComboBox<>();
        cboCustomer.setPrefWidth(220);
        cboCustomer.setPromptText("Khách lẻ");
        HBox.setHgrow(cboCustomer, Priority.ALWAYS);
        GeneratedViewSupport.bind(controller, "cboCustomer", cboCustomer);

        ComboBox<String> cboPayment = new ComboBox<>();
        cboPayment.setPrefWidth(180);
        GeneratedViewSupport.bind(controller, "cboPayment", cboPayment);
        customerRow.getChildren().addAll(cboCustomer, cboPayment);

        TableView<InvoiceDetail> tableCart = new TableView<>();
        VBox.setVgrow(tableCart, Priority.ALWAYS);
        GeneratedViewSupport.bind(controller, "tableCart", tableCart);
        tableCart.getColumns().addAll(
                bindColumn(controller, "colCartTen", column("Tên sách", 150)),
                bindColumn(controller, "colCartSL", column("SL", 75)),
                bindColumn(controller, "colCartGia", column("Đơn giá", 100)),
                bindColumn(controller, "colCartThanhTien", column("Thành tiền", 110)),
                bindColumn(controller, "colCartDieuChinh", column("Điều chỉnh", 110)),
                bindColumn(controller, "colCartXoa", column("Xóa", 55))
        );

        VBox summaryCard = new VBox(10);
        summaryCard.getStyleClass().add("pos-summary-card");

        Label lblTongTien = label("0 ₫", "summary-value");
        GeneratedViewSupport.bind(controller, "lblTongTien", lblTongTien);
        summaryCard.getChildren().add(summaryRow("Tạm tính", lblTongTien, "summary-label"));

        Label lblPromotionDiscount = label("- 0 ₫", "discount-value");
        GeneratedViewSupport.bind(controller, "lblPromotionDiscount", lblPromotionDiscount);
        summaryCard.getChildren().add(summaryRow("Khuyến mãi tự động", lblPromotionDiscount, "summary-label"));

        HBox voucherRow = new HBox(8);
        TextField txtVoucherCode = new TextField();
        txtVoucherCode.setPromptText("Nhập mã giảm giá");
        HBox.setHgrow(txtVoucherCode, Priority.ALWAYS);
        GeneratedViewSupport.bind(controller, "txtVoucherCode", txtVoucherCode);

        Button btnApplyVoucher = button("Áp dụng mã", "chip-button");
        GeneratedViewSupport.bindAction(btnApplyVoucher, controller, "handleApplyVoucher");
        GeneratedViewSupport.bind(controller, "btnApplyVoucher", btnApplyVoucher);
        voucherRow.getChildren().addAll(txtVoucherCode, btnApplyVoucher);

        Label lblVoucherCodeInfo = wrappingLabel("Mã giảm giá đang khả dụng sẽ hiển thị tại đây.", "side-panel-subtitle");
        GeneratedViewSupport.bind(controller, "lblVoucherCodeInfo", lblVoucherCodeInfo);

        Label lblVoucherStatus = wrappingLabel("Chưa áp dụng mã giảm giá.", "status-text-muted");
        GeneratedViewSupport.bind(controller, "lblVoucherStatus", lblVoucherStatus);

        Label lblVoucherDiscount = label("- 0 ₫", "discount-value");
        GeneratedViewSupport.bind(controller, "lblVoucherDiscount", lblVoucherDiscount);

        Label lblLoyaltyDiscount = label("- 0 ₫", "discount-value");
        GeneratedViewSupport.bind(controller, "lblLoyaltyDiscount", lblLoyaltyDiscount);

        Label lblAppliedPromotions = wrappingLabel("Khuyến mãi áp dụng: Chưa có", "promo-inline-label");
        GeneratedViewSupport.bind(controller, "lblAppliedPromotions", lblAppliedPromotions);

        Label lblThanhToan = label("0 ₫", "checkout-total-value");
        GeneratedViewSupport.bind(controller, "lblThanhToan", lblThanhToan);

        summaryCard.getChildren().addAll(
                voucherRow,
                lblVoucherCodeInfo,
                lblVoucherStatus,
                summaryRow("Mã giảm giá", lblVoucherDiscount, "summary-label"),
                summaryRow("Ưu đãi thành viên", lblLoyaltyDiscount, "summary-label"),
                lblAppliedPromotions,
                totalRow("Cần thanh toán", lblThanhToan)
        );

        HBox actionRow = new HBox(10);
        actionRow.setAlignment(Pos.CENTER_RIGHT);

        Button btnThanhToan = button("Thanh toán", "primary-button");
        btnThanhToan.setPrefHeight(42);
        btnThanhToan.setPrefWidth(170);
        GeneratedViewSupport.bindAction(btnThanhToan, controller, "handleCheckout");
        GeneratedViewSupport.bind(controller, "btnThanhToan", btnThanhToan);

        Button btnHuy = button("Làm mới", "chip-button");
        btnHuy.setPrefHeight(42);
        btnHuy.setPrefWidth(110);
        GeneratedViewSupport.bindAction(btnHuy, controller, "handleCancel");
        GeneratedViewSupport.bind(controller, "btnHuy", btnHuy);

        actionRow.getChildren().addAll(btnThanhToan, btnHuy);

        cartPanel.getChildren().addAll(cartHeader, customerRow, tableCart, summaryCard, actionRow);

        contentContainer.getChildren().addAll(booksPanel, cartPanel);
        pageRoot.getChildren().addAll(headerBar, contentContainer);
        root.setContent(pageRoot);

        GeneratedViewSupport.initialize(controller);
        return new ViewBundle(root, controller);
    }

    private static Label label(String text, String styleClass) {
        Label label = new Label(text);
        label.getStyleClass().add(styleClass);
        return label;
    }

    private static Label wrappingLabel(String text, String styleClass) {
        Label label = label(text, styleClass);
        label.setWrapText(true);
        return label;
    }

    private static Button button(String text, String styleClass) {
        Button button = new Button(text);
        button.setMnemonicParsing(false);
        button.getStyleClass().add(styleClass);
        return button;
    }

    private static Region spacer() {
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        return spacer;
    }

    private static HBox summaryRow(String title, Label valueLabel, String titleStyleClass) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("summary-row");
        row.getChildren().addAll(label(title, titleStyleClass), spacer(), valueLabel);
        return row;
    }

    private static HBox totalRow(String title, Label valueLabel) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("summary-total-row");
        row.getChildren().addAll(label(title, "checkout-total-label"), spacer(), valueLabel);
        return row;
    }

    private static <S, T> TableColumn<S, T> column(String text, double prefWidth) {
        TableColumn<S, T> column = new TableColumn<>(text);
        column.setPrefWidth(prefWidth);
        return column;
    }

    private static <T> T bindColumn(Object controller, String fieldName, T column) {
        return GeneratedViewSupport.bind(controller, fieldName, column);
    }
}
