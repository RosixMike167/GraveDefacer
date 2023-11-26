package it.ziopagnotta.gravedefacer.objects;

import eu.macsworks.premium.macslibs.utils.ColorTranslator;
import eu.macsworks.premium.macslibs.utils.InventoryBuilder;
import eu.macsworks.premium.macslibs.utils.ItemBuilder;
import it.ziopagnotta.gravedefacer.GraveDefacer;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Display;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.EulerAngle;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class Items {
    private static final JavaPlugin plugin = JavaPlugin.getProvidingPlugin(GraveDefacer.class);

    @NotNull
    public static ItemStack getModel() {
        ItemStack model = new ItemStack(Material.STICK);
        ItemMeta itemMeta = model.getItemMeta();

        itemMeta.setCustomModelData(666);

        model.setItemMeta(itemMeta);

        return model;
    }

    @NotNull
    public static ArmorStand getArmorStand(@NotNull Location origin, boolean equip) {
        ArmorStand model = (ArmorStand) origin.getWorld().spawnEntity(origin.subtract(0, 1.38, 0), EntityType.ARMOR_STAND);

        model.setArms(false);
        model.setBasePlate(false);
        model.setCanMove(false);
        model.setCanTick(false);
        model.setDisabledSlots(EquipmentSlot.values());
        model.setHeadPose(EulerAngle.ZERO);
        model.setVisible(false);
        model.setInvulnerable(true);
        model.setMetadata("gravedefacer", new FixedMetadataValue(plugin, "gravedefacer"));

        if(equip)
            model.setItem(EquipmentSlot.HEAD, Items.getModel());

        return model;
    }

    @NotNull
    public static TextDisplay getDisplay(@NotNull Location origin, @NotNull Component text) {
        TextDisplay display = origin.getWorld().spawn(origin.add(0, 1.1, 0), TextDisplay.class);

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            display.setAlignment(TextDisplay.TextAlignment.CENTER);
            display.setBillboard(Display.Billboard.CENTER);
            display.setVisibleByDefault(true);
            display.setViewRange(0.5f);
            display.text(text);

            Transformation transformation = display.getTransformation();
            transformation.getScale().set(1D);
            display.setTransformation(transformation);
        }, 3L);

        return display;
    }

    @NotNull
    public static Inventory getInventory(@NotNull Grave grave) {
        HashMap<Integer, ItemStack> contents = new HashMap<>();
        for(int i = 0; i < grave.getContents().size(); i++) {
            ItemStack itemStack = grave.getContents().get(i);

            if(itemStack == null || itemStack.isEmpty() || itemStack.getType().isAir()) continue;

            contents.put(i, grave.getContents().get(i));
        }

        return InventoryBuilder.builder()
                .named(ColorTranslator.translate(GraveDefacer.pluginConfig.getString("lang.inventory-title")).replace("%player%", grave.getOwner()))
                .slots(54)
                .filled(new ItemBuilder().item(new ItemStack(Material.GRAY_STAINED_GLASS_PANE)).name(" ").makeStatic().build())
                .setItems(contents)
                .build();
    }
}
