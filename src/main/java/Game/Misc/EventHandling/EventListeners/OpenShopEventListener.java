package Game.Misc.EventHandling.EventListeners;

import Game.Components.ShopItemInfoComponent;
import Game.Entities.BartenderPenguinEntity;
import Game.Entities.ShopItemEntity;
import Game.Misc.EventHandling.Events.OpenShopEvent;
import Game.Misc.PlayerStats;
import Game.Misc.ShopItemPrices;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Misc.Configs.DisplayConfig;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.EventListener;
import inf.elte.hu.gameengine_javafx.Misc.Layers.uiRoot;
import inf.elte.hu.gameengine_javafx.Misc.Sound.SoundEffect;
import inf.elte.hu.gameengine_javafx.Misc.Sound.SoundEffectStore;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Random;

public class OpenShopEventListener implements EventListener<OpenShopEvent> {
    private Pane shopWrapper = null;

    @Override
    public void onEvent(OpenShopEvent event) {
        if (shopWrapper == null) {
            addShopItems();
            createBuy();
        }
    }

    @Override
    public void onExit(OpenShopEvent event) {
        Platform.runLater(() -> {
            uiRoot.getInstance().getChildren().remove(shopWrapper);
            shopWrapper = null;
        });
    }

    private void addShopItems() {
        Random rand = new Random();
        EntityHub.getInstance().removeEntityManager(ShopItemEntity.class);
        new ShopItemEntity("Health", "Increase your health by 30 points.", ShopItemPrices.Health, () -> {
            if (PlayerStats.gold < ShopItemPrices.Health) {
                System.out.println("not enough gold");
                return;
            }
            PlayerStats.health = PlayerStats.health + 30;
            PlayerStats.gold -= ShopItemPrices.Health;
            BartenderPenguinEntity penguin = (BartenderPenguinEntity) EntityHub.getInstance().getEntitiesWithType(BartenderPenguinEntity.class).getFirst();
            SoundEffectStore.getInstance().add(new SoundEffect(penguin, "/assets/sound/sfx/goldGained.wav", "gold_" + penguin.getId(), 0.6f, 0.0f, 1000, false));
            ShopItemPrices.Health = (int) (ShopItemPrices.Health * rand.nextDouble(1.0, 1.05));
        });
        new ShopItemEntity("Melee damage", "Increase your melee damage by 10 points.(currently: " + PlayerStats.meleeDamage + ")", ShopItemPrices.MeleeDamage, () -> {
            if (PlayerStats.gold < ShopItemPrices.MeleeDamage) {
                System.out.println("not enough gold");
                return;
            }
            PlayerStats.meleeDamage = PlayerStats.meleeDamage + 10;
            PlayerStats.gold -= ShopItemPrices.MeleeDamage;
            BartenderPenguinEntity penguin = (BartenderPenguinEntity) EntityHub.getInstance().getEntitiesWithType(BartenderPenguinEntity.class).getFirst();
            SoundEffectStore.getInstance().add(new SoundEffect(penguin, "/assets/sound/sfx/goldGained.wav", "gold_" + penguin.getId(), 0.6f, 0.0f, 1000, false));
            ShopItemPrices.MeleeDamage = (int) (ShopItemPrices.MeleeDamage * rand.nextDouble(1.0, 1.05));
        });
        new ShopItemEntity("Ranged damage", "Increase your ranged damage by 10 points.(currently: " + PlayerStats.rangedDamage + ")", ShopItemPrices.RangedDamage, () -> {
            if (PlayerStats.gold < ShopItemPrices.RangedDamage) {
                System.out.println("not enough gold");
                return;
            }
            PlayerStats.rangedDamage = PlayerStats.rangedDamage + 10;
            PlayerStats.gold -= ShopItemPrices.RangedDamage;
            BartenderPenguinEntity penguin = (BartenderPenguinEntity) EntityHub.getInstance().getEntitiesWithType(BartenderPenguinEntity.class).getFirst();
            SoundEffectStore.getInstance().add(new SoundEffect(penguin, "/assets/sound/sfx/goldGained.wav", "gold_" + penguin.getId(), 0.6f, 0.0f, 1000, false));
            ShopItemPrices.RangedDamage = (int) (ShopItemPrices.RangedDamage * rand.nextDouble(1.0, 1.05));
        });
        new ShopItemEntity("Melee speed", "Decrease your melee cooldown by 5%(Up to 75% of base, currently: " + PlayerStats.meleeCooldown + ")", ShopItemPrices.MeleeSpeed, () -> {
            if (PlayerStats.gold < ShopItemPrices.MeleeSpeed) {
                System.out.println("not enough gold");
            }
            PlayerStats.meleeCooldown = Math.max(500L, (long) (PlayerStats.meleeCooldown * 0.95));
            PlayerStats.gold -= ShopItemPrices.MeleeSpeed;
            BartenderPenguinEntity penguin = (BartenderPenguinEntity) EntityHub.getInstance().getEntitiesWithType(BartenderPenguinEntity.class).getFirst();
            SoundEffectStore.getInstance().add(new SoundEffect(penguin, "/assets/sound/sfx/goldGained.wav", "gold_" + penguin.getId(), 0.6f, 0.0f, 1000, false));
            ShopItemPrices.MeleeSpeed = (int) (ShopItemPrices.MeleeSpeed * rand.nextDouble(1.0, 1.05));
        });
        new ShopItemEntity("Ranged speed", "Decrease your ranged cooldown by 5%(Up to 75% of base, currently: " + PlayerStats.rangedCooldown + ")", ShopItemPrices.RangedSpeed, () -> {
            if (PlayerStats.gold < ShopItemPrices.RangedSpeed) {
                System.out.println("not enough gold");
            }
            PlayerStats.rangedCooldown = Math.max(750L, (long) (PlayerStats.rangedCooldown * 0.95));
            PlayerStats.gold -= ShopItemPrices.RangedSpeed;
            BartenderPenguinEntity penguin = (BartenderPenguinEntity) EntityHub.getInstance().getEntitiesWithType(BartenderPenguinEntity.class).getFirst();
            SoundEffectStore.getInstance().add(new SoundEffect(penguin, "/assets/sound/sfx/goldGained.wav", "gold_" + penguin.getId(), 0.6f, 0.0f, 1000, false));
            ShopItemPrices.RangedSpeed = (int) (ShopItemPrices.RangedSpeed * rand.nextDouble(1.0, 1.05));
        });
        new ShopItemEntity("Melee resistance", "Increase your resistance to melee attacks by 5%(Up to 75% of base, currently: " + PlayerStats.meleeResistance * 100 + "%)", ShopItemPrices.MeleeResistance, () -> {
            if (PlayerStats.gold < ShopItemPrices.MeleeResistance) {
                System.out.println("not enough gold");
            }
            PlayerStats.meleeResistance = Math.min(PlayerStats.meleeResistance + 0.05, 0.75);
            PlayerStats.gold -= ShopItemPrices.MeleeResistance;
            BartenderPenguinEntity penguin = (BartenderPenguinEntity) EntityHub.getInstance().getEntitiesWithType(BartenderPenguinEntity.class).getFirst();
            SoundEffectStore.getInstance().add(new SoundEffect(penguin, "/assets/sound/sfx/goldGained.wav", "gold_" + penguin.getId(), 0.6f, 0.0f, 1000, false));
            ShopItemPrices.MeleeResistance = (int) (ShopItemPrices.MeleeResistance * rand.nextDouble(1.0, 1.05));
        });
        new ShopItemEntity("Ranged resistance", "Increase your resistance to ranged attacks by 5%(Up to 75% of base, currently: " + PlayerStats.rangedResistance * 100 + "%)", ShopItemPrices.RangedResistance, () -> {
            if (PlayerStats.gold < ShopItemPrices.RangedResistance) {
                System.out.println("not enough gold");
            }
            PlayerStats.rangedResistance = Math.min(PlayerStats.rangedResistance + 0.05, 0.75);
            PlayerStats.gold -= ShopItemPrices.RangedResistance;
            BartenderPenguinEntity penguin = (BartenderPenguinEntity) EntityHub.getInstance().getEntitiesWithType(BartenderPenguinEntity.class).getFirst();
            SoundEffectStore.getInstance().add(new SoundEffect(penguin, "/assets/sound/sfx/goldGained.wav", "gold_" + penguin.getId(), 0.6f, 0.0f, 1000, false));
            ShopItemPrices.RangedResistance = (int) (ShopItemPrices.RangedResistance * rand.nextDouble(1.0, 1.05));
        });
    }

