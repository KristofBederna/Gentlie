package Game.Misc.Scenes;

import Game.Components.AttackBoxComponent;
import Game.Entities.EventTriggerEntity;
import Game.Entities.Labels.EnterEnemyIslandLabel;
import Game.Entities.SnowBallEntity;
import Game.Misc.EventHandling.EventListeners.EnterEnemyIslandEventListener;
import Game.Misc.EventHandling.Events.EnterEnemyIslandEvent;
import Game.Misc.UtilityFunctions;
import Game.Systems.*;
import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.InteractiveComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.AccelerationComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.VelocityComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.CentralMassComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.DimensionComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.DirectionComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.PlayerComponent;
import inf.elte.hu.gameengine_javafx.Components.UIComponents.LabelComponent;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Core.SystemHub;
import inf.elte.hu.gameengine_javafx.Entities.PlayerEntity;
import inf.elte.hu.gameengine_javafx.Entities.WorldEntity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.ComplexShape;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Rectangle;
import inf.elte.hu.gameengine_javafx.Maths.Vector;
import inf.elte.hu.gameengine_javafx.Misc.Config;
import inf.elte.hu.gameengine_javafx.Misc.Direction;
import inf.elte.hu.gameengine_javafx.Misc.InputHandlers.MouseInputHandler;
import inf.elte.hu.gameengine_javafx.Misc.Scenes.GameScene;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.GameLoopStartUp;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.ResourceStartUp;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.SystemStartUp;
import inf.elte.hu.gameengine_javafx.Misc.Time;
import inf.elte.hu.gameengine_javafx.Systems.InputHandlingSystem;
import inf.elte.hu.gameengine_javafx.Systems.PathfindingSystem;
import inf.elte.hu.gameengine_javafx.Systems.PhysicsSystems.MovementDeterminerSystem;
import inf.elte.hu.gameengine_javafx.Systems.PhysicsSystems.MovementSystem;
import inf.elte.hu.gameengine_javafx.Systems.RenderingSystems.AnimationSystem;
import inf.elte.hu.gameengine_javafx.Systems.RenderingSystems.CameraSystem;
import inf.elte.hu.gameengine_javafx.Systems.RenderingSystems.ParticleSystem;
import inf.elte.hu.gameengine_javafx.Systems.RenderingSystems.RenderSystem;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.ResourceSystem;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.SoundSystem;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.text.TextAlignment;

import java.util.List;

public class DungeonScene extends GameScene {
    public DungeonScene(Parent parent, double width, double height) {
        super(parent, width, height);
    }

    @Override
    public void setup() {
        Config.wallTiles = List.of(0, 1, 3);
        Config.setTileScale(2.0);

        new ResourceStartUp();

        WorldEntity.getInstance(32, 32, "/assets/tileSets/gameTileSet.txt");

        declareEntities();

        UtilityFunctions.setUpCamera(1920,1080,32,32);

        new SystemStartUp(this::SystemStartUp);

        interactionSetup();

        GameLoopStartUp.restartLoop();
    }

    private void declareEntities() {
        new PlayerEntity(Config.scaledTileSize + Config.scaledTileSize / 2, Config.scaledTileSize + Config.scaledTileSize / 2, "idle", "/assets/images/Gentlie/Gentlie_Down_Idle.png", Config.scaledTileSize * 0.8 * 0.55, Config.scaledTileSize * 0.8);
        new EventTriggerEntity(0, Config.scaledTileSize, Config.scaledTileSize, Config.scaledTileSize * 3, new EnterEnemyIslandEvent(new Point(4 * 150, 2 * 150 + 150 * 0.25 - 1)), new EnterEnemyIslandEventListener());
        EnterEnemyIslandLabel enterEnemyIslandLabel = new EnterEnemyIslandLabel("Press 'E' to leave dungeon", Config.scaledTileSize, 3 * Config.scaledTileSize, Config.scaledTileSize * 0.75, Config.scaledTileSize * 0.75);
        enterEnemyIslandLabel.removeFromUI();
        enterEnemyIslandLabel.getComponent(LabelComponent.class).getUIElement().setTextAlignment(TextAlignment.CENTER);
    }

