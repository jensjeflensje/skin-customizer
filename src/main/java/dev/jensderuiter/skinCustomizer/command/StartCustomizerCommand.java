package dev.jensderuiter.skinCustomizer.command;

import dev.jensderuiter.skinCustomizer.customizer.SkinCustomizer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StartCustomizerCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        if (!sender.hasPermission("customizer.start")) return true;
        Player player = (Player) sender;

        SkinCustomizer customizer = new SkinCustomizer(player.getLocation());
        customizer.summon();

        return true;
    }
}
