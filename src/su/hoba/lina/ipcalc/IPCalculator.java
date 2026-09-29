package su.hoba.lina.ipcalc;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import su.hoba.lina.ipcalc.ui.MainCanvas;
import su.hoba.lina.ipcalc.ui.ResultCanvas;

public class IPCalculator extends MIDlet {
    private Display display;
    private MainCanvas mainCanvas;

    public void startApp() {
        display = Display.getDisplay(this);
        showMainScreen();
    }

    public void pauseApp() {}

    public void destroyApp(boolean unconditional) {}

    public void showMainScreen() {
        if (mainCanvas == null) {
            mainCanvas = new MainCanvas(this);
        }
        display.setCurrent(mainCanvas);
    }

    public void showResultScreen(int[] octets, int cidr) {
        ResultCanvas resultCanvas = new ResultCanvas(this, octets, cidr);
        display.setCurrent(resultCanvas);
    }

    public void closeApp() {
        destroyApp(true);
        notifyDestroyed();
    }
}