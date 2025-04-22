package Game.Misc.Scenes;

import Game.Components.AttackBoxComponent;
import Game.Components.HealthComponent;
import Game.Entities.BigSnowBallEntity;
import Game.Entities.EventTriggerEntity;
import Game.Entities.Labels.EnemyLabel;
import Game.Entities.Labels.EnterEnemyIslandLabel;
import Game.Entities.Labels.GoldLabel;
import Game.Entities.Labels.HealthLabel;
import Game.Entities.PolarBearEntity;
import Game.Entities.SnowBallEntity;
import Game.Misc.EventHandling.EventListeners.ExitDungeonEventListener;
import Game.Misc.EventHandling.Events.ExitDungeonEvent;
import Game.Misc.PlayerStats;
import Game.Misc.UtilityFunctions;
import Game.Systems.*;
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
import inf.elte.hu.gameengine_javafx.Misc.Configs.MapConfig;
import inf.elte.hu.gameengine_javafx.Misc.Direction;
import inf.elte.hu.gameengine_javafx.Misc.IgnoreFriction;
import inf.elte.hu.gameengine_javafx.Misc.InputHandlers.MouseInputHandler;
import inf.elte.hu.gameengine_javafx.Misc.Scenes.GameScene;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.GameLoopStartUp;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.ResourceStartUp;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.SystemStartUp;
import inf.elte.hu.gameengine_javafx.Misc.Time;
import inf.elte.hu.gameengine_javafx.Misc.Tuple;
import inf.elte.hu.gameengine_javafx.Systems.InputHandlingSystem;
import inf.elte.hu.gameengine_javafx.Systems.PathfindingSystem;
import inf.elte.hu.gameengine_javafx.Systems.PhysicsSystems.MovementDeterminerSystem;
import inf.elte.hu.gameengine_javafx.Systems.PhysicsSystems.MovementSystem;
import inf.elte.hu.gameengine_javafx.Systems.RenderingSystems.AnimationSystem;
import inf.elte.hu.gameengine_javafx.Systems.RenderingSystems.ParticleSystem;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.ResourceSystem;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.SoundSystem;
import javafx.scene.Parent;
import javafx.scene.input.MouseButton;
import javafx.scene.text.TextAlignment;

import java.util.List;

public class DungeonScene extends GameScene {
    Point spawn;

    public DungeonScene(Parent parent, double width, double height, Point spawn) {
        super(parent, width, height);
        this.spawn = spawn;
    }

    @Override
    public void setup() {
        MapConfig.wallTiles = List.of(0, 1, 3);
        MapConfig.setTileScale(2.0);

        IgnoreFriction.ignore.add(SnowBallEntity.class);
        IgnoreFriction.ignore.add(BigSnowBallEntity.class);

        new ResourceStartUp();

        WorldEntity.getInstance(32, 32, "/assets/tileSets/gameTileSet.txt");

        declareEntities();

        UtilityFunctions.setUpCamera(1920, 1080, 32, 32);

        new SystemStartUp(this::systemStartUp);

        interactionSetup();

        GameLoopStartUp.restartLoop();
    }

    private void declareEntities() {
        PlayerEntity player = new PlayerEntity(spawn.getX(), spawn.getY(), "idle", "/assets/images/Gentlie/Gentlie_Down_Idle.png", MapConfig.scaledTileSize * 0.8 * 0.55, MapConfig.scaledTileSize * 0.8);
        player.addComponent(new HealthComponent(PlayerStats.health));
        new EventTriggerEntity(0, MapConfig.scaledTileSize, MapConfig.scaledTileSize, MapConfig.scaledTileSize * 3, new ExitDungeonEvent(new Point(4 * 150, 2 * 150 + 150 * 0.25 - 1)), new ExitDungeonEventListener());
        new EnemyLabel(String.valueOf(EntityHub.getInstance().getEntitiesWithType(PolarBearEntity.class).size()), 100, 300, 100, 100);
        new GoldLabel(String.valueOf(PlayerStats.gold), 100, 100, 100, 100);
        new HealthLabel(String.format("%.0f", PlayerStats.health), 100, 200, 100, 100);
        EnterEnemyIslandLabel enterEnemyIslandLabel = new EnterEnemyIslandLabel("Press 'E' to leave dungeon", MapConfig.scaledTileSize, 3 * MapConfig.scaledTileSize, MapConfig.scaledTileSize * 0.75, MapConfig.scaledTileSize * 0.75);
        enterEnemyIslandLabel.removeFromUI();
        enterEnemyIslandLabel.getComponent(LabelComponent.class).getUIElement().setTextAlignment(TextAlignment.CENTER);
    }

