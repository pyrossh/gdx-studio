import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Pool.Poolable;

public class Stage3d extends InputAdapter implements Disposable {
	private float width, height;
	private ModelBatch modelBatch;
	private Environment environment;
	
	public ModelBuilder modelBuilder;
	
    private PerspectiveCamera camera;
    private Group3d root;
	private Actor3d scrollFocus;
	private Actor3d keyboardFocus;
    
    /** Creates a stage with a {@link #setViewport(float, float, boolean) viewport} equal to the device screen resolution. The stage
	 * will use its own {@link SpriteBatch}. */
	public Stage3d () {
		this(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), false);
	}

	/** Creates a stage with the specified {@link #setViewport(float, float, boolean) viewport} that doesn't keep the aspect ratio.
	 * The stage will use its own {@link SpriteBatch}, which will be disposed when the stage is disposed. */
	public Stage3d (float width, float height) {
		this(width, height, false);
	}

	/** Creates a stage with the specified {@link #setViewport(float, float, boolean) viewport}. The stage will use its own
	 * {@link SpriteBatch}, which will be disposed when the stage is disposed. */
	public Stage3d (float width, float height, boolean keepAspectRatio) {
		this.width = width;
		this.height = height;
		
		root = new Group3d();
    	root.setStage3d(this);
    	
    	modelBatch = new ModelBatch();
    	
    	camera =  new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    	camera.position.set(10f, 10f, 10f);
    	camera.lookAt(0,0,0);
    	camera.near = 0.1f;
    	camera.far = 300f;
    	camera.update();
    	environment = new Environment();
    	environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
        environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));
        
		setViewport(width, height, keepAspectRatio);
		modelBuilder = new ModelBuilder();
	}

	/** Sets up the stage size using a viewport that fills the entire screen without keeping the aspect ratio.
	 * @see #setViewport(float, float, boolean, float, float, float, float) */
	public void setViewport (float width, float height) {
		setViewport(width, height, false, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	/** Sets up the stage size using a viewport that fills the entire screen.
	 * @see #setViewport(float, float, boolean, float, float, float, float) */
	public void setViewport (float width, float height, boolean keepAspectRatio) {
		setViewport(width, height, keepAspectRatio, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	/** Sets up the stage size and viewport. The viewport is the glViewport position and size, which is the portion of the screen
	 * used by the stage. The stage size determines the units used within the stage, depending on keepAspectRatio:
	 * <p>
	 * If keepAspectRatio is false, the stage is stretched to fill the viewport, which may distort the aspect ratio.
	 * <p>
	 * If keepAspectRatio is true, the stage is first scaled to fit the viewport in the longest dimension. Next the shorter
	 * dimension is lengthened to fill the viewport, which keeps the aspect ratio from changing. The {@link #getGutterWidth()} and
	 * {@link #getGutterHeight()} provide access to the amount that was lengthened.
	 * @param viewportX The top left corner of the viewport in glViewport coordinates (the origin is bottom left).
	 * @param viewportY The top left corner of the viewport in glViewport coordinates (the origin is bottom left).
	 * @param viewportWidth The width of the viewport in pixels.
	 * @param viewportHeight The height of the viewport in pixels. */
	public void setViewport (float stageWidth, float stageHeight, boolean keepAspectRatio, float viewportX, float viewportY,
		float viewportWidth, float viewportHeight) {
		if (keepAspectRatio) {
			if (viewportHeight / viewportWidth < stageHeight / stageWidth) {
				float toViewportSpace = viewportHeight / stageHeight;
				float toStageSpace = stageHeight / viewportHeight;
				float deviceWidth = stageWidth * toViewportSpace;
				float lengthen = (viewportWidth - deviceWidth) * toStageSpace;
				this.width = stageWidth + lengthen;
				this.height = stageHeight;
			} else {
				float toViewportSpace = viewportWidth / stageWidth;
				float toStageSpace = stageWidth / viewportWidth;
				float deviceHeight = stageHeight * toViewportSpace;
				float lengthen = (viewportHeight - deviceHeight) * toStageSpace;
				this.height = stageHeight + lengthen;
				this.width = stageWidth;
			}
		} else {
			this.width = stageWidth;
			this.height = stageHeight;
		}
		
		camera.viewportWidth = this.width;
		camera.viewportHeight = this.height;
	}
    
    public void draw(){ 
    	camera.update();
        if (!root.isVisible()) return;
        modelBatch.begin(camera);
        root.drawChildren(modelBatch, environment);
        modelBatch.end();
    }
    
    /** Calls {@link #act(float)} with {@link Graphics#getDeltaTime()}. */
	public void act () {
		act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
	}
	
	/** Calls the {@link Actor#act(float)} method on each actor in the stage. Typically called each frame. This method also fires
	 * enter and exit events.
	 * @param delta Time in seconds since the last frame. */
	public void act(float delta) {
		root.act(delta);
	}
	
	/** Adds an actor to the root of the stage.
	 * @see Group#addActor(Actor)
	 * @see Actor#remove() */
	public void addActor3d(Actor3d actor3d) {
		root.addActor3d(actor3d);
	}

	/** Adds an action to the root of the stage.
	 * @see Group#addAction3d(Action) */
	public void addAction3d(Action3d action3d) {
		root.addAction3d(action3d);
	}

	/** Returns the root's child actors.
	 * @see Group#getChildren() */
	public Array<Actor3d> getActors3d() {
		return root.getChildren();
	}

	/** Adds a listener to the root.
	 * @see Actor#addListener(EventListener) */
	public boolean addListener (Event3dListener listener) {
		return root.addListener(listener);
	}

	/** Removes a listener from the root.
	 * @see Actor#removeListener(EventListener) */
	public boolean removeListener (Event3dListener listener) {
		return root.removeListener(listener);
	}
	
	/** Removes the root's children, actions, and listeners. */
	public void clear () {
		unfocusAll();
		root.clear();
	}

	/** Removes the touch, keyboard, and scroll focused actors. */
	public void unfocusAll () {
		scrollFocus = null;
		keyboardFocus = null;
		//cancelTouchFocus();
	}

	/** Removes the touch, keyboard, and scroll focus for the specified actor and any descendants. */
	public void unfocus(Actor3d actor) {
		if (scrollFocus != null && scrollFocus.isDescendantOf(actor)) scrollFocus = null;
		if (keyboardFocus != null && keyboardFocus.isDescendantOf(actor)) keyboardFocus = null;
	}

	/** Sets the actor that will receive key events.
	 * @param actor May be null. */
	public void setKeyboardFocus (Actor3d actor) {
		if (keyboardFocus == actor) return;
	}

	/** Gets the actor that will receive key events.
	 * @return May be null. */
	public Actor3d getKeyboardFocus () {
		return keyboardFocus;
	}

	/** Sets the actor that will receive scroll events.
	 * @param actor May be null. */
	public void setScrollFocus(Actor3d actor) {
		if (scrollFocus == actor) return;
	}

	/** Gets the actor that will receive scroll events.
	 * @return May be null. */
	public Actor3d getScrollFocus () {
		return scrollFocus;
	}
	
	public ModelBatch getModelBatch () {
		return modelBatch;
	}

	public PerspectiveCamera getCamera () {
		return camera;
	}

	/** Sets the stage's camera. The camera must be configured properly or {@link #setViewport(float, float, boolean)} can be called
	 * after the camera is set. {@link Stage#draw()} will call {@link Camera#update()} and use the {@link Camera#combined} matrix
	 * for the SpriteBatch {@link SpriteBatch#setProjectionMatrix(com.badlogic.gdx.math.Matrix4) projection matrix}. */
	public void setCamera (PerspectiveCamera camera) {
		this.camera = camera;
	}

	/** Returns the root group which holds all actors in the stage. */
	public Group3d getRoot () {
		return root;
	}
	
	public Environment getEnvironment(){
		return environment;
	}

	@Override
	public void dispose() {
		clear();
		modelBatch.dispose();
	}
}

class Event3d implements Poolable {
    private Stage3d stage;
    private Actor3d targetActor;
    private Actor3d listenerActor;
    private boolean capture; // true means event occurred during the capture phase
    private boolean bubbles = true; // true means propagate to target's parents
    private boolean handled; // true means the event was handled (the stage will eat the input)
    private boolean stopped; // true means event propagation was stopped
    private boolean cancelled; // true means propagation was stopped and any action that this event would cause should not happen

    /** Marks this event as handled. This does not affect event propagation inside scene2d, but causes the {@link Stage}
     * event methods to return false, which will eat the event so it is not passed on to the application under the stage. */
    public void handle () {
            handled = true;
    }

    /** Marks this event cancelled. This {@link #handle() handles} the event and {@link #stop() stops} the event
     * propagation. It also cancels any default action that would have been taken by the code that fired the event. Eg, if the
     * event is for a checkbox being checked, cancelling the event could uncheck the checkbox. */
    public void cancel () {
            cancelled = true;
            stopped = true;
            handled = true;
    }

    /** Marks this event has being stopped. This halts event propagation. Any other listeners on the {@link #getListenerActor()
     * listener actor} are notified, but after that no other listeners are notified. */
    public void stop () {
            stopped = true;
    }

    public void reset () {
            stage = null;
            targetActor = null;
            listenerActor = null;
            capture = false;
            bubbles = true;
            handled = false;
            stopped = false;
            cancelled = false;
    }

    /** Returns the actor that the event originated from. */
    public Actor3d getTarget () {
            return targetActor;
    }

    public void setTarget (Actor3d targetActor) {
            this.targetActor = targetActor;
    }

    /** Returns the actor that this listener is attached to. */
    public Actor3d getListenerActor () {
            return listenerActor;
    }

    public void setListenerActor (Actor3d listenerActor) {
            this.listenerActor = listenerActor;
    }

    public boolean getBubbles () {
            return bubbles;
    }

    /** If true, after the event is fired on the target actor, it will also be fired on each of the parent actors, all the way to
     * the root. */
    public void setBubbles (boolean bubbles) {
            this.bubbles = bubbles;
    }

    /** {@link #handle()} */
    public boolean isHandled () {
            return handled;
    }

    /** @see #stop() */
    public boolean isStopped () {
            return stopped;
    }

    /** @see #cancel() */
    public boolean isCancelled () {
            return cancelled;
    }

    public void setCapture (boolean capture) {
            this.capture = capture;
    }

    /** If true, the event was fired during the capture phase.
     * @see Actor#fire(Event) */
    public boolean isCapture () {
            return capture;
    }

    public void setStage (Stage3d stage) {
            this.stage = stage;
    }

    /** The stage for the actor the event was fired on. */
    public Stage3d getStage () {
            return stage;
    }
}

interface Event3dListener {
    public boolean handle (Event3d event);
}

class InputEvent3d extends Event3d {
    private Type type;
    private float stageX, stageY, stageZ;
    private int pointer, button, keyCode, scrollAmount;
    private char character;
    private Actor3d relatedActor;

    public void reset () {
            super.reset();
            relatedActor = null;
            button = -1;
    }

    /** The stage x coordinate where the event occurred. Valid for: touchDown, touchDragged, touchUp, mouseMoved, enter, and exit. */
    public float getStageX () {
            return stageX;
    }

    public void setStageX (float stageX) {
            this.stageX = stageX;
    }

    /** The stage x coordinate where the event occurred. Valid for: touchDown, touchDragged, touchUp, mouseMoved, enter, and exit. */
    public float getStageY () {
            return stageY;
    }

    public void setStageY (float stageY) {
            this.stageY = stageY;
    }

    /** The type of input event. */
    public Type getType () {
            return type;
    }

    public void setType (Type type) {
            this.type = type;
    }

    /** The pointer index for the event. The first touch is index 0, second touch is index 1, etc. Always -1 on desktop. Valid for:
     * touchDown, touchDragged, touchUp, enter, and exit. */
    public int getPointer () {
            return pointer;
    }

    public void setPointer (int pointer) {
            this.pointer = pointer;
    }

    /** The index for the mouse button pressed. Always 0 on Android. Valid for: touchDown and touchUp.
     * @see Buttons */
    public int getButton () {
            return button;
    }

    public void setButton (int button) {
            this.button = button;
    }

    /** The key code of the key that was pressed. Valid for: keyDown and keyUp. */
    public int getKeyCode () {
            return keyCode;
    }

    public void setKeyCode (int keyCode) {
            this.keyCode = keyCode;
    }

    /** The character for the key that was type. Valid for: keyTyped. */
    public char getCharacter () {
            return character;
    }

    public void setCharacter (char character) {
            this.character = character;
    }

    /** The amount the mouse was scrolled. Valid for: scrolled. */
    public int getScrollAmount () {
            return scrollAmount;
    }

    public void setScrollAmount (int scrollAmount) {
            this.scrollAmount = scrollAmount;
    }

    /** The actor related to the event. Valid for: enter and exit. For enter, this is the actor being exited, or null. For exit,
     * this is the actor being entered, or null. */
    public Actor3d getRelatedActor () {
            return relatedActor;
    }

    /** @param relatedActor May be null. */
    public void setRelatedActor (Actor3d relatedActor) {
            this.relatedActor = relatedActor;
    }

    /** Sets actorCoords to this event's coordinates relative to the specified actor.
     * @param actorCoords Output for resulting coordinates. */
    public Vector3 toCoordinates (Actor3d actor, Vector3 actorCoords) {
            actorCoords.set(stageX, stageY, stageZ);
            //actor.stageToLocalCoordinates(actorCoords);
            return actorCoords;
    }

    /** Returns true of this event is a touchUp triggered by {@link Stage#cancelTouchFocus()}. */
    public boolean isTouchFocusCancel () {
            return stageX == Integer.MIN_VALUE || stageY == Integer.MIN_VALUE;
    }

    public String toString () {
            return type.toString();
    }

    /** Types of low-level input events supported by stage2d. */
    static public enum Type {
            /** A new touch for a pointer on the stage was detected */
            touchDown,
            /** A pointer has stopped touching the stage. */
            touchUp,
            /** A pointer that is touching the stage has moved. */
            touchDragged,
            /** The mouse pointer has moved (without a mouse button being active). */
            mouseMoved,
            /** The mouse pointer or an active touch have entered (i.e., {@link Actor#hit(float, float, boolean) hit}) an actor. */
            enter,
            /** The mouse pointer or an active touch have exited an actor. */
            exit,
            /** The mouse scroll wheel has changed. */
            scrolled,
            /** A keyboard key has been pressed. */
            keyDown,
            /** A keyboard key has been released. */
            keyUp,
            /** A keyboard key has been pressed and released. */
            keyTyped
    }
}