package source;

import com.badlogic.gdx.scenes.scene2d.Actor;

import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import scene2d.*;

/** A Basic Demo Game Scene
 * <p>
 * This Scene showcases all the different features of the inbuilt Scene framework
 * <p>
 * @author pyros2097 */
public class BasicDemo extends Scene {
    Image pauseImage;
    Image pauseBtn;
    Map map;
    
    public BasicDemo(){
        //map = new Map(1, 24);
        //map.loadLayer(0);
        //map.loadLayer(1);
        pauseImage = new Image(Asset.skin.getRegion("default"));
        pauseBtn = (Image) findActor("PauseButton");
        Camera.addHud("PauseButton");
        Camera.addHud("Label1");
        Camera.addHud("Top");
        Camera.addHud("Bot");
        Camera.addHud("Left");
        Camera.addHud("Right");
        Camera.usePan = true;
    }

    @Override
    public void onClick(Actor actor){
        if(actor.getName().equals("PauseButton")){
             log("Clicked");
             if(!pauseState){
                 onPause();
             }
             else{
                 onResume();
             }
        }
    }

    @Override
    public void onTouchDown(Actor actor){}

    @Override
    public void onTouchUp(){}

    @Override
    public void onDragged(){}

    @Override
    public void onGesture(GestureType type){}

    @Override
    public void onKeyTyped(char key){
    }
    
    @Override
    public void onKeyUp(int keycode){
    }
    
    @Override
    public void onKeyDown(int keycode){
    }

    @Override
    public void onPause() {
        pauseState = true;
        pauseImage.setFillParent(true);
        pauseImage.setTouchable(Touchable.disabled);
        pauseImage.setColor(1, 1, 1, 0);
        pauseImage.addAction(Actions.alpha(0.6f, 0.7f, Interpolation.linear));
        pauseImage.setPosition(Camera.getX() - targetWidth/2,
                Camera.getY() - targetHeight/2);
        addActor(pauseImage);
        Camera.usePan = false;
    }

    @Override
    public void onResume() {
        pauseState = false;
        pauseImage.addAction(Actions.sequence(Actions.alpha(0f, 0.6f, Interpolation.linear),
            Actions.removeActor(pauseImage)));
        Camera.usePan = true;
    }

    @Override
    public void onDispose() {
        //Asset.unloadmap(1);
    }
}
