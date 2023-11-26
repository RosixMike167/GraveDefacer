package it.ziopagnotta.gravedefacer.listeners;

import it.ziopagnotta.gravedefacer.GraveDefacer;
import it.ziopagnotta.gravedefacer.config.Utils;
import it.ziopagnotta.gravedefacer.objects.Grave;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.logging.Level;

public class DeathEvent implements Listener {
    private final static Random RND = new Random();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(@NotNull PlayerDeathEvent event) {
        Player player = event.getPlayer();

        if(Utils.isBlockedWorld(player.getWorld().getName()))
            return;

        String owner = player.getName();

        if(!Utils.canPlayerHaveGrave(owner)) {
            String number = String.valueOf(GraveDefacer.graveFactory.getNumberGravesByOwner(owner));
            String limit = String.valueOf(GraveDefacer.pluginConfig.getInt("settings.player-graves-limit"));

            Utils.sendConfigMessage(player, GraveDefacer.pluginConfig.getString("lang.player-reached-grave-number")
                    .replace("%number%", number)
                    .replace("%limit%", limit)
            );
            return;
        }

        boolean drop = GraveDefacer.pluginConfig.getBoolean("settings.should-drop-instead-open-inv");
        boolean random = GraveDefacer.pluginConfig.getBoolean("settings.random-action-instead-fixed");

        Location location = Utils.getGroundLocation(player.getLocation(), player.getLocation());

        if(location == null) {
            Utils.getLogger().log(Level.WARNING, "failed to create grave for: " + owner + ". y is over the limits. [" + Utils.round(player.getY()) + "/-64, or +320");
            return;
        }

        Grave grave = new Grave.Builder()
                .owner(owner)
                .origin(location)
                .dropItems(random ? RND.nextBoolean() : drop)
                .contents(new ArrayList<>(Arrays.asList(player.getInventory().getContents())))
                .build();

        GraveDefacer.graveFactory.put(grave);
        grave.spawn();

        event.getDrops().clear();
    }
}
