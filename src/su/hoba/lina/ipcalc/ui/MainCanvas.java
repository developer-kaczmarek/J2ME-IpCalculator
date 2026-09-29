package su.hoba.lina.ipcalc.ui;

import su.hoba.lina.ipcalc.*;
import javax.microedition.lcdui.*;
import su.hoba.lina.ipcalc.theme.Color;
import su.hoba.lina.ipcalc.utils.Utils;

public class MainCanvas extends Canvas {
    private IPCalculator midlet;
    
    private String[] octets = {"192", "168", "0", "1"};
    private String mask = "24";
    private int focusedField = 0;
    
    private Image emptyStateImage;
    private Image backspaceIcon;

    public MainCanvas(IPCalculator midlet) {
        this.midlet = midlet;
        setFullScreenMode(true);

        emptyStateImage = Utils.loadImage("/su/hoba/lina/ipcalc/res/images/img_empty_state.png");
        backspaceIcon = Utils.loadImage("/su/hoba/lina/ipcalc/res/images/ic_asterisk.png");
    }

    protected void paint(Graphics g) {
        int w = getWidth();
        int h = getHeight();
        boolean isLandscape = w > h;

        g.setColor(Color.BACKGROUND);
        g.fillRect(0, 0, w, h);

        ComponentRenderer.drawTopBar(g, w, null, backspaceIcon, "Стереть");
        int contentY = isLandscape ? 32 : 40; 

        if (emptyStateImage != null) {
            g.drawImage(emptyStateImage, w / 2, contentY, Graphics.HCENTER | Graphics.TOP);
            contentY += emptyStateImage.getHeight();
        } else {
            contentY += isLandscape ? 20 : 40; 
        }

        g.setColor(Color.ON_BACKGROUND); 
        Font statusFont = Font.getFont(Font.FACE_PROPORTIONAL, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
        g.setFont(statusFont);
        
        int statusTextY = contentY + (isLandscape ? 6 : 24);
        g.drawString("Ожидание ввода данных", w / 2, statusTextY, Graphics.HCENTER | Graphics.TOP);
        int statusTextHeight = statusFont.getHeight();

        int fieldsY = statusTextY + statusTextHeight + (isLandscape ? 32 : 42); 
        int menuHeight = 24;
        if (fieldsY > h - menuHeight - 35) {
            fieldsY = h - menuHeight - 35;
        }

        int fieldW = 32;
        int totalBlockWidth = (4 * fieldW) + (3 * 10) + 15 + 24; 
        int startX = (w - totalBlockWidth) / 2;

        Font inputFont = Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);
        g.setFont(inputFont);

        int currentX = startX;
        for (int i = 0; i < 4; i++) {
            drawInputField(g, octets[i], currentX, fieldsY, fieldW, i == focusedField);
            currentX += fieldW;
            
            if (i < 3) {
                g.setColor(Color.ON_BACKGROUND);
                g.drawString(".", currentX + 5, fieldsY, Graphics.HCENTER | Graphics.BOTTOM);
                currentX += 10; 
            }
        }
        
        currentX += 5;
        g.setColor(Color.ON_BACKGROUND);
        g.drawString("/", currentX, fieldsY, Graphics.HCENTER | Graphics.BOTTOM);
        currentX += 15; 

        drawInputField(g, mask, currentX, fieldsY, 24, focusedField == 4);

        ComponentRenderer.drawBottomBar(g, w, h, null, "Ввод", "Выйти");
    }

    private void drawInputField(Graphics g, String text, int x, int y, int width, boolean focused) {
        g.setColor(Color.ON_BACKGROUND);
        g.drawString(text, x + (width / 2), y, Graphics.HCENTER | Graphics.BOTTOM);
        
        if (focused) {
            g.setColor(Color.PRIMARY);
            g.drawLine(x, y + 2, x + width, y + 2);
            g.drawLine(x, y + 3, x + width, y + 3);
        }
    }