    @Override
    protected void systemStartUp() {
        //Define systems to be started up here
        SystemHub systemHub = SystemHub.getInstance();
        systemHub.addSystem(MovementDeterminerSystem.class, new MovementDeterminerSystem(), 0);
        systemHub.addSystem(EventTileSystem.class, new EventTileSystem(), 1);
        systemHub.addSystem(AnimationSystem.class, new AnimationSystem(), 2);
        systemHub.addSystem(PolarBearMoverSystem.class, new PolarBearMoverSystem(), 3);
        systemHub.addSystem(PolarBearAttackSystem.class, new PolarBearAttackSystem(), 4);
        systemHub.addSystem(PathfindingSystem.class, new PathfindingSystem(), 5);
        systemHub.addSystem(MovementSystem.class, new MovementSystem(), 6);
        systemHub.addSystem(ParticleSystem.class, new ParticleSystem(), 7);
        systemHub.addSystem(InputHandlingSystem.class, new InputHandlingSystem(), 8);
        systemHub.addSystem(ResourceSystem.class, new ResourceSystem(), 9);
        systemHub.addSystem(CustomCameraSystem.class, new CustomCameraSystem(), 10);
        systemHub.addSystem(CustomCollisionSystem.class, new CustomCollisionSystem(), 11);
        systemHub.addSystem(SoundSystem.class, new SoundSystem(), 12);
        systemHub.addSystem(DungeonGeneratorSystem.class, new DungeonGeneratorSystem(2, 2), 13);
        systemHub.addSystem(PolarBearSpawnerSystem.class, new PolarBearSpawnerSystem(), 14);
        systemHub.addSystem(AttackSystem.class, new AttackSystem(), 15);
        systemHub.addSystem(RemoveDeadObjectSystem.class, new RemoveDeadObjectSystem(), 16);
        systemHub.addSystem(UserInterfaceSystem.class, new UserInterfaceSystem(), 17);
        systemHub.addSystem(CustomRenderSystem.class, new CustomRenderSystem(), 18);
        systemHub.addSystem(GameSaverSystem.class, new GameSaverSystem(), 19);
    }

