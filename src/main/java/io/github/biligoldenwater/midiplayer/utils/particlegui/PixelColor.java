package io.github.biligoldenwater.midiplayer.utils.particlegui;

import org.bukkit.Color;

public class PixelColor {
    private double red;
    private double green;
    private double blue;
    private double alpha;

    public PixelColor() {
        this.red = 0;
        this.green = 0;
        this.blue = 0;
        this.alpha = 1;
    }

    public PixelColor(double red, double green, double blue, double alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public PixelColor(double red, double green, double blue) {
        this(red, green, blue, 1);
    }

    public PixelColor(Color color) {
        this(color.getRed() / 255.0, color.getGreen() / 255.0, color.getBlue() / 255.0);
    }

    public PixelColor overlap(PixelColor pixelColor) {
        if (pixelColor.alpha == 1) {
            this.red = pixelColor.red;
            this.green = pixelColor.green;
            this.blue = pixelColor.blue;
            return this;
        }
        this.red = this.red * (1 - pixelColor.alpha) + pixelColor.red * pixelColor.alpha;
        this.green = this.green * (1 - pixelColor.alpha) + pixelColor.green * pixelColor.alpha;
        this.blue = this.blue * (1 - pixelColor.alpha) + pixelColor.blue * pixelColor.alpha;
        return this;
    }

    public boolean equals(PixelColor pixelColor) {
        return this.red == pixelColor.red && this.green == pixelColor.green && this.blue == pixelColor.blue && this.alpha == pixelColor.alpha;
    }

    public Color toBukkitColor() {
        return Color.fromRGB((int) (red * 255), (int) (green * 255), (int) (blue * 255));
    }

    public double getRed() {
        return red;
    }

    public void setRed(double red) {
        this.red = red;
    }

    public double getGreen() {
        return green;
    }

    public void setGreen(double green) {
        this.green = green;
    }

    public double getBlue() {
        return blue;
    }

    public void setBlue(double blue) {
        this.blue = blue;
    }

    public double getAlpha() {
        return alpha;
    }

    public void setAlpha(double alpha) {
        this.alpha = alpha;
    }
}
