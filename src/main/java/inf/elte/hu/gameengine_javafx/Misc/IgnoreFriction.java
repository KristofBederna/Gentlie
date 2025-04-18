package inf.elte.hu.gameengine_javafx.Misc;

import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Entities.ParticleEntity;

import java.util.ArrayList;
import java.util.List;

public class IgnoreFriction {
    public static List<Class<? extends Entity>> ignore = new ArrayList<>(List.of(ParticleEntity.class));
}
