package Game.Misc.Scenes;

import Game.Entities.CampfireEntity;
import Game.Entities.EventTriggerEntity;
import Game.Entities.Labels.GoldLabel;
import Game.Entities.Labels.HealthLabel;
import Game.Entities.WorldObject;
import Game.Misc.EventHandling.EventListeners.ExitHomeEventListener;
import Game.Misc.EventHandling.Events.ExitHomeEvent;
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
import inf.elte.hu.gameengine_javafx.Systems.PathfindingSystems.PathfindingSystem;
import inf.elte.hu.gameengine_javafx.Systems.PhysicsSystems.CollisionSystem;
import inf.elte.hu.gameengine_javafx.Systems.PhysicsSystems.MovementDeterminerSystem;
import inf.elte.hu.gameengine_javafx.Systems.PhysicsSystems.MovementSystem;
import inf.elte.hu.gameengine_javafx.Systems.RenderingSystems.AnimationSystem;
import inf.elte.hu.gameengine_javafx.Systems.RenderingSystems.CameraSystem;
import inf.elte.hu.gameengine_javafx.Systems.RenderingSystems.ParticleSystem;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.MapLoaderSystems.WorldLoaderSystem;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.ResourceSystem;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.SoundSystems.SoundSystem;
import javafx.scene.Parent;

import java.util.List;

public class HomeScene extends GameScene {
    Point spawn;

    public HomeScene(Parent parent, double width, double height, Point spawn) {
        super(parent, width, height);
        this.spawn = spawn;
    }

    @Override
    public void setup() {
        MapConfig.wallTiles = List.of(0, 1, 3);
        MapConfig.setTileScale(1.5);

        new ResourceStartUp();
        WorldEntity.getInstance("/assets/maps/gentlieHome.txt", "/assets/tileSets/gameTileSet.txt");

        declareEntities();

        UtilityFunctions.setUpCamera(1920, 1080, 16, 16);

        new SystemStartUp(this::systemStartUp);

        interactionSetup();

        GameLoopStartUp.restartLoop();
    }

    private void declareEntities() {
        new PlayerEntity(spawn.getX(), spawn.getY(), "idle", "/assets/images/Gentlie/Gentlie_Down_Idle.png", MapConfig.scaledTileSize * 2 * 0.55, MapConfig.scaledTileSize * 2);
        new GoldLabel(String.valueOf(PlayerStats.gold), 100, 100, 100, 100);
        new HealthLabel(String.format("%.0f", PlayerStats.health), 100, 200, 100, 100);
        new CampfireEntity(7 * MapConfig.scaledTileSize + MapConfig.scaledTileSize * 0.2, 5 * MapConfig.scaledTileSize, "/assets/images/Campfire/Campfire_1.png", MapConfig.scaledTileSize * 0.8, MapConfig.scaledTileSize * 0.8);
        new WorldObject(6 * MapConfig.scaledTileSize, 1 * MapConfig.scaledTileSize - MapConfig.scaledTileSize * 0.2, 3 * MapConfig.scaledTileSize, 3 * 0.27 * MapConfig.scaledTileSize, "/assets/images/Bed.png", true, 2);
        new WorldObject(2 * MapConfig.scaledTileSize + MapConfig.scaledTileSize * 0.3, 3 * MapConfig.scaledTileSize - MapConfig.scaledTileSize * 0.2, 1.5 * 0.15 * MapConfig.scaledTileSize, 1.5 * MapConfig.scaledTileSize, "/assets/images/Fishing_Rod.png", false, 2);
        new WorldObject(10 * MapConfig.scaledTileSize - MapConfig.scaledTileSize * 0.8, 9 * MapConfig.scaledTileSize + MapConfig.scaledTileSize * 0.3, 0.75 * 0.98 * MapConfig.scaledTileSize, 0.75 * MapConfig.scaledTileSize, "/assets/images/Backpack.png", false, 2);
        new EventTriggerEntity(6 * MapConfig.scaledTileSize, 11 * MapConfig.scaledTileSize + MapConfig.scaledTileSize * 0.8, 3 * MapConfig.scaledTileSize, 0.2 * MapConfig.scaledTileSize, new ExitHomeEvent(), new ExitHomeEventListener());
    }

    @Override
    protected void systemStartUp() {
        //Define systems to be started up here
        SystemHub systemHub = SystemHub.getInstance();
        systemHub.addSystem(MovementDeterminerSystem.class, new MovementDeterminerSystem(),0);
        systemHub.addSystem(EventTileSystem.class, new EventTileSystem(),1);
        systemHub.addSystem(AnimationSystem.class, new AnimationSystem(), 2);
        systemHub.addSystem(UserInterfaceSystem.class, new UserInterfaceSystem(), 4);
        systemHub.addSystem(PathfindingSystem.class, new PathfindingSystem(),5);
        systemHub.addSystem(MovementSystem.class, new MovementSystem(),6);
        systemHub.addSystem(ParticleSystem.class, new ParticleSystem(),7);
        systemHub.addSystem(InputHandlingSystem.class, new InputHandlingSystem(),8);
        systemHub.addSystem(CollisionSystem.class, new CollisionSystem(),9);
        systemHub.addSystem(ResourceSystem.class, new ResourceSystem(),10);
        systemHub.addSystem(CameraSystem.class, new CameraSystem(), 11);
        systemHub.addSystem(SoundSystem.class, new SoundSystem(), 12);
        systemHub.addSystem(WorldLoaderSystem.class, new WorldLoaderSystem(), 13);
        systemHub.addSystem(CustomRenderSystem.class, new CustomRenderSystem(), 14);
        systemHub.addSystem(GameSaverSystem.class, new GameSaverSystem(), 15);
        systemHub.addSystem(StepSoundEffectSystem.class, new StepSoundEffectSystem(), 16);
    }

    private void interactionSetup() {
        PlayerEntity player = (PlayerEntity)EntityHub.getInstance().getEntitiesWithComponent(PlayerComponent.class).getFirst();
        InteractiveComponent playerInteractiveComponent = player.getComponent(InteractiveComponent.class);

        UtilityFunctions.setUpMovement(playerInteractiveComponent, player);
        UtilityFunctions.showSettingsMenu(playerInteractiveComponent);
    }

    @Override
    public void breakdown() {
        UtilityFunctions.defaultBreakdownMethod();
    }
}
