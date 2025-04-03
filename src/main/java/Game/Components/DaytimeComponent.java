package Game.Components;

import Game.Misc.Enums.Daytime;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Component;

public class DaytimeComponent extends Component {
    private Daytime daytime;

    public DaytimeComponent(Daytime daytime) {
        this.daytime = daytime;
    }

    public Daytime getDaytime() {
        return daytime;
    }

    public void setDaytime(Daytime daytime) {
        this.daytime = daytime;
    }

    @Override
    public String getStatus() {
        return "";
    }
}
