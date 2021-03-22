package inf112.skeleton.app.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.app.assetManager.Assets;
import inf112.skeleton.app.game.CompleteRegisterPhase;
import inf112.skeleton.app.game.MainGame;
import inf112.skeleton.app.map.Board;
import inf112.skeleton.app.objects.Actors.Player;
import inf112.skeleton.app.objects.Actors.SimpleRobot;
import inf112.skeleton.app.screens.cardsUI.CardUI;
import static com.badlogic.gdx.Gdx.gl;
import static inf112.skeleton.app.game.MainGame.gameBoard;
import static java.lang.Math.round;
import static inf112.skeleton.app.game.MainGame.robots;

/**
 * Creates a game screen to be displayed while playing the game
 */
public class GameScreen extends InputAdapter implements Screen {

    private SpriteBatch batch;
    private BitmapFont font;

    private TiledMap map;

    // Layers on the map
    public TiledMapTileLayer tilePlayer;


    MainGame mainGame;
    private OrthogonalTiledMapRenderer mapRenderer;
    private OrthographicCamera gameCamera, uiCamera;
    CardUI cardui;

    private int viewPortWidth, viewPortHeight;

    Stage stage;
    Stage uiStage;
    FitViewport viewPort;
    private boolean debugMode;
    Player player;

    int width;
    int height;

    int menuHeight = (int) round(Gdx.graphics.getHeight() * 0.2);
    private RoboRally switcher;

    static Board board;


    public GameScreen(RoboRally switcher, Stage stage, FitViewport viewPort, boolean debugMode) {


        this.switcher = switcher;
        this.stage = stage;
        this.viewPort = viewPort;
        this.debugMode = debugMode;
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();

        batch = new SpriteBatch();
        font = new BitmapFont();
        font.setColor(Color.RED);


        // Load map and get board data
        map = new TmxMapLoader().load("Maps/Chess.tmx"); // Get map file
        //this.board = new Board(map); // Get map objects
        mainGame.setup(map);
        this.board = MainGame.gameBoard;
        //Set viewPort dimensions to dimensions of board
        viewPortHeight = (int) board.getBoardDimensions().y;
        viewPortWidth = (int) board.getBoardDimensions().x;

        uiStage = new Stage(new StretchViewport(viewPortWidth, viewPortHeight));

        // Make camera
        gameCamera = new OrthographicCamera();
        uiCamera   = new OrthographicCamera();
        // Set camera to correct view settings, making room for dashboard.
        gameCamera.setToOrtho(false, viewPortWidth, viewPortHeight + 4);  // Set mode. +4, to include room for dashboard.
        uiCamera.setToOrtho(false, viewPortWidth, viewPortHeight);

        // Set camera, but does not scale with the fit viewport
        //gameCamera.position.y = initialCameraY;
        gameCamera.update();
        uiCamera.update();

        uiStage.getViewport().setCamera(uiCamera);
        mapRenderer = new OrthogonalTiledMapRenderer(map,(float) 1/300);  // Render map
        mapRenderer.setView(gameCamera); // Attach camera to map

        //Handling player1
        tilePlayer = (TiledMapTileLayer) map.getLayers().get("Player");

        //MainGame.gameLoop();
    }

    /**
     * Method that performs action when key pressed is released
     * @param keycode: number id of pressed key
     */
    @Override
    public boolean keyUp(int keycode) {
        Player player = mainGame.robots.get(0);
        //Player playerTest = (Player) game.robots.get(0);
        //playerTest.moveRobotWASD(keycode);

        //Debug: Used to trigger a game phase
        if (keycode == Input.Keys.M) {
            TiledMapTileLayer playerTile = (TiledMapTileLayer) gameBoard.getMap().getLayers().get("Player");
            playerTile.setCell((int) player.getPosition().x, (int) player.getPosition().y, new TiledMapTileLayer.Cell()); // Clear previous robot image

            CompleteRegisterPhase phase = new CompleteRegisterPhase();
            phase.run();

            System.out.println("CompleteRegisterPhase is running.");
            System.out.println("Damage tokens: " + player.getProgramSheet().getDamage());
            System.out.println("Flags: " + player.getProgramSheet().getNumberOfFlags());
            System.out.println("Position: " + player.getPosition() + "\n");

            return true;
        }

        player.moveRobotWASD(keycode);
        return true;
    }

    /**
     * Change camera location based on WASD keystrokes
     */
    public void moveCamera(int keycode) {

        if(keycode == Input.Keys.LEFT)
            gameCamera.translate(-32,0);
        if(keycode == Input.Keys.RIGHT)
            gameCamera.translate(32,0);
        if(keycode == Input.Keys.UP)
            gameCamera.translate(0,32);
        if(keycode == Input.Keys.DOWN)
            gameCamera.translate(0,-32);
        if(keycode == Input.Keys.NUM_1)
            map.getLayers().get(0).setVisible(!map.getLayers().get(0).isVisible());
        if(keycode == Input.Keys.NUM_2)
            map.getLayers().get(1).setVisible(!map.getLayers().get(1).isVisible());
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }

    @Override
    public void render(float v) {
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);


        // RENDER GAME //
        Gdx.gl.glViewport( 0, menuHeight, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        gameCamera.update();
        uiCamera.update();

        //cardui.renderPlayer(tilePlayer); //TODO: Discuss rendering of robots.

        //Render all robot on the board, at their new position.
        for (SimpleRobot robot : robots) {
            tilePlayer.setCell((int) robot.getPosition().x, (int) robot.getPosition().y, robot.getPlayerCell());
        }

        mapRenderer.render();

        // Draw card visuals //
        Gdx.gl.glViewport( 0,0, Gdx.graphics.getWidth(),  menuHeight); // Set card deck menu height
        uiStage.act();
        uiStage.draw();
    }

    @Override
    public void show() {

        cardui = new CardUI(this, mainGame);
        cardui.setUpCards(0,0); // Generate buttons and listeners for actions

        if (debugMode) {
            Gdx.input.setInputProcessor(this);
        }
        else {

            Gdx.input.setInputProcessor(uiStage); // Set input to Card UI
        }
    }

    public void setGame(MainGame mainGame) {
        this.mainGame = mainGame;
    }


    @Override
    public void resize(int width, int height) { stage.getViewport().update(width, height, true); }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    public Stage getUIStage() {return this.uiStage; }
    public Stage getStage()   {return this.stage;   }

}