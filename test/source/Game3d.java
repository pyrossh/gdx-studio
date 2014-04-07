import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;

public class Game3d extends Scene {
    Actor3d ship;
    Actor3d skydome;
    Actor3d knight;
    Actor3d floor;
    ModelBuilder builder;
    
    public Game3d(){
       ship = Asset.loadModelObj("ship");//loads an obj model
       ship.scale(3f);
       skydome = Asset.loadModel("skydome"); //loads a g3db model
       builder = new ModelBuilder();
       builder.begin();
       MeshPartBuilder part = builder.part("floor", GL20.GL_TRIANGLES, Usage.Position | 
            Usage.TextureCoordinates | Usage.Normal, new Material());
       for (float x = -200f; x < 200f; x += 10f) {
           for (float z = -200f; z < 200f; z += 10f) {
               part.rect(x, 0, z + 10f, x + 10f, 0, z + 10f, x + 10f, 0, z, x, 0, z, 0, 1, 0);
           }
       }
       floor = new Actor3d(builder.end());
       floor.materials.get(0).set(TextureAttribute.createDiffuse(Asset.tex("concrete").getTexture()));
       Stage.addActor3d(floor);
       Stage.addActor3d(skydome);
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
