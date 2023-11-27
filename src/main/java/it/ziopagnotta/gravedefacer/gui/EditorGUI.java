package it.ziopagnotta.gravedefacer.gui;


import eu.macsworks.premium.macslibs.utils.ColorTranslator;
import eu.macsworks.premium.macslibs.utils.InventoryBuilder;
import eu.macsworks.premium.macslibs.utils.ItemBuilder;
import it.ziopagnotta.gravedefacer.config.Utils;
import it.ziopagnotta.gravedefacer.objects.Grave;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

import static it.ziopagnotta.gravedefacer.GraveDefacer.graveFactory;
import static it.ziopagnotta.gravedefacer.GraveDefacer.pluginConfig;
import static it.ziopagnotta.gravedefacer.config.Utils.*;

public class EditorGUI {
    @NotNull
    public static Inventory getInventory(@NotNull Grave grave, @NotNull Player player) {
        String name = grave.getOwner();

        return new InventoryBuilder()
                .named(ColorTranslator.translate(pluginConfig.getString("gui.editor.gui-title").replace("%player%", name)))
                .slots(27)
                .addItem(4, new ItemBuilder()
                        .skullOf(grave.getOwner())
                        .name(ColorTranslator.translate(pluginConfig.getString("gui.editor.grave-info-title")).replace("%player%", grave.getOwner()))
                        .lore(ColorTranslator.translate(pluginConfig.getString("gui.editor.grave-info-desc"))
                                .replace("%date%", Utils.formatInstant(grave.getInstant()))
                                .replace("%world%", grave.getOrigin().getWorld().getName())
                                .replace("%remtime%", Utils.getRemainingTime(grave)))
                        .makeStatic()
                        .build())
                .addItem(18, new ItemBuilder()
                        .name(ColorTranslator.translate(pluginConfig.getString("gui.list.close-gui-button")))
                        .material(Material.BARRIER)
                        .addEnchant(Enchantment.CHANNELING, 1)
                        .interactive(interactResult -> switchInv(interactResult.getClicker(), MainGUI.getInventory()))
                        .build())
                .addItem(11, ItemBuilder.builder()
                        .name(ColorTranslator.translate(pluginConfig.getString("gui.editor.teleport-button-title").replace("%player%", name)))
                        .lore(ColorTranslator.translate(pluginConfig.getString("gui.editor.teleport-button-desc")))
                        .material(Material.ELYTRA)
                        .addEnchant(Enchantment.DURABILITY, 1)
                        .interactive(interactResult -> {
                            player.teleportAsync(grave.getOrigin().add(0, 0.5, 0), PlayerTeleportEvent.TeleportCause.PLUGIN);
                            sendConfigMessage(player, pluginConfig.getString("lang.teleport-message").replace("%player%", name));
                            player.closeInventory();
                            getLogger().log(Level.INFO, "player: " + player.getName() + " teleported to the grave of player: " + name);
                        })
                        .build())
                .addItem(15, ItemBuilder.builder()
                        .name(ColorTranslator.translate(pluginConfig.getString("gui.editor.delete-button-title").replace("%player%", name)))
                        .lore(ColorTranslator.translate(pluginConfig.getString("gui.editor.delete-button-desc")))
                        .material(Material.BLAZE_POWDER)
                        .addEnchant(Enchantment.DURABILITY, 1)
                        .interactive(interactResult -> {
                            grave.despawn();
                            graveFactory.remove(grave);
                            sendConfigMessage(player, pluginConfig.getString("lang.delete-message").replace("%player%", name));
                            switchInv(player, GraveListGUI.getInventoryOf(null, true, 0));
                            getLogger().log(Level.INFO, "player: " + player.getName() + " deleted grave of player: " + name);
                        })
                        .build())
                .filled(new ItemBuilder().name(" ").material(Material.GRAY_STAINED_GLASS_PANE).makeStatic().build())
                .build();
    }
}
