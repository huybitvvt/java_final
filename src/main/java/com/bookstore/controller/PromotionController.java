package com.bookstore.controller;

import com.bookstore.controller.support.PromotionDialogSupport;
import com.bookstore.controller.support.PromotionPresentationSupport;
import com.bookstore.model.Promotion;
import com.bookstore.model.Voucher;
import com.bookstore.service.PromotionService;
import com.bookstore.service.VoucherService;
import com.bookstore.util.AppLog;
import com.bookstore.util.DialogService;
import com.bookstore.util.ExceptionMessageUtil;
import com.bookstore.util.FormatUtil;
import com.bookstore.util.InputFieldUtil;
import com.bookstore.util.NotificationService;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * Promotion center for admin users.
 */
public class PromotionController {

    private Label lblRealtimeBadge;
    private Label lblActivePromotionsMetric;
    private Label lblActiveVouchersMetric;
    private Label lblExpiringSoonMetric;
    private Label lblBestOfferMetric;
    private javafx.scene.control.TextField txtSearch;
    private ComboBox<String> cboStatus;
    private TableView<Promotion> tablePromotions;
    private TableColumn<Promotion, String> colPromotionName;
    private TableColumn<Promotion, String> colPromotionType;
    private TableColumn<Promotion, String> colPromotionValue;
    private TableColumn<Promotion, String> colPromotionPeriod;
    private TableColumn<Promotion, String> colPromotionStatus;
    private TableView<Voucher> tableVouchers;
    private TableColumn<Voucher, String> colVoucherCode;
    private TableColumn<Voucher, String> colVoucherType;
    private TableColumn<Voucher, String> colVoucherValue;
    private TableColumn<Voucher, String> colVoucherMinOrder;
    private TableColumn<Voucher, String> colVoucherStatus;
    private Label lblDetailTitle;
    private Label lblDetailWindow;
    private Label lblDetailCondition;
    private Label lblDetailImpact;
    private Label lblDetailStatus;
    private Label lblEngineSummary;
    private Label lblVoucherHighlights;

    private final PromotionService promotionService;
    private final VoucherService voucherService;
    private final ObservableList<Promotion> promotionItems;
    private final ObservableList<Voucher> voucherItems;

    private List<Promotion> allPromotions;
    private List<Voucher> allVouchers;

    public PromotionController() {
        this.promotionService = new PromotionService();
        this.voucherService = new VoucherService();
        this.promotionItems = FXCollections.observableArrayList();
        this.voucherItems = FXCollections.observableArrayList();
        this.allPromotions = new ArrayList<>();
        this.allVouchers = new ArrayList<>();
    }

    public void initialize() {
        setupFilters();
        setupPromotionTable();
        setupVoucherTable();
        setupSelectionBehavior();
        loadData();
    }

    private void setupFilters() {
        cboStatus.getItems().setAll(
                "Tất cả trạng thái",
                "Đang hoạt động",
                "Sắp hết hạn",
                "Chưa bắt đầu",
                "Đã kết thúc",
                "Tạm dừng"
        );
        cboStatus.getSelectionModel().selectFirst();
        cboStatus.valueProperty().addListener((obs, oldValue, newValue) -> applyFilters());

        InputFieldUtil.installCarryOverWorkaround(txtSearch);
        InputFieldUtil.bindKeywordSearch(txtSearch, this::applyFilters);
    }

    private void setupPromotionTable() {
        colPromotionName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPromotionType.setCellValueFactory(cell ->
                new SimpleStringProperty(PromotionPresentationSupport.formatPromotionType(cell.getValue())));
        colPromotionValue.setCellValueFactory(cell ->
                new SimpleStringProperty(PromotionPresentationSupport.formatPromotionValue(cell.getValue())));
        colPromotionPeriod.setCellValueFactory(cell ->
                new SimpleStringProperty(PromotionPresentationSupport.formatPeriod(
                        cell.getValue().getStartDate(), cell.getValue().getEndDate())));
        colPromotionStatus.setCellValueFactory(cell ->
                new SimpleStringProperty(PromotionPresentationSupport.resolvePromotionLifecycle(cell.getValue())));
        colPromotionStatus.setCellFactory(column -> PromotionPresentationSupport.createStatusBadgeCell());

        tablePromotions.setItems(promotionItems);
        tablePromotions.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tablePromotions.setFixedCellSize(48);
        tablePromotions.setPlaceholder(createPlaceholder("Không có khuyến mãi phù hợp."));

        DoubleBinding tableWidth = tablePromotions.widthProperty().subtract(26);
        colPromotionName.prefWidthProperty().bind(tableWidth.multiply(0.28));
        colPromotionType.prefWidthProperty().bind(tableWidth.multiply(0.18));
        colPromotionValue.prefWidthProperty().bind(tableWidth.multiply(0.16));
        colPromotionPeriod.prefWidthProperty().bind(tableWidth.multiply(0.22));
        colPromotionStatus.prefWidthProperty().bind(tableWidth.multiply(0.16));
    }

