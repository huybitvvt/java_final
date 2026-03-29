package com.bookstore.controller.support;

import com.bookstore.model.Promotion;
import com.bookstore.model.Voucher;
import com.bookstore.util.DialogService;
import com.bookstore.util.InputFieldUtil;
import com.bookstore.util.ThemeManager;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.Locale;

/**
 * Builds promotion and voucher forms outside the main controller.
 */
public final class PromotionDialogSupport {

    private PromotionDialogSupport() {
    }

    public static Promotion showPromotionDialog(Node owner, Promotion existingPromotion) {
        Promotion draft = existingPromotion == null
                ? new Promotion()
                : PromotionPresentationSupport.copyPromotion(existingPromotion);

        Dialog<ButtonType> dialog = new Dialog<>();
        DialogService.attachToOwner(dialog, owner);
        dialog.setTitle(existingPromotion == null ? "Thêm khuyến mãi" : "Cập nhật khuyến mãi");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setOnShown(event -> ThemeManager.applyTheme(dialog.getDialogPane().getScene()));
        dialog.getDialogPane().getStyleClass().add("app-dialog");

        TextField txtName = new TextField(PromotionPresentationSupport.safeText(draft.getName()));
        ComboBox<String> cboType = new ComboBox<>();
        cboType.getItems().setAll("Giảm % toàn đơn", "Giảm % theo thể loại", "Mua X tặng Y");
        cboType.setValue(PromotionPresentationSupport.resolvePromotionTypeLabel(draft.getType()));

        TextField txtValue = new TextField(draft.getValue() > 0 ? PromotionPresentationSupport.trimDouble(draft.getValue()) : "");
        TextField txtCategory = new TextField(PromotionPresentationSupport.safeText(draft.getCategory()));
        TextField txtMinimumQuantity = new TextField(draft.getMinimumQuantity() == null ? "" : String.valueOf(draft.getMinimumQuantity()));
        DatePicker dpStartDate = new DatePicker(draft.getStartDate());
        DatePicker dpEndDate = new DatePicker(draft.getEndDate());
        ComboBox<String> cboStatus = new ComboBox<>();
        cboStatus.getItems().setAll("Đang bật", "Tạm dừng");
        cboStatus.setValue("ACTIVE".equalsIgnoreCase(draft.getStatus()) || draft.getStatus() == null ? "Đang bật" : "Tạm dừng");
        TextArea txtDescription = new TextArea(PromotionPresentationSupport.safeText(draft.getDescription()));
        txtDescription.setPrefRowCount(3);
        txtDescription.setWrapText(true);

        Runnable updateFormByType = () -> {
            String selected = cboType.getValue();
            boolean byCategory = "Giảm % theo thể loại".equals(selected);
            boolean buyGet = "Mua X tặng Y".equals(selected);
            txtCategory.setDisable(!byCategory && !buyGet);
            txtMinimumQuantity.setDisable(!buyGet);
            txtValue.setDisable(buyGet);
            txtValue.setPromptText(buyGet ? "Tự động = 1 sách tặng" : "Nhập giá trị giảm");
            if (!byCategory && !buyGet) {
                txtCategory.clear();
            }
            if (!buyGet) {
                txtMinimumQuantity.clear();
            } else if (PromotionPresentationSupport.safeText(txtValue.getText()).isBlank()) {
                txtValue.setText("1");
            }
        };
        cboType.valueProperty().addListener((obs, oldValue, newValue) -> updateFormByType.run());
        if (cboType.getValue() == null) {
            cboType.getSelectionModel().selectFirst();
        }
        updateFormByType.run();

        GridPane grid = createDialogGrid();
        grid.addRow(0, new Label("Tên chương trình"), txtName);
        grid.addRow(1, new Label("Loại áp dụng"), cboType);
        grid.addRow(2, new Label("Giá trị"), txtValue);
        grid.addRow(3, new Label("Thể loại"), txtCategory);
        grid.addRow(4, new Label("Số lượng tối thiểu"), txtMinimumQuantity);
        grid.addRow(5, new Label("Bắt đầu"), dpStartDate);
        grid.addRow(6, new Label("Kết thúc"), dpEndDate);
        grid.addRow(7, new Label("Trạng thái"), cboStatus);
        grid.addRow(8, new Label("Mô tả"), txtDescription);

        Label hintLabel = new Label("Khuyến mãi được áp tự động tại quầy thanh toán theo đúng loại rule đang cấu hình.");
        hintLabel.getStyleClass().add("section-subtitle");
        VBox content = new VBox(14, hintLabel, grid);
        content.getStyleClass().add("dialog-content");
        dialog.getDialogPane().setContent(content);

        styleDialogButtons(dialog, existingPromotion == null ? "Tạo khuyến mãi" : "Lưu thay đổi");
        InputFieldUtil.installCarryOverWorkaround(
                txtName, txtValue, txtCategory, txtMinimumQuantity, txtDescription,
                dpStartDate.getEditor(), dpEndDate.getEditor()
        );

        if (dialog.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return null;
        }

        draft.setName(txtName.getText().trim());
        String promotionType = PromotionPresentationSupport.resolvePromotionTypeCode(cboType.getValue());
        draft.setType(promotionType);
        draft.setValue(PromotionPresentationSupport.resolvePromotionValue(promotionType, txtValue.getText()));
        draft.setCategory(PromotionPresentationSupport.parseOptionalText(txtCategory.getText()));
        draft.setMinimumQuantity(PromotionPresentationSupport.parseOptionalInt(txtMinimumQuantity.getText(), "số lượng tối thiểu"));
        draft.setStartDate(dpStartDate.getValue());
        draft.setEndDate(dpEndDate.getValue());
        draft.setStatus("Tạm dừng".equals(cboStatus.getValue()) ? "INACTIVE" : "ACTIVE");
        draft.setDescription(txtDescription.getText().trim());
        return draft;
    }

