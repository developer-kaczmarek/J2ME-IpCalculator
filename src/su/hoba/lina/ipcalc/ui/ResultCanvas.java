package su.hoba.lina.ipcalc.ui;

import javax.microedition.lcdui.*;
import java.util.Vector;
import su.hoba.lina.ipcalc.IPCalculator;
import su.hoba.lina.ipcalc.model.CalculatorLogic;
import su.hoba.lina.ipcalc.theme.Color;

public class ResultCanvas extends Canvas {
    private IPCalculator midlet;
    private Vector calculations;
    
    private int scrollOffset = 0;
    private int itemHeight = 52; 

    public ResultCanvas(IPCalculator midlet, int[] octets, int cidr) {
        this.midlet = midlet;
        setFullScreenMode(true);
        this.calculations = CalculatorLogic.calculate(octets, cidr);
    }

    protected void paint(Graphics g) {
        int w = getWidth();
        int h = getHeight();

        g.setColor(Color.BACKGROUND);
        g.fillRect(0, 0, w, h);

        ComponentRenderer.drawTopBar(g, w, "Результаты", null, null);

        int topBarHeight = 24;
        int bottomBarHeight = 24;
        int listAreaY = topBarHeight;
        int listAreaHeight = h - topBarHeight - bottomBarHeight;

        g.setClip(0, listAreaY, w, listAreaHeight);

        Font titleFont = Font.getFont(Font.FACE_SYSTEM, Font.STYLE_BOLD, Font.SIZE_SMALL);
        Font valueFont = Font.getFont(Font.FACE_MONOSPACE, Font.STYLE_PLAIN, Font.SIZE_MEDIUM);

        int currentY = listAreaY - scrollOffset;

        for (int i = 0; i < calculations.size(); i++) {
            CalculatorLogic.Calculation item = (CalculatorLogic.Calculation) calculations.elementAt(i);

            if (currentY + itemHeight > listAreaY && currentY < listAreaY + listAreaHeight) {
                
                g.setFont(titleFont);
                g.setColor(Color.ON_BACKGROUND);
                g.drawString(item.title, 12, currentY + 6, Graphics.LEFT | Graphics.TOP);

                g.setFont(valueFont);
                g.setColor(Color.ON_SURFACE_VARIANT);
                g.drawString(item.value, 12, currentY + 24, Graphics.LEFT | Graphics.TOP);
               
                g.setColor(Color.OUTLINE_VARIANT);
                g.drawLine(12, currentY + itemHeight - 1, w - 16, currentY + itemHeight - 1);
            }

            currentY += itemHeight;
        }

        g.setClip(0, 0, w, h);
        drawScrollbar(g, w, listAreaY, listAreaHeight);

        ComponentRenderer.drawBottomBar(g, w, h, null, null, "Назад");
    }

    private void drawScrollbar(Graphics g, int w, int areaY, int areaHeight) {
        int totalContentHeight = calculations.size() * itemHeight;
        if (totalContentHeight <= areaHeight) return;

        int sbWidth = 4; 
        int sbX = w - 8;  
        
        g.setColor(Color.OUTLINE_VARIANT);
        g.fillRect(sbX, areaY + 4, sbWidth, areaHeight - 8);

        int thumbHeight = (areaHeight * areaHeight) / totalContentHeight;
        if (thumbHeight < 15) thumbHeight = 15; 
        if (thumbHeight > areaHeight - 8) thumbHeight = areaHeight - 8;

        int maxScroll = totalContentHeight - areaHeight;
        int availableTrackSpace = (areaHeight - 8) - thumbHeight;
        
        int thumbY = areaY + 4;
        if (maxScroll > 0) {
            thumbY += (scrollOffset * availableTrackSpace) / maxScroll;
        }

        g.setColor(Color.PRIMARY);
        g.fillRect(sbX, thumbY, sbWidth, thumbHeight);
    }

    protected void keyPressed(int keyCode) {
        int action = getGameAction(keyCode);
        int topBarHeight = 24;
        int bottomBarHeight = 24;
        int listAreaHeight = getHeight() - topBarHeight - bottomBarHeight;
        int maxScroll = Math.max(0, (calculations.size() * itemHeight) - listAreaHeight);

        if (action == UP) {
            scrollOffset = Math.max(0, scrollOffset - itemHeight);
        } else if (action == DOWN) {
            scrollOffset = Math.min(maxScroll, scrollOffset + itemHeight);
        } else if (keyCode == -7) {
            midlet.showMainScreen();
        }

        repaint();
    }
}