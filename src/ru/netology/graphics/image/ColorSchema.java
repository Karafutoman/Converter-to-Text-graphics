package ru.netology.graphics.image;

public class ColorSchema implements TextColorSchema {
    public static final char[] C2 = {'■', '$', '@', '%', '*', '+', '*', '-'};

    @Override
    public char convert(int color) {
        return C2[color/32]; // переводим 256 цветов в наши (8 бит) 8 символов
    }
}

