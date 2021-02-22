package inf112.skeleton.app.screens;
import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import inf112.skeleton.app.RoboRally;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import inf112.skeleton.app.buttons.PlayButton;
import inf112.skeleton.app.buttons.SimpleButton;

import static com.badlogic.gdx.Gdx.gl;

public class TitleScreen implements Screen {

    final RoboRally game;
    private Rectangle bucket;
    private OrthographicCamera camera;
    private Stage stage;

    float width;
    float height;

    public TitleScreen(final RoboRally game) {
        this.game = game;
        // create the camera and the SpriteBatch

    }

    @Override
    public void show() {
        width = Gdx.graphics.getWidth();
        height = Gdx.graphics.getHeight();

        ImageButton playButton = new PlayButton(width * 0.4F,height * 0.7F).getButton();
        stage = new Stage(new ScreenViewport());
        stage.addActor(playButton);

        playButton.addListener(new InputListener(){
            @Override
            public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new GameScreen(game));
            }
            @Override
            public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
                return true;
            }});

        Gdx.input.setInputProcessor(stage);

    }

    @Override
    public void render(float delta) {
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();
        game.font.draw(game.batch, "WELCOME TO ROBORALLY", width*0.34F, height * 0.75F);
        game.batch.end();
        stage.act();
        stage.draw();


    }

    @Override
    public void resize(int i, int i1) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }



    @Override
    public void dispose() {
        stage.dispose();

    }
}