package Game.Misc.EventHandling.EventListeners;

import Game.Components.ShopItemInfoComponent;
import Game.Entities.ShopItemEntity;
import Game.Misc.EventHandling.Events.OpenShopEvent;
import Game.Misc.PlayerStats;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Misc.Config;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.EventListener;
import inf.elte.hu.gameengine_javafx.Misc.Layers.uiRoot;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.util.List;

public class OpenShopEventListener implements EventListener<OpenShopEvent> {
    private Pane shopWrapper = null;

    @Override
    public void onEvent(OpenShopEvent event) {
        if (shopWrapper == null) {
            createBuy();
        }
    }

    @Override
    public void onExit() {
        Platform.runLater(() -> {
            uiRoot.getInstance().getChildren().remove(shopWrapper);
            shopWrapper = null;
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

        scrollPane.setLayoutX(Config.resolution.first()/2-300);
        scrollPane.setLayoutY(Config.resolution.second()/2-200);

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
        });
        buyButton.setDisable(PlayerStats.gold < price);

        buyButton.setOnMouseEntered(event -> buyButton.setStyle("-fx-background-color: #388e3c; -fx-text-fill: white; -fx-cursor: hand;"));
        buyButton.setOnMouseExited(event -> buyButton.setStyle("-fx-background-color: #459b48; -fx-text-fill: white;"));


        box.getChildren().addAll(nameLabel, priceLabel, descriptionLabel, buyButton);
        return box;
    }
}
