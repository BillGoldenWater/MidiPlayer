package io.github.biligoldenwater.midiplayer.utils.particlegui.interfaces;

import io.github.biligoldenwater.midiplayer.utils.particlegui.Pixel;

public abstract class Component {
    private int x;
    private int y;
    private int width;
    private int height;
    protected Pixel[] pixels;

    public Component(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.pixels = new Pixel[width * height];
    }

    public abstract void render();

    public void setPixel(int x, int y, Pixel pixel) {
        pixels[x + y * width] = pixel;
    }

    public Pixel getPixel(int x, int y) {
        return pixels[x + y * width];
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
        this.pixels = new Pixel[width * height];
    }
}