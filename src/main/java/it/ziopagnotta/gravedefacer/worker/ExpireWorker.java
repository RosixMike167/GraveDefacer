package it.ziopagnotta.gravedefacer.worker;

import it.ziopagnotta.gravedefacer.GraveDefacer;
import it.ziopagnotta.gravedefacer.config.Utils;
import it.ziopagnotta.gravedefacer.objects.Grave;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
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

            if(!GraveDefacer.pluginConfig.getBoolean("settings.send-message-when-grave-expire"))
                return;

            Player player = Bukkit.getPlayer(grave.getOwner());

            if(player == null || !player.isOnline()) {
                return;
            }

            Utils.sendConfigMessage(player, GraveDefacer.pluginConfig.getString("lang.expire-grave-message"));
        }
    }

    public void start() {
        runTaskTimer(plugin, 0L, 20L);
    }
}
