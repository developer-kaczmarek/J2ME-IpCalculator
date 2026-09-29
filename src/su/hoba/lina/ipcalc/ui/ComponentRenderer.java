package su.hoba.lina.ipcalc.ui;

import javax.microedition.lcdui.*;
import su.hoba.lina.ipcalc.theme.Color;

public class ComponentRenderer {

    // Высота баров по умолчанию
    public static final int BAR_HEIGHT = 24;

    /**
     * Отрисовка верхнего бара (Top Bar / Status Bar)
     * @param g Графика
     * @param w Ширина экрана
     * @param title Заголовок по центру (если нужен)
     * @param leftIcon Иконка слева (может быть null)
     * @param leftText Текст слева (например, "Backspace")
     */
    public static void drawTopBar(Graphics g, int w, String title, Image leftIcon, String leftText) {
        // Подложка
        g.setColor(Color.SURFACE);
        g.fillRect(0, 0, w, BAR_HEIGHT);
        
        // Нижняя граница бара
        g.setColor(Color.ON_SURFACE_VARIANT);
        g.drawLine(0, BAR_HEIGHT, w, BAR_HEIGHT);
        
        Font font = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL);
        g.setFont(font);
        
        // 1. Отрисовка контента слева
        int startX = 10;
        if (leftIcon != null) {
            int iconY = (BAR_HEIGHT - leftIcon.getHeight()) / 2;
            g.drawImage(leftIcon, startX, iconY, Graphics.LEFT | Graphics.TOP);
            startX += leftIcon.getWidth() + 6;
        }
        if (leftText != null && !leftText.equals("")) {
            int textY = (BAR_HEIGHT - font.getHeight()) / 2;
            g.setColor(Color.ON_SURFACE);
            g.drawString(leftText, startX, textY, Graphics.LEFT | Graphics.TOP);
        }
        
        // 2. Отрисовка заголовка по центру
        if (title != null && !title.equals("")) {
            Font titleFont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_BOLD, Font.SIZE_SMALL);
            g.setFont(titleFont);
            g.setColor(Color.ON_SURFACE);
            int titleX = (w - titleFont.stringWidth(title)) / 2;
            int titleY = (BAR_HEIGHT - titleFont.getHeight()) / 2;
            g.drawString(title, titleX, titleY, Graphics.LEFT | Graphics.TOP);
        }
    }

    /**
     * Отрисовка нижнего меню (Bottom Menu Bar)
     * @param g Графика
     * @param w Ширина экрана
     * @param h Высота экрана
     * @param leftText Текст слева
     * @param centerText Текст по центру
     * @param rightText Текст справа
     */
    public static void drawBottomBar(Graphics g, int w, int h, String leftText, String centerText, String rightText) {
        int menuY = h - BAR_HEIGHT;
        
        // Подложка меню
        g.setColor(Color.SURFACE);
        g.fillRect(0, menuY, w, BAR_HEIGHT);
        
        // Верхняя граница меню
        g.setColor(Color.ON_SURFACE_VARIANT);
        g.drawLine(0, menuY, w, menuY);
        
        Font menuFont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_SMALL);
        g.setFont(menuFont);
        g.setColor(Color.ON_SURFACE);
        
        int textY = menuY + (BAR_HEIGHT - menuFont.getHeight()) / 2;
        
        // Слева
        if (leftText != null) {
            g.drawString(leftText, 10, textY, Graphics.LEFT | Graphics.TOP);
        }
        
        // По центру
        if (centerText != null) {
            g.drawString(centerText, w / 2, textY, Graphics.HCENTER | Graphics.TOP);
        }
        
        // Справа
        if (rightText != null) {
            g.drawString(rightText, w - 10, textY, Graphics.RIGHT | Graphics.TOP);
        }
    }
}