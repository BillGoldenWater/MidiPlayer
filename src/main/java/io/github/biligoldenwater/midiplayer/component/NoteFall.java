package io.github.biligoldenwater.midiplayer.component;

import io.github.biligoldenwater.midiplayer.utils.particlegui.PixelColor;
import io.github.biligoldenwater.midiplayer.utils.particlegui.component.Block;
import io.github.biligoldenwater.midiplayer.utils.particlegui.component.Panel;
import io.github.biligoldenwater.midiplayer.utils.particlegui.interfaces.Component;
import org.bukkit.Color;

import java.util.ArrayList;
import java.util.List;

public class NoteFall extends Panel {

    public NoteFall(int x, int y, int width, int height, boolean drawBorder) {
        super(x, y, width, height, drawBorder);
    }

    public void update() {
        List<Component> needRemove = new ArrayList<>();

        forEachChildren((child) -> {
            if (child instanceof Note) {
                Note block = (Note) child;

                long startTs = block.getStartTs() / 100;
                long tsNow = System.currentTimeMillis() / 100;

                if (!block.ended) {
                    block.setSize(1, Math.max((int) (tsNow - startTs), 1));
                } else {
                    block.setY((int) (tsNow - startTs) - block.getHeight());
                }

                if (block.getY() > clientHeight) {
                    needRemove.add(block);
                }
            }
        });

        needRemove.forEach(this::removeChild);

        onChange();
    }

    public void noteOn(int note) {
        addChild(new Note(note, System.currentTimeMillis()));
    }

    public void noteOff(int note) {
        forEachChildren((child) -> {
            if (child instanceof Note) {
                Note block = (Note) child;
                if (!block.ended && block.getNote() == note) {
                    block.ended = true;
                }
            }
        });
    }

    public static class Note extends Block {
        private final long startTs;
        private final int note;

        private boolean ended;

        public Note(int note, long startTs) {
            super((note + 1) * 2, 0, 1, 1);
            this.startTs = startTs;
            this.note = note;

            this.setColor(new PixelColor(Color.LIME));
            ended = false;
        }

        public long getStartTs() {
            return startTs;
        }

        public int getNote() {
            return note;
        }

        public boolean isEnded() {
            return ended;
        }

        public void end() {
            this.ended = true;
        }
    }
}
