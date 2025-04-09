package Game.Entities;

import Game.Components.ShopItemInfoComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;

public class ShopItemEntity extends Entity {
    public ShopItemEntity(String itemName, String itemDescription, double price, Runnable onBuy) {
        addComponent(new ShopItemInfoComponent(itemName, itemDescription, price, onBuy));

        addToManager();
    }
}
