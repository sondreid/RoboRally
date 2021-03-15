package inf112.skeleton.app.cards;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import inf112.skeleton.app.assetManager.Assets;
import inf112.skeleton.app.objects.Actors.SimpleRobot;

/**
 * Abstract simple class.
 */
public abstract class SimpleProgramCard implements IProgramCard {

    private ImageButton cardButton;
    private final int priority;
    private CardType cardtype;
    private Texture texture;


    SimpleProgramCard(int priority, CardType cardType) {
        this.priority =  priority;
        this.cardtype = cardType;
        getTexture();
        Drawable drawable = new TextureRegionDrawable(new TextureRegion(texture));
        cardButton = new ImageButton(drawable);


    }


    public int getPriority() {
        return this.priority;
    }

    @Override
    public CardType getType() {return this.cardtype;}


    public void setSize(int w, int h) {this.cardButton.setSize(w,h);}

    public void setPosition(float wPos, float hPos) { cardButton.setPosition(wPos, hPos); }

    public ImageButton getCard() { return cardButton; }


    public abstract void action(TiledMapTileLayer tilePlayer, SimpleRobot robot);



    private void getTexture() {
        switch (cardtype) {
            case MOVE1: texture = Assets.manager.get(Assets.Move1Card);  break;
            case MOVE2: texture = Assets.manager.get(Assets.Move2Card); break;
            case MOVE3: texture = Assets.manager.get(Assets.Move3Card); break;
            case ROTATELEFT: texture = Assets.manager.get(Assets.RotateLeftCard); break;
            case ROTATERIGHT: texture = Assets.manager.get(Assets.RotateRightCard); break;
            case UTURN: texture = Assets.manager.get(Assets.UTurnCard); break;
            default: throw new IllegalArgumentException("Expected enum cardtype of type (Move1, move2, move3, rotateleft, rotateright, uturn). Got :" + cardtype);
        }
    }

}
