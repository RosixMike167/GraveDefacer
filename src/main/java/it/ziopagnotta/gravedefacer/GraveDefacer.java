package it.ziopagnotta.gravedefacer;

import it.ziopagnotta.gravedefacer.commands.GraveCommand;
import it.ziopagnotta.gravedefacer.commands.ReloadConfigCommand;
import it.ziopagnotta.gravedefacer.config.PluginConfig;
import it.ziopagnotta.gravedefacer.config.Utils;
import it.ziopagnotta.gravedefacer.gui.MainGUI;
import it.ziopagnotta.gravedefacer.listeners.DeathEvent;
import it.ziopagnotta.gravedefacer.listeners.InteractEvent;
import it.ziopagnotta.gravedefacer.objects.Grave;
import it.ziopagnotta.gravedefacer.objects.GraveFactory;
import it.ziopagnotta.gravedefacer.worker.ExpireWorker;
import org.bukkit.plugin.java.JavaPlugin;

public class GraveDefacer extends JavaPlugin {
    public static GraveFactory graveFactory;
    public static PluginConfig pluginConfig;
    public static GraveCommand mainCommand;
    private static ExpireWorker expireWorker;

    @Override
    public void onEnable() {
        graveFactory = new GraveFactory();
        pluginConfig = new PluginConfig(this, "config");
        mainCommand = new GraveCommand("grave");

        getServer().getPluginManager().registerEvents(new DeathEvent(), this);
        getServer().getPluginManager().registerEvents(new InteractEvent(), this);

        getCommand("grave").setExecutor(mainCommand);
        defineMainCommand();

        Utils.loadBlockedWorlds();
        checkWorker();
    }

    @Override
    public void onDisable() {
        graveFactory.clear();
        expireWorker.cancel();

        graveFactory = null;
        expireWorker = null;
    }

    //TODO: finish commands
    private void defineMainCommand() {
        mainCommand.setDefaultBehavior((player, strings) -> player.openInventory(MainGUI.getInventory()));
        mainCommand.addSubCommand(new ReloadConfigCommand("reload"));
    }

    public void checkWorker() {
        if(expireWorker != null)
            return;

        expireWorker = new ExpireWorker(this);
        expireWorker.start();
    }
}
