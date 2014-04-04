import com.badlogic.gdx.scenes.scene2d.Actor;

public class Game3d extends Scene {
    Actor3d ship;
    
    public Game3d(){
       ship = Asset.loadModel("ship");
       ship.scale(3f);
       Stage.addActor3d(ship);
    }

    @Override
    public void onClick(Actor actor){
        if(actor.getName().equals("Button1"))
           ship.addAction3d(Actions3d.moveBy(1f, 0f, 0f, 0.4f));
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
    public void onPause(){
    }
    
    @Override
    public void onResume(){
    }
        
    @Override
    public void onDispose(){
    }
}
