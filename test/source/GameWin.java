package source;

import com.badlogic.gdx.scenes.scene2d.Actor;
import scene2d.*;

/** A Basic Game Win Scene for the Game
 * <p>
 * The Game Scene displays that the player has won the game and then switches to Menu
 * <p>
 * @author pyros2097 */
public class GameWin extends Scene {

    public GameWin(){
       setSceneWithDelay("Menu", 3f);
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
    public void onKeyTyped(char key){
    }
    
    @Override
    public void onKeyUp(int keycode){
    }
    
    @Override
    public void onKeyDown(int keycode){
    }

    @Override
    public void onPause(){
    }
    
    @Override
    public void onResume(){
    }
        
    @Override
    public void onDispose(){
    }
}
