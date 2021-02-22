package inf112.skeleton.app;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import inf112.skeleton.app.map.Tiles;

public class Main {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration cfg = new Lwjgl3ApplicationConfiguration();
        cfg.setTitle("ROBORALLY");
        cfg.setWindowedMode(1000,1000);

        System.out.println(Tiles.valueOf(102));

        Game game = new Game();
        new Lwjgl3Application(game, cfg);

    }
}