    private void setupVoucherTable() {
        colVoucherCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colVoucherType.setCellValueFactory(cell ->
                new SimpleStringProperty(PromotionPresentationSupport.formatVoucherType(cell.getValue())));
        colVoucherValue.setCellValueFactory(cell ->
                new SimpleStringProperty(PromotionPresentationSupport.formatVoucherValue(cell.getValue())));
        colVoucherMinOrder.setCellValueFactory(cell ->
                new SimpleStringProperty(FormatUtil.formatCurrency(cell.getValue().getMinimumOrderValue())));
        colVoucherStatus.setCellValueFactory(cell ->
                new SimpleStringProperty(PromotionPresentationSupport.resolveVoucherLifecycle(cell.getValue())));
        colVoucherStatus.setCellFactory(column -> PromotionPresentationSupport.createStatusBadgeCell());

        tableVouchers.setItems(voucherItems);
        tableVouchers.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tableVouchers.setFixedCellSize(48);
        tableVouchers.setPlaceholder(createPlaceholder("Không có mã giảm giá phù hợp."));

        DoubleBinding tableWidth = tableVouchers.widthProperty().subtract(26);
        colVoucherCode.prefWidthProperty().bind(tableWidth.multiply(0.22));
        colVoucherType.prefWidthProperty().bind(tableWidth.multiply(0.16));
        colVoucherValue.prefWidthProperty().bind(tableWidth.multiply(0.18));
        colVoucherMinOrder.prefWidthProperty().bind(tableWidth.multiply(0.20));
        colVoucherStatus.prefWidthProperty().bind(tableWidth.multiply(0.24));
    }

