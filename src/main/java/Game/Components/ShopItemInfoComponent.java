package Game.Components;

import inf.elte.hu.gameengine_javafx.Core.Architecture.Component;

public class ShopItemInfoComponent extends Component {
    private String itemName;
    private String itemDescription;
    private double price;
    private Runnable onBuy;

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

    @Override
    public String getStatus() {
        return "";
    }
}
