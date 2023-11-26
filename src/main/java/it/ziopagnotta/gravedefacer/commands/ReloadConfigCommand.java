package it.ziopagnotta.gravedefacer.commands;

import eu.macsworks.premium.macslibs.objects.MacsCommand;
import eu.macsworks.premium.macslibs.objects.MacsSubcommand;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

public class ReloadConfigCommand extends MacsSubcommand {
    public ReloadConfigCommand(String id) {
        super(id);
    }

    @Override
    public void setRootCommand(MacsCommand rootCommand) {
        super.setRootCommand(rootCommand);
    }

    @Override
    public void setUsage(String usage) {
        super.setUsage(usage);
    }

    @Override
    public void setCommandInfo(BiConsumer<Player, String[]> commandInfo) {
        super.setCommandInfo(commandInfo);
    }

    @Override
    public void setRequiredArgs(String requiredArgs) {
        super.setRequiredArgs(requiredArgs);
    }

    @Override
    public void setRequiredPerm(String requiredPerm) {
        super.setRequiredPerm(requiredPerm);
    }
}
