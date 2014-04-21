package source;

import com.badlogic.gdx.scenes.scene2d.Actor;
import scene2d.*;

/** A Basic Main Menu for the Game
 * <p>
 * The Main Menu Displays all the scenes to which the game can switch too. Based on which button or
 * image or widget pressed the scene can be switched. 
 * <p>
 * @author pyros2097 */
public class Menu extends Scene {

    public Menu(){
        log("Hello World");
        log("new dat");
        showToast("Toasting", 2f);
    }

    @Override
    public void onClick(Actor actor){
        if(actor.getName().equals("Play"))
           setScene("Game");
        if(actor.getName().equals("Play3d"))
           setScene("Game3d");
        if(actor.getName().equals("Options"))
           setScene("Options");
        if(actor.getName().equals("Exit"))
           showMessageDialog("Exit", "    Are you sure you want \n    to Quit the Game   ");
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
