package Game.Misc.EventHandling.EventListeners;

import Game.Misc.EventHandling.Events.OpenShopEvent;
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
            uiRoot.getInstance().unloadAll();
            shopWrapper = null;
        });
    }

    private void createBuy() {
        VBox shopVBox = new VBox(20);
        shopVBox.setPadding(new Insets(20));
        shopVBox.setStyle("-fx-background-color: transparent;");

        for (int i = 1; i <= 8; i++) {
            VBox itemBox = createShopItem(
                    "Item " + i,
                    "This is a detailed description of item " + i + ". It might have cool effects or benefits.",
                    9.99 + i
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

    private VBox createShopItem(String name, String description, double price) {
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

        box.getChildren().addAll(nameLabel, priceLabel, descriptionLabel, buyButton);
        return box;
    }
}
