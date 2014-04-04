import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/** The Configuration/Settings for the Game
 * <p>
 * The Config class contains all the necessary options for all different platforms into one class.
 * Here you can save all the data of the game that is required to be persistent.<br>
 * @author pyros2097 */

public final class Config {
    static final String MUSIC = "music";
    static final String SOUND = "sound";
    static final String VOLUME_MUSIC = "volumeMusic";
	static final String VOLUME_SOUND = "volumeSOUND";
    static final String VIBRATION = "vibration";
    static final String BATTLE_ANIMATION = "battleanimation";
    static final String SEMI_AUTOMATIC = "semiautomatic";
    static final String FIRST_LAUNCH = "firstLaunch";
    static final String LEVELS = "levels";
    static final String CURRENT_LEVEL = "currentlevel";
    static final String SAVEDATA= "savedata";
    static final String TOTAL_TIME= "totaltime";
    static final String PANSCROLL = "panscroll";
    static final String DRAGSCROLL = "dragscroll";
    static final String PANSPEED = "panspeed";
    static final String DRAGSPEED = "dragspeed";
    static final String KEYBOARD = "keyboard";
    static final String SCORE = "score";

    public static Preferences prefs;
    
    public static boolean isMusic;
    public static boolean isSound;
   
    public static float volMusic;
    public static float volSound;
    
    public static boolean usePan;
    public static boolean useDrag;
    public static boolean useKeyboard;
    
    public static float speedPan;
    public static float speedDrag;
    
    private static int score;
    
    static void setup(){
       prefs = Gdx.app.getPreferences(Stage.configJson.getString("title"));
       isMusic = prefs.getBoolean(MUSIC, true);
       isSound = prefs.getBoolean(SOUND, true);
       
       volMusic = prefs.getFloat(VOLUME_MUSIC, 1f);
       volSound = prefs.getFloat(VOLUME_SOUND, 1f);
        
       usePan = prefs.getBoolean(PANSCROLL, true);
       useDrag = prefs.getBoolean(DRAGSCROLL, true);
       useKeyboard = prefs.getBoolean(KEYBOARD, true);
        
       speedPan = prefs.getFloat(PANSPEED, 5f);
       speedDrag = prefs.getFloat(DRAGSPEED, 5f);
       
       score = prefs.getInteger(SCORE, 0);
    }
   
    
    
    public static String readSaveData(){
        return prefs.getString(SAVEDATA, "nodata");
    }
 
    public static void writeSaveData(String ue){
        prefs.putString(SAVEDATA, ue);
        prefs.flush();
    }
    
    public static float readTotalTime(){
        return prefs.getFloat(TOTAL_TIME, 0.0f);
    }
 
    public static void writeTotalTime(float secondstime){
        float curr = readTotalTime();
        prefs.putFloat(TOTAL_TIME, secondstime+curr);
        prefs.flush();
    }
    
    static String addZero(int value){
		String str = "";
		if(value < 10)
			 str = "0" + value;
		else
			str = "" + value;
		return str;
	}
    
    public static int levels(){
        return prefs.getInteger(LEVELS, 20);
    }
 
    public static void setLevels(int ue){
        prefs.putInteger(LEVELS, ue);
        prefs.flush();
    }
    
    public static int currentLevel(){
        return prefs.getInteger(CURRENT_LEVEL, 0);
    }
 
    public static void setCurrentLevel(int ue){
        prefs.putInteger(CURRENT_LEVEL, ue);
        prefs.flush();
    }
    
    public static boolean isBattleEnabled(){
        return prefs.getBoolean(BATTLE_ANIMATION, true);
    }
 
    public static void setBattle(boolean ue){
        prefs.putBoolean(BATTLE_ANIMATION, ue);
        prefs.flush();
    }
    
    public static boolean isSemiAutomatic(){
        return prefs.getBoolean(SEMI_AUTOMATIC, false);
    }
 
    public static void setSemiAutomatic(boolean ue){
        prefs.putBoolean(SEMI_AUTOMATIC, ue);
        prefs.flush();
    }
 
 
    public static void setPanScroll(boolean ue){
        prefs.putBoolean(PANSCROLL, ue);
        prefs.flush();
        usePan = ue ;
    }
 
    public static void setDragScroll(boolean ue){
        prefs.putBoolean(DRAGSCROLL, ue);
        prefs.flush();
        useDrag = ue;
    }
 
    public static void setPanSpeed(float ue){
        prefs.putFloat(PANSPEED, ue);
        prefs.flush();
        speedPan = ue;
    }
    
    public static void setDragSpeed(float ue){
        prefs.putFloat(VOLUME_SOUND, ue);
        prefs.flush();
        speedDrag = ue;
    }
    
    public static void setKeyboard(boolean ue){
        prefs.putBoolean(KEYBOARD, ue);
        prefs.flush();
        useKeyboard = ue;
    }
    
    public static void setSound(boolean ue){
        prefs.putBoolean(SOUND, ue);
        prefs.flush();
        isSound = ue ;
        
    }
 
    public static void setMusic(boolean ue){
        prefs.putBoolean(MUSIC, ue);
        prefs.flush();
        isMusic = ue;
    }
 
    public static void setMusicVolume(float ue){
        prefs.putFloat(VOLUME_MUSIC, ue);
        prefs.flush();
        volMusic = ue;
        Asset.musicVolume();
    }
    
    public static void setSoundVolume(float ue){
        prefs.putFloat(VOLUME_SOUND, ue);
        prefs.flush();
        volSound = ue;
    }
    
    public static void setScore(int value){
        prefs.putInteger(SCORE, value);
        prefs.flush();
        score = value;
    }
    
    public static int getScore(){
        return score;
    }
    
    public static void setVibration(boolean ue){
		prefs.putBoolean(VIBRATION, ue);
		prefs.flush();
	}

    public static boolean getVibration(){
		return prefs.getBoolean(VIBRATION, true);
	}
	
	public static void setFirstLaunchDone(){
		prefs.putBoolean(FIRST_LAUNCH, false);
		prefs.flush();
		
	}
	
	public static boolean isFirstLaunch(){
		return prefs.getBoolean(FIRST_LAUNCH, true);
	}
}
