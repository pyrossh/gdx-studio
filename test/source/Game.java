package source;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import scene2d.*;

/** A Basic Game Scene for the Game
 * <p>
 * The Game Scene consists of all the characters, Huds, controllers for the game. All the logic for
 * the game is written in the public void act(float delta) method.
 * In this method we ckeck all the
 * conditions for the player to win the level and changes scenes accordingly.
 * <p>
 * @author pyros2097 */
public class Game extends Scene {
    Label timer;
    float startTime;
    float diff;
    ImageJson player, enemy;
    float playerSpeed = 3f;
    boolean touchedDown = false;
    String btnName = "";
    
    public Game(){
        Camera.addHud("Up");
        Camera.addHud("Right");
        Camera.addHud("Left");
        Camera.addHud("Down");
        Camera.addHud("Timer");
        Camera.addHud("Label3");
        Camera.addHud("Fire");

        Camera.setFollowSpeed(1f);
        player = (ImageJson) findActor("Player");
        enemy = (ImageJson) findActor("Enemy");
        Camera.followActorContinuously(player);
        timer = (Label)findActor("Timer");
        startTime = gameUptime;
    }

    @Override
    public void onTouchDown(Actor actor){
        log(actor.getName());
        touchedDown = true;
        btnName = actor.getName();
    }

    @Override
    public void onClick(Actor actor){
        if(actor.getName().equals("Back"))
            setScene("Menu");
        if(actor.getName().equals("Fire"))
            doFire();
    }

    void doFire(){
        ImageJson bullet = new ImageJson("771");
        bullet.setPosition(player.getX() + 50, player.getY() + 50);
        bullet.setSize(25, 25);
        bullet.addAction(Actions.sequence(Actions.moveBy(500, 0, 1f), Actions.removeActor(bullet)));
        addActor(bullet);
    }

    @Override
    public void act(float delta){
        super.act(delta);
        diff = gameUptime - startTime;
        timer.setText("Timer: "+diff);
        if(diff>=30)
            setScene("GameOver");
        if(collides(player, enemy))
            setScene("GameWin");
        //player.clearActions();
        switch(btnName){
            case "Right": player.addAction(Actions.moveBy(playerSpeed, 0, 0.1f)); break;
            case "Up": player.addAction(Actions.moveBy(0, playerSpeed, 0.1f)); break;
            case "Left": player.addAction(Actions.moveBy(-playerSpeed, 0, 0.1f)); break;
            case "Down": player.addAction(Actions.moveBy(0, -playerSpeed, 0.1f)); break;
        }
    }

    @Override
    public void onTouchUp(){
         touchedDown = false;
         btnName = "";
    }

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
