package it.ziopagnotta.gravedefacer.gui;

import eu.macsworks.libs.anvilgui.AnvilGUI;
import eu.macsworks.premium.macslibs.utils.ColorTranslator;
import eu.macsworks.premium.macslibs.utils.ItemBuilder;
import it.ziopagnotta.gravedefacer.GraveDefacer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;

import static it.ziopagnotta.gravedefacer.GraveDefacer.pluginConfig;
import static it.ziopagnotta.gravedefacer.config.Utils.sendConfigMessage;

public class SearchGUI {
    public static void openInventory(Player player) {
        new AnvilGUI.Builder()
                .plugin(JavaPlugin.getProvidingPlugin(GraveDefacer.class))
                .title(ColorTranslator.translate(pluginConfig.getString("gui.search.gui-title")))

                .itemLeft(new ItemBuilder()
                        .name(ColorTranslator.translate(pluginConfig.getString("gui.search.left-button-title")))
                        .material(Material.PAPER)
                        .addEnchant(Enchantment.DURABILITY, 1)
                        .makeStatic()
                        .build())
                .itemOutput(new ItemBuilder()
                        .name("")
                        .material(Material.ENCHANTED_BOOK)
                        .makeStatic()
                        .build())
                .onClick((slot, stateSnapshot) -> {
                    String text = stateSnapshot.getText();

                    if(slot != AnvilGUI.Slot.OUTPUT) {
                        return Collections.singletonList(AnvilGUI.ResponseAction.run(() -> {}));
                    }

                    Player target = Bukkit.getPlayer(text);

                    if (target == null || !target.isOnline())
                        return Collections.singletonList(AnvilGUI.ResponseAction.run(() ->
                                sendConfigMessage(player, pluginConfig.getString("lang.search-no-found-message").replace("%player%", text)))
                        );

                    return Collections.singletonList(AnvilGUI.ResponseAction.run(() -> {
                        player.closeInventory();
                        Inventory graveListInventory = GraveListGUI.getInventoryOf(target, true, 0);

                        if(graveListInventory == null) {
                            sendConfigMessage(player, pluginConfig.getString("lang.list-gui-empty-message"));
                            return;
                        }

                        player.openInventory(graveListInventory);
                    }));
                })
                .open(player);
    }
}
