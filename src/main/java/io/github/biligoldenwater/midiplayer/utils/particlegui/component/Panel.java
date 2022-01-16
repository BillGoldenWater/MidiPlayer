package io.github.biligoldenwater.midiplayer.utils.particlegui.component;

import io.github.biligoldenwater.midiplayer.utils.particlegui.PixelColor;
import io.github.biligoldenwater.midiplayer.utils.particlegui.interfaces.Component;
import org.bukkit.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Panel extends Component {
    // final option
    private final boolean drawBorder;
    // final data
    private final List<Component> children;
    // option
    private PixelColor borderColor;

    public Panel(int x, int y, int width, int height, boolean drawBorder) {
        super(x, y, width, height, false);
        if (drawBorder) {
            this.clientWidth = Math.max(width - 2, 0);
            this.clientHeight = Math.max(height - 2, 0);
        }

        this.drawBorder = drawBorder;

        this.children = new ArrayList<>();

        this.borderColor = new PixelColor(Color.BLACK);

        this.render();
    }

    public void render() {
        int width = getWidth();
        List<Component> children = new ArrayList<>(this.children);

        this.pixels = new PixelColor[width * getHeight()];

        //region draw children
        for (Component child : children) {
            if (!child.needRender()) continue;

            child.render();
        }
        for (Component child : children) {
            int childX = child.getX() + (drawBorder ? 1 : 0);
            int childY = child.getY() + (drawBorder ? 1 : 0);
            for (int y = 0; y < child.getHeight(); y++) {
                for (int x = 0; x < child.getWidth(); x++) {
                    int posX = childX + x;
                    int posY = childY + y;

                    // 当超出窗口边界时
                    if (posX < (drawBorder ? 1 : 0) || posY < (drawBorder ? 1 : 0)) continue;
                    if (posX >= this.getWidth() - (drawBorder ? 1 : 0) ||
                            posY >= this.getHeight() - (drawBorder ? 1 : 0))
                        continue;

                    PixelColor pixelColor = child.getPixel(x, y);
                    if (pixelColor == null || pixelColor.getAlpha() == 0) continue; // 组件像素颜色为空或完全透明时

                    PixelColor originalPixelColor = getPixel(posX, posY);
                    if (originalPixelColor == null) { // 如果窗口当前像素为空
                        originalPixelColor = pixelColor;
                    } else {
                        originalPixelColor.overlap(pixelColor);
                    }

                    pixels[posX + posY * width] = originalPixelColor;
                }
            }
        }
        //endregion

        //region draw border
        if (drawBorder) {
            for (int y = 0; y < this.getHeight(); y++) {
                pixels[y * width] = borderColor;
                pixels[(this.getWidth() - 1) + y * width] = borderColor;
            }
            for (int x = 0; x < this.getWidth(); x++) {
                pixels[x] = borderColor;
                pixels[x + (this.getHeight() - 1) * width] = borderColor;
            }
        }
        //endregion

        changeApplied();
    }

    @Override
    public boolean needRender() {
        for (Component child : children) {
            if (child.needRender()) return true;
        }
        return super.needRender();
    }

    public void addChild(Component child) {
        onChange();
        child.setParent(this);
        this.children.add(child);
    }

    public void removeChild(Component child) {
        child.setParent(null);
        this.children.remove(child);
    }

    public void forEachChildren(Consumer<? super Component> action) {
        children.forEach(action);
    }

    public int getChildrenSize() {
        return children.size();
    }

    public boolean isDrawBorder() {
        return drawBorder;
    }

    public PixelColor getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(PixelColor borderColor) {
        onChange();
        this.borderColor = borderColor;
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        if (drawBorder) {
            this.clientWidth = Math.max(width - 2, 0);
            this.clientHeight = Math.max(height - 2, 0);
        }
    }
}
