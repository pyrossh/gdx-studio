import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

/** A Basic Demo Game Scene
 * <p>
 * This Scene showcases all the different features of the inbuilt Stage framework
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
        //Stage.addActor(map);
        pauseImage = new Image(Asset.skin.getRegion("default"));
        pauseBtn = (Image) Stage.findActor("PauseButton");
        Stage.addHud(pauseBtn);
        Stage.enablePanning();
    }

    @Override
    public void onClick(Actor actor){
        if(actor.getName().equals("PauseButton")){
             Stage.log("Clicked");
             if(!Stage.pauseState){
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
        Stage.pauseState = true;
        pauseImage.setFillParent(true);
        pauseImage.setTouchable(Touchable.disabled);
        pauseImage.setColor(1, 1, 1, 0);
        pauseImage.addAction(Actions.alpha(0.6f, 0.7f, Interpolation.linear));
        pauseImage.setPosition(Stage.getCameraX() - Stage.targetWidth/2,
                Stage.getCameraY() - Stage.targetHeight/2);
        Stage.addActor(pauseImage);
        Stage.disablePanning();
    }

    @Override
    public void onResume() {
        Stage.pauseState = false;
        pauseImage.addAction(Actions.sequence(Actions.alpha(0f, 0.6f, Interpolation.linear),
            Actions.removeActor(pauseImage)));
        Stage.enablePanning();
    }

    @Override
    public void onDispose() {
        //Asset.unloadmap(1);
    }
}
