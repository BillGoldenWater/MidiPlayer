package io.github.biligoldenwater.midiplayer.utils.particlegui;

import org.bukkit.Color;

public class Pixel {
    private double red;
    private double green;
    private double blue;
    private double alpha;

    public Pixel() {
        this.red = 0;
        this.green = 0;
        this.blue = 0;
        this.alpha = 1;
    }

    public Pixel(double red, double green, double blue, double alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public Pixel(double red, double green, double blue) {
        this(red, green, blue, 1);
    }

    public Pixel(Color color) {
        this(color.getRed() / 255.0, color.getGreen() / 255.0, color.getBlue() / 255.0);
    }

    public Pixel overlap(Pixel pixel) {
        if (pixel.alpha == 1) {
            this.red = pixel.red;
            this.green = pixel.green;
            this.blue = pixel.blue;
            return this;
        }
        this.red = this.red * (1 - pixel.alpha) + pixel.red * pixel.alpha;
        this.green = this.green * (1 - pixel.alpha) + pixel.green * pixel.alpha;
        this.blue = this.blue * (1 - pixel.alpha) + pixel.blue * pixel.alpha;
        return this;
    }

    public boolean equals(Pixel pixel) {
        return this.red == pixel.red && this.green == pixel.green && this.blue == pixel.blue && this.alpha == pixel.alpha;
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
