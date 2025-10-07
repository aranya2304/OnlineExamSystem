package com.examSystem.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.Window;
import java.awt.Color;
import javax.swing.AbstractButton;
import javax.swing.UIManager;

/**
 * Small UI helpers to ensure text is visible.
 * - Sets common UIManager foreground defaults to black.
 * - Traverses component trees and replaces white foreground with desired color.
 */
public final class UIUtils {
    private UIUtils() {
    }

    public static void applyGlobalButtonForeground(Color color) {
        if (color == null)
            return;
        UIManager.put("Button.foreground", color);
    }

    /**
     * Set a set of common UI defaults' foreground color to desired.
     * This ensures components created by LAF use black text by default.
     */
    public static void applyGlobalForegrounds(Color desired) {
        if (desired == null)
            return;
        UIManager.put("Label.foreground", desired);
        UIManager.put("Button.foreground", desired);
        UIManager.put("RadioButton.foreground", desired);
        UIManager.put("ToggleButton.foreground", desired);
        UIManager.put("MenuItem.foreground", desired);
        UIManager.put("Menu.foreground", desired);
        UIManager.put("CheckBox.foreground", desired);
        UIManager.put("TabbedPane.foreground", desired);
        UIManager.put("Table.foreground", desired);
        UIManager.put("Table.selectionForeground", desired);
        UIManager.put("TextField.foreground", desired);
        UIManager.put("TextArea.foreground", desired);
        UIManager.put("EditorPane.foreground", desired);
        UIManager.put("Tree.textForeground", desired);
        UIManager.put("TitledBorder.titleColor", desired);
    }

    public static void fixButtonTextColors(Component root, Color desired) {
        if (root == null || desired == null)
            return;
        if (root instanceof AbstractButton) {
            AbstractButton btn = (AbstractButton) root;
            Color fg = btn.getForeground();
            Color bg = btn.getBackground();
            // If button text is white (or background is white), force desired color.
            if (Color.WHITE.equals(fg) || Color.WHITE.equals(bg)) {
                btn.setForeground(desired);
            }
        }
        if (root instanceof Container) {
            Component[] children = ((Container) root).getComponents();
            for (Component c : children) {
                fixButtonTextColors(c, desired);
            }
        }
    }

    /**
     * Generic traversal: if a component's foreground is white, change it to
     * desired.
     * This covers labels, table renderers, etc.
     */
    public static void fixForegrounds(Component root, Color desired) {
        if (root == null || desired == null)
            return;
        try {
            Color fg = root.getForeground();
            if (Color.WHITE.equals(fg)) {
                root.setForeground(desired);
            }
        } catch (Throwable t) {
            // safe-guard: some components may throw on get/set, ignore
        }

        if (root instanceof Container) {
            Component[] children = ((Container) root).getComponents();
            for (Component c : children) {
                fixForegrounds(c, desired);
            }
        }
    }

    /**
     * Convenience: set global defaults and fix any existing components in the
     * window.
     */
    public static void ensureBlackButtons(Window window) {
        applyGlobalButtonForeground(Color.BLACK);
        if (window != null) {
            fixButtonTextColors(window, Color.BLACK);
            fixForegrounds(window, Color.BLACK);
        }
    }

    /**
     * Apply comprehensive global defaults and also fix any already-created window
     * tree.
     */
    public static void applyProjectWideBlackText(Window window) {
        applyGlobalForegrounds(Color.BLACK);
        applyGlobalButtonForeground(Color.BLACK);
        if (window != null) {
            fixForegrounds(window, Color.BLACK);
        }
    }
}