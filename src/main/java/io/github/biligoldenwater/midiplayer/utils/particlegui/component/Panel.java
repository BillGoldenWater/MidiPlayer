package io.github.biligoldenwater.midiplayer.utils.particlegui.component;

import io.github.biligoldenwater.midiplayer.utils.particlegui.Pixel;
import io.github.biligoldenwater.midiplayer.utils.particlegui.interfaces.Component;
import org.bukkit.Color;

import java.util.ArrayList;
import java.util.List;

public class Panel extends Component {
    private final boolean drawBorder;

    private final List<Component> children;

    public Panel(int x, int y, int width, int height, boolean drawBorder) {
        super(x, y, width, height);

        this.drawBorder = drawBorder;

        this.children = new ArrayList<>();
    }

    public void render() {
        int width = getWidth();

        //region draw children
        for (Component child : children) {
            child.render();
        }
        for (Component child : children) {
            int childX = child.getX() + (drawBorder ? 1 : 0);
            int childY = child.getY() + (drawBorder ? 1 : 0);
            for (int y = 0; y < child.getHeight(); y++) {
                for (int x = 0; x < child.getWidth(); x++) {
                    int posX = childX + x;
                    int posY = childY + y;

                    if (posX >= this.getWidth() - (drawBorder ? 1 : 0) ||
                            posY >= this.getHeight() - (drawBorder ? 1 : 0))
                        continue; // 当超出窗口边界时

                    Pixel pixel = child.getPixel(x, y);
                    if (pixel == null || pixel.getAlpha() == 0) continue; // 组件像素为空或完全透明时

                    Pixel originalPixel = getPixel(posX, posY);
                    if (originalPixel == null) { // 如果窗口当前像素为空
                        originalPixel = pixel;
                    } else {
                        originalPixel.overlap(pixel);
                    }

                    pixels[posX + posY * width] = originalPixel;
                }
            }
        }
        //endregion

        //region draw border
        if (drawBorder) {
            for (int y = 0; y < this.getHeight(); y++) {
                pixels[y * width] = new Pixel(Color.BLACK);
                pixels[(this.getWidth() - 1) + y * width] = new Pixel(Color.BLACK);
            }
            for (int x = 0; x < this.getWidth(); x++) {
                pixels[x] = new Pixel(Color.BLACK);
                pixels[x + (this.getHeight() - 1) * width] = new Pixel(Color.BLACK);
            }
        }
        //endregion
    }

    public void addChild(Component child) {
        this.children.add(child);
    }

    public void removeChild(Component child) {
        this.children.remove(child);
    }
}
