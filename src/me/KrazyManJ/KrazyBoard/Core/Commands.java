package me.KrazyManJ.KrazyBoard.Core;

import me.KrazyManJ.KrazyBoard.Main;
import me.KrazyManJ.KrazyBoard.Utils.Format;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class Commands implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] strings) {
        switch (command.getName()){
            case "krazyboard" -> {
                Main.getInstance().reloadConfig();
                Main.getManager().reloadBoardContent();
                if (sender instanceof Player player) player.sendMessage("Plugin was successfully reloaded!");
            }
            case "tabulka" -> {
                if (sender instanceof Player player) {
                    Main.getManager().changeBoardVisionState(player);
                    if (Main.getManager().hasEnabledBoard(player)) player.sendMessage(
                            Format.colorize(Main.getInstance().getConfig().getString("language.tabulka toggle on")));
                    else player.sendMessage(Format.colorize(Main.getInstance().getConfig().getString("language.tabulka toggle off")));
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return new ArrayList<>();
    }
}