    private void createBuy() {
        VBox shopVBox = new VBox(20);
        shopVBox.setPadding(new Insets(20));
        shopVBox.setStyle("-fx-background-color: transparent;");

        List<Entity> items = EntityHub.getInstance().getEntitiesWithType(ShopItemEntity.class);

        for (int i = 0; i < items.size(); i++) {
            ShopItemEntity item = (ShopItemEntity) items.get(i);
            ShopItemInfoComponent info = item.getComponent(ShopItemInfoComponent.class);
            VBox itemBox = createShopItem(
                    info.getItemName(),
                    info.getItemDescription(),
                    info.getPrice(),
                    info.getOnBuy()
            );
            shopVBox.getChildren().add(itemBox);
        }

        ScrollPane scrollPane = new ScrollPane(shopVBox);
        scrollPane.setFitToWidth(true);
        scrollPane.setMaxWidth(600);
        scrollPane.setMaxHeight(400);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent; -fx-border-color: transparent;");

        Pane wrapper = new Pane();
        wrapper.setStyle("-fx-background-color: transparent;");
        wrapper.getChildren().add(scrollPane);

        scrollPane.setLayoutX(DisplayConfig.resolution.first() / 2 - 300);
        scrollPane.setLayoutY(DisplayConfig.resolution.second() / 2 - 200);

        shopWrapper = wrapper;

        Platform.runLater(() -> {
            uiRoot.getInstance().getChildren().add(shopWrapper);
        });
    }

