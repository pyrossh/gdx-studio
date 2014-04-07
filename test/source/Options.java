import com.badlogic.gdx.scenes.scene2d.Actor;

/** A Basic Options Scene for the Game
 * <p>
 * The Options Scene displays the necessary settings which can be modified by the user like
 * musicOn, soundOn, panCamera, battleAnimations etc. The widgets once changed must persist
 * the changes to the preferences which can be accessed by Config.prefs
 * <p>
 * @author pyros2097 */
public class Options extends Scene {

    public Options(){
        Stage.log("Hello From Options");
    }

    @Override
    public void onClick(Actor actor){
        if(actor.getName().equals("Back"))
           Stage.setScene("Menu");
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
    public void onPause(){
    }
    
    @Override
    public void onResume(){
    }
        
    @Override
    public void onDispose(){
    }
}