    private void interactionSetup() {
        PlayerEntity player = (PlayerEntity) EntityHub.getInstance().getEntitiesWithComponent(PlayerComponent.class).getFirst();
        InteractiveComponent playerInteractiveComponent = player.getComponent(InteractiveComponent.class);

        UtilityFunctions.setUpMovement(playerInteractiveComponent, player);
        UtilityFunctions.showSettingsMenu(playerInteractiveComponent);

        playerInteractiveComponent.mapInput(MouseButton.PRIMARY, PlayerStats.meleeCooldown, () -> {
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

            boolean isInvalidDirection = false;

            if (angleDeg >= 337.5 || angleDeg < 22.5) {
                directionX = 1;
                directionY = 0;   // E
                if (direction == Direction.LEFT || direction == Direction.UP || direction == Direction.DOWN)
                    isInvalidDirection = true;
            } else if (angleDeg < 67.5) {
                directionX = 1;
                directionY = -1;  // NE
                if (direction == Direction.LEFT || direction == Direction.DOWN) isInvalidDirection = true;
            } else if (angleDeg < 112.5) {
                directionX = 0;
                directionY = -1;  // N
                if (direction != Direction.UP && direction != Direction.ALL) isInvalidDirection = true;
            } else if (angleDeg < 157.5) {
                directionX = -1;
                directionY = -1; // NW
                if (direction == Direction.RIGHT || direction == Direction.DOWN) isInvalidDirection = true;
            } else if (angleDeg < 202.5) {
                directionX = -1;
                directionY = 0;  // W
                if (direction == Direction.RIGHT) isInvalidDirection = true;
            } else if (angleDeg < 247.5) {
                directionX = -1;
                directionY = 1;  // SW
                if (direction == Direction.RIGHT || direction == Direction.UP) isInvalidDirection = true;
            } else if (angleDeg < 292.5) {
                directionX = 0;
                directionY = 1;   // S
                if (direction != Direction.DOWN && direction != Direction.ALL) isInvalidDirection = true;
            } else {
                directionX = 1;
                directionY = 1;   // SE
                if (direction == Direction.LEFT || direction == Direction.UP) isInvalidDirection = true;
            }

            if (isInvalidDirection) {
                playerInteractiveComponent.getLastTimeCalled().put(
                        new Tuple<>(null, MouseButton.PRIMARY),
                        new Tuple<>(System.currentTimeMillis(), 20L)
                );
                return;
            } else {
                playerInteractiveComponent.getLastTimeCalled().put(new Tuple<>(null, MouseButton.PRIMARY), new Tuple<>(System.currentTimeMillis(), PlayerStats.meleeCooldown));
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


        playerInteractiveComponent.mapInput(MouseButton.SECONDARY, PlayerStats.rangedCooldown, () -> {
            boolean isInvalidDirection = false;
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
                if (direction == Direction.LEFT || direction == Direction.UP || direction == Direction.DOWN) {
                    isInvalidDirection = true;
                }
            } else if (angleDeg < 67.5) {
                if (direction == Direction.LEFT || direction == Direction.DOWN) {
                    isInvalidDirection = true;
                }
            } else if (angleDeg < 112.5) {
                if (direction != Direction.UP && direction != Direction.ALL) {
                    isInvalidDirection = true;
                }
            } else if (angleDeg < 157.5) {
                if (direction == Direction.RIGHT || direction == Direction.DOWN) {
                    isInvalidDirection = true;
                }
            } else if (angleDeg < 202.5) {
                if (direction == Direction.RIGHT) {
                    isInvalidDirection = true;
                }
            } else if (angleDeg < 247.5) {
                if (direction == Direction.RIGHT || direction == Direction.UP) {
                    isInvalidDirection = true;
                }
            } else if (angleDeg < 292.5) {
                if (direction != Direction.DOWN && direction != Direction.ALL) {
                    isInvalidDirection = true;
                }
            } else {
                if (direction == Direction.LEFT || direction == Direction.UP) {
                    isInvalidDirection = true;
                }
            }

            if (isInvalidDirection) {
                playerInteractiveComponent.getLastTimeCalled().put(
                        new Tuple<>(null, MouseButton.SECONDARY),
                        new Tuple<>(System.currentTimeMillis(), 20L)
                );
                return;
            } else {
                playerInteractiveComponent.getLastTimeCalled().put(new Tuple<>(null, MouseButton.SECONDARY), new Tuple<>(System.currentTimeMillis(), PlayerStats.rangedCooldown));
            }

            double length = Math.sqrt(dx * dx + dy * dy);
            double speed = 1500 * Time.getInstance().getDeltaTime();
            Vector throwDirection = new Vector((dx / length) * speed, (dy / length) * speed);

            new SnowBallEntity(playerX, playerY, MapConfig.scaledTileSize / 5, MapConfig.scaledTileSize / 5, throwDirection);
            player.getComponent(VelocityComponent.class).stopMovement();
            player.getComponent(AccelerationComponent.class).stopMovement();
        });
    }

    @Override
    public void breakdown() {
        UtilityFunctions.defaultBreakdownMethod();
    }
}
