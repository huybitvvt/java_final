package com.bookstore.view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.Cursor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class GeneratedViewSupport {

    private GeneratedViewSupport() {
    }

    public static <T> T bind(Object controller, String fieldName, T value) {
        if (controller == null || fieldName == null || fieldName.isBlank()) {
            return value;
        }
        try {
            Field field = findField(controller.getClass(), fieldName);
            if (field != null) {
                field.setAccessible(true);
                field.set(controller, value);
            }
        } catch (ReflectiveOperationException | RuntimeException ex) {
            throw new IllegalStateException("Không thể bind field " + fieldName, ex);
        }
        return value;
    }

    public static void initialize(Object controller) {
        if (controller == null) {
            return;
        }
        try {
            Method method = findMethod(controller.getClass(), "initialize");
            if (method != null && method.getParameterCount() == 0) {
                method.setAccessible(true);
                method.invoke(controller);
            }
        } catch (ReflectiveOperationException | RuntimeException ex) {
            throw new IllegalStateException("Không thể gọi initialize() cho " + controller.getClass().getName(), ex);
        }
    }

    public static void bindAction(Object target, Object controller, String methodName) {
        if (target == null || controller == null || methodName == null || methodName.isBlank()) {
            return;
        }

        EventHandler<ActionEvent> handler = event -> invokeControllerMethod(controller, methodName, event);

        try {
            if (target instanceof MenuItem menuItem) {
                menuItem.setOnAction(handler);
                return;
            }

            Method setter = findCompatibleMethod(target.getClass(), "setOnAction", EventHandler.class);
            if (setter != null) {
                setter.invoke(target, handler);
            }
        } catch (ReflectiveOperationException | RuntimeException ex) {
            throw new IllegalStateException("Không thể bind action #" + methodName, ex);
        }
    }

    public static void setProperty(Object instance, String name, String value) {
        if (instance == null || name == null || name.isBlank()) {
            return;
        }
        try {
            Method setter = findSetter(instance.getClass(), setterName(name));
            if (setter == null) {
                return;
            }
            Class<?> parameterType = setter.getParameterTypes()[0];
            setter.invoke(instance, convertValue(value, parameterType));
        } catch (ReflectiveOperationException | RuntimeException ex) {
            throw new IllegalStateException("Không thể set property " + name + "=" + value, ex);
        }
    }

    public static void setObjectProperty(Object instance, String name, Object value) {
        if (instance == null || name == null || name.isBlank() || value == null) {
            return;
        }
        try {
            Method setter = findCompatibleMethod(instance.getClass(), setterName(name), value.getClass());
            if (setter != null) {
                setter.invoke(instance, value);
            }
        } catch (ReflectiveOperationException | RuntimeException ex) {
            throw new IllegalStateException("Không thể set object property " + name, ex);
        }
    }

    public static void addStyleClasses(Node node, String value) {
        if (node == null || value == null || value.isBlank()) {
            return;
        }
        node.getStyleClass().addAll(splitListValue(value));
    }

    public static void addStylesheets(Parent parent, String value) {
        if (parent == null || value == null || value.isBlank()) {
            return;
        }
        for (String stylesheet : splitListValue(value)) {
            parent.getStylesheets().add(resolveResourceUrl(stylesheet));
        }
    }

    public static void applyConstraint(Node node, String name, String value) {
        if (node == null || name == null || name.isBlank()) {
            return;
        }
        try {
            switch (name) {
                case "AnchorPane.topAnchor" -> AnchorPane.setTopAnchor(node, Double.parseDouble(value));
                case "AnchorPane.rightAnchor" -> AnchorPane.setRightAnchor(node, Double.parseDouble(value));
                case "AnchorPane.bottomAnchor" -> AnchorPane.setBottomAnchor(node, Double.parseDouble(value));
                case "AnchorPane.leftAnchor" -> AnchorPane.setLeftAnchor(node, Double.parseDouble(value));
                case "HBox.hgrow" -> javafx.scene.layout.HBox.setHgrow(node, Priority.valueOf(value));
                case "VBox.vgrow" -> javafx.scene.layout.VBox.setVgrow(node, Priority.valueOf(value));
                case "BorderPane.alignment" -> BorderPane.setAlignment(node, Pos.valueOf(value));
                case "GridPane.columnIndex" -> javafx.scene.layout.GridPane.setColumnIndex(node, Integer.parseInt(value));
                case "GridPane.rowIndex" -> javafx.scene.layout.GridPane.setRowIndex(node, Integer.parseInt(value));
                case "GridPane.columnSpan" -> javafx.scene.layout.GridPane.setColumnSpan(node, Integer.parseInt(value));
                case "GridPane.rowSpan" -> javafx.scene.layout.GridPane.setRowSpan(node, Integer.parseInt(value));
                default -> throw new IllegalArgumentException("Constraint chưa được hỗ trợ: " + name);
            }
        } catch (RuntimeException ex) {
            throw new IllegalStateException("Không thể áp constraint " + name + "=" + value, ex);
        }
    }

    public static void addChild(Pane parent, Node child) {
        parent.getChildren().add(child);
    }

    public static void addItem(MenuButton parent, MenuItem child) {
        parent.getItems().add(child);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static void addColumn(TableView<?> parent, TableColumn<?, ?> child) {
        ((TableView) parent).getColumns().add((TableColumn) child);
    }

    public static String resolveResourceUrl(String value) {
        if (value == null || value.isBlank()) {
            return value;
        }
        if (value.startsWith("@")) {
            String resourcePath = value.substring(1);
            Path sourcePath = Path.of(System.getProperty("user.dir"), "src", "main", "resources",
                    resourcePath.startsWith("/") ? resourcePath.substring(1) : resourcePath);
            if (Files.exists(sourcePath)) {
                try {
                    return sourcePath.toUri().toURL().toExternalForm();
                } catch (MalformedURLException ignored) {
                    // Fall through to classpath/relative lookup below.
                }
            }

            URL classpathUrl = GeneratedViewSupport.class.getResource(resourcePath);
            if (classpathUrl != null) {
                return classpathUrl.toExternalForm();
            }
        }
        return value;
    }

    private static List<String> splitListValue(String value) {
        String[] parts = value.trim().split("\\s+");
        List<String> result = new ArrayList<>();
        for (String part : parts) {
            if (!part.isBlank()) {
                result.add(part);
            }
        }
        return result;
    }

    private static void invokeControllerMethod(Object controller, String methodName, ActionEvent event) {
        try {
            Method zeroArg = findMethod(controller.getClass(), methodName);
            if (zeroArg != null && zeroArg.getParameterCount() == 0) {
                zeroArg.setAccessible(true);
                zeroArg.invoke(controller);
                return;
            }

            Method eventMethod = findCompatibleMethod(controller.getClass(), methodName, ActionEvent.class);
            if (eventMethod != null) {
                eventMethod.setAccessible(true);
                eventMethod.invoke(controller, event);
                return;
            }

            throw new NoSuchMethodException(methodName);
        } catch (ReflectiveOperationException | RuntimeException ex) {
            throw new IllegalStateException("Không thể gọi action #" + methodName, ex);
        }
    }

    private static Field findField(Class<?> type, String name) {
        Class<?> current = type;
        while (current != null) {
            try {
                return current.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        return null;
    }

    private static Method findMethod(Class<?> type, String name) {
        Class<?> current = type;
        while (current != null) {
            for (Method method : current.getDeclaredMethods()) {
                if (method.getName().equals(name)) {
                    return method;
                }
            }
            current = current.getSuperclass();
        }
        return null;
    }

    private static Method findSetter(Class<?> type, String name) {
        for (Method method : type.getMethods()) {
            if (method.getName().equals(name) && method.getParameterCount() == 1) {
                return method;
            }
        }
        return null;
    }

    private static Method findCompatibleMethod(Class<?> type, String name, Class<?> parameterType) {
        for (Method method : type.getMethods()) {
            if (!method.getName().equals(name) || method.getParameterCount() != 1) {
                continue;
            }
            if (method.getParameterTypes()[0].isAssignableFrom(parameterType)) {
                return method;
            }
        }
        return null;
    }

    private static String setterName(String propertyName) {
        return "set" + propertyName.substring(0, 1).toUpperCase(Locale.ROOT) + propertyName.substring(1);
    }

    private static Object convertValue(String value, Class<?> targetType) {
        if (String.class.equals(targetType)) {
            return value;
        }
        if (double.class.equals(targetType) || Double.class.equals(targetType)) {
            return Double.parseDouble(value);
        }
        if (int.class.equals(targetType) || Integer.class.equals(targetType)) {
            return Integer.parseInt(value.contains(".") ? value.substring(0, value.indexOf('.')) : value);
        }
        if (boolean.class.equals(targetType) || Boolean.class.equals(targetType)) {
            return Boolean.parseBoolean(value);
        }
        if (Pos.class.equals(targetType)) {
            return Pos.valueOf(value);
        }
        if (Priority.class.equals(targetType)) {
            return Priority.valueOf(value);
        }
        if (Paint.class.equals(targetType) || Color.class.equals(targetType)) {
            return parsePaint(value);
        }
        if (Cursor.class.equals(targetType)) {
            return parseCursor(value);
        }
        if (targetType.isEnum()) {
            @SuppressWarnings({"rawtypes", "unchecked"})
            Object enumValue = Enum.valueOf((Class<Enum>) targetType.asSubclass(Enum.class), value);
            return enumValue;
        }
        return value;
    }

    private static Paint parsePaint(String value) {
        try {
            return Color.web(value);
        } catch (IllegalArgumentException ex) {
            return Color.valueOf(value);
        }
    }

    private static Cursor parseCursor(String value) {
        try {
            return (Cursor) Cursor.class.getField(value).get(null);
        } catch (ReflectiveOperationException | RuntimeException ex) {
            return Cursor.cursor(value);
        }
    }
}
