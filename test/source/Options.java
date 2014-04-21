package source;

import com.badlogic.gdx.scenes.scene2d.Actor;
import scene2d.*;

/** A Basic Main Menu for the Game
 * <p>
 * The Main Menu Displays all the scenes to which the game can switch too. Based on which button or
 * image or widget pressed the scene can be switched. 
 * <p>
 * @author pyros2097 */
public class Options extends Scene {

    public Options(){
    }

    @Override
    public void onClick(Actor actor){
        if(actor.getName().equals("Back"))
           setScene("Menu");
    }

    @Override
    public void onTouchDown(Actor actor){
    }

    @Override
    public void onTouchUp(){}

     @Override
    public void onDragged(){}

    @Override
    public void onGesture(GestureType type){
        if(type == GestureType.Up)
            log("Up");
        else if(type == GestureType.Down)
            log("Down");
    }

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
