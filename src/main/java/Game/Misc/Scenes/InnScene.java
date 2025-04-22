package Game.Misc.Scenes;

import Game.Entities.BartenderPenguinEntity;
import Game.Entities.EventTriggerEntity;
import Game.Entities.Labels.GoldLabel;
import Game.Entities.Labels.HealthLabel;
import Game.Entities.WorldObject;
import Game.Misc.EventHandling.EventListeners.ExitInnEventListener;
import Game.Misc.EventHandling.EventListeners.OpenShopEventListener;
import Game.Misc.EventHandling.Events.ExitInnEvent;
import Game.Misc.EventHandling.Events.OpenShopEvent;
import Game.Misc.PlayerStats;
import Game.Misc.UtilityFunctions;
import Game.Systems.*;
import inf.elte.hu.gameengine_javafx.Components.InteractiveComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.PlayerComponent;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Core.SystemHub;
import inf.elte.hu.gameengine_javafx.Entities.PlayerEntity;
import inf.elte.hu.gameengine_javafx.Entities.WorldEntity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Misc.Configs.MapConfig;
import inf.elte.hu.gameengine_javafx.Misc.Scenes.GameScene;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.GameLoopStartUp;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.ResourceStartUp;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.SystemStartUp;
import inf.elte.hu.gameengine_javafx.Systems.InputHandlingSystem;
import inf.elte.hu.gameengine_javafx.Systems.PathfindingSystem;
import inf.elte.hu.gameengine_javafx.Systems.PhysicsSystems.CollisionSystem;
import inf.elte.hu.gameengine_javafx.Systems.PhysicsSystems.MovementDeterminerSystem;
import inf.elte.hu.gameengine_javafx.Systems.PhysicsSystems.MovementSystem;
import inf.elte.hu.gameengine_javafx.Systems.RenderingSystems.AnimationSystem;
import inf.elte.hu.gameengine_javafx.Systems.RenderingSystems.CameraSystem;
import inf.elte.hu.gameengine_javafx.Systems.RenderingSystems.ParticleSystem;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.ResourceSystem;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.SoundSystem;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.WorldLoaderSystem;
import javafx.scene.Parent;

import java.util.List;

public class InnScene extends GameScene {
    Point spawn;

    public InnScene(Parent parent, double width, double height, Point spawn) {
        super(parent, width, height);
        this.spawn = spawn;
    }

    @Override
    public void setup() {
        MapConfig.wallTiles = List.of(0, 1, 3);
        MapConfig.setTileScale(1.5);

        new ResourceStartUp();
        WorldEntity.getInstance("/assets/maps/inn.txt", "/assets/tileSets/innTileSet.txt");

        declareEntities();

        UtilityFunctions.setUpCamera(1920, 1080, 16, 16);

        new SystemStartUp(this::systemStartUp);

        interactionSetup();

        GameLoopStartUp.restartLoop();
    }

    private void declareEntities() {
        new PlayerEntity(spawn.getX(), spawn.getY(), "idle", "/assets/images/Gentlie/Gentlie_Down_1.png", MapConfig.scaledTileSize * 2 * 0.55, MapConfig.scaledTileSize * 2);
        new GoldLabel(String.valueOf(PlayerStats.gold), 100, 100, 100, 100);
        new HealthLabel(String.format("%.0f", PlayerStats.health), 100, 200, 100, 100);
        new BartenderPenguinEntity(9 * MapConfig.scaledTileSize + MapConfig.scaledTileSize / 2, 2 * MapConfig.scaledTileSize, "/assets/images/Penguins/Penguin_Down_1.png", MapConfig.scaledTileSize * 2 * 0.55, MapConfig.scaledTileSize * 2);
        new WorldObject(9 * MapConfig.scaledTileSize, 3 * MapConfig.scaledTileSize - MapConfig.scaledTileSize * 0.5, 3 * MapConfig.scaledTileSize, 2 * MapConfig.scaledTileSize, "/assets/images/Inn_Bar.png", true, 4);
        new WorldObject(9 * MapConfig.scaledTileSize, -MapConfig.scaledTileSize * 0.1, 3 * MapConfig.scaledTileSize, 2 * MapConfig.scaledTileSize, "/assets/images/Inn_Bar_Shelf.png", true, 2);
        new WorldObject(3 * MapConfig.scaledTileSize, 2 * MapConfig.scaledTileSize, 3 * MapConfig.scaledTileSize, 3 * 0.625 * MapConfig.scaledTileSize, "/assets/images/Inn_Table.png", true, 2);
        new WorldObject(3 * MapConfig.scaledTileSize, 5 * MapConfig.scaledTileSize, 3 * MapConfig.scaledTileSize, 3 * 0.625 * MapConfig.scaledTileSize, "/assets/images/Inn_Table.png", true, 2);
        new EventTriggerEntity(5 * MapConfig.scaledTileSize, 10 * MapConfig.scaledTileSize + MapConfig.scaledTileSize * 0.8, 3 * MapConfig.scaledTileSize, 0.2 * MapConfig.scaledTileSize, new ExitInnEvent(), new ExitInnEventListener());
        new EventTriggerEntity(10 * MapConfig.scaledTileSize, 3 * MapConfig.scaledTileSize, MapConfig.scaledTileSize, 2 * MapConfig.scaledTileSize, new OpenShopEvent(), new OpenShopEventListener());
    }

    @Override
    protected void systemStartUp() {
        //Define systems to be started up here
        SystemHub systemHub = SystemHub.getInstance();
        systemHub.addSystem(ShopPriceUpdateSystem.class, new ShopPriceUpdateSystem(), -1);
        systemHub.addSystem(MovementDeterminerSystem.class, new MovementDeterminerSystem(), 0);
        systemHub.addSystem(EventTileSystem.class, new EventTileSystem(), 1);
        systemHub.addSystem(AnimationSystem.class, new AnimationSystem(), 2);
        systemHub.addSystem(PathfindingSystem.class, new PathfindingSystem(), 3);
        systemHub.addSystem(MovementSystem.class, new MovementSystem(), 4);
        systemHub.addSystem(ParticleSystem.class, new ParticleSystem(), 5);
        systemHub.addSystem(InputHandlingSystem.class, new InputHandlingSystem(), 6);
        systemHub.addSystem(CollisionSystem.class, new CollisionSystem(), 7);
        systemHub.addSystem(ResourceSystem.class, new ResourceSystem(), 8);
        systemHub.addSystem(CameraSystem.class, new CameraSystem(), 9);
        systemHub.addSystem(SoundSystem.class, new SoundSystem(), 10);
        systemHub.addSystem(WorldLoaderSystem.class, new WorldLoaderSystem(), 11);
        systemHub.addSystem(UserInterfaceSystem.class, new UserInterfaceSystem(), 12);
        systemHub.addSystem(CustomRenderSystem.class, new CustomRenderSystem(), 13);
        systemHub.addSystem(GameSaverSystem.class, new GameSaverSystem(), 14);
    }

    private void interactionSetup() {
        PlayerEntity player = (PlayerEntity) EntityHub.getInstance().getEntitiesWithComponent(PlayerComponent.class).getFirst();
        InteractiveComponent playerInteractiveComponent = player.getComponent(InteractiveComponent.class);

        UtilityFunctions.setUpMovement(playerInteractiveComponent, player);
        UtilityFunctions.showSettingsMenu(playerInteractiveComponent);
    }

    @Override
    public void breakdown() {
        UtilityFunctions.defaultBreakdownMethod();
    }
}
