package inf.elte.hu.gameengine_javafx.Components;

import inf.elte.hu.gameengine_javafx.Core.Architecture.Component;
import inf.elte.hu.gameengine_javafx.Misc.LightType;

public class LightComponent extends Component {
    LightType type;
    double intensity;

    public LightComponent(LightType type, double intensity) {
        this.type = type;
        this.intensity = intensity;
    }

    public LightType getType() {
        return type;
    }

    public void setType(LightType type) {
        this.type = type;
    }

    public double getIntensity() {
        return intensity;
    }

    public void setIntensity(double intensity) {
        this.intensity = intensity;
    }

    @Override
    public String getStatus() {
        return "";
    }
}
