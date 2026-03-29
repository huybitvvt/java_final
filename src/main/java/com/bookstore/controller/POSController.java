package com.bookstore.controller;

import com.bookstore.controller.support.PosPricingViewSupport;
import com.bookstore.controller.support.PosTableSupport;
import com.bookstore.model.Book;
import com.bookstore.model.Customer;
import com.bookstore.model.Invoice;
import com.bookstore.model.InvoiceDetail;
import com.bookstore.model.PricingResult;
import com.bookstore.model.Promotion;
import com.bookstore.session.SessionContext;
import com.bookstore.service.BookService;
import com.bookstore.service.CustomerService;
import com.bookstore.service.InvoiceService;
import com.bookstore.service.PricingService;
import com.bookstore.service.PromotionService;
import com.bookstore.service.VoucherService;
import com.bookstore.util.AnimationUtil;
import com.bookstore.util.AppLog;
import com.bookstore.util.AutoCompleteUtil;
import com.bookstore.util.DialogService;
import com.bookstore.util.FormatUtil;
import com.bookstore.util.IconUtil;
import com.bookstore.util.InputFieldUtil;
import com.bookstore.util.NotificationService;
import com.bookstore.util.ResponsiveLayoutUtil;
import com.bookstore.util.TooltipUtil;
import javafx.beans.binding.DoubleBinding;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import org.controlsfx.control.textfield.AutoCompletionBinding;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * POS Controller - Xử lý bán hàng
 */
public class POSController {
    private VBox pageRoot;
    private HBox headerBar;
    private VBox booksPanel;
    private VBox cartPanel;
    private TextField txtSearchBook;
    private TextField txtVoucherCode;
    private Button btnAddToCart;
    private Button btnApplyVoucher;
    private Button btnThanhToan;
    private Button btnHuy;
    private ComboBox<String> cboCategoryFilter;
    private ComboBox<Customer> cboCustomer;
    private ComboBox<String> cboPayment;
    private TableView<Book> tableBooks;
    private TableColumn<Book, Integer> colBookMa;
    private TableColumn<Book, String> colBookTen;
    private TableColumn<Book, String> colBookTacGia;
    private TableColumn<Book, String> colBookTheLoai;
    private TableColumn<Book, Double> colBookGia;
    private TableColumn<Book, Integer> colBookTon;
    private TableView<InvoiceDetail> tableCart;
    private TableColumn<InvoiceDetail, InvoiceDetail> colCartTen;
    private TableColumn<InvoiceDetail, Integer> colCartSL;
    private TableColumn<InvoiceDetail, Double> colCartGia;
    private TableColumn<InvoiceDetail, Double> colCartThanhTien;
    private TableColumn<InvoiceDetail, Void> colCartDieuChinh;
    private TableColumn<InvoiceDetail, Void> colCartXoa;
    private Label lblCartCount;
    private Label lblTongTien;
    private Label lblPromotionDiscount;
    private Label lblVoucherDiscount;
    private Label lblLoyaltyDiscount;
    private Label lblThanhToan;
    private Label lblVoucherStatus;
    private Label lblAppliedPromotions;
    private Label lblCustomerTier;
    private Label lblActivePromotions;
    private Label lblVoucherCodeInfo;

    private final BookService bookService;
    private final CustomerService customerService;
    private final InvoiceService invoiceService;
    private final PromotionService promotionService;
    private final VoucherService voucherService;
    private final PricingService pricingService;
    private final ObservableList<Book> bookList;
    private final ObservableList<InvoiceDetail> cartList;

    private List<Book> allBooks;
    private PricingResult currentPricingResult;
    private String appliedVoucherCode;
    private AutoCompletionBinding<String> bookSuggestions;
    private final boolean compactMode;

    public POSController() {
        this.bookService = new BookService();
        this.customerService = new CustomerService();
        this.invoiceService = new InvoiceService();
        this.promotionService = new PromotionService();
        this.voucherService = new VoucherService();
        this.pricingService = new PricingService();
        this.bookList = FXCollections.observableArrayList();
        this.cartList = FXCollections.observableArrayList();
        this.allBooks = new ArrayList<>();
        this.currentPricingResult = new PricingResult();
        this.appliedVoucherCode = "";
        this.compactMode = ResponsiveLayoutUtil.isCompactScreen();
    }

