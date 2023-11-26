package it.ziopagnotta.gravedefacer.objects;

import it.ziopagnotta.gravedefacer.GraveDefacer;
import it.ziopagnotta.gravedefacer.config.Utils;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.TextDisplay;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

public class Grave {
    private final String owner;
    private final Location origin;
    private ArmorStand model;
    private final List<ItemStack> contents;
    private TextDisplay display;
    private final boolean dropItems;
    private final Instant instant;

    private Grave(Location origin, String owner, boolean dropItems, List<ItemStack> contents) {
        this.origin = origin;
        this.owner = owner;
        this.dropItems = dropItems;
        this.contents = contents;
        instant = Instant.now();
    }

    public String getOwner() {
        return owner;
    }

    public boolean isDropItems() {
        return dropItems;
    }

    public Location getOrigin() {
        return origin.clone();
    }

    public ArmorStand getModel() {
        return model;
    }

    public List<ItemStack> getContents() {
        return Collections.unmodifiableList(contents);
    }

    public Instant getInstant() {
        return instant;
    }

    public void spawn() {
        if(model != null) {
            return;
        }

        Bukkit.getScheduler().runTaskLater(JavaPlugin.getProvidingPlugin(GraveDefacer.class), () ->
            model = Items.getArmorStand(getOrigin(), true),
                1L
        );

        Component text = Utils.fromLegacy(GraveDefacer.pluginConfig.getString("lang.display-title"));

        if(GraveDefacer.pluginConfig.getBoolean("settings.allow-display-sub-title")) {
            text = text.append(Component.newline())
                    .append(Utils.fromLegacy(GraveDefacer.pluginConfig.getString("lang.display-sub-title")))
                    .replaceText(builder -> builder.match("%player%").replacement(owner));
        }

        display = Items.getDisplay(getOrigin(), text);
    }

    public void despawn() {
        if(model == null || display == null) {
            return;
        }

        model.remove();
        display.remove();
    }

    public static class Builder {
        private String owner;
        private boolean dropItems;
        private Location origin;
        private List<ItemStack> contents;

        public Builder owner(String owner) {
            this.owner = owner;
            return this;
        }

        public Builder origin(Location origin) {
            this.origin = origin;
            return this;
        }

        public Builder dropItems(boolean dropItems) {
            this.dropItems = dropItems;
            return this;
        }

        public Builder contents(List<ItemStack> contents) {
            this.contents = contents;
            return this;
        }


        public Grave build() {
            if(owner == null || origin == null || contents == null)
                return null;

            return new Grave(origin, owner, dropItems, contents);
        }
    }
}
