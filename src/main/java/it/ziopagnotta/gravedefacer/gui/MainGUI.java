package it.ziopagnotta.gravedefacer.gui;

import eu.macsworks.premium.macslibs.utils.ColorTranslator;
import eu.macsworks.premium.macslibs.utils.InventoryBuilder;
import eu.macsworks.premium.macslibs.utils.ItemBuilder;
import it.ziopagnotta.gravedefacer.GraveDefacer;
import it.ziopagnotta.gravedefacer.config.Utils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import static it.ziopagnotta.gravedefacer.config.Utils.sendConfigMessage;

public class MainGUI {
    public static Inventory getInventory() {
        return new InventoryBuilder()
                .named(ColorTranslator.translate(GraveDefacer.pluginConfig.getString("gui.main.gui-title")))
                .slots(27)
                .addItem(11, new ItemBuilder()
                        .name(ColorTranslator.translate(GraveDefacer.pluginConfig.getString("gui.main.reload-button-title")))
                        .lore(ColorTranslator.translate(GraveDefacer.pluginConfig.getString("gui.main.reload-button-desc")))
                        .addEnchant(Enchantment.CHANNELING, 1)
                        .material(Material.CLOCK)
                        .interactive(interactResult -> {
                            Player player = interactResult.getClicker();

                            if(!player.hasPermission("gravedefacer.admin")) {
                                sendConfigMessage(player, GraveDefacer.pluginConfig.getString("lang.no-permission"));
                                return;
                            }

                            Utils.reloadConfig();
                            sendConfigMessage(player, GraveDefacer.pluginConfig.getString("lang.config-reload-message"));
                        })
                        .build()
                )
                .addItem(13, new ItemBuilder()
                        .name(ColorTranslator.translate(GraveDefacer.pluginConfig.getString("gui.main.list-button-title")))
                        .lore(ColorTranslator.translate(GraveDefacer.pluginConfig.getString("gui.main.list-button-desc")))
                        .addEnchant(Enchantment.CHANNELING, 1)
                        .material(Material.KNOWLEDGE_BOOK)
                        .interactive(interactResult -> {
                            Player player = interactResult.getClicker();

                            if(!player.hasPermission("gravedefacer.admin")) {
                                sendConfigMessage(player, GraveDefacer.pluginConfig.getString("lang.no-permission"));
                                return;
                            }

                            Inventory graveListInventory = GraveListGUI.getInventoryOf(null, true, 0);

                            if(graveListInventory == null) {
                                sendConfigMessage(player, GraveDefacer.pluginConfig.getString("lang.list-gui-empty-message"));
                                return;
                            }

                            interactResult.getClicker().openInventory(graveListInventory);
                        })
                        .build()
                )
                .addItem(15, new ItemBuilder()
                        .name(ColorTranslator.translate(GraveDefacer.pluginConfig.getString("gui.main.own-list-button-title")))
                        .lore(ColorTranslator.translate(GraveDefacer.pluginConfig.getString("gui.main.own-list-button-desc")))
                        .addEnchant(Enchantment.CHANNELING, 1)
                        .material(Material.PAPER)
                        .interactive(interactResult -> {
                            Player player = interactResult.getClicker();

                            if(!player.hasPermission("gravedefacer.playerlist")) {
                                sendConfigMessage(player, GraveDefacer.pluginConfig.getString("lang.no-permission"));
                                return;
                            }

                            Inventory graveListInventory = GraveListGUI.getInventoryOf(player, false, 0);

                            if(graveListInventory == null) {
                                sendConfigMessage(player, GraveDefacer.pluginConfig.getString("lang.list-gui-empty-message"));
                                return;
                            }

                            interactResult.getClicker().openInventory(graveListInventory);
                        })
                        .build()
                )
                .filled(new ItemBuilder().name(" ").material(Material.GRAY_STAINED_GLASS_PANE).makeStatic().build())
                .build();
    }
}
