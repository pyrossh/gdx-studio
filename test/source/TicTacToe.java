package source;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import java.util.Random;

import scene2d.*;

/** 
 *  A Basic TicTacToe Game
 Scene
 *  
 * @author pyros2097 */
public class TicTacToe extends Scene {

    Box[][] boxes;
    public static int turnCounter = 0;

    /* Game Constants */
    private static GameMode gameMode = GameMode.SINGLE_PLAYER_VS_COMPUTER;
    public static Turn currentTurn = Turn.Player;

    public TicTacToe(){
        boxes = new Box[3][3];
        for(int i = 0;i < 3;i++){
            for(int j = 0;j < 3;j++){
                Box box = new Box(i, j);
                boxes[i][j] = box;
                addActor(box, box.getWidth()*j, box.getHeight()*i);
            }
        }
    }

    /* Check if mode is current return true */
    public static boolean mode(GameMode gm){
        if(gameMode == gm)
            return true;
        else
            return false;
    }
    
    public static void setMode(GameMode gm){
        gameMode = gm;
        log("Game Mode: " + gameMode.toString());
    }
        
    public void reset(){
        log("Reset");
        turnCounter = 0;
        setScene("TicTacToe");
    }

    public void playerWin(){
        log("Player Win");
        reset();
    }
    
    public void computerWin(){
        log("Computer Win");
        reset();
    }

    int random1 = 0;
    int random2 = 0;
    Random rand = new Random();
    void AI(){
        if(mode(GameMode.SINGLE_PLAYER_VS_COMPUTER)){
            if(currentTurn == Turn.Computer){
                log("AI");
                random1 = rand.nextInt(3);
                random2 = rand.nextInt(3);
                if(!boxes[random1][random2].isMarked){
                    boxes[random1][random2].markByComputer();
                    return;
                }
            }
        }
    }
    
    void checkForRowY(int j){
        if(boxes[j][0].type != MarkType.None && boxes[j][1].type != MarkType.None  
                && boxes[j][2].type != MarkType.None )
            if(boxes[j][0].type ==  boxes[j][1].type && boxes[j][0].type == boxes[j][2].type )
                if(boxes[j][0].type == MarkType.X)
                    playerWin();
                else
                    computerWin();
    }
    
    void checkForRowX(int j){
        if(boxes[0][j].type != MarkType.None && boxes[1][j].type != MarkType.None  
                && boxes[2][j].type != MarkType.None )
            if(boxes[0][j].type ==  boxes[1][j].type && boxes[0][j].type == boxes[2][j].type )
                if(boxes[0][j].type == MarkType.X)
                    playerWin();
                else
                    computerWin();
    }
    
    void checkForDiagonal(){
        if(boxes[0][0].type != MarkType.None && boxes[1][1].type != MarkType.None  
                && boxes[2][2].type != MarkType.None )
            if(boxes[0][0].type ==  boxes[1][1].type && boxes[0][0].type == boxes[2][2].type )
                if(boxes[0][0].type == MarkType.X)
                    playerWin();
                else
                    computerWin();
        if(boxes[0][2].type != MarkType.None && boxes[1][1].type != MarkType.None  
                && boxes[2][0].type != MarkType.None )
            if(boxes[0][2].type ==  boxes[1][1].type && boxes[0][2].type == boxes[2][0].type )
                if(boxes[0][2].type == MarkType.X)
                    playerWin();
                else
                    computerWin();
    }

    @Override
    public void act(float delta){
        super.act(delta);
        AI();
        if(turnCounter < 9){
            for(int i=0; i<3;i++){
                checkForRowY(i);
                checkForRowX(i);
            }
            checkForDiagonal();
        }
        else{
            log("Draw Match");
            reset();
        }
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

class Box extends Group{
    private final int row, col;
    private final Image bg = new Image(Asset.tex("square"));
    private final Image x = new Image(Asset.tex("x"));
    private final Image o = new Image(Asset.tex("o"));
    public boolean isMarked = false;
    public MarkType type = MarkType.None;
    
    Box(int row, int col){
        this.row = row;
        this.col = col;
        bg.setPosition(row*bg.getWidth(), col*bg.getHeight());
        o.setPosition(row*bg.getWidth(), col*bg.getHeight());
        x.setPosition(row*bg.getWidth(), col*bg.getHeight());
        addActor(bg);
        addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(!Box.this.isMarked){
                    if(TicTacToe.mode(GameMode.SINGLE_PLAYER))
                        if(TicTacToe.currentTurn == Turn.Player)
                            markByPlayer();
                        else
                            markByComputer();
                    else if(TicTacToe.mode(GameMode.SINGLE_PLAYER_VS_COMPUTER))
                        if(TicTacToe.currentTurn == Turn.Player)
                            markByPlayer();
                    Scene.log("Box Clicked "+Box.this.row+Box.this.col);
                }
            }
        });
    }
    
    public void markByPlayer(){
        isMarked = true;
        TicTacToe.turnCounter++;
        addActor(x);
        TicTacToe.currentTurn = Turn.Computer;
        type = MarkType.X;
    }
    
    public void markByComputer(){
        isMarked = true;
        TicTacToe.turnCounter++;
        addActor(o);
        TicTacToe.currentTurn = Turn.Player;
        type = MarkType.O;
    }
}

enum MarkType {
    None,
    X,
    O
}

enum Turn {
    Player,
    Computer,
    OtherPlayer
}

enum GameMode {
    SINGLE_PLAYER,
    SINGLE_PLAYER_VS_COMPUTER,
    MULTI_PLAYER,
}