    private void SystemStartUp() {
        //Define systems to be started up here
        SystemHub systemHub = SystemHub.getInstance();
        systemHub.addSystem(MovementDeterminerSystem.class, new MovementDeterminerSystem(), 0);
        systemHub.addSystem(EventTileSystem.class, new EventTileSystem(), 1);
        systemHub.addSystem(AnimationSystem.class, new AnimationSystem(), 2);
        systemHub.addSystem(PolarBearMoverSystem.class, new PolarBearMoverSystem(), 3);
        systemHub.addSystem(PathfindingSystem.class, new PathfindingSystem(), 4);
        systemHub.addSystem(MovementSystem.class, new MovementSystem(), 5);
        systemHub.addSystem(ParticleSystem.class, new ParticleSystem(), 6);
        systemHub.addSystem(InputHandlingSystem.class, new InputHandlingSystem(), 7);
        systemHub.addSystem(CustomCollisionSystem.class, new CustomCollisionSystem(), 8);
        systemHub.addSystem(ResourceSystem.class, new ResourceSystem(), 9);
        systemHub.addSystem(CameraSystem.class, new CameraSystem(), 10);
        systemHub.addSystem(SoundSystem.class, new SoundSystem(), 11);
        systemHub.addSystem(DungeonGeneratorSystem.class, new DungeonGeneratorSystem(2, 2), 12);
        systemHub.addSystem(PolarBearSpawnerSystem.class, new PolarBearSpawnerSystem(), 13);
        systemHub.addSystem(AttackSystem.class, new AttackSystem(), 14);
        systemHub.addSystem(RemoveDeadObjectSystem.class, new RemoveDeadObjectSystem(), 15);
        systemHub.addSystem(RenderSystem.class, new RenderSystem(), 16);
    }

