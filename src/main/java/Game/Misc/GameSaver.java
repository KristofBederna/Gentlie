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

public class GameSaver {
    public static void saveEntityStats() {
        File file = new File(PlayerStats.currentSave + "/entityStats.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PlayerStats.currentSave + "/entityStats.txt"))) {
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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void loadEntityStats() {
        try (BufferedReader reader = new BufferedReader(new FileReader(PlayerStats.currentSave + "/entityStats.txt"))) {
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

    public static void saveShopPrices() {
        File file = new File(PlayerStats.currentSave + "/shopPrices.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PlayerStats.currentSave + "/shopPrices.txt"))) {
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

    public static void saveDungeonState() {
        var PolarBears = EntityHub.getInstance().getEntitiesWithType(PolarBearEntity.class);
        var Chests = EntityHub.getInstance().getEntitiesWithType(ChestEntity.class);
        if (PolarBears.isEmpty()) {
            File map = new File(PlayerStats.currentSave + "/lastMapGenerated.txt");
            if (map.exists())
                map.delete();
            File entities = new File(PlayerStats.currentSave + "/dungeonEntities.txt");
            if (entities.exists())
                entities.delete();
        } else {
            File file = new File(PlayerStats.currentSave + "/dungeonEntities.txt");
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(PlayerStats.currentSave + "/dungeonEntities.txt"))) {
                writer.write("PolarBears\n");
                for (Entity polarBear : PolarBears) {
                    PositionComponent pos = polarBear.getComponent(PositionComponent.class);
                    double x = pos.getGlobalX();
                    double y = pos.getGlobalY();

                    writer.write(x + " " + y);

                    writer.newLine();
                }
                writer.write("Chests\n");
                for (int i = 0; i < Chests.size(); i++) {
                    Entity chest = Chests.get(i);
                    CentralMassComponent pos = chest.getComponent(CentralMassComponent.class);
                    double x = pos.getCentralX();
                    double y = pos.getCentralY();
                    writer.write(x + " " + y);
                    if (i < Chests.size() - 1) {
                        writer.newLine();
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void saveTime() {
        File file = new File(PlayerStats.currentSave + "/time.txt");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PlayerStats.currentSave + "/time.txt"))) {
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