    public void initialize() {
        runInitStep("configureBookColumns", () -> PosTableSupport.configureBookColumns(
                colBookMa, colBookTen, colBookTacGia, colBookTheLoai, colBookGia, colBookTon));
        runInitStep("configureCartColumns", () -> PosTableSupport.configureCartColumns(
                colCartTen, colCartSL, colCartGia, colCartThanhTien, colCartDieuChinh, colCartXoa,
                this::resolveCartBookName, this::changeQuantity, this::removeFromCart));
        runInitStep("configureCustomerComboBox", () -> PosTableSupport.configureCustomerComboBox(cboCustomer));
        runInitStep("configureTableLayout", () -> PosTableSupport.configureTableLayout(
                tableBooks, tableCart,
                colBookMa, colBookTen, colBookTacGia, colBookTheLoai, colBookGia, colBookTon,
                colCartTen, colCartSL, colCartGia, colCartThanhTien, colCartDieuChinh, colCartXoa));
        runInitStep("decorateControls", this::decorateControls);
        runInitStep("loadBooks", this::loadBooks);
        runInitStep("loadCategories", this::loadCategories);
        runInitStep("loadCustomers", this::loadCustomers);
        runInitStep("setupPaymentMethods", this::setupPaymentMethods);
        runInitStep("renderPromotionHighlights", this::renderPromotionHighlights);

        runInitStep("setupTableDefaults", () -> {
            tableBooks.getSortOrder().clear();
            tableBooks.getSortOrder().add(colBookMa);
            colBookMa.setSortType(TableColumn.SortType.ASCENDING);
            tableBooks.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            tableCart.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
            tableCart.setItems(cartList);
        });
        runInitStep("applyCompactLayout", this::applyCompactLayout);
        runInitStep("installInputWorkaround", () -> InputFieldUtil.installCarryOverWorkaround(txtSearchBook, txtVoucherCode));
        runInitStep("bindBookSearch", () -> InputFieldUtil.bindKeywordSearch(txtSearchBook, this::applyBookFilters));
        runInitStep("bindCategoryFilter", () ->
                cboCategoryFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyBookFilters()));
        runInitStep("bindCustomerPricing", () ->
                cboCustomer.valueProperty().addListener((obs, oldVal, newVal) -> updatePricing()));
        runInitStep("bindVoucherInput", () ->
                txtVoucherCode.textProperty().addListener((obs, oldVal, newVal) -> handleVoucherInputChange(newVal)));

