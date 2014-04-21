package scene2d;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;

/** The MapActor Class
 * <p>
 * The MapActor is a SceneActor that can be used as a static tile, animated tile, animated actor or as a plain
 * actor.<br>
 * 1.For using it as a Static Tile use:<br>
 *  	MapActor(TextureRegion region, int row, int col, int id, int tileSize)<br>
 * 2.For using it as a Animated Tile/Actor use:<br>
 * 	 	MapActor(Animation a, int row, int col, int id, int tileSize)<br>
 * 3.For using it as a plain Actor use:<br>
 * 	 	MapActor(int row, int col, int tileSize)<br>
 * 
 * It has many important methods like moveTo, moveBy, collides, intersects, getCenterX, getCenterY
 * 
 * <b>Note:</b> Only Use setPositionXY and SetPositionRC on this actor do not use the Actor's setPosition method
 * as it causes problems
 * @author pyros2097 */
public class MapActor extends Actor {
	private int row;
	private int col;
	private int tileSize;
	
	public TextureRegion tileImage;
	public int index;
	
  	protected Animation animation;
  	protected boolean isAnimationActive = false;
  	protected boolean isAnimationLooping = true;
	protected TextureRegion keyFrame;

	// Particle
	public ParticleEffect particleEffect;
	public float particlePosX = 0.0f;
	public float particlePosY = 0.0f;
	public boolean isParticleEffectActive;

	// When tiles coords row and column are directly specified
	public MapActor(int row, int col, int tileSize){
		setTileSize(tileSize);
		setPositionRC(row, col);
		setSize(tileSize, tileSize); // All map units are 24x24
	}
	
	public MapActor(TextureRegion region, int row, int col, int id, int tileSize){
		this(row, col, tileSize);
		index = id;
		if(region != null)
			tileImage = region;
	}
	
	
	public MapActor(Animation a, int row, int col, int id, int tileSize) {
		this(row, col, tileSize);
		index = id;
		if(a != null){
			animation = a;
			isAnimationActive = true;
		}
		
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		batch.setColor(getColor());
		if(tileImage != null)
			batch.draw(tileImage, getX(), getY(), tileSize, tileSize);
		//font("normal").draw(batch, ""+index, getX(), getY()+tileSize);
		if (isAnimationActive && animation != null) {
			keyFrame = animation.getKeyFrame(Scene.stateTime, isAnimationLooping);
			batch.draw(keyFrame, getX(), getY(), tileSize, tileSize);
		}
		drawParticleEffect(batch);
	}
	
	public void drawParticleEffect(Batch batch){
		if (isParticleEffectActive) {
			particleEffect.draw(batch, Gdx.graphics.getDeltaTime());
			particleEffect.setPosition(getX() + particlePosX, getY()+ particlePosY);
		}
	}

	/**
	 * Set particle for this actor, centerPosition is used to center the
	 * particle on this actor sizes
	 * */
	public void setParticleEffect(ParticleEffect particleEffect,
			boolean isParticleEffectActive, boolean isStart,
			boolean centerPosition) {
		this.particleEffect = particleEffect;
		this.isParticleEffectActive = isParticleEffectActive;
		if (!centerPosition) {
			this.particleEffect.setPosition(getX(), getY());
		} else {
			particlePosX = getWidth() / 2.0f;
			particlePosY = getHeight() / 2.0f;
			this.particleEffect.setPosition(getX() + particlePosX, getY()
					+ particlePosY);
		}

		if (isStart) {
			this.particleEffect.start();
		}
	}

	/**
	 * Set particle position
	 * */
	public void setParticlePositionXY(float x, float y) {
		particlePosX = x;
		particlePosY = y;
	}
	
	
	public void setTileSize(int tsize){
		tileSize = tsize;
	}
	
	public int getTileSize(){
		return tileSize;
	}
	
	public int getCol(){
		return col;
	}
	
	public int getRow(){
		return row;
	}
	
	/*
	 * Col is always x-axis and Row is y-axis
	 */
	public void setPositionRC(int row, int col){
		setPosition(col*tileSize, row*tileSize);
	}
	
	/*
	 * The setPosition calls the super Actor's setPosition method and also updates the row and col
	 * position of the tile
	 */
	@Override
	public void setPosition(float x, float y){
		super.setPosition(x, y);
		this.row =(int)this.getY()/tileSize;
		this.col = (int)this.getX()/tileSize;
	}
	
	/**
	 * Translate actor in a direction of speed without stopping. Actor moves in
	 * constants speed set without acceleration
	 * 
	 * @param speedX
	 *            axis-X speed
	 * @param speedY
	 *            axis-Y speed
	 * @param delta
	 *            the delta time for accurate speed
	 * */
	public void translateWithoutAcc(float speedX, float speedY, float delta) {
		setPosition(getX() + (speedX * delta), getY() + (speedY * delta));
	}
	
	/**
	 * Get center x point of an object
	 * <p>
	 * EXAMPLE<br>
	 * Object's width 200, and we touched the screen in 400 in position X, and
	 * we want to center the object according to our touch position. (200 / 2 =
	 * 100 then 400 - 100), so 300 our center position
	 * 
	 * */
	public static float getCenterX(float eventX, float objectWidth) {
		return eventX - (objectWidth / 2);
	}

	/**
	 * @see getCenterX()
	 * */
	public static float getCenterY(float eventY, float objectHeight) {
		return eventY - (objectHeight / 2);
	}
	
	public boolean intersects(MapActor other){
		//Sink.log(""+row+" : "+other.getRow());
		if(col == other.getCol() && row == other.getRow())
			return true;
		return false;
	}
	
	public Animation getAnimation(){
		return animation;
	}
}
