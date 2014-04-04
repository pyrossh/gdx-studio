package com.badlogic.gdx.tools.particleeditor;

import java.awt.EventQueue;
import java.io.File;

import javax.swing.JOptionPane;

import org.lwjgl.opengl.GL11;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter.NumericValue;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class ParticleRenderer implements ApplicationListener, InputProcessor {
	/**
	 * 
	 */
	private ParticlePanel particlePanel;

	/**
	 * @param particlePanel
	 */
	public ParticleRenderer(ParticlePanel particlePanel) {
		this.particlePanel = particlePanel;
	}

	private float maxActiveTimer;
	private int maxActive, lastMaxActive;
	private boolean mouseDown;
	private int activeCount;
	private int mouseX, mouseY;
	private BitmapFont font;
	private SpriteBatch spriteBatch;
	private Sprite bgImage; // BOZO - Add setting background image to UI.

	@Override
	public void create () {
		if (spriteBatch != null) return;

		Texture.setEnforcePotImages(false);

		spriteBatch = new SpriteBatch();

		this.particlePanel.worldCamera = new OrthographicCamera();
		this.particlePanel.textCamera = new OrthographicCamera();

		this.particlePanel.pixelsPerMeter = new NumericValue();
		this.particlePanel.pixelsPerMeter.setValue(1.0f);
		this.particlePanel.pixelsPerMeter.setAlwaysActive(true);

		this.particlePanel.zoomLevel = new NumericValue();
		this.particlePanel.zoomLevel.setValue(1.0f);
		this.particlePanel.zoomLevel.setAlwaysActive(true);
		
		this.particlePanel.deltaMultiplier = new NumericValue();
		this.particlePanel.deltaMultiplier.setValue(1.0f);
		this.particlePanel.deltaMultiplier.setAlwaysActive(true);

		font = new BitmapFont(Gdx.files.getFileHandle("default.fnt", FileType.Internal), Gdx.files.getFileHandle("default.png",
			FileType.Internal), true);
		this.particlePanel.effectPanel.newExampleEmitter("Untitled", true);
		// if (resources.openFile("/editor-bg.png") != null) bgImage = new Image(gl, "/editor-bg.png");
		//Gdx.input.setInputProcessor(this);
	}

	@Override
	public void resize (int width, int height) {
		Gdx.gl.glViewport(0, 0, width, height);

		if (this.particlePanel.pixelsPerMeter.getValue() <= 0) {
			this.particlePanel.pixelsPerMeter.setValue(1);
		}
		this.particlePanel.worldCamera.setToOrtho(false, width / this.particlePanel.pixelsPerMeter.getValue(), height / this.particlePanel.pixelsPerMeter.getValue());
		this.particlePanel.worldCamera.update();

		this.particlePanel.textCamera.setToOrtho(true, width, height);
		this.particlePanel.textCamera.update();

		this.particlePanel.effect.setPosition(this.particlePanel.worldCamera.viewportWidth / 2, this.particlePanel.worldCamera.viewportHeight / 2);
	}

	@Override
	public void render () {
		int viewWidth = Gdx.graphics.getWidth();
		int viewHeight = Gdx.graphics.getHeight();

		float delta = Math.max(0, Gdx.graphics.getDeltaTime() * this.particlePanel.deltaMultiplier.getValue());

		Gdx.gl.glClear(GL11.GL_COLOR_BUFFER_BIT);

		if ((this.particlePanel.pixelsPerMeter.getValue() != this.particlePanel.pixelsPerMeterPrev) || (this.particlePanel.zoomLevel.getValue() != this.particlePanel.zoomLevelPrev)) {
			if (this.particlePanel.pixelsPerMeter.getValue() <= 0) {
				this.particlePanel.pixelsPerMeter.setValue(1);
			}

			this.particlePanel.worldCamera.setToOrtho(false, viewWidth / this.particlePanel.pixelsPerMeter.getValue(), viewHeight / this.particlePanel.pixelsPerMeter.getValue());
			this.particlePanel.worldCamera.zoom = this.particlePanel.zoomLevel.getValue();
			this.particlePanel.worldCamera.update();
			this.particlePanel.effect.setPosition(this.particlePanel.worldCamera.viewportWidth / 2, this.particlePanel.worldCamera.viewportHeight / 2);
			this.particlePanel.zoomLevelPrev = this.particlePanel.zoomLevel.getValue();
			this.particlePanel.pixelsPerMeterPrev = this.particlePanel.pixelsPerMeter.getValue();
		}
		
		spriteBatch.setProjectionMatrix(this.particlePanel.worldCamera.combined);

		spriteBatch.begin();
		spriteBatch.enableBlending();
		spriteBatch.setBlendFunction(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		if (bgImage != null) {
			bgImage.setPosition(viewWidth / 2 - bgImage.getWidth() / 2, viewHeight / 2 - bgImage.getHeight() / 2);
			bgImage.draw(spriteBatch);
		}

		activeCount = 0;
		boolean complete = true;
		for (ParticleEmitter emitter : this.particlePanel.effect.getEmitters()) {
			if (emitter.getSprite() == null && emitter.getImagePath() != null) loadImage(emitter);
			boolean enabled = this.particlePanel.isEnabled(emitter);
			if (enabled) {
				if (emitter.getSprite() != null) emitter.draw(spriteBatch, delta);
				activeCount += emitter.getActiveCount();
				if (!emitter.isComplete()) complete = false;
			}
		}
		if (complete) this.particlePanel.effect.start();

		maxActive = Math.max(maxActive, activeCount);
		maxActiveTimer += delta;
		if (maxActiveTimer > 3) {
			maxActiveTimer = 0;
			lastMaxActive = maxActive;
			maxActive = 0;
		}

		if (mouseDown) {
			// gl.drawLine(mouseX - 6, mouseY, mouseX + 5, mouseY);
			// gl.drawLine(mouseX, mouseY - 5, mouseX, mouseY + 6);
		}

		spriteBatch.setProjectionMatrix(this.particlePanel.textCamera.combined);

		font.draw(spriteBatch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 5, 15);
		font.draw(spriteBatch, "Count: " + activeCount, 5, 35);
		font.draw(spriteBatch, "Max: " + lastMaxActive, 5, 55);
		font.draw(spriteBatch, (int)(this.particlePanel.getEmitter().getPercentComplete() * 100) + "%", 5, 75);

		spriteBatch.end();

		// gl.drawLine((int)(viewWidth * getCurrentParticles().getPercentComplete()), viewHeight - 1, viewWidth, viewHeight -
		// 1);
	}

	private void loadImage (ParticleEmitter emitter) {
		final String imagePath = emitter.getImagePath();
		String imageName = new File(imagePath.replace('\\', '/')).getName();
		try {
			FileHandle file;
			if (imagePath.equals("particle.png"))
				file = Gdx.files.classpath(imagePath);
			else
				file = Gdx.files.absolute(imagePath);
			emitter.setSprite(new Sprite(new Texture(file)));
		} catch (GdxRuntimeException ex) {
			ex.printStackTrace();
			EventQueue.invokeLater(new Runnable() {
				public void run () {
					JOptionPane.showMessageDialog(ParticleRenderer.this.particlePanel, "Error loading image:\n" + imagePath);
				}
			});
			emitter.setImagePath(null);
		}
	}

	public boolean keyDown (int keycode) {
		return false;
	}

	public boolean keyUp (int keycode) {
		return false;
	}

	public boolean keyTyped (char character) {
		return false;
	}

	public boolean touchDown (int x, int y, int pointer, int newParam) {
		Vector3 touchPoint = new Vector3(x, y, 0);
		this.particlePanel.worldCamera.unproject(touchPoint);
		this.particlePanel.effect.setPosition(touchPoint.x, touchPoint.y);
		return false;
	}

	public boolean touchUp (int x, int y, int pointer, int button) {
		//ParticleEditor.this.dispatchEvent(new WindowEvent(ParticleEditor.this, WindowEvent.WINDOW_LOST_FOCUS));
		//ParticleEditor.this.dispatchEvent(new WindowEvent(ParticleEditor.this, WindowEvent.WINDOW_GAINED_FOCUS));
		this.particlePanel.requestFocusInWindow();
		return false;
	}

	public boolean touchDragged (int x, int y, int pointer) {
		Vector3 touchPoint = new Vector3(x, y, 0);
		this.particlePanel.worldCamera.unproject(touchPoint);
		this.particlePanel.effect.setPosition(touchPoint.x, touchPoint.y);
		return false;
	}

	@Override
	public void dispose () {
	}

	@Override
	public void pause () {
	}

	@Override
	public void resume () {
	}

	@Override
	public boolean mouseMoved (int x, int y) {
		return false;
	}

	@Override
	public boolean scrolled (int amount) {
		return false;
	}
}