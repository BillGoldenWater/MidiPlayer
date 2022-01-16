package io.github.biligoldenwater.midiplayer.utils.particlegui;

import io.github.biligoldenwater.midiplayer.utils.particlegui.component.Window;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class ParticleGui extends BukkitRunnable {
    private final Player player;
    private final boolean debug;

    private double distance = 10;
    private Window window;

    private long drawCall = 0;
    private long lastEnd = 0;

    private Location tLoc; // targetLoc
    private double yaw = 0;
    private double pitch = 0;
    private Vector pitchAxis;

    public ParticleGui(Player targetPlayer, Window window) {
        this(targetPlayer, window, false);
    }

    public ParticleGui(Player targetPlayer, Window window, boolean debug) {
        this.player = targetPlayer;
        this.window = window;
        this.debug = debug;
    }

    @Override
    public void run() {
        long start = 0;
        if (debug) {
            player.sendMessage("===========================================");
            start = System.currentTimeMillis();
            player.sendMessage(String.format("delay: %dms", start - lastEnd));
        }

        this.updateOffsetInfo();
        if (window.needRender()) window.render();
        this.drawWindow(window);

        if (debug) {
            player.sendMessage(String.format("drawCall: %d", drawCall));
            drawCall = 0;

            long end = System.currentTimeMillis();
            player.sendMessage(String.format("cost: %dms", end - start));
            lastEnd = end;
        }
    }

    public void drawWindow(Window window) {
        int width = window.getWidth();
        int height = window.getHeight();

        int halfWidth = width / 2;
        int halfHeight = height / 2;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                PixelColor pixelColor = window.getPixel(x, y);
                if (pixelColor != null) {
                    drawPixel(halfWidth - x, y - halfHeight, pixelColor);
                }
            }
        }
    }

    public void drawPixel(int x, int y, PixelColor pixelColor) {
        Vector pixelLoc = new Vector();

        pixelLoc.setX(x * 0.04);
        pixelLoc.setY(y * -1 * 0.04);
        pixelLoc.setZ(distance);

        pixelLoc.rotateAroundY(yaw);
        pixelLoc.rotateAroundAxis(pitchAxis, pitch);
        Location tLoc = this.tLoc.clone().add(pixelLoc);

        Particle.DustOptions dust = new Particle.DustOptions(pixelColor.toBukkitColor(), 0.2f);

        player.spawnParticle(Particle.REDSTONE, tLoc.getX(), tLoc.getY(), tLoc.getZ(), 2, 0, 0, 0, 0, dust);

        if (debug) drawCall++;
    }

    public void updateOffsetInfo() {
        if (player.isSneaking()) return;

        this.tLoc = player.getEyeLocation(); // targetLoc

        double yaw = this.tLoc.getYaw();
        if (yaw < 0) yaw = yaw + 360;
        yaw = Math.toRadians(yaw) * -1;

        double pitch = this.tLoc.getPitch();
        pitch = Math.toRadians(pitch);

        Vector pitchAxis = new Vector();
        pitchAxis.setX(1);
        pitchAxis.rotateAroundY(yaw);

        this.yaw = yaw;
        this.pitch = pitch;
        this.pitchAxis = pitchAxis;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public Window getWindow() {
        return window;
    }

    public void setWindow(Window window) {
        this.window = window;
    }
}