    private void setupSelectionBehavior() {
        tablePromotions.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, promotion) -> {
            if (promotion != null) {
                tableVouchers.getSelectionModel().clearSelection();
                updatePromotionDetails(promotion);
            } else if (tableVouchers.getSelectionModel().getSelectedItem() == null) {
                showDefaultDetails();
            }
        });

        tableVouchers.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, voucher) -> {
            if (voucher != null) {
                tablePromotions.getSelectionModel().clearSelection();
                updateVoucherDetails(voucher);
            } else if (tablePromotions.getSelectionModel().getSelectedItem() == null) {
                showDefaultDetails();
            }
        });
    }

    private void loadData() {
        allPromotions = new ArrayList<>(promotionService.getAllPromotions());
        allPromotions.sort(Comparator.comparing(Promotion::getStartDate, Comparator.nullsLast(Comparator.reverseOrder())));

        allVouchers = new ArrayList<>(voucherService.getAllVouchers());
        allVouchers.sort(Comparator.comparing(Voucher::getStartDate, Comparator.nullsLast(Comparator.reverseOrder())));

        updateOverviewMetrics();
        updateEngineSummary();
        applyFilters();
    }

    private void handleAddPromotion() {
        openPromotionDialogSafely(null);
    }

    private void handleAddVoucher() {
        openVoucherDialogSafely(null);
    }

    private void handleEditSelected() {
        Promotion promotion = tablePromotions.getSelectionModel().getSelectedItem();
        if (promotion != null) {
            openPromotionDialogSafely(promotion);
            return;
        }

        Voucher voucher = tableVouchers.getSelectionModel().getSelectedItem();
        if (voucher != null) {
            openVoucherDialogSafely(voucher);
            return;
        }

        DialogService.showWarning(txtSearch, "Chưa chọn dữ liệu", "Hãy chọn một khuyến mãi hoặc voucher để chỉnh sửa.");
    }

    private void handleDeleteSelected() {
        Promotion promotion = tablePromotions.getSelectionModel().getSelectedItem();
        if (promotion != null) {
            if (DialogService.confirm(tablePromotions, "Xóa khuyến mãi",
                    "Bạn có chắc muốn xóa chương trình \"" + promotion.getName() + "\"?", "Xóa")) {
                if (promotionService.deletePromotion(promotion.getId())) {
                    loadData();
                    NotificationService.showSuccess(tablePromotions, "Đã xóa khuyến mãi thành công");
                } else {
                    DialogService.showError(tablePromotions, "Lỗi", "Không thể xóa khuyến mãi.");
                }
            }
            return;
        }

        Voucher voucher = tableVouchers.getSelectionModel().getSelectedItem();
        if (voucher != null) {
            if (DialogService.confirm(tableVouchers, "Xóa mã giảm giá",
                    "Bạn có chắc muốn xóa voucher \"" + voucher.getCode() + "\"?", "Xóa")) {
                if (voucherService.deleteVoucher(voucher.getId())) {
                    loadData();
                    NotificationService.showSuccess(tableVouchers, "Đã xóa mã giảm giá thành công");
                } else {
                    DialogService.showError(tableVouchers, "Lỗi", "Không thể xóa mã giảm giá.");
                }
            }
            return;
        }

        DialogService.showWarning(txtSearch, "Chưa chọn dữ liệu", "Hãy chọn một khuyến mãi hoặc voucher để xóa.");
    }

    private void openPromotionDialogSafely(Promotion existingPromotion) {
        try {
            Promotion draft = PromotionDialogSupport.showPromotionDialog(tablePromotions, existingPromotion);
            if (draft == null) {
                return;
            }
            boolean success = existingPromotion == null
                    ? promotionService.addPromotion(draft)
                    : promotionService.updatePromotion(draft);

            if (success) {
                resetFilters();
                loadData();
                selectPromotionById(draft.getId());
                NotificationService.showSuccess(tablePromotions,
                        existingPromotion == null ? "Đã tạo khuyến mãi thành công" : "Đã cập nhật khuyến mãi");
            } else {
                DialogService.showError(tablePromotions, "Lỗi", "Không thể lưu khuyến mãi.");
            }
        } catch (IllegalArgumentException e) {
            DialogService.showError(tablePromotions, "Dữ liệu chưa hợp lệ",
                    ExceptionMessageUtil.resolve(e, "Vui lòng kiểm tra lại dữ liệu khuyến mãi."));
        } catch (RuntimeException e) {
            AppLog.error(PromotionController.class, "Khong the luu khuyen mai", e);
            DialogService.showError(tablePromotions, "Lỗi",
                    "Không thể lưu khuyến mãi: " + ExceptionMessageUtil.resolve(e, "Lỗi không xác định"));
        }
    }

    private void openVoucherDialogSafely(Voucher existingVoucher) {
        try {
            Voucher draft = PromotionDialogSupport.showVoucherDialog(tableVouchers, existingVoucher);
            if (draft == null) {
                return;
            }
            boolean success = existingVoucher == null
                    ? voucherService.addVoucher(draft)
                    : voucherService.updateVoucher(draft);

            if (success) {
                resetFilters();
                loadData();
                selectVoucherById(draft.getId());
                NotificationService.showSuccess(tableVouchers,
                        existingVoucher == null ? "Đã tạo mã giảm giá thành công" : "Đã cập nhật mã giảm giá");
            } else {
                DialogService.showError(tableVouchers, "Lỗi", "Không thể lưu voucher.");
            }
        } catch (IllegalArgumentException e) {
            DialogService.showError(tableVouchers, "Dữ liệu chưa hợp lệ",
                    ExceptionMessageUtil.resolve(e, "Vui lòng kiểm tra lại dữ liệu voucher."));
        } catch (RuntimeException e) {
            AppLog.error(PromotionController.class, "Khong the luu voucher", e);
            DialogService.showError(tableVouchers, "Lỗi",
                    "Không thể lưu voucher: " + ExceptionMessageUtil.resolve(e, "Lỗi không xác định"));
        }
    }

    private void applyFilters() {
        String keyword = txtSearch.getText() == null ? "" : txtSearch.getText().trim().toLowerCase(Locale.ROOT);
        String status = cboStatus.getValue();

        promotionItems.setAll(allPromotions.stream()
                .filter(promotion -> PromotionPresentationSupport.matchesPromotionKeyword(promotion, keyword))
                .filter(promotion -> PromotionPresentationSupport.matchesStatusFilter(
                        PromotionPresentationSupport.resolvePromotionLifecycle(promotion), status))
                .toList());

        voucherItems.setAll(allVouchers.stream()
                .filter(voucher -> PromotionPresentationSupport.matchesVoucherKeyword(voucher, keyword))
                .filter(voucher -> PromotionPresentationSupport.matchesStatusFilter(
                        PromotionPresentationSupport.resolveVoucherLifecycle(voucher), status))
                .toList());

        if (!promotionItems.isEmpty()) {
            tablePromotions.getSelectionModel().selectFirst();
        } else if (!voucherItems.isEmpty()) {
            tableVouchers.getSelectionModel().selectFirst();
        } else {
            showDefaultDetails();
        }
    }

    private void resetFilters() {
        txtSearch.clear();
        cboStatus.getSelectionModel().selectFirst();
    }

    private void selectPromotionById(int id) {
        promotionItems.stream()
                .filter(promotion -> promotion.getId() == id)
                .findFirst()
                .ifPresent(promotion -> tablePromotions.getSelectionModel().select(promotion));
    }

    private void selectVoucherById(int id) {
        voucherItems.stream()
                .filter(voucher -> voucher.getId() == id)
                .findFirst()
                .ifPresent(voucher -> tableVouchers.getSelectionModel().select(voucher));
    }

    private void updateOverviewMetrics() {
        LocalDate today = LocalDate.now();

        long activePromotions = allPromotions.stream()
                .filter(promotion -> promotion.isActive(today))
                .count();
        long activeVouchers = allVouchers.stream()
                .filter(voucher -> voucher.isAvailable(today))
                .count();
        long expiringSoon = allPromotions.stream()
                .filter(promotion -> PromotionPresentationSupport.resolvePromotionLifecycle(promotion).equals("Sắp hết hạn"))
                .count()
                + allVouchers.stream()
                .filter(voucher -> PromotionPresentationSupport.resolveVoucherLifecycle(voucher).equals("Sắp hết hạn"))
                .count();

        lblActivePromotionsMetric.setText(String.valueOf(activePromotions));
        lblActiveVouchersMetric.setText(String.valueOf(activeVouchers));
        lblExpiringSoonMetric.setText(String.valueOf(expiringSoon));
        lblBestOfferMetric.setText(PromotionPresentationSupport.resolveBestOfferMetric(allPromotions, allVouchers));
        lblRealtimeBadge.setText((activePromotions + activeVouchers) + " quy tắc đang sẵn sàng cho POS");
    }

    private void updateEngineSummary() {
        lblEngineSummary.setText(PromotionPresentationSupport.buildEngineSummary(
                promotionService.getActivePromotions(LocalDate.now())));
        lblVoucherHighlights.setText(PromotionPresentationSupport.buildVoucherHighlights(
                voucherService.getAllVouchers()));
    }

    private void updatePromotionDetails(Promotion promotion) {
        lblDetailTitle.setText(promotion.getName());
        lblDetailWindow.setText("Hiệu lực: " + PromotionPresentationSupport.formatPeriod(
                promotion.getStartDate(), promotion.getEndDate()));
        lblDetailCondition.setText("Điều kiện: " + PromotionPresentationSupport.resolvePromotionCondition(promotion));
        lblDetailImpact.setText("Tác động tại POS: " + PromotionPresentationSupport.formatPromotionValue(promotion));
        lblDetailStatus.setText("Trạng thái: " + PromotionPresentationSupport.resolvePromotionLifecycle(promotion));
    }

    private void updateVoucherDetails(Voucher voucher) {
        lblDetailTitle.setText("Voucher " + voucher.getCode());
        lblDetailWindow.setText("Hiệu lực: " + PromotionPresentationSupport.formatPeriod(
                voucher.getStartDate(), voucher.getEndDate()));
        lblDetailCondition.setText("Điều kiện: Đơn tối thiểu "
                + FormatUtil.formatCurrency(voucher.getMinimumOrderValue())
                + " • " + PromotionPresentationSupport.formatVoucherUsage(voucher));
        lblDetailImpact.setText("Tác động tại POS: " + PromotionPresentationSupport.formatVoucherValue(voucher));
        lblDetailStatus.setText("Trạng thái: " + PromotionPresentationSupport.resolveVoucherLifecycle(voucher));
    }

    private void showDefaultDetails() {
        lblDetailTitle.setText("Chọn một khuyến mãi hoặc voucher");
        lblDetailWindow.setText("Hiệu lực: Chưa có mục nào được chọn.");
        lblDetailCondition.setText("Điều kiện: Chọn ở bảng bên trái hoặc bên phải để xem quy tắc áp dụng.");
        lblDetailImpact.setText("Tác động tại POS: Hệ thống sẽ dùng cùng engine giá đang hoạt động ở quầy thanh toán.");
        lblDetailStatus.setText("Trạng thái: Sẵn sàng");
    }

    private Label createPlaceholder(String message) {
        Label label = new Label(message);
        label.getStyleClass().add("empty-state-label");
        return label;
    }
}
