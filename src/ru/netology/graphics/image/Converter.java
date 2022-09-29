package ru.netology.graphics.image;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class Converter implements TextGraphicsConverter {
    private int width;
    private int height;
    private double maxRatio;
    private TextColorSchema schema;
    private int newWidth;
    private int newHeight;


    public Converter() {
        schema = new ColorSchema();
    }

    @Override
    public String convert(String url) throws IOException, BadImageSizeException {

        BufferedImage img = ImageIO.read(new URL(url)); // чтение с адреса url картинки
        maximumRatio(img); // максимальное соотношение
        resizeImage(img); // изменение размера изображения
        char[][] graph = new char[newHeight][newWidth]; // массив для новой высоты и ширины картинки
        Image scaledImage = img.getScaledInstance(newWidth, newHeight, BufferedImage.SCALE_SMOOTH); //картинка плавно сузиться
        // на новые размеры и мы получаем новую ссылку
        BufferedImage bwImg = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_BYTE_GRAY); // Создадим новую пустую картинку нужных размеров, заранее указав последним
        // параметром чёрно-белую цветовую палитру
        Graphics2D graphics = bwImg.createGraphics(); //Просим у этой картинки инструмент для рисования на ней
        graphics.drawImage(scaledImage, 0, 0, null); // А этому инструменту скажем, чтобы он скопировал содержимое из нашей суженной картинки
        var bwRaster = bwImg.getRaster(); // инструмент для прохода по пикселям изображения. спросить пиксель на нужных
        // нам координатах, указав номер столбца (w) и строки (h)
        /// Логикой превращения цвета в символ будет заниматься другой объект
        for (int h = 0; h < newHeight; h++) {
            for (int w = 0; w < newWidth; w++) {
                int color = bwRaster.getPixel(w, h, new int[3])[0];
                char c = schema.convert(color);
                graph[h][w] = c;
            }
        }
        StringBuilder sb = new StringBuilder();
        printText(graph, sb);
        return sb.toString();
    }

    @Override
    public void setMaxWidth(int width) { //Устанавливает максимальную ширину результирующего изображения в "текстовых пикселях".
        this.width = width;
    }

    @Override
    public void setMaxHeight(int height) { //Устанавливает максимальную высоту результирующего изображения в "текстовых пикселях".
        this.height = height;
    }

    @Override
    public void setMaxRatio(double maxRatio) { //Устанавливает максимально допустимое соотношение сторон исходного изображения.
        this.maxRatio = maxRatio;
    }

    @Override
    public void setTextColorSchema(TextColorSchema colorSchema) { //Устанавливает символьную цветовую схему, которую будет использовать конвертер
        this.schema = colorSchema;
    }

    private void maximumRatio(BufferedImage img) throws BadImageSizeException { // метод определения максимального соотношения картинки
        double ratio;
        if (img.getWidth() / img.getHeight() > img.getHeight() / img.getWidth()) {
            ratio = (double) img.getWidth() / (double) img.getHeight();
        } else {
            ratio = (double) img.getHeight() / (double) img.getWidth();
        } if (ratio > maxRatio && maxRatio != 0) {
            throw new BadImageSizeException(ratio, maxRatio);}
    }

    private void printText(char[][] graph, StringBuilder sb) { // Собираем все символы в один большой текст,
        // чтобы не было узким, каждый пиксель превращать в два повторяющихся символа, полученных от схемы
        for (int i = 0; i < graph.length; i++) {
            for (int j = 0; j < graph[i].length; j++) {
                sb.append(graph[i][j]);
                sb.append(graph[i][j]);
            }
            sb.append("\n"); //В конце каждой строчки текстового изображения находится символ переноса строки, который в джаве пишется как \n
        }
    }

    private void resizeImage(BufferedImage img) { // метод изменения размера изображения
        double widthСoefficient = 0;
        double heightСoefficient = 0;
        if (img.getWidth() > width || img.getHeight() > height) {
            if (width != 0) {
                widthСoefficient = img.getWidth() / width;
            } else widthСoefficient = 1;
            if (height != 0) {
                heightСoefficient = img.getHeight() / height;
            } else heightСoefficient = 1;
            double maxCoeff = Math.max(widthСoefficient, heightСoefficient);
            newWidth = (int) (img.getWidth() / maxCoeff);
            newHeight = (int) (img.getHeight() / maxCoeff);
        } else {
            newWidth = img.getWidth();
            newHeight = img.getHeight();
        }
    }
}