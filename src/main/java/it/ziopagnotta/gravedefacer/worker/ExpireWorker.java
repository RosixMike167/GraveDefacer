package it.ziopagnotta.gravedefacer.worker;

import it.ziopagnotta.gravedefacer.GraveDefacer;
import it.ziopagnotta.gravedefacer.config.Utils;
import it.ziopagnotta.gravedefacer.objects.Grave;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ExpireWorker extends BukkitRunnable {
    private final JavaPlugin plugin;

    public ExpireWorker(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for(Grave grave : GraveDefacer.graveFactory.getGravesCache()) {
            if(!Utils.hasExpired(grave)) {
                continue;
            }

            grave.despawn();
            GraveDefacer.graveFactory.remove(grave);
        }
    }

    public void start() {
        runTaskTimer(plugin, 0L, 20L);
    }
}
