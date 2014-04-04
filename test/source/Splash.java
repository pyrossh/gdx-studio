import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/** A Basic SplashScreen for the Game
 * <p>
 * The SplashScene loads all the splash assets to be shown directly from the file system
 * manually, so all the loaded assets have to be disposed manually because we are
 * most probably going to use them only once throughout the game.
 * <p>
 * @author pyros2097 */
public class Splash extends Scene {
    Texture bg1, bg2;
    String loadingText = "Loading";
    int textlen = 7;
    Label loadingLabel;

    public Splash(){
        Asset.skin = new Skin(Gdx.files.internal("skin/uiskin.json"));
        bg1 = new Texture("splash/libgdx.png");
        bg2 = new  Texture("splash/libgdx.png");
        
        final Image imgbg1 = new Image(bg1);
        final Image imgbg2 = new Image(bg1);
        imgbg1.setFillParent(true);
        imgbg2.setFillParent(true);
        loadingLabel = new Label(loadingText, Asset.skin);
        loadingLabel.setColor(Color.BLACK);
        loadingLabel.setFontScale(3f);
        Stage.addActor(imgbg1);
        Stage.addActor(imgbg2);
        Stage.addActor(loadingLabel, Stage.targetWidth/2, 23f);
        Stage.addAction(Actions.sequence(Actions.delay(4f), Actions.removeActor(imgbg1)
        , Actions.delay(4f), Actions.removeActor(imgbg2)));
        
        loadingLabel.addAction(Actions.forever(Actions.forever(Actions.sequence(
            Actions.delay(0.5f), new Action(){
           @Override
           public boolean act(float delta){
             if(loadingLabel.getText().length() == textlen){
                if(textlen == 11){
                    loadingText = "Loading";
                    loadingLabel.setText(loadingText);
                    textlen = 7;
                }
                loadingLabel.setText(loadingText);
                loadingText+= ".";
                textlen += 1;
            } 
            return false;
        }
        }))));
    }

    @Override
    public void onClick(Actor actor){}

    @Override
    public void onTouchDown(Actor actor){}

    @Override
    public void onTouchUp(){}

     @Override
    public void onDragged(){}

    @Override
    public void onGesture(GestureType type){}

     @Override
    public void onPause(){
    }
    
    @Override
    public void onResume(){
    }

    @Override
    public void onDispose() {
        bg1.dispose();
        bg2.dispose();
    }
}
