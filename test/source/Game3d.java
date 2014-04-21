package source;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.VertexAttributes.Usage;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import scene3d.Actor3d;
import scene3d.Camera3d;
import scene3d.actions.Actions3d;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.Input.Keys;
import scene2d.*;

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
       knight = Asset.loadModel("knight");
       knight.setPosition(-20f, 18f, 0f);
       knight.setPitch(-90f);
       addActor3d(floor);
       addActor3d(skydome);
       addActor3d(ship);
       addActor3d(knight);
       stage3d.getCamera().position.set(knight.getX()+ 13f, knight.getY() + 24f, knight.getZ() + 45f);
       //Camera3d.followOffset(20f, 20f, -20f);
      // Camera3d.followActor3d(knight, false);
    }

    float angle, angle2;
    @Override
    public void act(float delta){
        super.act(delta);
        angle = MathUtils.cosDeg(knight.getYaw() - 90); //90 degrees is correction factor
        angle2 = -MathUtils.sinDeg(knight.getYaw() - 90);
        if (upKey) {
            knight.addAction3d(Actions3d.moveBy(angle, 0f, angle2, 1f));
            stage3d.getCamera().translate(angle, 0f, angle2);
        } 
        else if (downKey) {
            knight.addAction3d(Actions3d.moveBy(-angle, 0f, -angle2, 1f));
            stage3d.getCamera().translate(angle, 0f, angle2);
        }
        else if (rightKey) {
            knight.rotateYaw(-2f);
            if(stage3d.getCamera().direction.z > -0.76f)
                Camera3d.rotateBy(-2f, 0f, 0f, 0f);
        } 
        else if (leftKey) {
            knight.rotateYaw(2f);
            if(stage3d.getCamera().direction.z > -0.76f)
                Camera3d.rotateBy(-2f, 0f, 0f, 0f);
        } 
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
    
    boolean rightKey, leftKey, upKey, downKey, spaceKey;
    @Override
    public void onKeyUp(int keycode){
        if (keycode == Keys.LEFT) leftKey = false;
        if (keycode == Keys.RIGHT) rightKey = false;
        if (keycode == Keys.UP) upKey = false;
        if (keycode == Keys.DOWN) downKey = false;
        if (keycode == Keys.SPACE) spaceKey = false;
    }
    
    @Override
    public void onKeyDown(int keycode){
        if (keycode == Keys.LEFT) leftKey = true;
        if (keycode == Keys.RIGHT) rightKey = true;
        if (keycode == Keys.UP) upKey = true;
        if (keycode == Keys.DOWN) downKey = true;
        if (keycode == Keys.SPACE) spaceKey = true;
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