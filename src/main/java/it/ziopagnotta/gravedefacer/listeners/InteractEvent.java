package it.ziopagnotta.gravedefacer.listeners;

import it.ziopagnotta.gravedefacer.GraveDefacer;
import it.ziopagnotta.gravedefacer.config.Utils;
import it.ziopagnotta.gravedefacer.config.PluginConfig;
import it.ziopagnotta.gravedefacer.objects.Grave;
import it.ziopagnotta.gravedefacer.objects.GraveFactory;
import it.ziopagnotta.gravedefacer.objects.Items;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class InteractEvent implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(@NotNull PlayerInteractAtEntityEvent event) {
        if(!(event.getRightClicked() instanceof ArmorStand entity) || !entity.hasMetadata("gravedefacer"))
            return;

        Player player = event.getPlayer();

        if(Utils.isBlockedWorld(player.getWorld().getName()))
            return;

        event.setCancelled(true);

        GraveFactory graveFactory = GraveDefacer.graveFactory;
        Optional<Grave> optionalGrave = graveFactory.getGraveByEntity(entity);

        if(optionalGrave.isEmpty())
            return;

        Grave grave = optionalGrave.get();
        PluginConfig config = GraveDefacer.pluginConfig;
        boolean isSamePlayer = player.getName().equals(grave.getOwner());

        if(!config.getBoolean("settings.allow-player-deface-own-grave") && isSamePlayer) {
            return;
        }

        if(config.getBoolean("settings.create-explosion-on-deface")) {
            float power = (float) config.getInt("settings.explosion-power");
            boolean fire = config.getBoolean("settings.should-explosion-set-fire-around");
            boolean bblocks = config.getBoolean("settings.should-explosion-break-blocks");

            grave.getOrigin().getWorld().createExplosion(grave.getModel(), grave.getOrigin().add(0, 1, 0), power, fire, bblocks);
        }

        if(grave.isDropItems()) {
            for(ItemStack itemStack : grave.getContents()) {
                if(itemStack == null || itemStack.isEmpty())
                    continue;

                grave.getOrigin().getWorld().dropItemNaturally(grave.getOrigin().add(0, 1.5, 0), itemStack);
            }
        } else
            player.openInventory(Items.getInventory(grave));

        grave.despawn();
        graveFactory.remove(grave);

        player.getLocation().getWorld().playSound(
                player.getLocation(),
                Bukkit.createBlockData(Material.STONE).getSoundGroup().getBreakSound(),
                1f,
                1f
        );

        if(!config.getBoolean("settings.send-global-chat-message-on-deface") || !config.getBoolean("settings.send-global-chat-message-on-deface-own-grave"))
            return;

        if(isSamePlayer) {
            Bukkit.getOnlinePlayers().forEach(players ->
                    Utils.sendConfigMessage(players, config.getString("lang.own-chat-message")
                            .replace("%player%", player.getName()))
            );

            return;
        }

        Bukkit.getOnlinePlayers().forEach(players ->
                Utils.sendConfigMessage(players, config.getString("lang.chat-message")
                        .replace("%player%", player.getName())
                        .replace("%victim%", grave.getOwner()))
        );
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(@NotNull EntityDamageEvent event) {
        if(event.getEntity() instanceof ArmorStand armorStand && armorStand.hasMetadata("gravedefacer"))
            event.setCancelled(true);
    }
}