        runInitStep("playAnimations", () -> AnimationUtil.playStagger(List.of(headerBar, booksPanel, cartPanel)));
        runInitStep("updatePricing", this::updatePricing);
    }

    private void decorateControls() {
        IconUtil.apply(btnAddToCart, "fas-cart-plus", "Thêm sách đang chọn vào giỏ");
        IconUtil.apply(btnApplyVoucher, "fas-ticket-alt", "Áp dụng mã giảm giá cho đơn hàng");
        IconUtil.apply(btnThanhToan, "fas-credit-card", "Thanh toán và tạo hóa đơn");
        IconUtil.apply(btnHuy, "fas-sync-alt", "Làm mới giỏ hàng hiện tại");
        TooltipUtil.install(txtSearchBook, "Nhập tên sách, tác giả hoặc thể loại để tìm nhanh");
        TooltipUtil.install(cboCategoryFilter, "Lọc nhanh danh sách theo thể loại");
        TooltipUtil.install(cboCustomer, "Chọn khách hàng để áp dụng hạng thành viên");
        TooltipUtil.install(cboPayment, "Chọn phương thức thanh toán");
    }

    private void applyCompactLayout() {
        if (!compactMode) {
            return;
        }

        btnAddToCart.setText("Thêm");
        btnApplyVoucher.setText("Áp mã");
        btnHuy.setText("Mới");
        btnThanhToan.setText("Thanh toán");
        txtSearchBook.setPromptText("Tìm theo tên hoặc tác giả");
        cboCategoryFilter.setPromptText("Thể loại");
        colBookTen.setText("Tên sách");
        colBookTacGia.setText("Tác giả");
        colBookTheLoai.setText("TL");
        colBookGia.setText("Giá");
        colCartGia.setText("Đ.giá");
        colCartThanhTien.setText("T.tiền");
        colCartDieuChinh.setText("+/-");

        if (booksPanel.getParent() instanceof VBox) {
            booksPanel.setMinWidth(0);
            booksPanel.setPrefWidth(Region.USE_COMPUTED_SIZE);
            booksPanel.setMaxWidth(Double.MAX_VALUE);
            cartPanel.setMinWidth(0);
            cartPanel.setPrefWidth(Region.USE_COMPUTED_SIZE);
            cartPanel.setMaxWidth(Double.MAX_VALUE);
        } else {
            booksPanel.setMinWidth(680);
            booksPanel.setPrefWidth(760);
            booksPanel.setMaxWidth(Double.MAX_VALUE);
            cartPanel.setMinWidth(440);
            cartPanel.setPrefWidth(500);
            cartPanel.setMaxWidth(560);
            HBox.setHgrow(booksPanel, Priority.ALWAYS);
            HBox.setHgrow(cartPanel, Priority.SOMETIMES);
        }

        cboCategoryFilter.setPrefWidth(150);
        cboCustomer.setPrefWidth(200);
        cboPayment.setPrefWidth(150);
        btnThanhToan.setPrefWidth(148);
        btnHuy.setPrefWidth(92);
    }

    private String resolveCartBookName(InvoiceDetail detail) {
        if (detail == null) {
            return "";
        }
        if (detail.getSach() != null
                && detail.getSach().getTenSach() != null
                && !detail.getSach().getTenSach().isBlank()) {
            return detail.getSach().getTenSach();
        }
        return allBooks.stream()
                .filter(book -> book.getMaSach() == detail.getMaSach())
                .map(Book::getTenSach)
                .filter(name -> name != null && !name.isBlank())
                .findFirst()
                .orElse("Sách #" + detail.getMaSach());
    }

    private void loadBooks() {
        List<Book> books = bookService.getAllBooks();
        if (books == null || books.isEmpty()) {
            allBooks = new ArrayList<>();
            bookList.clear();
            tableBooks.setItems(bookList);
            tableBooks.setPlaceholder(PosTableSupport.createPlaceholder(
                    "Không tải được dữ liệu sách. Kiểm tra kết nối MySQL hoặc dữ liệu hiện đang trống."));
            return;
        }

        tableBooks.setPlaceholder(PosTableSupport.createPlaceholder("Không tìm thấy sách phù hợp."));
        books.sort(Comparator.comparingInt(Book::getMaSach));
        allBooks = books;
        refreshSearchSuggestions();
        applyBookFilters();
    }

    private void loadCategories() {
        List<String> categories = bookService.getAllCategories();
        cboCategoryFilter.getItems().clear();
        cboCategoryFilter.getItems().add("Tất cả thể loại");
        if (categories != null) {
            cboCategoryFilter.getItems().addAll(categories);
        }
        cboCategoryFilter.getSelectionModel().selectFirst();
    }

    private void loadCustomers() {
        cboCustomer.getItems().clear();
        List<Customer> customers = customerService.getAllCustomers();
        if (customers == null) {
            lblCustomerTier.setText("Không tải được khách hàng");
            return;
        }
        customers.stream()
                .filter(customer -> customer != null)
                .sorted(Comparator.comparing(customer -> safeText(customer.getHoTen()), String.CASE_INSENSITIVE_ORDER))
                .forEach(cboCustomer.getItems()::add);
    }

    private void setupPaymentMethods() {
        cboPayment.getItems().setAll("Tiền mặt", "Chuyển khoản", "Quẹt thẻ");
        cboPayment.getSelectionModel().select("Tiền mặt");
    }

    private void renderPromotionHighlights() {
        List<Promotion> promotions = promotionService.getActivePromotions(LocalDate.now());
        if (promotions == null || promotions.isEmpty()) {
            lblActivePromotions.setText("Hiện chưa có khuyến mãi nào đang áp dụng tự động.");
            return;
        }

        List<String> summaries = promotions.stream()
                .filter(promotion -> promotion != null && promotion.getName() != null && !promotion.getName().isBlank())
                .limit(3)
                .map(Promotion::getName)
                .toList();
        lblActivePromotions.setText(summaries.isEmpty()
                ? "Hiện chưa có khuyến mãi nào đang áp dụng tự động."
                : String.join(" • ", summaries));
    }

    private void handleAddToCart() {
        Book selectedBook = tableBooks.getSelectionModel().getSelectedItem();
        if (selectedBook == null) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn sách để thêm vào giỏ.");
            return;
        }

        InvoiceDetail existingDetail = cartList.stream()
                .filter(detail -> detail.getMaSach() == selectedBook.getMaSach())
                .findFirst()
                .orElse(null);

        if (existingDetail != null) {
            if (existingDetail.getSoLuong() >= selectedBook.getSoLuongTon()) {
                showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Số lượng trong giỏ đã đạt tồn kho hiện có.");
                return;
            }
            existingDetail.setSoLuong(existingDetail.getSoLuong() + 1);
            existingDetail.tinhThanhTien();
        } else {
            if (selectedBook.getSoLuongTon() <= 0) {
                showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Sách đã hết hàng.");
                return;
            }
            InvoiceDetail detail = new InvoiceDetail();
            detail.setMaSach(selectedBook.getMaSach());
            detail.setSach(selectedBook);
            detail.setSoLuong(1);
            detail.setDonGia(selectedBook.getGiaBia());
            detail.setGiamGia(0);
            detail.tinhThanhTien();
            cartList.add(detail);
        }

        tableCart.refresh();
        updatePricing();
    }

    private void handleApplyVoucher() {
        appliedVoucherCode = txtVoucherCode.getText() != null ? txtVoucherCode.getText().trim() : "";
        updatePricing();
        if (appliedVoucherCode.isBlank()) {
            NotificationService.showInfo(tableCart, "Đã xóa mã giảm giá khỏi đơn hàng");
            return;
        }
        if (currentPricingResult.getVoucher() != null) {
            NotificationService.showSuccess(tableCart, "Áp dụng mã " + currentPricingResult.getVoucher().getCode() + " thành công");
        } else {
            NotificationService.showWarning(tableCart, "Mã giảm giá chưa hợp lệ hoặc chưa đủ điều kiện");
        }
    }

    private void handleCheckout() {
        if (cartList.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Giỏ hàng đang trống.");
            return;
        }

        if (cboPayment.getValue() == null || cboPayment.getValue().isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Vui lòng chọn phương thức thanh toán.");
            return;
        }

        PricingResult pricing = pricingService.calculate(new ArrayList<>(cartList), cboCustomer.getValue(), appliedVoucherCode);
        Invoice invoice = new Invoice();
        invoice.setMaKhachHang(cboCustomer.getValue() != null ? cboCustomer.getValue().getMaKhachHang() : 0);
        invoice.setMaNhanVien(resolveEmployeeId());
        invoice.setTongTien(pricing.getSubtotal());
        invoice.setGiamGia(pricing.getEffectiveDiscountPercent());
        invoice.setThanhToan(pricing.getFinalTotal());
        invoice.setPhuongThucThanhToan(cboPayment.getValue());
        invoice.setGhiChu(buildCheckoutNote(pricing));

        if (invoiceService.createInvoice(invoice, new ArrayList<>(cartList))) {
            NotificationService.showSuccess(tableCart,
                    "Thanh toán thành công: " + FormatUtil.formatCurrency(pricing.getFinalTotal()));
            handleCancel();
            loadBooks();
        } else {
            showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể tạo hóa đơn. Vui lòng thử lại.");
        }
    }

    private void handleCancel() {
        cartList.clear();
        txtVoucherCode.clear();
        appliedVoucherCode = "";
        cboCustomer.getSelectionModel().clearSelection();
        cboPayment.getSelectionModel().select("Tiền mặt");
        tableCart.refresh();
        updatePricing();
    }

    private void applyBookFilters() {
        String keyword = txtSearchBook.getText() != null ? txtSearchBook.getText().trim().toLowerCase() : "";
        String category = cboCategoryFilter.getValue();

        bookList.clear();
        allBooks.stream()
                .filter(book -> category == null || category.equals("Tất cả thể loại")
                        || (book.getTheLoai() != null && book.getTheLoai().equalsIgnoreCase(category)))
                .filter(book -> keyword.isBlank()
                        || containsIgnoreCase(book.getTenSach(), keyword)
                        || containsIgnoreCase(book.getTacGia(), keyword))
                .sorted(Comparator.comparingInt(Book::getMaSach))
                .forEach(bookList::add);

        tableBooks.setItems(bookList);
        tableBooks.sort();
    }

    private void updatePricing() {
        currentPricingResult = pricingService.calculate(new ArrayList<>(cartList), cboCustomer.getValue(), appliedVoucherCode);
        PosPricingViewSupport.applyPricingState(
                cartList,
                currentPricingResult,
                cboCustomer.getValue(),
                appliedVoucherCode,
                txtVoucherCode,
                voucherService,
                lblCartCount,
                lblTongTien,
                lblPromotionDiscount,
                lblVoucherDiscount,
                lblLoyaltyDiscount,
                lblThanhToan,
                lblVoucherStatus,
                lblAppliedPromotions,
                lblCustomerTier,
                lblVoucherCodeInfo
        );
    }

    private void changeQuantity(int index, int delta) {
        if (index < 0 || index >= cartList.size()) {
            return;
        }

        InvoiceDetail detail = cartList.get(index);
        int newQuantity = detail.getSoLuong() + delta;
        int maxQuantity = detail.getSach() != null ? detail.getSach().getSoLuongTon() : Integer.MAX_VALUE;

        if (newQuantity <= 0) {
            cartList.remove(index);
        } else if (newQuantity > maxQuantity) {
            showAlert(Alert.AlertType.WARNING, "Cảnh báo", "Không thể vượt quá số lượng tồn kho hiện có.");
            return;
        } else {
            detail.setSoLuong(newQuantity);
            detail.tinhThanhTien();
        }

        tableCart.refresh();
        updatePricing();
    }

    private void removeFromCart(int index) {
        if (index >= 0 && index < cartList.size()) {
            cartList.remove(index);
            tableCart.refresh();
            updatePricing();
        }
    }

    private void handleVoucherInputChange(String newValue) {
        if (newValue == null || newValue.isBlank()) {
            appliedVoucherCode = "";
            updatePricing();
            return;
        }

        if (!newValue.trim().equalsIgnoreCase(appliedVoucherCode)) {
            lblVoucherStatus.getStyleClass().removeAll("status-text-success", "status-text-error", "status-text-muted");
            lblVoucherStatus.getStyleClass().add("status-text-muted");
            lblVoucherStatus.setText("Bấm \"Áp dụng mã\" để xác nhận mã mới.");
        }
        updatePricing();
    }

    private int resolveEmployeeId() {
        if (SessionContext.getCurrentUser() != null && SessionContext.getCurrentUser().getMaNhanVien() > 0) {
            return SessionContext.getCurrentUser().getMaNhanVien();
        }
        return 1;
    }

    private String buildCheckoutNote(PricingResult pricing) {
        List<String> notes = new ArrayList<>();
        if (!pricing.getAppliedPromotions().isEmpty()) {
            notes.add("KM: " + String.join(", ", pricing.getAppliedPromotions()));
        }
        if (pricing.getVoucher() != null) {
            notes.add("Voucher: " + pricing.getVoucher().getCode());
        }
        if (cboCustomer.getValue() != null) {
            notes.add("Hạng KH: " + PosPricingViewSupport.formatTierLabel(pricing.getLoyaltyTier()));
        }
        return String.join(" | ", notes);
    }

    private boolean containsIgnoreCase(String value, String keyword) {
        return value != null && value.toLowerCase().contains(keyword);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        switch (type) {
            case ERROR -> DialogService.showError(tableCart, title, message);
            case WARNING -> DialogService.showWarning(tableCart, title, message);
            default -> DialogService.showInfo(tableCart, title, message);
        }
    }

    private void refreshSearchSuggestions() {
        if (bookSuggestions != null) {
            bookSuggestions.dispose();
        }
        bookSuggestions = AutoCompleteUtil.bindBookSuggestions(txtSearchBook, () -> allBooks, this::applyBookFilters);
    }

    private String safeText(String value) {
        return value == null ? "" : value.trim();
    }

    private void runInitStep(String step, Runnable action) {
        try {
            action.run();
        } catch (RuntimeException e) {
            AppLog.error(POSController.class, "Khong the khoi tao buoc POS: " + step, e);
            if (tableBooks != null && tableBooks.getPlaceholder() == null) {
                tableBooks.setPlaceholder(PosTableSupport.createPlaceholder(
                        "Không thể khởi tạo đầy đủ quầy thanh toán. Một số tính năng đã được tắt để màn hình vẫn mở."));
            }
            if (lblActivePromotions != null && (lblActivePromotions.getText() == null || lblActivePromotions.getText().isBlank())) {
                lblActivePromotions.setText("Không thể tải danh sách khuyến mãi.");
            }
        }
    }
}
