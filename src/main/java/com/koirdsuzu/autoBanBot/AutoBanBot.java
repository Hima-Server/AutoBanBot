package com.koirdsuzu.autoBanBot;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AutoBanBot extends JavaPlugin implements Listener {

    private final Map<UUID, Integer> chatCounts = new HashMap<>();
    private final Map<UUID, Long> lastChatTime = new HashMap<>();
    private File banListFile;
    private YamlConfiguration banListConfig;

    public AutoBanBot() {
        // 初期化時にconfigを読み込む
        reloadConfig();
    }

    // 除外プレイヤーのリストを保存する
    private List<String> excludedPlayers;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Bukkit.getPluginManager().registerEvents(this, this);
        setupBanListFile();
        // 除外プレイヤーリストの読み込み
        excludedPlayers = getConfig().getStringList("excluded-players");
    }

    private boolean isPlayerExcluded(Player player) {
        // プレイヤー名が除外リストに含まれているかチェック
        return excludedPlayers.contains(player.getName());
    }

    private void setupBanListFile() {
        banListFile = new File(getDataFolder(), "banlist.yml");
        if (!banListFile.exists()) {
            try {
                banListFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        banListConfig = YamlConfiguration.loadConfiguration(banListFile);
    }

    private void saveBanInfo(Player player, String reason) {
        String playerName = player.getName();
        UUID playerUUID = player.getUniqueId();
        String playerIP = player.getAddress() != null ? player.getAddress().getAddress().getHostAddress() : "Unknown";
        String banDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        String path = "banned-players." + playerUUID;
        banListConfig.set(path + ".MCID", playerName);
        banListConfig.set(path + ".UUID", playerUUID.toString());
        banListConfig.set(path + ".Date", banDate);
        banListConfig.set(path + ".IP", playerIP);
        banListConfig.set(path + ".Reason", reason);

        try {
            banListConfig.save(banListFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void banPlayer(Player player, String reason) {
        player.kickPlayer(reason);
        Bukkit.getBanList(BanList.Type.NAME).addBan(player.getName(), reason, null, "Server");
        saveBanInfo(player, reason);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        FileConfiguration config = getConfig();
        Player player = event.getPlayer();

        // 除外プレイヤーでない場合にのみBAN処理を行う
        if (!isPlayerExcluded(player) && config.getBoolean("ban-on.item-drop", false)) {
            String reason = config.getString("reason.item-drop", "You have been banned for dropping items!");
            banPlayer(player, reason);
        }
    }

    @EventHandler
    public void onEnterNether(PlayerPortalEvent event) {
        FileConfiguration config = getConfig();
        Player player = event.getPlayer();

        // 除外プレイヤーでない場合にのみBAN処理を行う
        if (!isPlayerExcluded(player)
                && config.getBoolean("ban-on.nether-entry", false)
                && event.getTo() != null
                && event.getTo().getWorld().getEnvironment() == World.Environment.NETHER) {

            String reason = config.getString("reason.nether-entry", "You have been banned for entering the Nether!");
            banPlayer(player, reason);
            event.setCancelled(true); // ネザーに入るのを防止
        }
    }

    @EventHandler
    public void onEnterEnd(PlayerPortalEvent event) {
        FileConfiguration config = getConfig();
        Player player = event.getPlayer();

        // 除外プレイヤーでない場合にのみBAN処理を行う
        if (!isPlayerExcluded(player)
                && config.getBoolean("ban-on.end-entry", false)
                && event.getTo() != null
                && event.getTo().getWorld().getEnvironment() == World.Environment.THE_END) {

            String reason = config.getString("reason.end-entry", "You have been banned for entering the End!");
            banPlayer(player, reason);
            event.setCancelled(true); // エンドに入るのを防止
        }
    }

    @EventHandler
    public void onTNTPlace(BlockPlaceEvent event) {
        FileConfiguration config = getConfig();
        Player player = event.getPlayer();

        // 除外プレイヤーでない場合にのみBAN処理を行う
        if (!isPlayerExcluded(player) && config.getBoolean("ban-on.tnt-place", false) && event.getBlock().getType() == Material.TNT) {
            String reason = config.getString("reason.tnt-place", "You have been banned for placing TNT!");
            banPlayer(player, reason);
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        FileConfiguration config = getConfig();
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        long now = System.currentTimeMillis();

        // チャットカウントを更新
        chatCounts.put(playerId, chatCounts.getOrDefault(playerId, 0) + 1);

        // 5秒以上経過している場合、チャットカウントをリセット
        if (now - lastChatTime.getOrDefault(playerId, 0L) > 5000) {
            chatCounts.put(playerId, 1);
        }

        lastChatTime.put(playerId, now);

        // チャットがスパム判定基準を超えた場合
        if (chatCounts.get(playerId) > 5 && !isPlayerExcluded(player)) {
            String reason = config.getString("reason.spam-chat", "You have been banned for spamming chat!");

            // 非同期イベント内で同期的にプレイヤーをBANする
            new BukkitRunnable() {
                @Override
                public void run() {
                    banPlayer(player, reason);
                }
            }.runTask(this); // runTaskで同期処理として実行

            chatCounts.put(playerId, 0); // BAN後にチャットカウントをリセット
        }
    }
}