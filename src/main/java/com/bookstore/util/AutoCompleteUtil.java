package com.bookstore.util;

import com.bookstore.model.Book;
import javafx.scene.control.TextField;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

/**
 * Smart suggestion helpers powered by ControlsFX.
 */
public final class AutoCompleteUtil {

    private AutoCompleteUtil() {
    }

    public static AutoCompletionBinding<String> bindBookSuggestions(TextField textField, Supplier<List<Book>> supplier) {
        return bindBookSuggestions(textField, supplier, null);
    }

    public static AutoCompletionBinding<String> bindBookSuggestions(TextField textField, Supplier<List<Book>> supplier,
                                                                    Runnable onCompleted) {
        if (textField == null || supplier == null) {
            return null;
        }

        try {
            AutoCompletionBinding<String> binding = TextFields.bindAutoCompletion(textField, request -> {
                String keyword = request.getUserText() == null ? "" : request.getUserText().trim().toLowerCase(Locale.ROOT);
                if (keyword.isBlank()) {
                    return List.of();
                }

                LinkedHashSet<String> suggestions = new LinkedHashSet<>();
                List<Book> books = supplier.get();
                if (books == null) {
                    return List.of();
                }

                for (Book book : books) {
                    if (book == null) {
                        continue;
                    }
                    addIfMatches(suggestions, book.getTenSach(), keyword);
                    addIfMatches(suggestions, book.getTacGia(), keyword);
                    addIfMatches(suggestions, book.getTheLoai(), keyword);
                    if (suggestions.size() >= 8) {
                        break;
                    }
                }

                return suggestions;
            });

            binding.setOnAutoCompleted(event -> {
                if (event == null || event.getCompletion() == null) {
                    return;
                }
                textField.setText(event.getCompletion());
                textField.positionCaret(event.getCompletion().length());
                if (onCompleted != null) {
                    onCompleted.run();
                }
            });
            return binding;
        } catch (LinkageError | RuntimeException ex) {
            AppLog.warn(AutoCompleteUtil.class,
                    "Book autocomplete unavailable, fallback to manual search only: " + ex.getMessage(), ex);
            return null;
        }
    }

    private static void addIfMatches(Collection<String> suggestions, String candidate, String keyword) {
        if (candidate == null || candidate.isBlank()) {
            return;
        }

        String normalized = candidate.toLowerCase(Locale.ROOT);
        if (normalized.startsWith(keyword) || normalized.contains(keyword)) {
            suggestions.add(candidate.trim());
        }
    }
}
