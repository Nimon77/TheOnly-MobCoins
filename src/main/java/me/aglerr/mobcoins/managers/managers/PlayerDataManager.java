package me.aglerr.mobcoins.managers.managers;

import me.aglerr.mobcoins.MobCoins;
import me.aglerr.mobcoins.PlayerData;
import me.aglerr.mobcoins.configs.ConfigValue;
import me.aglerr.mobcoins.database.SQLDatabase;
import me.aglerr.mobcoins.managers.Manager;
import me.aglerr.mobcoins.utils.Common;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class PlayerDataManager implements Manager {

    private final Map<String, PlayerData> playerDataMap = new HashMap<>();
    private final Map<String, PlayerData> valueModify = new HashMap<>();

    private final MobCoins plugin;

    public PlayerDataManager(MobCoins plugin) {
        this.plugin = plugin;
    }

    @Nullable
    public PlayerData getPlayerData(Player player) {
        return this.playerDataMap.get(player.getUniqueId().toString());
    }

    public void forceLoadPlayerData(Player player) {
        SQLDatabase database = plugin.getDatabase();
        String uuid = player.getUniqueId().toString();

        String command = "SELECT * FROM `" + database.getTable() + "` " +
                         "WHERE `uuid`=?";

        Common.debug(true,
                "Loading " + player.getName() + " data"
        );

        try (Connection connection = database.getConnection()) {
            try (PreparedStatement statement = connection.prepareStatement(command)) {
                statement.setString(1, uuid);
                try (ResultSet resultSet = statement.executeQuery()) {

                    PlayerData playerData;
                    if (resultSet.next()) {
                        double coins = Double.parseDouble(resultSet.getString("coins"));
                        playerData = new PlayerData(uuid, coins);
                    } else {
                        playerData = new PlayerData(uuid, ConfigValue.STARTING_BALANCE);
                    }

                    this.playerDataMap.put(uuid, playerData);
                    this.valueModify.put(uuid, playerData.clone());

                    Common.debug(true,
                            "Successfully loaded " + player.getName() + " data. (Coins: " + playerData.getCoins() + ")"
                    );
                }
            }
        } catch (SQLException e) {
            Common.error(true, "Error while loading " + player.getName() + " data!");
            e.printStackTrace();
        }


    }

    public void forceSavePlayerData(Player player) {
        String uuid = player.getUniqueId().toString();
        PlayerData playerData = this.playerDataMap.get(uuid);
        PlayerData valueModify = this.valueModify.get(uuid);

        Common.debug(true, "Saving " + player.getName() + " data");

        if (playerData == null) {
            Common.debug(true, "Returned because Player Data is null");
            return;
        }

        if (valueModify == null) {
            Common.debug(true, "Returned because Value Modify is null");
            return;
        }

        double coins1 = playerData.getCoins();
        double coins2 = valueModify.getCoins();

        if(coins1 != coins2){
            Common.debug(true, "Saving data to the database because value is not the same");
            Common.runTaskAsynchronously(() -> playerData.save(plugin.getDatabase()));
        }

        this.playerDataMap.remove(uuid);
        this.valueModify.remove(uuid);

        Common.debug(true, "Saving tasks is completed");

    }

    @Override
    public void load() {

    }

    @Override
    public void save() {
        SQLDatabase database = plugin.getDatabase();
        Common.debug(true, "Trying to save all players data");
        try(Connection connection = database.getConnection()){
            for(String uuid : this.playerDataMap.keySet()){
                PlayerData playerData = this.playerDataMap.get(uuid);
                PlayerData valueModify = this.valueModify.get(uuid);

                if(playerData.getCoins() == valueModify.getCoins()){
                    Common.debug(true,
                            "Not saving " + uuid + "data (Reason: coins amount the same)"
                    );
                    continue;
                }

                Common.debug(true, "Trying to save " + uuid + " data");

                String getRowCommand = "SELECT * FROM {table} WHERE uuid=?"
                        .replace("{table}", database.getTable());

                try(PreparedStatement statement = connection.prepareStatement(getRowCommand)){
                    statement.setString(1, playerData.getUUID());
                    try(ResultSet resultSet = statement.executeQuery()){
                        if(resultSet.next()){

                            String updateCommand = "UPDATE {table} SET coins=? WHERE uuid=?"
                                    .replace("{table}", database.getTable());

                            try(PreparedStatement updateStatement = connection.prepareStatement(updateCommand)){
                                updateStatement.setString(1, String.valueOf(playerData.getCoins()));
                                updateStatement.setString(2, playerData.getUUID());
                                updateStatement.executeUpdate();
                                Common.debug(true, "Updating " + playerData.getUUID() + " data (coins: " + playerData.getCoins() + ")");
                            }

                        } else {

                            String insertCommand = "INSERT INTO {table} (uuid, coins) VALUES (?,?);"
                                    .replace("{table}", database.getTable());

                            try(PreparedStatement insertStatement = connection.prepareStatement(insertCommand)){
                                insertStatement.setString(1, playerData.getUUID());
                                insertStatement.setString(2, String.valueOf(playerData.getCoins()));
                                insertStatement.execute();
                                Common.debug(true, "Inserting " + playerData.getUUID() + " data (coins: " + playerData.getCoins() + ")");
                            }

                        }
                    }
                }
            }
        } catch (SQLException e) {
            Common.error(true, "Error saving all players data!");
            e.printStackTrace();
            return;
        }
        Common.debug(true, "All players data are successfully saved");
    }

}