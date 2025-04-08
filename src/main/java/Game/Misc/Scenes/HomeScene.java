package Game.Misc.Scenes;

import Game.Entities.CampfireEntity;
import Game.Entities.EventTriggerEntity;
import Game.Entities.WorldObject;
import Game.Misc.EventHandling.EventListeners.ExitHomeEventListener;
import Game.Misc.EventHandling.Events.ExitHomeEvent;
import Game.Misc.UtilityFunctions;
import Game.Systems.EventTileSystem;
import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.InteractiveComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.PlayerComponent;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Core.SystemHub;
import inf.elte.hu.gameengine_javafx.Entities.PlayerEntity;
import inf.elte.hu.gameengine_javafx.Entities.WorldEntity;
import inf.elte.hu.gameengine_javafx.Misc.Config;
import inf.elte.hu.gameengine_javafx.Misc.InputHandlers.MouseInputHandler;
import inf.elte.hu.gameengine_javafx.Misc.Scenes.GameScene;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.GameLoopStartUp;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.ResourceStartUp;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.SystemStartUp;
import inf.elte.hu.gameengine_javafx.Systems.InputHandlingSystem;
import inf.elte.hu.gameengine_javafx.Systems.PathfindingSystem;
import inf.elte.hu.gameengine_javafx.Systems.PhysicsSystems.CollisionSystem;
import inf.elte.hu.gameengine_javafx.Systems.PhysicsSystems.MovementDeterminerSystem;
import inf.elte.hu.gameengine_javafx.Systems.PhysicsSystems.MovementSystem;
import inf.elte.hu.gameengine_javafx.Systems.RenderingSystems.*;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.ResourceSystem;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.SoundSystem;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.WorldLoaderSystem;
import javafx.scene.Parent;
import javafx.scene.input.MouseButton;

import java.util.List;

public class HomeScene extends GameScene {
    public HomeScene(Parent parent, double width, double height) {
        super(parent, width, height);
    }

    @Override
    public void setup() {
        Config.wallTiles = List.of(0, 1, 3);
        Config.setTileScale(1.5);

        new ResourceStartUp();
        WorldEntity.getInstance("/assets/maps/gentlieHome.txt", "/assets/tileSets/gameTileSet.txt");

        declareEntities();

        UtilityFunctions.setUpCamera(1920, 1080, 16, 16);

        new SystemStartUp(this::SystemStartUp);

        interactionSetup();

        GameLoopStartUp.restartLoop();
    }

    private void declareEntities() {
        new PlayerEntity(6*Config.scaledTileSize + Config.scaledTileSize /2, 2*Config.scaledTileSize, "idle", "/assets/images/Gentlie/Gentlie_Down_Idle.png", Config.scaledTileSize * 2 * 0.55, Config.scaledTileSize * 2);
        new CampfireEntity(7*Config.scaledTileSize+Config.scaledTileSize*0.2, 5*Config.scaledTileSize, "/assets/images/Campfire/Campfire_1.png", Config.scaledTileSize*0.8, Config.scaledTileSize*0.8);
        new WorldObject(6*Config.scaledTileSize, 1*Config.scaledTileSize-Config.scaledTileSize*0.2, 3*Config.scaledTileSize, 3*0.27*Config.scaledTileSize, "/assets/images/Bed.png", true, 2);
        new WorldObject(2*Config.scaledTileSize+Config.scaledTileSize*0.3, 3*Config.scaledTileSize-Config.scaledTileSize*0.2, 1.5*0.15*Config.scaledTileSize, 1.5*Config.scaledTileSize, "/assets/images/Fishing_Rod.png", false, 2);
        new WorldObject(10*Config.scaledTileSize-Config.scaledTileSize*0.8, 9*Config.scaledTileSize+Config.scaledTileSize*0.3, 0.75*0.98*Config.scaledTileSize, 0.75*Config.scaledTileSize, "/assets/images/Backpack.png", false, 2);
        new EventTriggerEntity(6*Config.scaledTileSize, 11*Config.scaledTileSize +Config.scaledTileSize *0.8, 3*Config.scaledTileSize, 0.2*Config.scaledTileSize, new ExitHomeEvent(), new ExitHomeEventListener());
    }

    private void SystemStartUp() {
        //Define systems to be started up here
        SystemHub systemHub = SystemHub.getInstance();
        systemHub.addSystem(MovementDeterminerSystem.class, new MovementDeterminerSystem(),0);
        systemHub.addSystem(EventTileSystem.class, new EventTileSystem(),1);
        systemHub.addSystem(AnimationSystem.class, new AnimationSystem(), 2);
        systemHub.addSystem(LightingSystem.class, new LightingSystem(),4);
        systemHub.addSystem(PathfindingSystem.class, new PathfindingSystem(),5);
        systemHub.addSystem(MovementSystem.class, new MovementSystem(),6);
        systemHub.addSystem(ParticleSystem.class, new ParticleSystem(),7);
        systemHub.addSystem(InputHandlingSystem.class, new InputHandlingSystem(),8);
        systemHub.addSystem(CollisionSystem.class, new CollisionSystem(),9);
        systemHub.addSystem(ResourceSystem.class, new ResourceSystem(),10);
        systemHub.addSystem(CameraSystem.class, new CameraSystem(), 11);
        systemHub.addSystem(SoundSystem.class, new SoundSystem(), 12);
        systemHub.addSystem(WorldLoaderSystem.class, new WorldLoaderSystem(), 13);
        systemHub.addSystem(RenderSystem.class, new RenderSystem(),14);
    }

    private void interactionSetup() {
        PlayerEntity player = (PlayerEntity)EntityHub.getInstance().getEntitiesWithComponent(PlayerComponent.class).getFirst();
        InteractiveComponent playerInteractiveComponent = player.getComponent(InteractiveComponent.class);

        UtilityFunctions.setUpMovement(playerInteractiveComponent, player);
        UtilityFunctions.showSettingsMenu(playerInteractiveComponent);

        playerInteractiveComponent.mapInput(MouseButton.PRIMARY, 100, () -> {player.getComponent(PositionComponent.class).setLocalX(MouseInputHandler.getInstance().getMouseX(), player); player.getComponent(PositionComponent.class).setLocalY(MouseInputHandler.getInstance().getMouseY(), player);});
    }

    @Override
    public void breakdown() {
        UtilityFunctions.defaultBreakdownMethod();
    }
}
