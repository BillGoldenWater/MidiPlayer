package io.github.biligoldenwater.midiplayer.utils.particlegui.component;

import io.github.biligoldenwater.midiplayer.utils.particlegui.PixelColor;
import io.github.biligoldenwater.midiplayer.utils.particlegui.interfaces.Component;
import org.bukkit.Color;

public class ProgressBar extends Component {
    private boolean fitParent = false;
    private boolean isHorizontal;
    private double percent;
    private PixelColor color;

    public ProgressBar(int x, int y, int width, int height, boolean isHorizontal) {
        super(x, y, width, height, false);

        this.color = new PixelColor(Color.BLUE);
        this.isHorizontal = isHorizontal;

        render();
    }

    public ProgressBar(int x, int y, int width, int height) {
        this(x, y, width, height, true);
    }

    @Override
    public void render() {
        if (fitParent && getParent() != null) {
            setSize(getParent().getClientWidth(), getParent().getClientHeight());
        } else {
            this.pixels = new PixelColor[getWidth() * getHeight()];
        }
        if (!isHorizontal) {
            for (int i = 0; i < pixels.length * percent * 0.01; i++) {
                pixels[i] = color;
            }
        } else {
            long pixelNum = 1;

            int width = getWidth();
            int height = getHeight();

            for (int x = 0; x < width; x++) {
                if (pixelNum > pixels.length * percent * 0.01) break;
                for (int y = height - 1; y >= 0; y--) {
                    if (pixelNum > pixels.length * percent * 0.01) break;
                    pixels[x + y * width] = color;
                    pixelNum++;
                }
            }
        }
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        onChange();
        this.percent = Math.min(percent, 100f);
    }

    public PixelColor getColor() {
        return color;
    }

    public void setColor(PixelColor color) {
        onChange();
        this.color = color;
    }

    public boolean isHorizontal() {
        return isHorizontal;
    }

    public void setHorizontal(boolean horizontal) {
        onChange();
        isHorizontal = horizontal;
    }

    public boolean isFitParent() {
        return fitParent;
    }

    public void setFitParent(boolean fitParent) {
        this.fitParent = fitParent;
    }
}