    private VBox createShopItem(String name, String description, double price, Runnable onBuy) {
        VBox box = new VBox(8);
        box.setPadding(new Insets(12));
        box.setStyle("-fx-border-color: #000000; -fx-border-radius: 10; -fx-background-radius: 10; -fx-background-color: rgb(240,240,240);");
        box.setPrefWidth(500);

        Label nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #000000;");

        Label priceLabel = new Label(String.format("Price: $%.2f", price));
        priceLabel.setStyle("-fx-text-fill: #000000;");

        Label descriptionLabel = new Label(description);
        descriptionLabel.setWrapText(true);
        descriptionLabel.setStyle("-fx-text-fill: #000000;");

        Button buyButton = new Button("Buy");
        buyButton.setStyle("-fx-background-color: #459b48; -fx-text-fill: white;");
        buyButton.setOnMouseClicked(event -> {
            onBuy.run();
            uiRoot.getInstance().getChildren().remove(shopWrapper);
            shopWrapper = null;
            addShopItems();
            createBuy();
        });
        buyButton.setDisable(PlayerStats.gold < price);

        buyButton.setOnMouseEntered(event -> buyButton.setStyle("-fx-background-color: #388e3c; -fx-text-fill: white; -fx-cursor: hand;"));
        buyButton.setOnMouseExited(event -> buyButton.setStyle("-fx-background-color: #459b48; -fx-text-fill: white;"));


        box.getChildren().addAll(nameLabel, priceLabel, descriptionLabel, buyButton);
        return box;
    }
}
