package it.ziopagnotta.gravedefacer.gui;

import com.google.common.collect.Lists;
import eu.macsworks.premium.macslibs.utils.ColorTranslator;
import eu.macsworks.premium.macslibs.utils.InventoryBuilder;
import eu.macsworks.premium.macslibs.utils.ItemBuilder;
import it.ziopagnotta.gravedefacer.objects.Grave;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static it.ziopagnotta.gravedefacer.GraveDefacer.graveFactory;
import static it.ziopagnotta.gravedefacer.GraveDefacer.pluginConfig;
import static it.ziopagnotta.gravedefacer.config.Utils.*;

public class GraveListGUI {
    public static @Nullable Inventory getInventoryOf(@Nullable Player target, boolean admin, int page) {
        List<List<Grave>> partition = Lists.partition(graveFactory.getGravesCache(), 45);

        if(partition.isEmpty()) {
            return null;
        }

        String title = admin
                ? ColorTranslator.translate(pluginConfig.getString("gui.list.gui-title"))
                : ColorTranslator.translate(pluginConfig.getString("gui.player.gui-title"));

        if(target != null) {
            title = title.replace("%player%", target.getName());
            partition = Lists.partition(graveFactory.getGravesByOwner(target.getName()), 45);
        }

        InventoryBuilder builder = new InventoryBuilder()
                .named(title.replace("%page%", String.valueOf(page)))
                .slots(54)

                /*       Close Button       */
                .addItem(45, new ItemBuilder()
                        .name(ColorTranslator.translate(pluginConfig.getString("gui.list.close-gui-button")))
                        .material(Material.BARRIER)
                        .addEnchant(Enchantment.CHANNELING, 1)
                        .interactive(interactResult -> switchInv(interactResult.getClicker(), MainGUI.getInventory()))
                        .build());

        /*       Grave list       */
        int slot = 0;
        for(Grave grave : partition.get(page)) {
            ItemBuilder item = new ItemBuilder()
                    .skullOf(grave.getOwner())
                    .name(ColorTranslator.translate(pluginConfig.getString("gui.list.grave-button-title").replace("%player%", grave.getOwner())))
                    .lore(ColorTranslator.translate(pluginConfig.getString(admin ? "gui.list.grave-button-desc" : "gui.list.grave-button-desc-noperm").replace("%date%", formatInstant(grave.getInstant()))));

            if(admin) {
                item.interactive(interactResult -> {
                    Player player = interactResult.getClicker();
                    switchInv(player, EditorGUI.getInventory(grave, player));
                });
            } else {
                item.makeStatic();
            }

            builder.addItem(slot++, item.build());
        }

        if(admin) {
            /*       Search GUI       */
            builder.addItem(53, new ItemBuilder()
                    .name(ColorTranslator.translate(pluginConfig.getString("gui.list.open-search-gui-button-title")))
                    .lore(ColorTranslator.translate(pluginConfig.getString("gui.list.open-search-gui-button-desc")))
                    .material(Material.BOOK)
                    .addEnchant(Enchantment.CHANNELING, 1)
                    .interactive(interactResult -> {
                        Player player = interactResult.getClicker();
                        player.closeInventory();
                        SearchGUI.openInventory(player);
                    })
                    .build());

            /*       Remove All       */
            builder.addItem(49, new ItemBuilder()
                    .name(ColorTranslator.translate(pluginConfig.getString("gui.list.remove-all-button-title")))
                    .lore(ColorTranslator.translate(pluginConfig.getString("gui.list.remove-all-button-desc")))
                    .material(Material.FIRE_CHARGE)
                    .addEnchant(Enchantment.CHANNELING, 1)
                    .interactive(interactResult -> {
                        Player player = interactResult.getClicker();
                        player.closeInventory();
                        graveFactory.clear();
                        sendConfigMessage(player, pluginConfig.getString("lang.remove-all-message"));
                    })
                    .build());
        }

        /*       previous page button       */
        int prev = page-1;
        if(prev >= 0) {
            builder.addItem(48, new ItemBuilder()
                    .name(ColorTranslator.translate(pluginConfig.getString("gui.list.prev-page-title").replace("%page%", String.valueOf(prev))))
                    .lore(ColorTranslator.translate(pluginConfig.getString("gui.list.prev-page-desc")))
                    .material(Material.ARROW)
                    .addEnchant(Enchantment.CHANNELING, 1)
                    .interactive(interactResult -> switchInv(interactResult.getClicker(), getInventoryOf(target, admin, prev)))
                    .build());
        }

        /*       next page button       */
        int next = page+1;
        if(next < partition.size()) {
            builder.addItem(50, new ItemBuilder()
                    .name(ColorTranslator.translate(pluginConfig.getString("gui.list.next-page-title").replace("%page%", String.valueOf(next))))
                    .lore(ColorTranslator.translate(pluginConfig.getString("gui.list.next-page-desc")))
                    .material(Material.ARROW)
                    .addEnchant(Enchantment.CHANNELING, 1)
                    .interactive(interactResult -> switchInv(interactResult.getClicker(), getInventoryOf(target, admin, next)))
                    .build());
        }

        builder.filled(new ItemBuilder().name(" ").material(Material.GRAY_STAINED_GLASS_PANE).makeStatic().build());

        return builder.build();
    }
}
