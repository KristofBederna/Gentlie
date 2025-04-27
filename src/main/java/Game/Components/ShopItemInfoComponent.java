package Game.Components;

import inf.elte.hu.gameengine_javafx.Core.Architecture.Component;

public class ShopItemInfoComponent extends Component {
    private final String itemName;
    private final String itemDescription;
    private double price;
    private final Runnable onBuy;

    public ShopItemInfoComponent(String itemName, String itemDescription, double price, Runnable onBuy) {
        this.itemName = itemName;
        this.itemDescription = itemDescription;
        this.price = price;
        this.onBuy = onBuy;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getItemName() {
        return itemName;
    }

    public String getItemDescription() {
        return itemDescription;
    }

    public Runnable getOnBuy() {
        return onBuy;
    }
}
