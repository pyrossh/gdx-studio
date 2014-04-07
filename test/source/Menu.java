import com.badlogic.gdx.scenes.scene2d.Actor;

/** A Basic Main Menu for the Game
 * <p>
 * The Main Menu Displays all the scenes to which the game can switch too. Based on which button or
 * image or widget pressed the scene can be switched.
 * <p>
 * @author pyros2097 */
public class Menu extends Scene {

    public Menu(){
        Stage.log("Hello World");
        Stage.log("new dat");
        Stage.showToast("Toasting", 2f);
    }

    @Override
    public void onClick(Actor actor){
        if(actor.getName().equals("Play"))
           Stage.setScene("Game");
        if(actor.getName().equals("Play3d"))
           Stage.setScene("Game3d");
        if(actor.getName().equals("Options"))
           Stage.setScene("Options");
        if(actor.getName().equals("Exit"))
            Stage.showMessageDialog("Exit", "    Are you sure you want \n    to Quit the Game   ");
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
            Stage.log("Up");
        else if(type == GestureType.Down)
            Stage.log("Down");
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
