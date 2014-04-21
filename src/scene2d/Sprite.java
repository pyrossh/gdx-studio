package scene2d;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Array;


public class Sprite extends Actor {
	
	private Array<String> textures = new Array<String>();
	
	private Animation animation;
	private TextureRegion keyFrame;
	private float duration = 1f;
	private int frameCount = 1;
	
	public boolean isAnimationActive = true;
	public boolean isAnimationLooping = true;

	public Sprite(String texName, int frameCount){
		textures.add(texName);
		setFrameCount(frameCount);
	}
	
	public Sprite(String texName, int frameCount, float duration){
		textures.add(texName);
		setFrameCount(frameCount, duration);
	}
	
	public Sprite(String... texNames){
		setTextures(texNames);
	}
	
	public Sprite(float duration, String... texNames){
		setTextures(duration, texNames);
		this.duration = duration;
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		super.draw(batch, parentAlpha);
		if (isAnimationActive && animation != null) {
			keyFrame = animation.getKeyFrame(Scene.stateTime, isAnimationLooping);
			batch.draw(keyFrame, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(),
					getScaleX(), getScaleY(), getRotation());
		}
	}
	
	public void setTextures(String... texNames){
		textures.clear();
		if(texNames.length == 1){
			textures.add(texNames[0]);
			animation = Asset.anim(texNames[0], frameCount);
			setSize(animation.getKeyFrame(0).getRegionWidth(), animation.getKeyFrame(0).getRegionHeight());
			return;
		}
		Array<TextureRegion> array = new Array<TextureRegion>();
		for(int i=0;i<texNames.length;i++){
			if(!texNames[i].trim().isEmpty()){
				array.add(Asset.tex(texNames[i]));
				textures.add(texNames[i]);
			}
		}
		animation = new Animation(1f/texNames.length, array);
		duration = 1f/texNames.length;
		setSize(array.first().getRegionWidth(), array.first().getRegionHeight());
	}
	
	public void setTextures(float duration, String... texNames){
		textures.clear();
		if(texNames.length == 1){
			textures.add(texNames[0]);
			animation = Asset.anim(texNames[0], frameCount, duration);
			setSize(animation.getKeyFrame(0).getRegionWidth(), animation.getKeyFrame(0).getRegionHeight());
			return;
		}
		Array<TextureRegion> array = new Array<TextureRegion>(); 
		for(int i=0;i<texNames.length;i++){
			if(!texNames[i].trim().isEmpty()){
				array.add(Asset.tex(texNames[i]));
				textures.add(texNames[i]);
			}
		}
		animation = new Animation(duration, array);
		setSize(array.first().getRegionWidth(), array.first().getRegionHeight());
	}
	
	public Array<String> getTextures(){
		return textures;
	}
	
	public void setDuration(float duration){
		this.duration = duration;
		StringBuilder sb = new StringBuilder();
		for(String s: textures){
			sb.append(s);
			sb.append(",");
		}
		setTextures(duration, sb.toString().split(","));
	}
	
	public float getDuration(){
		return this.duration;
	}
	
	public int getFrameCount(){
		return frameCount;
	}
	
	public void setFrameCount(int count){
		frameCount = count;
		animation = Asset.anim(textures.get(0), frameCount);
		setSize(animation.getKeyFrame(0).getRegionWidth(), animation.getKeyFrame(0).getRegionHeight());
	}
	
	public void setFrameCount(int count, float duration){
		frameCount = count;
		animation = Asset.anim(textures.get(0), frameCount, duration);
		setSize(animation.getKeyFrame(0).getRegionWidth(), animation.getKeyFrame(0).getRegionHeight());
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		for(String tex: textures){
			sb.append(tex);
			sb.append(",");
		}
		sb.deleteCharAt(sb.lastIndexOf(","));
		return sb.toString();
	}
}
