package io.github.biligoldenwater.midiplayer.modules;

import io.github.biligoldenwater.midiplayer.MidiPlayer;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class NoteParticle {
    private boolean isRunning;
    private final Location particleSpawnLocation;
    private final World world;

    public NoteParticle(Location particleSpawnLocation){
        this.world = particleSpawnLocation.getWorld();
        this.particleSpawnLocation = new Location(particleSpawnLocation.getWorld(),particleSpawnLocation.getBlockX(),particleSpawnLocation.getBlockY(),particleSpawnLocation.getBlockZ());
        isRunning = true;

    }

    public void start(JavaPlugin plugin, int delayMillis, double blockPerStep){
        BukkitRunnable updateParticleLocation = new BukkitRunnable() {
            @Override
            public void run() {
                plugin.getServer().getScheduler().runTask(MidiPlayer.getInstance(), new Runnable() {
                    @Override
                    public void run() {
                        Entity entity = particleSpawnLocation.getWorld().spawnEntity(particleSpawnLocation, EntityType.SHULKER_BULLET);
                        entity.setVelocity(new Vector(0, -0.5, 0));
                        entity.setGravity(false);
                        entity.setGlowing(true);
                    }
                });
//                while (isRunning){
//                    if (!world.getBlockAt(particleSpawnLocation).getType().equals(Material.AIR)){
//                        isRunning = false;
//                    }
//
//                    world.spawnParticle(Particle.END_ROD,particleSpawnLocation,1,0.1,0.1,0.1,0);
//
//                    particleSpawnLocation.setY(particleSpawnLocation.getY()-blockPerStep);
//
//                    try {
//                        Thread.sleep(delayMillis);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
                cancel();
            }
        };
        updateParticleLocation.runTaskAsynchronously(plugin);
    }

    public void stop(){
        isRunning = false;
    }

    public boolean isRunning(){
        return isRunning;
    }
}
