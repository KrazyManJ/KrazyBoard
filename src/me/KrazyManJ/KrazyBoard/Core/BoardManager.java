package me.KrazyManJ.KrazyBoard.Core;

import me.KrazyManJ.KrazyBoard.Main;
import me.KrazyManJ.KrazyBoard.Utils.Format;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BoardManager implements Listener {
    private final File file = new File(Main.getInstance().getDataFolder().getAbsolutePath(), "disabled.yml");
    private final FileConfiguration data;

    private final HashMap<UUID, Integer> tasks = new HashMap<>();
    private final List<UUID> disabledPlayers = new ArrayList<>();

    public boolean hasEnabledBoard(Player p){
        return !disabledPlayers.contains(p.getUniqueId());
    }
    public void changeBoardVisionState(Player p){
        if (hasEnabledBoard(p)){
            stopBoard(p);
            disabledPlayers.add(p.getUniqueId());
        }
        else{
            startBoard(p);
            disabledPlayers.remove(p.getUniqueId());
        }
    }


    private int refresh = Main.getInstance().getConfig().getInt("board refresh rate") > 0
            ? Main.getInstance().getConfig().getInt("board refresh rate")
            : 20;
    private List<String> lines = Main.getInstance().getConfig().getStringList("board lines");
    private String title = Main.getInstance().getConfig().getString("board title");

    public void reloadBoardContent(){
        refresh = Main.getInstance().getConfig().getInt("board refresh rate") > 0
                ? Main.getInstance().getConfig().getInt("board refresh rate")
                : 20;
        lines = Main.getInstance().getConfig().getStringList("board lines");
        title = Main.getInstance().getConfig().getString("board title");
    }
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public BoardManager() {
        if (!file.exists()) try { file.createNewFile(); } catch (IOException e) { e.printStackTrace(); }
        data = YamlConfiguration.loadConfiguration(file);
        for (String uuid : data.getStringList("disabled players")) disabledPlayers.add(UUID.fromString(uuid));
        Bukkit.getPluginManager().registerEvents(this,Main.getInstance());
        if (!Bukkit.getOnlinePlayers().isEmpty()) for (Player p : Bukkit.getOnlinePlayers()) if (hasEnabledBoard(p)) this.startBoard(p);
    }

    public void startBoard(Player p){
        createBoard(p);
        BukkitRunnable task = new BukkitRunnable() {
            @Override
            public void run() {
                updateBoard(p.getScoreboard(),p.getScoreboard().getObjective("KrazyBoard"),p);
            }
        };
        task.runTaskTimer(Main.getInstance(),20,refresh);
        tasks.put(p.getUniqueId(),task.getTaskId());
    }
    public void stopBoard(Player p){
        if (tasks.containsKey(p.getUniqueId())) Bukkit.getScheduler().cancelTask(tasks.get(p.getUniqueId()));
        p.setScoreboard(Objects.requireNonNull(Bukkit.getScoreboardManager()).getNewScoreboard());
    }
    @SuppressWarnings("ConstantConditions")
    private void createBoard(Player p){
        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = board.registerNewObjective("KrazyBoard", "dummy", Format.colorize(title));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        updateBoard(board,objective,p);
        p.setScoreboard(board);
    }

    @SuppressWarnings("ConstantConditions")
    private void updateBoard(Scoreboard board, Objective obj, Player p){
        int count = lines.size()-1;
        for (String line : lines){
            Team team = board.getTeam("KrazyBoard_"+count) != null ? board.getTeam("KrazyBoard_"+count) : board.registerNewTeam("KrazyBoard_"+count);
            team.setSuffix(Format.colorize(Main.isPAPI() ? PlaceholderAPI.setPlaceholders(p, line) : line));
            team.addEntry(Format.colorize("&r".repeat(count)));
            obj.getScore(Format.colorize("&r".repeat(count))).setScore(count);
            count--;
        }
    }
    public void saveData(){
        List<String> uuidString = new ArrayList<>();
        for (UUID ids : disabledPlayers) uuidString.add(ids.toString());
        data.set("disabled players",uuidString);
        try { data.save(file); } catch (IOException e) { e.printStackTrace(); }
    }

    @EventHandler
    @SuppressWarnings("unused")
    private void on(PlayerJoinEvent event) {
        if (hasEnabledBoard(event.getPlayer())) Main.getManager().startBoard(event.getPlayer());
    }

    @EventHandler
    @SuppressWarnings("unused")
    private void on(PlayerQuitEvent event) {
        if (hasEnabledBoard(event.getPlayer())) Main.getManager().stopBoard(event.getPlayer());
    }
}