    public static Voucher showVoucherDialog(Node owner, Voucher existingVoucher) {
        Voucher draft = existingVoucher == null
                ? new Voucher()
                : PromotionPresentationSupport.copyVoucher(existingVoucher);

        Dialog<ButtonType> dialog = new Dialog<>();
        DialogService.attachToOwner(dialog, owner);
        dialog.setTitle(existingVoucher == null ? "Thêm voucher" : "Cập nhật voucher");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        dialog.setOnShown(event -> ThemeManager.applyTheme(dialog.getDialogPane().getScene()));
        dialog.getDialogPane().getStyleClass().add("app-dialog");

        TextField txtCode = new TextField(PromotionPresentationSupport.safeText(draft.getCode()));
        ComboBox<String> cboType = new ComboBox<>();
        cboType.getItems().setAll("Phần trăm", "Trừ trực tiếp");
        cboType.setValue(PromotionPresentationSupport.resolveVoucherTypeLabel(draft.getType()));
        TextField txtValue = new TextField(draft.getValue() > 0 ? PromotionPresentationSupport.trimDouble(draft.getValue()) : "");
        TextField txtMinimumOrder = new TextField(draft.getMinimumOrderValue() > 0
                ? PromotionPresentationSupport.trimDouble(draft.getMinimumOrderValue()) : "0");
        TextField txtUsageLimit = new TextField(String.valueOf(Math.max(draft.getUsageLimit(), 0)));
        TextField txtUsedCount = new TextField(String.valueOf(Math.max(draft.getUsedCount(), 0)));
        DatePicker dpStartDate = new DatePicker(draft.getStartDate());
        DatePicker dpEndDate = new DatePicker(draft.getEndDate());
        ComboBox<String> cboStatus = new ComboBox<>();
        cboStatus.getItems().setAll("Đang bật", "Tạm dừng");
        cboStatus.setValue("ACTIVE".equalsIgnoreCase(draft.getStatus()) || draft.getStatus() == null ? "Đang bật" : "Tạm dừng");

        GridPane grid = createDialogGrid();
        grid.addRow(0, new Label("Mã voucher"), txtCode);
        grid.addRow(1, new Label("Loại giảm"), cboType);
        grid.addRow(2, new Label("Giá trị"), txtValue);
        grid.addRow(3, new Label("Đơn tối thiểu"), txtMinimumOrder);
        grid.addRow(4, new Label("Giới hạn lượt"), txtUsageLimit);
        grid.addRow(5, new Label("Đã dùng"), txtUsedCount);
        grid.addRow(6, new Label("Bắt đầu"), dpStartDate);
        grid.addRow(7, new Label("Kết thúc"), dpEndDate);
        grid.addRow(8, new Label("Trạng thái"), cboStatus);

        Label hintLabel = new Label("Voucher được nhập tay tại POS và sẽ tự kiểm tra đơn tối thiểu, thời hạn và lượt dùng.");
        hintLabel.getStyleClass().add("section-subtitle");
        VBox content = new VBox(14, hintLabel, grid);
        content.getStyleClass().add("dialog-content");
        dialog.getDialogPane().setContent(content);

        styleDialogButtons(dialog, existingVoucher == null ? "Tạo voucher" : "Lưu thay đổi");
        InputFieldUtil.installCarryOverWorkaround(
                txtCode, txtValue, txtMinimumOrder, txtUsageLimit, txtUsedCount,
                dpStartDate.getEditor(), dpEndDate.getEditor()
        );

        if (dialog.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return null;
        }

        draft.setCode(txtCode.getText().trim().toUpperCase(Locale.ROOT));
        draft.setType(PromotionPresentationSupport.resolveVoucherTypeCode(cboType.getValue()));
        draft.setValue(PromotionPresentationSupport.parseRequiredDouble(txtValue.getText(), "giá trị voucher"));
        draft.setMinimumOrderValue(PromotionPresentationSupport.parseRequiredDouble(txtMinimumOrder.getText(), "đơn tối thiểu"));
        draft.setUsageLimit(PromotionPresentationSupport.parseRequiredNonNegativeInt(txtUsageLimit.getText(), "giới hạn lượt"));
        draft.setUsedCount(PromotionPresentationSupport.parseRequiredNonNegativeInt(txtUsedCount.getText(), "số lượt đã dùng"));
        draft.setStartDate(dpStartDate.getValue());
        draft.setEndDate(dpEndDate.getValue());
        draft.setStatus("Tạm dừng".equals(cboStatus.getValue()) ? "INACTIVE" : "ACTIVE");
        return draft;
    }

    private static GridPane createDialogGrid() {
        GridPane grid = new GridPane();
        grid.getStyleClass().add("dialog-grid");
        return grid;
    }

    private static void styleDialogButtons(Dialog<ButtonType> dialog, String okText) {
        Button okButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        okButton.setText(okText);
        okButton.getStyleClass().add("primary-button");

        Button cancelButton = (Button) dialog.getDialogPane().lookupButton(ButtonType.CANCEL);
        cancelButton.setText("Hủy");
        cancelButton.getStyleClass().add("chip-button");
    }
}
