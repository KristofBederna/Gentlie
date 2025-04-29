package Game.Misc;

import Game.Entities.ChestEntity;
import Game.Entities.PolarBearEntity;
import Game.Misc.Enums.Daytime;
import Game.Misc.Scenes.*;
import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.CentralMassComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Core.SystemHub;
import inf.elte.hu.gameengine_javafx.Entities.PlayerEntity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.SceneManagementSystem;
import javafx.scene.layout.BorderPane;

import java.io.*;

/**
 * Utility class responsible for saving and loading various game-related data,
 * including player and enemy stats, shop prices, dungeon entities, and time data.
 */
public class GameSaver {

    /**
     * Saves the current stats of the statistics to a file.
     * This includes health, damage, resistance values, player position and spawning rates.
     */
    public static void saveEntityStats() {
        File file = new File(PlayerStats.currentSave + "/statistics.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("PolarBears\n");
            writer.write(String.valueOf(EnemyStats.health));
            writer.newLine();
            writer.write(String.valueOf(EnemyStats.meleeDamage));
            writer.newLine();
            writer.write(String.valueOf(EnemyStats.rangedDamage));
            writer.newLine();
            writer.write(String.valueOf(EnemyStats.meleeResistance));
            writer.newLine();
            writer.write(String.valueOf(EnemyStats.rangedResistance));

            writer.newLine();
            writer.write("Player\n");
            writer.write(EntityHub.getInstance().getEntitiesWithType(PlayerEntity.class).getFirst().getComponent(PositionComponent.class).getGlobalX() + " " + EntityHub.getInstance().getEntitiesWithType(PlayerEntity.class).getFirst().getComponent(PositionComponent.class).getGlobalY());
            writer.newLine();
            writer.write(SystemHub.getInstance().getSystem(SceneManagementSystem.class).getCurrentScene().getClass().getSimpleName());
            writer.newLine();
            writer.write(String.valueOf(PlayerStats.health));
            writer.newLine();
            writer.write(String.valueOf(PlayerStats.meleeDamage));
            writer.newLine();
            writer.write(String.valueOf(PlayerStats.rangedDamage));
            writer.newLine();
            writer.write(String.valueOf(PlayerStats.meleeResistance));
            writer.newLine();
            writer.write(String.valueOf(PlayerStats.rangedResistance));
            writer.newLine();
            writer.write(String.valueOf(PlayerStats.gold));
            writer.newLine();
            writer.write(String.valueOf(PlayerStats.meleeKills));
            writer.newLine();
            writer.write(String.valueOf(PlayerStats.rangedKills));
            writer.newLine();
            writer.write(String.valueOf(PlayerStats.totalMeleeKills));
            writer.newLine();
            writer.write(String.valueOf(PlayerStats.totalRangedKills));
            writer.newLine();
            writer.write(String.valueOf(PlayerStats.meleeCooldown));
            writer.newLine();
            writer.write(String.valueOf(PlayerStats.rangedCooldown));

            writer.newLine();
            writer.write("GeneratorConfig\n");
            writer.write(String.valueOf(DungeonGenerationConfig.enemySpawnFactorReset));
            writer.newLine();
            writer.write(String.valueOf(DungeonGenerationConfig.chestSpawnFactorReset));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads player and enemy stats from a previously saved file and restores the game state.
     * Also triggers scene change based on saved player position and scene name.
     */
    public static void loadEntityStats() {
        try (BufferedReader reader = new BufferedReader(new FileReader(PlayerStats.currentSave + "/statistics.txt"))) {
            reader.readLine();
            EnemyStats.health = Double.parseDouble(reader.readLine());
            EnemyStats.meleeDamage = Double.parseDouble(reader.readLine());
            EnemyStats.rangedDamage = Double.parseDouble(reader.readLine());
            EnemyStats.meleeResistance = Double.parseDouble(reader.readLine());
            EnemyStats.rangedResistance = Double.parseDouble(reader.readLine());
            reader.readLine();
            String[] split = reader.readLine().split(" ");
            String lastScene = reader.readLine();
            switch (lastScene) {
                case "DungeonScene":
                    SystemHub.getInstance().getSystem(SceneManagementSystem.class).requestSceneChange(new DungeonScene(new BorderPane(), 1920, 1080, new Point(Double.parseDouble(split[0]), Double.parseDouble(split[1]))));
                    break;
                case "EnemyIslandScene":
                    SystemHub.getInstance().getSystem(SceneManagementSystem.class).requestSceneChange(new EnemyIslandScene(new BorderPane(), 1920, 1080, new Point(Double.parseDouble(split[0]), Double.parseDouble(split[1]))));
                    break;
                case "HomeIslandScene":
                    SystemHub.getInstance().getSystem(SceneManagementSystem.class).requestSceneChange(new HomeIslandScene(new BorderPane(), 1920, 1080, new Point(Double.parseDouble(split[0]), Double.parseDouble(split[1]))));
                    break;
                case "HomeScene":
                    SystemHub.getInstance().getSystem(SceneManagementSystem.class).requestSceneChange(new HomeScene(new BorderPane(), 1920, 1080, new Point(Double.parseDouble(split[0]), Double.parseDouble(split[1]))));
                    break;
                case "InnScene":
                    SystemHub.getInstance().getSystem(SceneManagementSystem.class).requestSceneChange(new InnScene(new BorderPane(), 1920, 1080, new Point(Double.parseDouble(split[0]), Double.parseDouble(split[1]))));
                    break;
            }
            PlayerStats.health = Double.parseDouble(reader.readLine());
            PlayerStats.meleeDamage = Double.parseDouble(reader.readLine());
            PlayerStats.rangedDamage = Double.parseDouble(reader.readLine());
            PlayerStats.meleeResistance = Double.parseDouble(reader.readLine());
            PlayerStats.rangedResistance = Double.parseDouble(reader.readLine());
            PlayerStats.gold = Integer.parseInt(reader.readLine());
            PlayerStats.meleeKills = Integer.parseInt(reader.readLine());
            PlayerStats.rangedKills = Integer.parseInt(reader.readLine());
            PlayerStats.totalMeleeKills = Integer.parseInt(reader.readLine());
            PlayerStats.totalRangedKills = Integer.parseInt(reader.readLine());
            PlayerStats.meleeCooldown = Long.parseLong(reader.readLine());
            PlayerStats.rangedCooldown = Long.parseLong(reader.readLine());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Saves the current prices of shop items to a file.
     */
    public static void saveShopPrices() {
        File file = new File(PlayerStats.currentSave + "/shopPrices.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(String.valueOf(ShopItemPrices.Health));
            writer.newLine();
            writer.write(String.valueOf(ShopItemPrices.MeleeDamage));
            writer.newLine();
            writer.write(String.valueOf(ShopItemPrices.RangedDamage));
            writer.newLine();
            writer.write(String.valueOf(ShopItemPrices.MeleeSpeed));
            writer.newLine();
            writer.write(String.valueOf(ShopItemPrices.RangedSpeed));
            writer.newLine();
            writer.write(String.valueOf(ShopItemPrices.MeleeResistance));
            writer.newLine();
            writer.write(String.valueOf(ShopItemPrices.RangedResistance));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads shop item prices from a saved file.
     */
    public static void loadShopPrices() {
        try (BufferedReader reader = new BufferedReader(new FileReader(PlayerStats.currentSave + "/shopPrices.txt"))) {
            ShopItemPrices.Health = Integer.parseInt(reader.readLine());
            ShopItemPrices.MeleeDamage = Integer.parseInt(reader.readLine());
            ShopItemPrices.RangedDamage = Integer.parseInt(reader.readLine());
            ShopItemPrices.MeleeSpeed = Integer.parseInt(reader.readLine());
            ShopItemPrices.RangedSpeed = Integer.parseInt(reader.readLine());
            ShopItemPrices.MeleeResistance = Integer.parseInt(reader.readLine());
            ShopItemPrices.RangedResistance = Integer.parseInt(reader.readLine());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Saves the current positions of polar bears and chests in the dungeon to a file.
     */
    public static void saveDungeonState() {
        var PolarBears = EntityHub.getInstance().getEntitiesWithType(PolarBearEntity.class);
        var Chests = EntityHub.getInstance().getEntitiesWithType(ChestEntity.class);
        File file = new File(PlayerStats.currentSave + "/dungeonEntities.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("PolarBears\n");
            for (Entity polarBear : PolarBears) {
                PositionComponent pos = polarBear.getComponent(PositionComponent.class);
                writer.write(pos.getGlobalX() + " " + pos.getGlobalY());
                writer.newLine();
            }
            writer.write("Chests\n");
            for (int i = 0; i < Chests.size(); i++) {
                CentralMassComponent pos = Chests.get(i).getComponent(CentralMassComponent.class);
                writer.write(pos.getCentralX() + " " + pos.getCentralY());
                if (i < Chests.size() - 1) {
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Saves the current in-game time and related data to a file.
     */
    public static void saveTime() {
        File file = new File(PlayerStats.currentSave + "/time.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write(DayTimeData.lastDayTime.toString());
            writer.newLine();
            writer.write(String.valueOf((int) DayTimeData.lastUpdate));
            writer.newLine();
            writer.write(String.valueOf(DayTimeData.periodsPassed));
            writer.newLine();
            writer.write(String.valueOf(DayTimeData.lastUpdatedPeriod));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Loads time-related data and adjusts internal time calculations based on real elapsed time.
     */
    public static void loadTime() {
        try (BufferedReader reader = new BufferedReader(new FileReader(PlayerStats.currentSave + "/time.txt"))) {
            DayTimeData.lastDayTime = Daytime.valueOf(reader.readLine());
            long lastUpdate = Long.parseLong(reader.readLine());
            long diff = System.currentTimeMillis() - lastUpdate;
            DayTimeData.lastUpdate = lastUpdate + diff;
            DayTimeData.periodsPassed = Integer.parseInt(reader.readLine());
            DayTimeData.lastUpdatedPeriod = Integer.parseInt(reader.readLine());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
