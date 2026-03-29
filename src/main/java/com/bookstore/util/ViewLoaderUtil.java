package com.bookstore.util;

import com.bookstore.view.ViewBundle;
import com.bookstore.view.ViewId;
import com.bookstore.view.generated.GeneratedViewRegistry;
import javafx.scene.Parent;

/**
 * JavaFX view loader backed by generated code-only builders.
 */
public final class ViewLoaderUtil {

    private ViewLoaderUtil() {
    }

    public static Parent load(ViewId viewId) {
        return loadView(viewId).getRoot();
    }

    public static ViewBundle loadView(ViewId viewId) {
        return GeneratedViewRegistry.load(viewId);
    }
}
