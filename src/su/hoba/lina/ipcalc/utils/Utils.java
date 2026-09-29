package su.hoba.lina.ipcalc.utils;

import java.io.IOException;
import javax.microedition.lcdui.Image;

public class Utils {

    /**
     * Безопасная загрузка изображения из ресурсов JAR-архива.
     * Если файл не найден или поврежден, вернет null вместо вылета приложения.
     * 
     * @param path Абсолютный путь к ресурсу внутри jar
     * @return Объект Image или null при ошибке
     */
    public static Image loadImage(String path) {
        try {
            return Image.createImage(path);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}