package it.ziopagnotta.gravedefacer.config;

import it.ziopagnotta.gravedefacer.GraveDefacer;
import it.ziopagnotta.gravedefacer.gui.MainGUI;
import it.ziopagnotta.gravedefacer.objects.Grave;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Utils {
    private static final List<String> blockedWorlds = new ArrayList<>();
    private static final LegacyComponentSerializer LEGACY_SERIALIZER = LegacyComponentSerializer.legacyAmpersand();

    @NotNull
    public static Component fromLegacy(@NotNull String legacyText) {
        return LEGACY_SERIALIZER.deserialize(legacyText);
    }

    @NotNull
    public static String formatInstant(@NotNull Instant instant) {
        int hh = instant.atZone(ZoneId.systemDefault()).getHour();
        int mm = instant.atZone(ZoneId.systemDefault()).getMinute();
        String date = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).format(LocalDate.ofInstant(instant, ZoneId.systemDefault()));

        return date + " at " + formatTime(hh) + ":" + formatTime(mm);
    }

    @Contract(pure = true)
    @NotNull
    private static String formatTime(int time) {
        return time < 10 ? "0" + time : String.valueOf(time);
    }

    @NotNull
    public static String getRemainingTime(@NotNull Grave grave) {
        int settingTime = GraveDefacer.pluginConfig.getInt("settings.graves-expire-time-seconds");
        Instant start = Instant.now();
        Instant end = grave.getInstant().plusSeconds(settingTime);

        long diff = Duration.between(start, end).toSeconds();

        return String.valueOf(diff);
    }

    public static void switchInv(@NotNull Player player, Inventory nextInv) {
        player.closeInventory();
        if(nextInv == null)
            nextInv = MainGUI.getInventory();
        player.openInventory(nextInv);
    }

    @NotNull
    public static Logger getLogger() {
        return JavaPlugin.getProvidingPlugin(GraveDefacer.class).getLogger();
    }

    public static void reloadConfig() {
        GraveDefacer.pluginConfig.reload();
        blockedWorlds.clear();
        loadBlockedWorlds();
        JavaPlugin.getPlugin(GraveDefacer.class).checkWorker();
    }

    public static void sendConfigMessage(@NotNull Player player, @NotNull String path) {
        player.sendMessage(fromLegacy(GraveDefacer.pluginConfig.getString("lang.prefix"))
                .append(fromLegacy(path)).replaceText(builder -> builder.match("\n").replacement(Component.newline())));
    }

    public static void loadBlockedWorlds() {
        blockedWorlds.addAll(GraveDefacer.pluginConfig.getStringList("settings.disabled-worlds"));
    }

    public static boolean isBlockedWorld(String worldName) {
        return blockedWorlds.contains(worldName);
    }

    @Nullable
    public static Location getGroundLocation(@NotNull Location location, Location saved) {
        if(location.getY() < -64 || location.getY() > 320)
            return null;

        Block block = location.getBlock();

        if(!block.isSolid() || block.isEmpty()) {
            return getGroundLocation(block.getRelative(BlockFace.DOWN).getLocation(), saved);
        }

        return new Location(saved.getWorld(), saved.getX(), block.getY()+1, saved.getZ(), saved.getYaw(), 0);
    }

    public static double round(double number) {
        return Math.floor(number * 100) / 100;
    }

    public static boolean canPlayerHaveGrave(String player) {
        if(!GraveDefacer.pluginConfig.getBoolean("settings.enable-player-graves-limit"))
            return true;

        return GraveDefacer.graveFactory.getNumberGravesByOwner(player) < GraveDefacer.pluginConfig.getInt("settings.player-graves-limit");
    }

    public static boolean cannotExpire() {
        return GraveDefacer.pluginConfig.getInt("settings.graves-expire-time-seconds") == -1;
    }

    public static boolean hasExpired(@NotNull Grave grave) {
        return Math.abs(Duration.between(Instant.now(), grave.getInstant()).toSeconds()) >= GraveDefacer.pluginConfig.getInt("settings.graves-expire-time-seconds");
    }
}