    private void interactionSetup() {
        PlayerEntity player = (PlayerEntity)EntityHub.getInstance().getEntitiesWithComponent(PlayerComponent.class).getFirst();
        InteractiveComponent playerInteractiveComponent = player.getComponent(InteractiveComponent.class);

        UtilityFunctions.setUpMovement(playerInteractiveComponent, player);
        UtilityFunctions.showSettingsMenu(playerInteractiveComponent);

        playerInteractiveComponent.mapInput(KeyCode.ENTER, 100, () -> {player.getComponent(PositionComponent.class).setLocalX(MouseInputHandler.getInstance().getMouseX(), player); player.getComponent(PositionComponent.class).setLocalY(MouseInputHandler.getInstance().getMouseY(), player);});

        playerInteractiveComponent.mapInput(MouseButton.PRIMARY, 2000, () -> {
            double playerX = player.getComponent(CentralMassComponent.class).getCentralX();
            double playerY = player.getComponent(CentralMassComponent.class).getCentralY();

            double width = player.getComponent(DimensionComponent.class).getWidth();
            double height = player.getComponent(DimensionComponent.class).getHeight();

            double dx = MouseInputHandler.getInstance().getMouseX() - playerX;
            double dy = MouseInputHandler.getInstance().getMouseY() - playerY;

            double angle = Math.atan2(-dy, dx);
            double angleDeg = Math.toDegrees(angle);
            if (angleDeg < 0) angleDeg += 360;

            int directionX;
            int directionY;

            DirectionComponent directionComponent = player.getComponent(DirectionComponent.class);
            Direction direction = directionComponent.getDirection();

            if (angleDeg >= 337.5 || angleDeg < 22.5) {
                directionX = 1; directionY = 0;   // E
                if (direction == Direction.LEFT || direction == Direction.UP || direction == Direction.DOWN) {
                    return;
                }
            } else if (angleDeg < 67.5) {
                directionX = 1; directionY = -1;  // NE
                if (direction == Direction.LEFT || direction == Direction.DOWN) {
                    return;
                }
            } else if (angleDeg < 112.5) {
                directionX = 0; directionY = -1;  // N
                if (direction != Direction.UP) {
                    if (direction != Direction.ALL) {
                        return;
                    }
                }
            } else if (angleDeg < 157.5) {
                directionX = -1; directionY = -1; // NW
                if (direction == Direction.RIGHT || direction == Direction.DOWN) {
                    return;
                }
            } else if (angleDeg < 202.5) {
                directionX = -1; directionY = 0;  // W
                if (direction == Direction.RIGHT) {
                    return;
                }
            } else if (angleDeg < 247.5) {
                directionX = -1; directionY = 1;  // SW
                if (direction == Direction.RIGHT || direction == Direction.UP) {
                    return;
                }
            } else if (angleDeg < 292.5) {
                directionX = 0; directionY = 1;   // S
                if (direction != Direction.DOWN) {
                    if (direction != Direction.ALL) {
                        return;
                    }
                }
            } else {
                directionX = 1; directionY = 1;   // SE
                if (direction == Direction.LEFT || direction == Direction.UP) {
                    return;
                }
            }

            ComplexShape attackBox = new ComplexShape(new Rectangle(new Point(playerX - width / 2, playerY - height / 2), width, height).getPoints());

            double offsetX = directionX * (width / 2 + width / 2);
            double offsetY = directionY * (height / 2 + height / 2);
            attackBox.translate(offsetX, offsetY);

            if (player.getComponent(AttackBoxComponent.class) == null) {
                player.addComponent(new AttackBoxComponent(attackBox.getPoints(), 100));
                player.getComponent(VelocityComponent.class).stopMovement();
                player.getComponent(AccelerationComponent.class).stopMovement();
            }
        });

        playerInteractiveComponent.mapInput(MouseButton.SECONDARY, 3000, () -> {
            double playerX = player.getComponent(CentralMassComponent.class).getCentralX();
            double playerY = player.getComponent(CentralMassComponent.class).getCentralY();

            double dx = MouseInputHandler.getInstance().getMouseX() - playerX;
            double dy = MouseInputHandler.getInstance().getMouseY() - playerY;

            double angle = Math.atan2(-dy, dx);
            double angleDeg = Math.toDegrees(angle);
            if (angleDeg < 0) angleDeg += 360;

            DirectionComponent directionComponent = player.getComponent(DirectionComponent.class);
            Direction direction = directionComponent.getDirection();

            if (angleDeg >= 337.5 || angleDeg < 22.5) {
                if (direction == Direction.LEFT || direction == Direction.UP || direction == Direction.DOWN) return;
            } else if (angleDeg < 67.5) {
                if (direction == Direction.LEFT || direction == Direction.DOWN) return;
            } else if (angleDeg < 112.5) {
                if (direction != Direction.UP && direction != Direction.ALL) return;
            } else if (angleDeg < 157.5) {
                if (direction == Direction.RIGHT || direction == Direction.DOWN) return;
            } else if (angleDeg < 202.5) {
                if (direction == Direction.RIGHT) return;
            } else if (angleDeg < 247.5) {
                if (direction == Direction.RIGHT || direction == Direction.UP) return;
            } else if (angleDeg < 292.5) {
                if (direction != Direction.DOWN && direction != Direction.ALL) return;
            } else {
                if (direction == Direction.LEFT || direction == Direction.UP) return;
            }

            double length = Math.sqrt(dx * dx + dy * dy);
            double speed = 1500 * Time.getInstance().getDeltaTime();
            Vector throwDirection = new Vector((dx / length) * speed, (dy / length) * speed);

            new SnowBallEntity(playerX, playerY, Config.scaledTileSize / 5, Config.scaledTileSize / 5, throwDirection);
            player.getComponent(VelocityComponent.class).stopMovement();
            player.getComponent(AccelerationComponent.class).stopMovement();
        });
    }

    @Override
    public void breakdown() {
        UtilityFunctions.defaultBreakdownMethod();
    }
}
