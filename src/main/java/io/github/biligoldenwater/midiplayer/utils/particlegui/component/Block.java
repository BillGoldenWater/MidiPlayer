package io.github.biligoldenwater.midiplayer.utils.particlegui.component;

import io.github.biligoldenwater.midiplayer.utils.particlegui.PixelColor;
import io.github.biligoldenwater.midiplayer.utils.particlegui.interfaces.Component;
import org.bukkit.Color;

public class Block extends Component {
    private PixelColor color;

    public Block(int x, int y, int width, int height) {
        super(x, y, width, height);

        this.color = new PixelColor(Color.BLACK);
    }

    @Override
    public void render() {

    }

    @Override
    public PixelColor getPixel(int x, int y) {
        return color;
    }

    public PixelColor getColor() {
        return color;
    }

    public void setColor(PixelColor color) {
        this.color = color;
    }
}
