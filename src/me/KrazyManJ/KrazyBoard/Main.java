package me.KrazyManJ.KrazyBoard;

import me.KrazyManJ.KrazyBoard.Core.BoardManager;
import me.KrazyManJ.KrazyBoard.Core.Commands;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("unused")
public class Main extends JavaPlugin {
    private static Main instance;
    public static boolean isPAPI(){ return Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null; }

    private static BoardManager manager;
    public static BoardManager getManager() { return manager; }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        manager = new BoardManager();
        Commands commands = new Commands();
        this.getCommand("krazyboard").setExecutor(commands);
        this.getCommand("krazyboard").setTabCompleter(commands);
        this.getCommand("tabulka").setExecutor(commands);
        this.getCommand("tabulka").setTabCompleter(commands);
    }

    @Override
    public void onDisable() {
        manager.saveData();
        for (Player p : Bukkit.getOnlinePlayers()) manager.stopBoard(p);
    }

    public static Main getInstance() {
        return instance;
    }
}
