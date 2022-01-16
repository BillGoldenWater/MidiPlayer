package io.github.biligoldenwater.midiplayer.component;

import io.github.biligoldenwater.midiplayer.utils.particlegui.PixelColor;
import io.github.biligoldenwater.midiplayer.utils.particlegui.component.Block;
import io.github.biligoldenwater.midiplayer.utils.particlegui.component.ProgressBar;
import io.github.biligoldenwater.midiplayer.utils.particlegui.component.Window;
import org.bukkit.Color;

public class MidiDisplay {
    private final Window window;
    private final ProgressBar progressBar;
    private final NoteFall noteFall;

    private final long tickLength;

    public MidiDisplay(long tickLength) {
        this.tickLength = tickLength;
        int width = 2 * 128 + 7;
        int height = 100 + 9;

        this.window = new Window(width, height);
        this.window.setBorderColor(new PixelColor(Color.GRAY));

        width = this.window.getClientWidth();
        height = this.window.getClientHeight();

        this.progressBar = new ProgressBar(1, 1, width - 2, 2);
        this.window.addChild(progressBar);

        Block block1 = new Block(0, 4, width, 1);
        block1.setColor(new PixelColor(Color.GRAY));
        this.window.addChild(block1);

        this.noteFall = new NoteFall(1, 6, width - 2, height - 7, true);
        this.noteFall.setBorderColor(new PixelColor(Color.GRAY));
        this.window.addChild(noteFall);
    }

    public void setTick(long tick) {
        progressBar.setPercent((1f * tick / tickLength) * 100);
        noteFall.update();
    }

    public void noteOn(int note) {
        noteFall.noteOn(note);
    }

    public void noteOff(int note) {
        noteFall.noteOff(note);
    }

    public Window getWindow() {
        return window;
    }
}