    protected void keyPressed(int keyCode) {
        boolean isLandscape = getWidth() > getHeight();

        // 1. ПЕРЕХВАТ КЛАВИАТУРЫ ДЛЯ QWERTY (Nokia E72) В ЛАНДШАФТНОМ РЕЖИМЕ
        if (isLandscape && isQwertyDigitKey(keyCode)) {
            char mapped = mapAnyQwertyChar(keyCode);
            if (mapped == '*') {
                backspace(); // Стираем символ, если замапилось в звёздочку (u / г)
            } else if (mapped != '\0') {
                appendDigit(mapped); // Вводим цифру
            }
            repaint();
            return; 
        }

        // 2. Стандартный ввод цифр (для кнопочных телефонов вроде Nokia 5220)
        if (keyCode >= KEY_NUM0 && keyCode <= KEY_NUM9) {
            appendDigit((char) keyCode);
            repaint();
            return;
        }

        // 3. Стандартная навигация и служебные клавиши
        int action = getGameAction(keyCode);

        if (action == LEFT) {
            validateCurrentField();
            focusedField = Math.max(0, focusedField - 1);
        } 
        else if (action == RIGHT) {
            validateCurrentField();
            focusedField = Math.min(4, focusedField + 1);
        } 
        else if (keyCode == KEY_STAR || keyCode == 8 || keyCode == -8) { 
            backspace();
        } 
        else if (keyCode == -7) {
            midlet.closeApp();
        } 
        else if (action == FIRE) {
            triggerEnter();
        }
        
        repaint();
    }

    private boolean isQwertyDigitKey(int keyCode) {
        char c = Character.toLowerCase((char) keyCode);
        return c == 'r' || c == 't' || c == 'y' || c == 'u' || 
               c == 'f' || c == 'g' || c == 'h' || 
               c == 'v' || c == 'b' || c == 'n' || c == 'm' || 
               c == 'к' || c == 'е' || c == 'н' || c == 'г' || 
               c == 'а' || c == 'п' || c == 'р' || 
               c == 'м' || c == 'и' || c == 'т' || c == 'ь' ||
               (c >= '0' && c <= '9');
    }

    private char mapAnyQwertyChar(int keyCode) {
        char c = Character.toLowerCase((char) keyCode);
        
        if (c >= '0' && c <= '9') {
            return c;
        }

        switch (c) {
            case 'r': case 'к': return '1';
            case 't': case 'е': return '2';
            case 'y': case 'н': return '3';
            case 'u': case 'г': return '*';
            case 'f': case 'а': return '4';
            case 'g': case 'п': return '5';
            case 'h': case 'р': return '6';
            case 'v': case 'м': return '7';
            case 'b': case 'и': return '8';
            case 'n': case 'т': return '9';
            case 'm': case 'ь': return '0';
            default: return '\0';
        }
    }

    private void appendDigit(char num) {
        if (num == '\0') return;
        
        if (focusedField < 4) {
            if (octets[focusedField].equals("0")) {
                octets[focusedField] = String.valueOf(num);
            } else if (octets[focusedField].length() < 3) {
                String candidate = octets[focusedField] + num;
                if (Integer.parseInt(candidate) <= 255) {
                    octets[focusedField] = candidate;
                }
            }
        } else {
            if (mask.equals("0")) {
                mask = String.valueOf(num);
            } else if (mask.length() < 2) {
                String candidate = mask + num;
                if (Integer.parseInt(candidate) <= 32) {
                    mask = candidate;
                }
            }
        }
    }

    private void backspace() {
        if (focusedField < 4) {
            if (octets[focusedField].length() > 0) {
                octets[focusedField] = octets[focusedField].substring(0, octets[focusedField].length() - 1);
            }
            if (octets[focusedField].length() == 0) {
                octets[focusedField] = "0";
            }
        } else {
            if (mask.length() > 0) {
                mask = mask.substring(0, mask.length() - 1);
            }
            if (mask.length() == 0) {
                mask = "0";
            }
        }
    }

    private void triggerEnter() {
        validateCurrentField();
        if (isIpValid()) {
            int[] intOctets = new int[4];
            for (int i = 0; i < 4; i++) {
                intOctets[i] = Integer.parseInt(octets[i]);
            }
            int cidrVal = Integer.parseInt(mask);
            midlet.showResultScreen(intOctets, cidrVal);
        }
    }

    private void validateCurrentField() {
        if (focusedField < 4) {
            if (octets[focusedField].length() == 0) {
                octets[focusedField] = "0";
            }
        } else {
            if (mask.length() == 0) {
                mask = "24";
            }
        }
    }

    private boolean isIpValid() {
        try {
            for (int i = 0; i < 4; i++) {
                int val = Integer.parseInt(octets[i]);
                if (val < 0 || val > 255) return false;
            }
            int mVal = Integer.parseInt(mask);
            if (mVal < 0 || mVal > 32) return false;
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}