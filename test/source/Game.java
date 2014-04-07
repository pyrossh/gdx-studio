import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

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
        Stage.addHud(Stage.findActor("Up"));
        Stage.addHud(Stage.findActor("Right"));
        Stage.addHud(Stage.findActor("Left"));
        Stage.addHud(Stage.findActor("Down"));
        Stage.addHud(Stage.findActor("Timer"));
        Stage.addHud(Stage.findActor("Label3"));
        Stage.addHud(Stage.findActor("Fire"));

        Stage.setFollowSpeed(0.33f);
        player = (ImageJson) Stage.findActor("Player");
        enemy = (ImageJson) Stage.findActor("Enemy");
        Stage.followActorContinuously(player);
        timer = (Label)Stage.findActor("Timer");
        startTime = Stage.gameUptime;
    }

    @Override
    public void onTouchDown(Actor actor){
        touchedDown = true;
        btnName = actor.getName();
    }

    @Override
    public void onClick(Actor actor){
        if(actor.getName().equals("Back"))
            Stage.setScene("Menu");
        if(actor.getName().equals("Fire"))
            doFire();
    }

    void doFire(){
        ImageJson bullet = new ImageJson("771");
        bullet.setPosition(player.getX() + 50, player.getY() + 50);
        bullet.setSize(25, 25);
        bullet.addAction(Actions.sequence(Actions.moveBy(500, 0, 1f), Actions.removeActor(bullet)));
        Stage.addActor(bullet);
    }

    @Override
    public void act(float delta){
        diff = Stage.gameUptime - startTime;
        timer.setText("Timer: "+diff);
        if(diff>=30)
            Stage.setScene("GameOver");
        if(Stage.collides(player, enemy))
            Stage.setScene("GameWin");
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
