package com.bookstore.controller.support;

import com.bookstore.model.Book;
import com.bookstore.model.Customer;
import com.bookstore.model.InvoiceDetail;
import com.bookstore.util.FormatUtil;
import com.bookstore.util.ResponsiveLayoutUtil;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;

import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Table and combo-box configuration helpers for the POS screen.
 */
public final class PosTableSupport {

    private PosTableSupport() {
    }

    public static void configureBookColumns(
            TableColumn<Book, Integer> colBookMa,
            TableColumn<Book, String> colBookTen,
            TableColumn<Book, String> colBookTacGia,
            TableColumn<Book, String> colBookTheLoai,
            TableColumn<Book, Double> colBookGia,
            TableColumn<Book, Integer> colBookTon
    ) {
        colBookMa.setCellValueFactory(new PropertyValueFactory<>("maSach"));
        colBookTen.setCellValueFactory(new PropertyValueFactory<>("tenSach"));
        colBookTacGia.setCellValueFactory(new PropertyValueFactory<>("tacGia"));
        colBookTheLoai.setCellValueFactory(new PropertyValueFactory<>("theLoai"));
        colBookGia.setCellValueFactory(new PropertyValueFactory<>("giaBia"));
        colBookTon.setCellValueFactory(new PropertyValueFactory<>("soLuongTon"));

        colBookGia.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : FormatUtil.formatCurrency(item));
            }
        });
    }

    public static void configureCartColumns(
            TableColumn<InvoiceDetail, InvoiceDetail> colCartTen,
            TableColumn<InvoiceDetail, Integer> colCartSL,
            TableColumn<InvoiceDetail, Double> colCartGia,
            TableColumn<InvoiceDetail, Double> colCartThanhTien,
            TableColumn<InvoiceDetail, Void> colCartDieuChinh,
            TableColumn<InvoiceDetail, Void> colCartXoa,
            Function<InvoiceDetail, String> nameResolver,
            BiConsumer<Integer, Integer> quantityChanger,
            Consumer<Integer> itemRemover
    ) {
        colCartTen.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue()));
        colCartTen.setCellFactory(column -> new TableCell<>() {
            private final Label nameLabel = new Label();

            {
                nameLabel.getStyleClass().add("cart-book-name");
                nameLabel.setMaxWidth(Double.MAX_VALUE);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }

            @Override
            protected void updateItem(InvoiceDetail detail, boolean empty) {
                super.updateItem(detail, empty);
                if (empty || detail == null) {
                    setGraphic(null);
                    return;
                }

                nameLabel.setText(nameResolver.apply(detail));
                setGraphic(nameLabel);
            }
        });

        colCartSL.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getSoLuong()));
        colCartGia.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getDonGia()));
        colCartThanhTien.setCellValueFactory(cellData -> new ReadOnlyObjectWrapper<>(cellData.getValue().getThanhTien()));

        colCartGia.setCellFactory(column -> currencyCell());
        colCartThanhTien.setCellFactory(column -> currencyCell());

        colCartDieuChinh.setSortable(false);
        colCartDieuChinh.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }

                Button btnMinus = new Button("-");
                btnMinus.getStyleClass().addAll("button", "chip-button", "cart-adjust-button");
                btnMinus.setOnAction(event -> quantityChanger.accept(getIndex(), -1));

                Button btnPlus = new Button("+");
                btnPlus.getStyleClass().addAll("button", "chip-button", "cart-adjust-button");
                btnPlus.setOnAction(event -> quantityChanger.accept(getIndex(), 1));

                setGraphic(new HBox(6, btnMinus, btnPlus));
            }
        });

        colCartXoa.setSortable(false);
        colCartXoa.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                    return;
                }

                Button btnDelete = new Button("X");
                btnDelete.getStyleClass().addAll("button", "chip-button", "cart-delete-button");
                btnDelete.setOnAction(event -> itemRemover.accept(getIndex()));
                setGraphic(btnDelete);
            }
        });
    }

    public static void configureTableLayout(
            TableView<Book> tableBooks,
            TableView<InvoiceDetail> tableCart,
            TableColumn<Book, Integer> colBookMa,
            TableColumn<Book, String> colBookTen,
            TableColumn<Book, String> colBookTacGia,
            TableColumn<Book, String> colBookTheLoai,
            TableColumn<Book, Double> colBookGia,
            TableColumn<Book, Integer> colBookTon,
            TableColumn<InvoiceDetail, InvoiceDetail> colCartTen,
            TableColumn<InvoiceDetail, Integer> colCartSL,
            TableColumn<InvoiceDetail, Double> colCartGia,
            TableColumn<InvoiceDetail, Double> colCartThanhTien,
            TableColumn<InvoiceDetail, Void> colCartDieuChinh,
            TableColumn<InvoiceDetail, Void> colCartXoa
    ) {
        boolean compactMode = ResponsiveLayoutUtil.isCompactScreen();
        tableBooks.setFixedCellSize(compactMode ? 42 : 48);
        tableCart.setFixedCellSize(compactMode ? 42 : 48);
        tableBooks.setPlaceholder(createPlaceholder("Không tìm thấy sách phù hợp."));
        tableCart.setPlaceholder(createPlaceholder("Giỏ hàng đang trống."));

        DoubleBinding bookWidth = tableBooks.widthProperty().subtract(26);
        double[] bookRatios = compactMode
                ? new double[]{0.08, 0.36, 0.19, 0.11, 0.16, 0.10}
                : new double[]{0.08, 0.34, 0.18, 0.14, 0.16, 0.10};
        colBookMa.prefWidthProperty().bind(bookWidth.multiply(bookRatios[0]));
        colBookTen.prefWidthProperty().bind(bookWidth.multiply(bookRatios[1]));
        colBookTacGia.prefWidthProperty().bind(bookWidth.multiply(bookRatios[2]));
        colBookTheLoai.prefWidthProperty().bind(bookWidth.multiply(bookRatios[3]));
        colBookGia.prefWidthProperty().bind(bookWidth.multiply(bookRatios[4]));
        colBookTon.prefWidthProperty().bind(bookWidth.multiply(bookRatios[5]));

        DoubleBinding cartWidth = tableCart.widthProperty().subtract(26);
        double[] cartRatios = compactMode
                ? new double[]{0.38, 0.10, 0.14, 0.16, 0.16, 0.06}
                : new double[]{0.42, 0.08, 0.14, 0.14, 0.16, 0.06};
        colCartTen.prefWidthProperty().bind(cartWidth.multiply(cartRatios[0]));
        colCartSL.prefWidthProperty().bind(cartWidth.multiply(cartRatios[1]));
        colCartGia.prefWidthProperty().bind(cartWidth.multiply(cartRatios[2]));
        colCartThanhTien.prefWidthProperty().bind(cartWidth.multiply(cartRatios[3]));
        colCartDieuChinh.prefWidthProperty().bind(cartWidth.multiply(cartRatios[4]));
        colCartXoa.prefWidthProperty().bind(cartWidth.multiply(cartRatios[5]));
    }

    public static void configureCustomerComboBox(ComboBox<Customer> cboCustomer) {
        cboCustomer.setButtonCell(createCustomerCell());
        cboCustomer.setCellFactory(listView -> createCustomerCell());
    }

    public static Label createPlaceholder(String message) {
        Label placeholder = new Label(message);
        placeholder.getStyleClass().add("empty-state-label");
        return placeholder;
    }

    private static ListCell<Customer> createCustomerCell() {
        return new ListCell<>() {
            @Override
            protected void updateItem(Customer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getHoTen() + " - " + FormatUtil.formatPhone(item.getSoDienThoai()));
                }
            }
        };
    }

    private static TableCell<InvoiceDetail, Double> currencyCell() {
        return new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : FormatUtil.formatCurrency(item));
            }
        };
    }
}
