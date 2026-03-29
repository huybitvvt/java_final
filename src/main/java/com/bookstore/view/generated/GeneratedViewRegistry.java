package com.bookstore.view.generated;

import com.bookstore.view.ViewBundle;
import com.bookstore.view.ViewId;
import com.bookstore.view.BookView;
import com.bookstore.view.LoginView;
import com.bookstore.view.MainView;
import com.bookstore.view.PosView;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

public final class GeneratedViewRegistry {
    private static final Map<ViewId, Supplier<ViewBundle>> BUILDERS = new EnumMap<>(ViewId.class);

    static {
        BUILDERS.put(ViewId.BOOK, BookView::create);
        BUILDERS.put(ViewId.BOOK_DIALOG, BookDialogViewBuilder::create);
        BUILDERS.put(ViewId.CUSTOMER, CustomerViewBuilder::create);
        BUILDERS.put(ViewId.CUSTOMER_DIALOG, CustomerDialogViewBuilder::create);
        BUILDERS.put(ViewId.DASHBOARD, DashboardViewBuilder::create);
        BUILDERS.put(ViewId.EMPLOYEE, EmployeeViewBuilder::create);
        BUILDERS.put(ViewId.IMPORT, ImportViewBuilder::create);
        BUILDERS.put(ViewId.INVENTORY, InventoryViewBuilder::create);
        BUILDERS.put(ViewId.INVOICE, InvoiceViewBuilder::create);
        BUILDERS.put(ViewId.LOGIN, LoginView::create);
        BUILDERS.put(ViewId.MAIN, MainView::create);
        BUILDERS.put(ViewId.POS, PosView::create);
        BUILDERS.put(ViewId.PROMOTION, PromotionViewBuilder::create);
        BUILDERS.put(ViewId.STATISTICS, StatisticsViewBuilder::create);
        BUILDERS.put(ViewId.SUPPLIER, SupplierViewBuilder::create);
    }

    private GeneratedViewRegistry() {
    }

    public static ViewBundle load(ViewId viewId) {
        Supplier<ViewBundle> builder = BUILDERS.get(viewId);
        if (builder == null) {
            throw new IllegalArgumentException("Không có view builder cho: " + viewId);
        }
        return builder.get();
    }
}
