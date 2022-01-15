package io.github.biligoldenwater.midiplayer.utils.particlegui.interfaces;

import io.github.biligoldenwater.midiplayer.utils.particlegui.PixelColor;

public abstract class Component {
    private int x;
    private int y;
    private int width;
    private int height;
    private Component parent;
    protected PixelColor[] pixels;

    public Component(int x, int y, int width, int height) {
        this(x, y, width, height, true);
    }

    public Component(int x, int y, int width, int height, boolean renderImmediately) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.pixels = new PixelColor[width * height];
        if (renderImmediately) this.render();
    }

    public abstract void render();

    public boolean needRender() {
        return false;
    }

    public void setPixel(int x, int y, PixelColor pixelColor) {
        pixels[x + y * width] = pixelColor;
    }

    public PixelColor getPixel(int x, int y) {
        return pixels[x + y * width];
    }

    public Component getParent() {
        return parent;
    }

    public void setParent(Component parent) {
        this.parent = parent;
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
        this.pixels = new PixelColor[width * height];
    }
}