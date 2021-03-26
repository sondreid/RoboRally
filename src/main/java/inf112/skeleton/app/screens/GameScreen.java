package inf112.skeleton.app.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.FitViewport;
import inf112.skeleton.app.RoboRally;
import inf112.skeleton.app.game.CompleteRegisterPhase;
import inf112.skeleton.app.game.GameLoopEventHandler;
import inf112.skeleton.app.game.MainGame;
import inf112.skeleton.app.map.Board;
import inf112.skeleton.app.multiplayer.RRClient;
import inf112.skeleton.app.multiplayer.RRServer;
import inf112.skeleton.app.objects.Actors.Player;
import inf112.skeleton.app.screens.cardsUI.CardUI;

import com.esotericsoftware.minlog.Log;

import java.io.IOException;


import static com.badlogic.gdx.Gdx.gl;
import static inf112.skeleton.app.game.MainGame.gameBoard;
import static java.lang.Math.round;
import static inf112.skeleton.app.game.MainGame.robots;

/**
 * Creates a game screen to be displayed while playing the game
 */
public class GameScreen extends InputAdapter implements Screen {

    private final SpriteBatch batch;
    private final BitmapFont font;

    private final TiledMap map;

    // Layers on the map
    public TiledMapTileLayer tilePlayer;


    MainGame mainGame;
    private GameLoopEventHandler gameLoopEventHandler; //Handles redrawing of players, win & lose condition.
    private final OrthogonalTiledMapRenderer mapRenderer;
    private final OrthographicCamera gameCamera, uiCamera;
    private CardUI cardui;

    private final int viewPortWidth, viewPortHeight;

    Stage stage;
    Stage uiStage;
    FitViewport viewPort;
    private final boolean debugMode;
    Player player;
    MainGame game;

    int width;
    int height;

    int menuHeight = (int) round(Gdx.graphics.getHeight() * 0.2);
    private final RoboRally switcher;

    static Board board;

    private boolean hosting;
    private String ip;
    private RRServer server;
    private RRClient client;
    private String name;


    public GameScreen(RoboRally switcher, Stage stage, FitViewport viewPort, boolean debugMode) {
        this.switcher = switcher;
        this.stage = stage;
        this.viewPort = viewPort;

        this.debugMode = debugMode;
        this.width = Gdx.graphics.getWidth();
        this.height = Gdx.graphics.getHeight();

        this.batch = new SpriteBatch();
        this.font = new BitmapFont();
        this.font.setColor(Color.RED);



        // Load map and get board data
        this.map = new TmxMapLoader().load("Maps/Chess.tmx"); // Get map file
        //this.board = new Board(map); // Get map objects
        mainGame.setup(map);
        this.board = MainGame.gameBoard;
        //Set viewPort dimensions to dimensions of board
        this.viewPortHeight = (int) board.getBoardDimensions().y;
        this.viewPortWidth = (int) board.getBoardDimensions().x;

        this.uiStage = new Stage(new FitViewport(viewPortWidth, viewPortHeight-4));

        // Make camera
        this.gameCamera = new OrthographicCamera();
        this.uiCamera   = new OrthographicCamera();
        // Set camera to correct view settings, making room for dashboard.
        this.gameCamera.setToOrtho(false, viewPortWidth, viewPortHeight + 4);  // Set mode. +4, to include room for dashboard.
        this.uiCamera.setToOrtho(false, viewPortWidth*2, viewPortHeight/2);

        // Set camera, but does not scale with the fit viewport
        //gameCamera.position.y = initialCameraY;
        this.gameCamera.update();
        this.uiCamera.update();

        this.uiStage.getViewport().setCamera(uiCamera);
        this.mapRenderer = new OrthogonalTiledMapRenderer(map,(float) 1/300);  // Render map
        this.mapRenderer.setView(gameCamera); // Attach camera to map

        //Handling player1
        this.tilePlayer = (TiledMapTileLayer) map.getLayers().get("Player");


        this.gameLoopEventHandler = new GameLoopEventHandler(switcher, tilePlayer); //Mostly for testing. Handles re-drawing of players at their new positions.

        //MainGame.gameLoop();
    }



    /**
     * Method that performs action when key pressed is released
     * @param keycode: number id of pressed key
     */
    @Override
    public boolean keyUp(int keycode) {
        robots.get(0).moveRobotWASD(keycode);
        return true;
    }

    /**
     * Change camera location based on WASD keystrokes
     */
    public void moveCamera(int keycode) {

        if(keycode == Input.Keys.LEFT)
            this.gameCamera.translate(-32,0);
        if(keycode == Input.Keys.RIGHT)
            this.gameCamera.translate(32,0);
        if(keycode == Input.Keys.UP)
            this.gameCamera.translate(0,32);
        if(keycode == Input.Keys.DOWN)
            this.gameCamera.translate(0,-32);
        if(keycode == Input.Keys.NUM_1)
            this.map.getLayers().get(0).setVisible(!map.getLayers().get(0).isVisible());
        if(keycode == Input.Keys.NUM_2)
            this.map.getLayers().get(1).setVisible(!map.getLayers().get(1).isVisible());
    }

    @Override
    public void dispose() {
        this.batch.dispose();
        this.font.dispose();
    }

    @Override
    public void render(float v) {
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT);

        // RENDER GAME //
        Gdx.gl.glViewport( 0, menuHeight, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        this.gameCamera.update();
        this.uiCamera.update();

        gameLoopEventHandler.run(); //Handles re-drawing of players.   | Changes texture if on pit or flag, only for testing

        mapRenderer.render();

        // Draw card visuals //
        Gdx.gl.glViewport( 0,0, Gdx.graphics.getWidth(),  menuHeight); // Set card deck menu height
        this.uiStage.act();
        this.uiStage.draw();
    }

    @Override
    public void show() {

        this.cardui = new CardUI(mainGame);
        uiStage.addActor(cardui.getTable());
        this.cardui.setUpCards((int) (uiCamera.viewportWidth) / 2, (int) (uiCamera.viewportHeight / 4), this); // Generate buttons and listeners for actions


        if (this.debugMode) {
            Gdx.input.setInputProcessor(this);
        } else {
            Gdx.input.setInputProcessor(this.uiStage); // Set input to Card UI
        }

        client = new RRClient(name);

        //TODO Bug : If singleplayer is selected, game still tries to connect to an ip. Should not do so.
        if (hosting != false) {
            Log.info("starting server");
            try {
                server = new RRServer();
                client.connect("localhost");
            } catch (IOException e) {
                e.printStackTrace();  //no idea what this does, haven't read the documentation on IOException
                Log.info("Unable to start server.");
                Gdx.app.exit();
            }
        } else {
            client.connect(ip);
        }
    }


    public void setGame(MainGame mainGame) {
        this.mainGame = mainGame;
    }


    @Override
    public void resize(int width, int height) {
        this.stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override

    public void hide() {
        client.ceaseClient();
        if (server != null) {
            server.ceaseServer();
        }
    }
    public Stage getUIStage() {
        return this.uiStage;
    }
    public Stage getStage() {
        return this.stage;
    }



    /**
     *
     * @param hosting
     * @param ip
     * @param name
     */
    public void setMultiPlayer(boolean hosting, String ip, String name) {
        this.hosting = hosting;
        this.name = name;
        if(!ip.isEmpty()) {
            this.ip = ip;
        } else {
            this.ip = "localhost";
        }
    }

}

