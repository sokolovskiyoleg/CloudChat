package me.cloudm1nd3.cloudchat.storage;

import me.cloudm1nd3.cloudchat.CloudChat;
import me.cloudm1nd3.cloudchat.managers.ChatPlayerManager;
import me.cloudm1nd3.cloudchat.objects.ChatPlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import java.util.stream.Stream;

public class PlayerData {
    private static final CloudChat plugin = CloudChat.getInstance();
    private static final String PLAYER_DATA_DIRECTORY_PATH = plugin.getDataFolder().getAbsolutePath() + "/PlayerData";


    public static void loadPlayerData(){
        File playerDataDirectory = new File(PLAYER_DATA_DIRECTORY_PATH);

        if (!playerDataDirectory.exists() && !playerDataDirectory.mkdirs()) {
            plugin.getLogger().severe("Cant create player-data directory");
            return;
        }

        Path directory = playerDataDirectory.toPath();
        try (Stream<Path> stream = Files.walk(directory)) {
            stream.filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".yml"))
                    .forEach(path -> {
                        ChatPlayer chatPlayer = readPlayerDataFromFile(path.toFile());

                        if (chatPlayer != null) {
                            ChatPlayerManager.getInstance().addPlayerToCache(chatPlayer);
                        }
                    });

        } catch (IOException e) {
            plugin.getLogger().severe("Error loading playerData.");
            e.printStackTrace();
        }
    }

    public static ChatPlayer loadPlayerData(Player player){
        Path path = Path.of(PLAYER_DATA_DIRECTORY_PATH, player.getUniqueId() + ".yml");
        File playerDataFile = path.toFile();

        if (!playerDataFile.exists()) {
            return null;
        }

        return readPlayerDataFromFile(playerDataFile);
    }

    public static ChatPlayer readPlayerDataFromFile(File file){
        try {
            FileConfiguration playerConfiguration = YamlConfiguration.loadConfiguration(file);
            String uuidString = file.getName().replace(".yml", "");
            UUID uuid = UUID.fromString(uuidString);
            String name = playerConfiguration.getString("name", "");
            boolean hiddenChat = playerConfiguration.getBoolean("hiddenChat", false);


            return new ChatPlayer(uuid, name, hiddenChat);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void savePlayerData(ChatPlayer chatPlayer){
        if(chatPlayer == null){
            return;
        }

        try{
            Path path = Path.of(PLAYER_DATA_DIRECTORY_PATH, chatPlayer.getUUID() + ".yml");
            File playerDataFile = path.toFile();

            FileConfiguration playerDataConfiguration = YamlConfiguration.loadConfiguration(playerDataFile);
            if(!playerDataFile.exists()){
                playerDataConfiguration.save(playerDataFile);
            }

            playerDataConfiguration.set("name", chatPlayer.getName());
            playerDataConfiguration.set("hiddenChat", chatPlayer.getHiddenChat());


            playerDataConfiguration.save(playerDataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
