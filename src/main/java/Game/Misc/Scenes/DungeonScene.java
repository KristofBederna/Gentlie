package Game.Misc.Scenes;

import Game.Components.AttackBoxComponent;
import Game.Entities.Labels.EnterEnemyIslandLabel;
import Game.Entities.EntryEntity;
import Game.Entities.SnowBallEntity;
import Game.Misc.EventHandling.EventListeners.EnterEnemyIslandEventListener;
import Game.Misc.EventHandling.Events.EnterEnemyIslandEvent;
import Game.Systems.*;
import inf.elte.hu.gameengine_javafx.Components.Default.PositionComponent;
import inf.elte.hu.gameengine_javafx.Components.HitBoxComponents.HitBoxComponent;
import inf.elte.hu.gameengine_javafx.Components.InteractiveComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.AccelerationComponent;
import inf.elte.hu.gameengine_javafx.Components.PhysicsComponents.VelocityComponent;
import inf.elte.hu.gameengine_javafx.Components.PropertyComponents.*;
import inf.elte.hu.gameengine_javafx.Components.UIComponents.CheckBoxComponent;
import inf.elte.hu.gameengine_javafx.Components.UIComponents.ComboBoxComponent;
import inf.elte.hu.gameengine_javafx.Components.UIComponents.LabelComponent;
import inf.elte.hu.gameengine_javafx.Components.UIComponents.SliderComponent;
import inf.elte.hu.gameengine_javafx.Core.Architecture.Entity;
import inf.elte.hu.gameengine_javafx.Core.EntityHub;
import inf.elte.hu.gameengine_javafx.Core.ResourceHub;
import inf.elte.hu.gameengine_javafx.Core.SystemHub;
import inf.elte.hu.gameengine_javafx.Entities.CameraEntity;
import inf.elte.hu.gameengine_javafx.Entities.PlayerEntity;
import inf.elte.hu.gameengine_javafx.Entities.UIEntities.*;
import inf.elte.hu.gameengine_javafx.Entities.WorldEntity;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.ComplexShape;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Point;
import inf.elte.hu.gameengine_javafx.Maths.Geometry.Rectangle;
import inf.elte.hu.gameengine_javafx.Maths.Vector;
import inf.elte.hu.gameengine_javafx.Misc.Config;
import inf.elte.hu.gameengine_javafx.Misc.Direction;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.EventListeners.FullScreenToggleEventListener;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.EventListeners.ResolutionChangeEventListener;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.Events.FullScreenToggleEvent;
import inf.elte.hu.gameengine_javafx.Misc.EventHandling.Events.ResolutionChangeEvent;
import inf.elte.hu.gameengine_javafx.Misc.InputHandlers.MouseInputHandler;
import inf.elte.hu.gameengine_javafx.Misc.Layers.GameCanvas;
import inf.elte.hu.gameengine_javafx.Misc.Layers.uiRoot;
import inf.elte.hu.gameengine_javafx.Misc.Scenes.GameScene;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.GameLoopStartUp;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.ResourceStartUp;
import inf.elte.hu.gameengine_javafx.Misc.StartUpClasses.SystemStartUp;
import inf.elte.hu.gameengine_javafx.Misc.Time;
import inf.elte.hu.gameengine_javafx.Misc.Tuple;
import inf.elte.hu.gameengine_javafx.Systems.InputHandlingSystem;
import inf.elte.hu.gameengine_javafx.Systems.PathfindingSystem;
import inf.elte.hu.gameengine_javafx.Systems.PhysicsSystems.CollisionSystem;
import inf.elte.hu.gameengine_javafx.Systems.PhysicsSystems.MovementDeterminerSystem;
import inf.elte.hu.gameengine_javafx.Systems.PhysicsSystems.MovementSystem;
import inf.elte.hu.gameengine_javafx.Systems.RenderingSystems.*;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.ResourceSystem;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.SceneManagementSystem;
import inf.elte.hu.gameengine_javafx.Systems.ResourceSystems.SoundSystem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

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

        new PlayerEntity(Config.scaledTileSize + Config.scaledTileSize / 2, Config.scaledTileSize + Config.scaledTileSize / 2, "idle", "/assets/images/Gentlie/Gentlie_Down_Idle.png", Config.scaledTileSize * 0.8 * 0.55, Config.scaledTileSize * 0.8);

        new EntryEntity(0, Config.scaledTileSize, Config.scaledTileSize, Config.scaledTileSize * 3, new EnterEnemyIslandEvent(new Point(4 * 150, 2 * 150 + 150 * 0.25 - 1)), new EnterEnemyIslandEventListener());
        EnterEnemyIslandLabel enterEnemyIslandLabel = new EnterEnemyIslandLabel("Press 'E' to leave dungeon", Config.scaledTileSize, 3 * Config.scaledTileSize, Config.scaledTileSize * 0.75, Config.scaledTileSize * 0.75);
        enterEnemyIslandLabel.removeFromUI();
        enterEnemyIslandLabel.getComponent(LabelComponent.class).getUIElement().setTextAlignment(TextAlignment.CENTER);


        CameraEntity.getInstance(1920, 1080, 32 * Config.scaledTileSize, 32 * Config.scaledTileSize);
        CameraEntity.getInstance().attachTo(EntityHub.getInstance().getEntitiesWithComponent(PlayerComponent.class).getFirst());

        new SystemStartUp(this::SystemStartUp);

        interactionSetup();

        GameLoopStartUp.restartLoop();
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
        PlayerEntity player = (PlayerEntity) EntityHub.getInstance().getEntitiesWithComponent(PlayerComponent.class).getFirst();
        InteractiveComponent playerInteractiveComponent = player.getComponent(InteractiveComponent.class);
        playerInteractiveComponent.mapInput(KeyCode.UP, 10, () -> moveUp(player), () -> counterUp(player));
        playerInteractiveComponent.mapInput(KeyCode.DOWN, 10, () -> moveDown(player), () -> counterDown(player));
        playerInteractiveComponent.mapInput(KeyCode.LEFT, 10, () -> moveLeft(player), () -> counterLeft(player));
        playerInteractiveComponent.mapInput(KeyCode.RIGHT, 10, () -> moveRight(player), () -> counterRight(player));
        playerInteractiveComponent.mapInput(KeyCode.ENTER, 100, () -> {
            player.getComponent(PositionComponent.class).setLocalX(MouseInputHandler.getInstance().getMouseX(), player);
            player.getComponent(PositionComponent.class).setLocalY(MouseInputHandler.getInstance().getMouseY(), player);
        });
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

            int directionX = 0;
            int directionY = 0;

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
        playerInteractiveComponent.mapInput(KeyCode.ESCAPE, 100, () -> {
            Time.getInstance().setTimeScale(0.0);
            ButtonEntity start = new ButtonEntity("Back to main menu", Config.resolution.first()/2 - 50*Config.relativeWidthRatio, Config.resolution.second()/2 - 150*Config.relativeHeightRatio, 200*Config.relativeWidthRatio, 80*Config.relativeHeightRatio, () -> SystemHub.getInstance().getSystem(SceneManagementSystem.class).requestSceneChange(new MainScene(new BorderPane(), Config.resolution.first(), Config.resolution.second())));
            ButtonEntity settings = new ButtonEntity("Settings", Config.resolution.first()/2 - 50*Config.relativeWidthRatio, Config.resolution.second()/2 - 50*Config.relativeHeightRatio, 200*Config.relativeWidthRatio, 80*Config.relativeHeightRatio, () -> showSettings());
            ButtonEntity exit = new ButtonEntity("Back", Config.resolution.first()/2 - 50*Config.relativeWidthRatio, Config.resolution.second()/2 + 50*Config.relativeHeightRatio, 200*Config.relativeWidthRatio, 80*Config.relativeHeightRatio, () -> {
                uiRoot.getInstance().unloadAll();
                Time.getInstance().setTimeScale(1.0);
            });
        });
    }

    private void showSettings() {
        uiRoot.getInstance().unloadAll();
        LabelEntity label = new LabelEntity("Settings", Config.resolution.first()/2 - 20*Config.relativeWidthRatio, Config.resolution.second()/2 - 250*Config.relativeHeightRatio, 200*Config.relativeWidthRatio, 0);

        ButtonEntity back = new ButtonEntity("Back", Config.resolution.first()/2 - 50*Config.relativeWidthRatio, Config.resolution.second()/2 + 350*Config.relativeHeightRatio, 200*Config.relativeWidthRatio, 80*Config.relativeHeightRatio, () -> {
            uiRoot.getInstance().unloadAll();
            ButtonEntity start = new ButtonEntity("Back to main menu", Config.resolution.first()/2 - 50*Config.relativeWidthRatio, Config.resolution.second()/2 - 150*Config.relativeHeightRatio, 200*Config.relativeWidthRatio, 80*Config.relativeHeightRatio, () -> SystemHub.getInstance().getSystem(SceneManagementSystem.class).requestSceneChange(new MainScene(new BorderPane(), Config.resolution.first(), Config.resolution.second())));
            ButtonEntity settings = new ButtonEntity("Settings", Config.resolution.first()/2 - 50*Config.relativeWidthRatio, Config.resolution.second()/2 - 50*Config.relativeHeightRatio, 200*Config.relativeWidthRatio, 80*Config.relativeHeightRatio, () -> showSettings());
            ButtonEntity exit = new ButtonEntity("Back", Config.resolution.first()/2 - 50*Config.relativeWidthRatio, Config.resolution.second()/2 + 50*Config.relativeHeightRatio, 200*Config.relativeWidthRatio, 80*Config.relativeHeightRatio, () -> {
                uiRoot.getInstance().unloadAll();
                Time.getInstance().setTimeScale(1.0);
            });
        });

        LabelEntity soundLabel = new LabelEntity("Master volume: ", Config.resolution.first()/2 - 200*Config.relativeWidthRatio, Config.resolution.second()/2 - 150*Config.relativeHeightRatio, 200*Config.relativeWidthRatio, 0);
        SliderEntity sound = new SliderEntity(Config.resolution.first()/2 - 20*Config.relativeWidthRatio, Config.resolution.second()/2 - 150*Config.relativeHeightRatio, 200*Config.relativeWidthRatio, 0, 0.0f, 1.0f, Config.masterVolume);

        Slider slider2 = sound.getComponent(SliderComponent.class).getUIElement();
        slider2.valueProperty().addListener((observable, oldValue, newValue) -> {
            Config.masterVolume = newValue.floatValue();
        });

        LabelEntity musicLabel = new LabelEntity("Music volume: ", Config.resolution.first()/2 - 200*Config.relativeWidthRatio, Config.resolution.second()/2 - 100*Config.relativeHeightRatio, 200*Config.relativeWidthRatio, 0);
        SliderEntity music = new SliderEntity(Config.resolution.first()/2 - 20*Config.relativeWidthRatio, Config.resolution.second()/2 - 100*Config.relativeHeightRatio, 200*Config.relativeWidthRatio, 0, 0.0f, 1.0f, Config.backgroundMusicVolume);

        Slider slider = music.getComponent(SliderComponent.class).getUIElement();
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            Config.backgroundMusicVolume = newValue.floatValue();
        });

        ObservableList<Tuple<Double, Double>> options = FXCollections.observableArrayList();
        options.add(new Tuple<>(600.0, 800.0));
        options.add(new Tuple<>(1024.0, 768.0));
        options.add(new Tuple<>(1280.0, 720.0));
        options.add(new Tuple<>(1440.0, 900.0));
        options.add(new Tuple<>(1920.0, 1080.0));
        options.add(new Tuple<>(2560.0, 1440.0));
        LabelEntity resolutionLabel = new LabelEntity("Resolution: ", Config.resolution.first()/2 - 200*Config.relativeWidthRatio, Config.resolution.second()/2 - 50*Config.relativeHeightRatio, 200*Config.relativeWidthRatio, 0);
        ComboBoxEntity<Tuple<Double,Double>> resolution = new ComboBoxEntity<>(new ComboBoxComponent<>(Config.resolution.first()/2 - 20*Config.relativeWidthRatio, Config.resolution.second()/2 - 50*Config.relativeHeightRatio, 200*Config.relativeWidthRatio, 0, options));

        ComboBox<Tuple<Double, Double>> comboBox = (ComboBox<Tuple<Double, Double>>) resolution.getComponent(ComboBoxComponent.class).getUIElement();

        comboBox.setValue(Config.resolution);

        comboBox.setCellFactory(cb -> new ListCell<>() {
            @Override
            protected void updateItem(Tuple<Double, Double> item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.first().intValue() + "x" + item.second().intValue());
                }
            }
        });

        comboBox.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Tuple<Double, Double> item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.first().intValue() + "x" + item.second().intValue());
                }
            }
        });

        comboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                Config.resolution = new Tuple<>(newVal.first(), newVal.second());
                new ResolutionChangeEventListener().onEvent(new ResolutionChangeEvent(newVal.first(), newVal.second()));
            }
        });

        LabelEntity fullScreenLabel = new LabelEntity("Fullscreen mode: ", Config.resolution.first()/2 - 200*Config.relativeWidthRatio, Config.resolution.second()/2, 200*Config.relativeWidthRatio, 0);
        CheckBoxEntity fullscreen = new CheckBoxEntity("",Config.resolution.first()/2 - 20*Config.relativeWidthRatio, Config.resolution.second()/2, 200*Config.relativeWidthRatio, 0);

        CheckBox checkBox = fullscreen.getComponent(CheckBoxComponent.class).getUIElement();

        checkBox.setSelected(Config.fullScreenMode);

        checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                Config.fullScreenMode = newVal;

                Stage stage = (Stage) checkBox.getScene().getWindow();

                if (stage != null) {
                    new FullScreenToggleEventListener().onEvent(new FullScreenToggleEvent(stage));
                } else {
                    System.err.println("Stage is null");
                }
            }
        });
    }
    private void moveUp(Entity e) {
        double dy = -4 * Time.getInstance().getDeltaTime();
        e.getComponent(AccelerationComponent.class).getAcceleration().setDy(dy);
    }

    private void moveDown(Entity e) {
        double dy = 4 * Time.getInstance().getDeltaTime();
        e.getComponent(AccelerationComponent.class).getAcceleration().setDy(dy);
    }

    private void moveLeft(Entity e) {
        double dx = -4 * Time.getInstance().getDeltaTime();
        e.getComponent(AccelerationComponent.class).getAcceleration().setDx(dx);
    }

    private void moveRight(Entity e) {
        double dx = 4 * Time.getInstance().getDeltaTime();
        e.getComponent(AccelerationComponent.class).getAcceleration().setDx(dx);
    }

    private void counterUp(Entity e) {
        e.getComponent(AccelerationComponent.class).getAcceleration().setDy(0);
    }

    private void counterDown(Entity e) {
        e.getComponent(AccelerationComponent.class).getAcceleration().setDy(0);
    }

    private void counterRight(Entity e) {
        e.getComponent(AccelerationComponent.class).getAcceleration().setDx(0);
    }

    private void counterLeft(Entity e) {
        e.getComponent(AccelerationComponent.class).getAcceleration().setDx(0);
    }

    @Override
    public void breakdown() {
        EntityHub.getInstance().unloadAll();
        EntityHub.resetInstance();
        CameraEntity.resetInstance();
        WorldEntity.resetInstance();
        SystemHub.getInstance().shutDownSystems();
        GameLoopStartUp.stopGameLoop();
        ResourceHub.getInstance().clearResources();
        ResourceHub.resetInstance();
        uiRoot.getInstance().unloadAll();
        GameCanvas.getInstance().getGraphicsContext2D().clearRect(0, 0, GameCanvas.getInstance().getWidth(), GameCanvas.getInstance().getHeight());
        System.gc();
    }
}
