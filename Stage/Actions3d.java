import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import com.badlogic.gdx.utils.Pool.Poolable;

public class Actions3d {

	/** Returns a new or pooled action of the specified type. */
	static public <T extends Action3d> T action (Class<T> type) {
		Pool<T> pool = Pools.get(type);
		T action = pool.obtain();
		action.setPool(pool);
		return action;
	}

	static public AddAction addAction (Action3d action) {
		AddAction addAction = action(AddAction.class);
		addAction.setAction(action);
		return addAction;
	}

	static public AddAction addAction (Action3d action, Actor3d targetActor) {
		AddAction addAction = action(AddAction.class);
		addAction.setTargetActor(targetActor);
		addAction.setAction(action);
		return addAction;
	}

	static public RemoveAction removeAction (Action3d action) {
		RemoveAction removeAction = action(RemoveAction.class);
		removeAction.setAction(action);
		return removeAction;
	}

	static public RemoveAction removeAction (Action3d action, Actor3d targetActor) {
		RemoveAction removeAction = action(RemoveAction.class);
		removeAction.setTargetActor(targetActor);
		removeAction.setAction(action);
		return removeAction;
	}

	/** Moves the actor instantly. */
	static public MoveToAction moveTo (float x, float y, float z) {
		return moveTo(x, y, z, 0, null);
	}

	static public MoveToAction moveTo (float x, float y, float z, float duration) {
		return moveTo(x, y, z, duration, null);
	}

	static public MoveToAction moveTo (float x, float y, float z ,float duration, Interpolation interpolation) {
		MoveToAction action = action(MoveToAction.class);
		action.setPosition(x, y, z);
		action.setDuration(duration);
		action.setInterpolation(interpolation);
		return action;
	}

	/** Moves the actor instantly. */
	static public MoveByAction moveBy (float amountX, float amountY, float amountZ) {
		return moveBy(amountX, amountY, amountZ, 0, null);
	}

	static public MoveByAction moveBy (float amountX, float amountY, float amountZ, float duration) {
		return moveBy(amountX, amountY, amountZ, duration, null);
	}

	static public MoveByAction moveBy (float amountX, float amountY, float amountZ, float duration, Interpolation interpolation) {
		MoveByAction action = action(MoveByAction.class);
		action.setAmount(amountX, amountY, amountZ);
		action.setDuration(duration);
		action.setInterpolation(interpolation);
		return action;
	}

	/** Scales the actor instantly. */
	static public ScaleToAction scaleTo (float x, float y, float z) {
		return scaleTo(x, y, z, 0, null);
	}

	static public ScaleToAction scaleTo (float x, float y, float z, float duration) {
		return scaleTo(x, y, z, duration, null);
	}

	static public ScaleToAction scaleTo (float x, float y, float z, float duration, Interpolation interpolation) {
		ScaleToAction action = action(ScaleToAction.class);
		action.setScale(x, y, z);
		action.setDuration(duration);
		action.setInterpolation(interpolation);
		return action;
	}

	/** Scales the actor instantly. */
	static public ScaleByAction scaleBy (float amountX, float amountY, float amountZ) {
		return scaleBy(amountX, amountY, amountZ, 0, null);
	}

	static public ScaleByAction scaleBy (float amountX, float amountY, float amountZ, float duration) {
		return scaleBy(amountX, amountY, amountZ, duration, null);
	}

	static public ScaleByAction scaleBy (float amountX, float amountY, float amountZ, float duration, Interpolation interpolation) {
		ScaleByAction action = action(ScaleByAction.class);
		action.setAmount(amountX, amountY, amountZ);
		action.setDuration(duration);
		action.setInterpolation(interpolation);
		return action;
	}

	/** Rotates the actor instantly. */
	static public RotateToAction rotateTo (float rotation) {
		return rotateTo(rotation, 0, null);
	}

	static public RotateToAction rotateTo (float rotation, float duration) {
		return rotateTo(rotation, duration, null);
	}

	static public RotateToAction rotateTo (float rotation, float duration, Interpolation interpolation) {
		RotateToAction action = action(RotateToAction.class);
		action.setRotation(rotation);
		action.setDuration(duration);
		action.setInterpolation(interpolation);
		return action;
	}

	/** Rotates the actor instantly. */
	static public RotateByAction rotateBy (float rotationAmount) {
		return rotateBy(rotationAmount, 0, null);
	}

	static public RotateByAction rotateBy (float rotationAmount, float duration) {
		return rotateBy(rotationAmount, duration, null);
	}

	static public RotateByAction rotateBy (float rotationAmount, float duration, Interpolation interpolation) {
		RotateByAction action = action(RotateByAction.class);
		action.setAmount(rotationAmount);
		action.setDuration(duration);
		action.setInterpolation(interpolation);
		return action;
	}



	static public VisibleAction show () {
		return visible(true);
	}

	static public VisibleAction hide () {
		return visible(false);
	}

	static public VisibleAction visible (boolean visible) {
		VisibleAction action = action(VisibleAction.class);
		action.setVisible(visible);
		return action;
	}

	static public RemoveActorAction removeActor (Actor3d removeActor) {
		RemoveActorAction action = action(RemoveActorAction.class);
		action.setRemoveActor(removeActor);
		return action;
	}

	static public DelayAction delay (float duration) {
		DelayAction action = action(DelayAction.class);
		action.setDuration(duration);
		return action;
	}

	static public DelayAction delay (float duration, Action3d delayedAction) {
		DelayAction action = action(DelayAction.class);
		action.setDuration(duration);
		action.setAction(delayedAction);
		return action;
	}

	static public TimeScaleAction timeScale (float scale, Action3d scaledAction) {
		TimeScaleAction action = action(TimeScaleAction.class);
		action.setScale(scale);
		action.setAction(scaledAction);
		return action;
	}

	static public SequenceAction sequence (Action3d action1) {
		SequenceAction action = action(SequenceAction.class);
		action.addAction(action1);
		return action;
	}

	static public SequenceAction sequence (Action3d action1, Action3d action2) {
		SequenceAction action = action(SequenceAction.class);
		action.addAction(action1);
		action.addAction(action2);
		return action;
	}

	static public SequenceAction sequence (Action3d action1, Action3d action2, Action3d action3) {
		SequenceAction action = action(SequenceAction.class);
		action.addAction(action1);
		action.addAction(action2);
		action.addAction(action3);
		return action;
	}

	static public SequenceAction sequence (Action3d action1, Action3d action2, Action3d action3, Action3d action4) {
		SequenceAction action = action(SequenceAction.class);
		action.addAction(action1);
		action.addAction(action2);
		action.addAction(action3);
		action.addAction(action4);
		return action;
	}

	static public SequenceAction sequence (Action3d action1, Action3d action2, Action3d action3, Action3d action4, Action3d action5) {
		SequenceAction action = action(SequenceAction.class);
		action.addAction(action1);
		action.addAction(action2);
		action.addAction(action3);
		action.addAction(action4);
		action.addAction(action5);
		return action;
	}

	static public SequenceAction sequence (Action3d... actions) {
		SequenceAction action = action(SequenceAction.class);
		for (int i = 0, n = actions.length; i < n; i++)
			action.addAction(actions[i]);
		return action;
	}

	static public SequenceAction sequence () {
		return action(SequenceAction.class);
	}

	static public ParallelAction parallel (Action3d action1) {
		ParallelAction action = action(ParallelAction.class);
		action.addAction(action1);
		return action;
	}

	static public ParallelAction parallel (Action3d action1, Action3d action2) {
		ParallelAction action = action(ParallelAction.class);
		action.addAction(action1);
		action.addAction(action2);
		return action;
	}

	static public ParallelAction parallel (Action3d action1, Action3d action2, Action3d action3) {
		ParallelAction action = action(ParallelAction.class);
		action.addAction(action1);
		action.addAction(action2);
		action.addAction(action3);
		return action;
	}

	static public ParallelAction parallel (Action3d action1, Action3d action2, Action3d action3, Action3d action4) {
		ParallelAction action = action(ParallelAction.class);
		action.addAction(action1);
		action.addAction(action2);
		action.addAction(action3);
		action.addAction(action4);
		return action;
	}

	static public ParallelAction parallel (Action3d action1, Action3d action2, Action3d action3, Action3d action4, Action3d action5) {
		ParallelAction action = action(ParallelAction.class);
		action.addAction(action1);
		action.addAction(action2);
		action.addAction(action3);
		action.addAction(action4);
		action.addAction(action5);
		return action;
	}

	static public ParallelAction parallel (Action3d... actions) {
		ParallelAction action = action(ParallelAction.class);
		for (int i = 0, n = actions.length; i < n; i++)
			action.addAction(actions[i]);
		return action;
	}

	static public ParallelAction parallel () {
		return action(ParallelAction.class);
	}

	static public RepeatAction repeat (int count, Action3d repeatedAction) {
		RepeatAction action = action(RepeatAction.class);
		action.setCount(count);
		action.setAction(repeatedAction);
		return action;
	}

	static public RepeatAction forever (Action3d repeatedAction) {
		RepeatAction action = action(RepeatAction.class);
		action.setCount(RepeatAction.FOREVER);
		action.setAction(repeatedAction);
		return action;
	}

	static public RunnableAction run (Runnable runnable) {
		RunnableAction action = action(RunnableAction.class);
		action.setRunnable(runnable);
		return action;
	}

	static public AfterAction after (Action3d action) {
		AfterAction afterAction = action(AfterAction.class);
		afterAction.setAction(action);
		return afterAction;
	}

	public static class AddAction extends Action3d {
		private Actor3d targetActor;
		private Action3d action;

		@Override
		public boolean act (float delta) {
			(targetActor != null ? targetActor : actor3d).addAction3d(action);
			return true;
		}

		public Actor3d getTargetActor() {
			return targetActor;
		}

		/** Sets the actor to add an action to. If null (the default), the {@link #getActor() actor} will be used. */
		public void setTargetActor(Actor3d actor) {
			this.targetActor = actor;
		}

		public Action3d getAction () {
			return action;
		}

		public void setAction(Action3d action) {
			this.action = action;
		}

		public void restart () {
			if (action != null) action.restart();
		}

		@Override
		public void reset () {
			super.reset();
			targetActor = null;
			action = null;
		}
	}

	/** Executes an action only after all other actions on the actor at the time this action was added have finished.
	 * @author Nathan Sweet */
	 public static class AfterAction extends DelegateAction {
		private Array<Action3d> waitForActions = new Array<Action3d>(false, 4);

		@Override
		public void setActor3d(Actor3d actor3d) {
			if (actor3d != null) waitForActions.addAll(actor3d.getActions3d());
			super.setActor3d(actor3d);
		}

		@Override
		public void restart () {
			super.restart();
			waitForActions.clear();
		}

		@Override
		protected boolean delegate (float delta) {
			Array<Action3d> currentActions = actor3d.getActions3d();
			if (currentActions.size == 1) waitForActions.clear();
			for (int i = waitForActions.size - 1; i >= 0; i--) {
				Action3d action = waitForActions.get(i);
				int index = currentActions.indexOf(action, true);
				if (index == -1) waitForActions.removeIndex(i);
			}
			if (waitForActions.size > 0) return false;
			return action.act(delta);
		}
	 }

	 /** Delays execution of an action or inserts a pau
	  * 
	  * se in a {@link SequenceAction}.
	  * @author Nathan Sweet */
	 public static class DelayAction extends DelegateAction {
		 private float duration, time;

		 public DelayAction () {
		 }

		 public DelayAction (float duration) {
			 this.duration = duration;
		 }

		 @Override
		 protected boolean delegate (float delta) {
			 if (time < duration) {
				 time += delta;
				 if (time < duration) return false;
				 delta = time - duration;
			 }
			 if (action == null) return true;
			 return action.act(delta);
		 }

		 /** Causes the delay to be complete. */
		 public void finish () {
			 time = duration;
		 }

		 @Override
		 public void restart () {
			 super.restart();
			 time = 0;
		 }

		 /** Gets the time spent waiting for the delay. */
		 public float getTime () {
			 return time;
		 }

		 /** Sets the time spent waiting for the delay. */
		 public void setTime (float time) {
			 this.time = time;
		 }

		 public float getDuration () {
			 return duration;
		 }

		 /** Sets the length of the delay in seconds. */
		 public void setDuration (float duration) {
			 this.duration = duration;
		 }
	 }

	 /** Base class for an action that wraps another action.
	  * @author Nathan Sweet */
	 abstract public static class DelegateAction extends Action3d {
		 protected Action3d action;

		 /** Sets the wrapped action. */
		 public void setAction (Action3d action) {
			 this.action = action;
		 }

		 public Action3d getAction () {
			 return action;
		 }

		 abstract protected boolean delegate (float delta);

		 @Override
		 public final boolean act (float delta) {
			 Pool pool = getPool();
			 setPool(null); // Ensure this action can't be returned to the pool inside the delegate action.
			 try {
				 return delegate(delta);
			 } finally {
				 setPool(pool);
			 }
		 }

		 @Override
		 public void restart () {
			 if (action != null) action.restart();
		 }

		 @Override
		 public void reset () {
			 super.reset();
			 action = null;
		 }

		 @Override
		 public void setActor3d (Actor3d actor3d) {
			 if (action != null) action.setActor3d(actor3d);
			 super.setActor3d(actor3d);
		 }

		 @Override
		 public String toString () {
			 return super.toString() + (action == null ? "" : "(" + action + ")");
		 }
	 }

	 public static class FloatAction extends TemporalAction {
		 private float start, end;
		 private float value;

		 /** Creates a FloatAction that transitions from 0 to 1. */
		 public FloatAction () {
			 start = 0;
			 end = 1;
		 }

		 /** Creates a FloatAction that transitions from start to end. */
		 public FloatAction (float start, float end) {
			 this.start = start;
			 this.end = end;
		 }

		 @Override
		 protected void begin () {
			 value = start;
		 }

		 @Override
		 protected void update (float percent) {
			 value = start + (end - start) * percent;
		 }

		 /** Gets the current float value. */
		 public float getValue () {
			 return value;
		 }

		 /** Sets the current float value. */
		 public void setValue (float value) {
			 this.value = value;
		 }

		 public float getStart () {
			 return start;
		 }

		 /** Sets the value to transition from. */
		 public void setStart (float start) {
			 this.start = start;
		 }

		 public float getEnd () {
			 return end;
		 }

		 /** Sets the value to transition to. */
		 public void setEnd (float end) {
			 this.end = end;
		 }
	 }

	 /** An action that has an int, whose value is transitioned over time.
	  * @author Nathan Sweet */
	 public static class IntAction extends TemporalAction {
		 private int start, end;
		 private int value;

		 /** Creates an IntAction that transitions from 0 to 1. */
		 public IntAction () {
			 start = 0;
			 end = 1;
		 }

		 /** Creates an IntAction that transitions from start to end. */
		 public IntAction (int start, int end) {
			 this.start = start;
			 this.end = end;
		 }

		 @Override
		 protected void begin () {
			 value = start;
		 }

		 @Override
		 protected void update (float percent) {
			 value = (int)(start + (end - start) * percent);
		 }

		 /** Gets the current int value. */
		 public int getValue () {
			 return value;
		 }

		 /** Sets the current int value. */
		 public void setValue (int value) {
			 this.value = value;
		 }

		 public int getStart () {
			 return start;
		 }

		 /** Sets the value to transition from. */
		 public void setStart (int start) {
			 this.start = start;
		 }

		 public int getEnd () {
			 return end;
		 }

		 /** Sets the value to transition to. */
		 public void setEnd (int end) {
			 this.end = end;
		 }
	 }

	 /** Moves an actor to a relative position.
	  * @author Nathan Sweet */
	 public static class MoveByAction extends RelativeTemporalAction {
		 private float amountX, amountY, amountZ;

		 @Override
		 protected void updateRelative (float percentDelta) {
			 actor3d.translate(amountX * percentDelta, amountY * percentDelta, amountZ * percentDelta);
		 }

		 public void setAmount (float x, float y, float z) {
			 amountX = x;
			 amountY = y;
			 amountZ = z;
		 }

		 public float getAmountX () {
			 return amountX;
		 }

		 public void setAmountX (float x) {
			 amountX = x;
		 }

		 public float getAmountY () {
			 return amountY;
		 }

		 public void setAmountY (float y) {
			 amountY = y;
		 }

		 public float getAmountZ () {
			 return amountZ;
		 }

		 public void setAmountZ (float z) {
			 amountZ = z;
		 }
	 }

	 public static class MoveToAction extends TemporalAction {
		 private float startX, startY, startZ;
		 private float endX, endY, endZ;

		 @Override
		 protected void begin () {
			 if(actor3d != null){
				 startX = actor3d.getX();
				 startY = actor3d.getY();
				 startZ = actor3d.getZ();
			 }
		 }

		 @Override
		 protected void update (float percent) {
			 if(actor3d != null){
				 actor3d.setPosition(startX + (endX - startX) * percent, startY + (endY - startY) * percent,
						 startZ + (endZ - startZ) * percent);
			 }
		 }


		 public void setPosition (float x, float y, float z) {
			 endX = x;
			 endY = y;
			 endZ = z;
		 }

		 public float getX () {
			 return endX;
		 }

		 public void setX (float x) {
			 endX = x;
		 }

		 public float getY () {
			 return endY;
		 }

		 public void setY (float y) {
			 endY = y;
		 }

		 public float getZ () {
			 return endZ;
		 }

		 public void setZ (float z) {
			 endZ = z;
		 }
	 }

	 /** Executes a number of actions at the same time.
	  * @author Nathan Sweet */
	  public static class ParallelAction extends Action3d {
		 Array<Action3d> actions = new Array<Action3d>(4);
		 private boolean complete;

		 public ParallelAction () {
		 }

		 public ParallelAction (Action3d action1) {
			 addAction(action1);
		 }

		 public ParallelAction (Action3d action1, Action3d action2) {
			 addAction(action1);
			 addAction(action2);
		 }

		 public ParallelAction (Action3d action1, Action3d action2, Action3d action3) {
			 addAction(action1);
			 addAction(action2);
			 addAction(action3);
		 }

		 public ParallelAction (Action3d action1, Action3d action2, Action3d action3, Action3d action4) {
			 addAction(action1);
			 addAction(action2);
			 addAction(action3);
			 addAction(action4);
		 }

		 public ParallelAction (Action3d action1, Action3d action2, Action3d action3, Action3d action4, Action3d action5) {
			 addAction(action1);
			 addAction(action2);
			 addAction(action3);
			 addAction(action4);
			 addAction(action5);
		 }

		 @Override
		 public boolean act (float delta) {
			 if (complete) return true;
			 complete = true;
			 Pool pool = getPool();
			 setPool(null); // Ensure this action can't be returned to the pool while executing.
			 try {
				 Array<Action3d> actions = this.actions;
				 for (int i = 0, n = actions.size; i < n && actor3d != null; i++) {
					 if (!actions.get(i).act(delta)) complete = false;
					 if (actor3d == null) return true; // This action was removed.
				 }
				 return complete;
			 } finally {
				 setPool(pool);
			 }
		 }

		 @Override
		 public void restart () {
			 complete = false;
			 Array<Action3d> actions = this.actions;
			 for (int i = 0, n = actions.size; i < n; i++)
				 actions.get(i).restart();
		 }

		 @Override
		 public void reset () {
			 super.reset();
			 actions.clear();
		 }

		 public void addAction (Action3d action) {
			 actions.add(action);
			 if (actor3d != null) 
				 action.setActor3d(actor3d);
		 }

		 @Override
		 public void setActor3d(Actor3d actor) {
			 Array<Action3d> actions = this.actions;
			 for (int i = 0, n = actions.size; i < n; i++)
				 actions.get(i).setActor3d(actor);
			 super.setActor3d(actor);
		 }

		 public Array<Action3d> getActions () {
			 return actions;
		 }

		 @Override
		 public String toString () {
			 StringBuilder buffer = new StringBuilder(64);
			 buffer.append(super.toString());
			 buffer.append('(');
			 Array<Action3d> actions = this.actions;
			 for (int i = 0, n = actions.size; i < n; i++) {
				 if (i > 0) buffer.append(", ");
				 buffer.append(actions.get(i));
			 }
			 buffer.append(')');
			 return buffer.toString();
		 }
	  }

	  /** Base class for actions that transition over time using the percent complete since the last frame.
	   * @author Nathan Sweet */
	  abstract public static class RelativeTemporalAction extends TemporalAction {
		  private float lastPercent;

		  @Override
		  protected void begin () {
			  lastPercent = 0;
		  }

		  @Override
		  protected void update (float percent) {
			  updateRelative(percent - lastPercent);
			  lastPercent = percent;
		  }

		  abstract protected void updateRelative (float percentDelta);
	  }

	  /** Removes an action from an actor.
	   * @author Nathan Sweet */
	  public static class RemoveAction extends Action3d {
		  private Actor3d targetActor;
		  private Action3d action;

		  @Override
		  public boolean act (float delta) {
			  (targetActor != null ? targetActor : actor3d).removeAction3d(action);
			  return true;
		  }

		  public Actor3d getTargetActor () {
			  return targetActor;
		  }

		  /** Sets the actor to remove an action from. If null (the default), the {@link #getActor() actor} will be used. */
		  public void setTargetActor (Actor3d actor) {
			  this.targetActor = actor;
		  }

		  public Action3d getAction () {
			  return action;
		  }

		  public void setAction(Action3d action) {
			  this.action = action;
		  }

		  @Override
		  public void reset () {
			  super.reset();
			  targetActor = null;
			  action = null;
		  }
	  }

	  /** Removes an actor from the stage.
	   * @author Nathan Sweet */
	  public static class RemoveActorAction extends Action3d {
		  private Actor3d removeActor;
		  private boolean removed;

		  @Override
		  public boolean act (float delta) {
			  if (!removed) {
				  removed = true;
				  (removeActor != null ? removeActor : actor3d).remove();
			  }
			  return true;
		  }

		  @Override
		  public void restart () {
			  removed = false;
		  }

		  @Override
		  public void reset () {
			  super.reset();
			  removeActor = null;
		  }

		  public Actor3d getRemoveActor () {
			  return removeActor;
		  }

		  /** Sets the actor to remove. If null (the default), the {@link #getActor() actor} will be used. */
		  public void setRemoveActor (Actor3d removeActor) {
			  this.removeActor = removeActor;
		  }
	  }

	  /** Repeats an action a number of times or forever.
	   * @author Nathan Sweet */
	  public static class RepeatAction extends DelegateAction {
		  static public final int FOREVER = -1;

		  private int repeatCount, executedCount;
		  private boolean finished;

		  @Override
		  protected boolean delegate (float delta) {
			  if (executedCount == repeatCount) return true;
			  if (action.act(delta)) {
				  if (finished) return true;
				  if (repeatCount > 0) executedCount++;
				  if (executedCount == repeatCount) return true;
				  if (action != null) action.restart();
			  }
			  return false;
		  }

		  /** Causes the action to not repeat again. */
		  public void finish () {
			  finished = true;
		  }

		  @Override
		  public void restart () {
			  super.restart();
			  executedCount = 0;
			  finished = false;
		  }

		  /** Sets the number of times to repeat. Can be set to {@link #FOREVER}. */
		  public void setCount (int count) {
			  this.repeatCount = count;
		  }

		  public int getCount () {
			  return repeatCount;
		  }
	  }

	  /** Sets the actor's rotation from its current value to a relative value.
	   * @author Nathan Sweet */
	  public static class RotateByAction extends RelativeTemporalAction {
		  private float amount;

		  @Override
		  protected void updateRelative (float percentDelta) {
			  actor3d.rotate(amount * percentDelta);
		  }

		  public float getAmount () {
			  return amount;
		  }

		  public void setAmount (float rotationAmount) {
			  amount = rotationAmount;
		  }
	  }

	  /** Sets the actor's rotation from its current value to a specific value.
	   * @author Nathan Sweet */
	  public static class RotateToAction extends TemporalAction {
		  private float start, end;

		  @Override
		  protected void begin () {
			  start = actor3d.getRotation();
		  }

		  @Override
		  protected void update (float percent) {
			  actor3d.setRotation(start + (end - start) * percent);
		  }

		  public float getRotation () {
			  return end;
		  }

		  public void setRotation (float rotation) {
			  this.end = rotation;
		  }
	  }

	  /** An action that runs a {@link Runnable}. Alternatively, the {@link #run()} method can be overridden instead of setting a
	   * runnable.
	   * @author Nathan Sweet */
	  public static class RunnableAction extends Action3d {
		  private Runnable runnable;
		  private boolean ran;

		  @Override
		  public boolean act (float delta) {
			  if (!ran) {
				  ran = true;
				  run();
			  }
			  return true;
		  }

		  /** Called to run the runnable. */
		  public void run () {
			  Pool pool = getPool();
			  setPool(null); // Ensure this action can't be returned to the pool inside the runnable.
			  try {
				  runnable.run();
			  } finally {
				  setPool(pool);
			  }
		  }

		  @Override
		  public void restart () {
			  ran = false;
		  }

		  @Override
		  public void reset () {
			  super.reset();
			  runnable = null;
		  }

		  public Runnable getRunnable () {
			  return runnable;
		  }

		  public void setRunnable (Runnable runnable) {
			  this.runnable = runnable;
		  }
	  }

	  /** Scales an actor's scale to a relative size.
	   * @author Nathan Sweet */
	  public static class ScaleByAction extends RelativeTemporalAction {
		  private float amountX, amountY, amountZ;

		  @Override
		  protected void updateRelative (float percentDelta) {
			  actor3d.scale(amountX * percentDelta, amountY * percentDelta, amountZ * percentDelta);
		  }

		  public void setAmount (float x, float y, float z) {
			  amountX = x;
			  amountY = y;
			  amountZ = z;
		  }

		  public void setAmount (float scale) {
			  amountX = scale;
			  amountY = scale;
			  amountZ = scale;
		  }

		  public float getAmountX () {
			  return amountX;
		  }

		  public void setAmountX (float x) {
			  this.amountX = x;
		  }

		  public float getAmountY () {
			  return amountY;
		  }

		  public void setAmountY (float y) {
			  this.amountY = y;
		  }

		  public float getAmountZ () {
			  return amountZ;
		  }

		  public void setAmountZ (float z) {
			  this.amountZ = z;
		  }

	  }

	  /** Sets the actor's scale from its current value to a specific value.
	   * @author Nathan Sweet */
	  public static class ScaleToAction extends TemporalAction {
		  private float startX, startY, startZ;
		  private float endX, endY, endZ;

		  @Override
		  protected void begin () {
			  startX = actor3d.getScaleX();
			  startY = actor3d.getScaleY();
			  startZ = actor3d.getScaleZ();
		  }

		  @Override
		  protected void update (float percent) {
			  actor3d.setScale(startX + (endX - startX) * percent, startY + (endY - startY) * percent,
					  startZ + (endZ - startZ) * percent);
		  }

		  public void setScale (float x, float y, float z) {
			  endX = x;
			  endY = y;
			  endZ = z;
		  }

		  public void setScale (float scale) {
			  endX = scale;
			  endY = scale;
			  endZ = scale;
		  }

		  public float getX () {
			  return endX;
		  }

		  public void setX (float x) {
			  this.endX = x;
		  }

		  public float getY () {
			  return endY;
		  }

		  public void setY (float y) {
			  this.endY = y;
		  }

		  public float getZ () {
			  return endZ;
		  }

		  public void setZ (float z) {
			  this.endZ = z;
		  }
	  }

	  /** Executes a number of actions one at a time.
	   * @author Nathan Sweet */
	  public static class SequenceAction extends ParallelAction {
		  private int index;

		  public SequenceAction () {
		  }

		  public SequenceAction (Action3d action1) {
			  addAction(action1);
		  }

		  public SequenceAction (Action3d action1, Action3d action2) {
			  addAction(action1);
			  addAction(action2);
		  }

		  public SequenceAction (Action3d action1, Action3d action2, Action3d action3) {
			  addAction(action1);
			  addAction(action2);
			  addAction(action3);
		  }

		  public SequenceAction (Action3d action1, Action3d action2, Action3d action3, Action3d action4) {
			  addAction(action1);
			  addAction(action2);
			  addAction(action3);
			  addAction(action4);
		  }

		  public SequenceAction (Action3d action1, Action3d action2, Action3d action3, Action3d action4, Action3d action5) {
			  addAction(action1);
			  addAction(action2);
			  addAction(action3);
			  addAction(action4);
			  addAction(action5);
		  }

		  @Override
		  public boolean act (float delta) {
			  if (index >= actions.size) return true;
			  Pool<Action3d> pool = getPool();
			  setPool(null); // Ensure this action can't be returned to the pool while executings.
			  try {
				  if (actions.get(index).act(delta)) {
					  if (actor3d == null) return true; // This action was removed.
					  index++;
					  if (index >= actions.size) return true;
				  }
				  return false;
			  } finally {
				  setPool(pool);
			  }
		  }

		  @Override
		  public void restart () {
			  super.restart();
			  index = 0;
		  }
	  }

	  /** Base class for actions that transition over time using the percent complete.
	   * @author Nathan Sweet */
	  abstract public static class TemporalAction extends Action3d {
		  private float duration, time;
		  private Interpolation interpolation;
		  private boolean reverse, began, complete;

		  public TemporalAction () {
		  }

		  public TemporalAction (float duration) {
			  this.duration = duration;
		  }

		  public TemporalAction (float duration, Interpolation interpolation) {
			  this.duration = duration;
			  this.interpolation = interpolation;
		  }

		  @Override
		  public boolean act (float delta) {
			  if (complete) return true;
			  Pool pool = getPool();
			  setPool(null); // Ensure this action can't be returned to the pool while executing.
			  try {
				  if (!began) {
					  begin();
					  began = true;
				  }
				  time += delta;
				  complete = time >= duration;
				  float percent;
				  if (complete)
					  percent = 1;
				  else {
					  percent = time / duration;
					  if (interpolation != null) percent = interpolation.apply(percent);
				  }
				  update(reverse ? 1 - percent : percent);
				  if (complete) end();
				  return complete;
			  } finally {
				  setPool(pool);
			  }
		  }

		  /** Called the first time {@link #act(float)} is called. This is a good place to query the {@link #actor actor's} starting
		   * state. */
		  protected void begin () {
		  }

		  /** Called the last time {@link #act(float)} is called. */
		  protected void end () {
		  }

		  /** Called each frame.
		   * @param percent The percentage of completion for this action, growing from 0 to 1 over the duration. If
		   *           {@link #setReverse(boolean) reversed}, this will shrink from 1 to 0. */
		  abstract protected void update (float percent);

		  /** Skips to the end of the transition. */
		  public void finish () {
			  time = duration;
		  }

		  @Override
		  public void restart () {
			  time = 0;
			  began = false;
			  complete = false;
		  }

		  @Override
		  public void reset () {
			  super.reset();
			  reverse = false;
			  interpolation = null;
		  }

		  /** Gets the transition time so far. */
		  public float getTime () {
			  return time;
		  }

		  /** Sets the transition time so far. */
		  public void setTime (float time) {
			  this.time = time;
		  }

		  public float getDuration () {
			  return duration;
		  }

		  /** Sets the length of the transition in seconds. */
		  public void setDuration (float duration) {
			  this.duration = duration;
		  }

		  public Interpolation getInterpolation () {
			  return interpolation;
		  }

		  public void setInterpolation (Interpolation interpolation) {
			  this.interpolation = interpolation;
		  }

		  public boolean isReverse () {
			  return reverse;
		  }

		  /** When true, the action's progress will go from 100% to 0%. */
		  public void setReverse (boolean reverse) {
			  this.reverse = reverse;
		  }
	  }

	  /** Multiplies the delta of an action.
	   * @author Nathan Sweet */
	  public static class TimeScaleAction extends DelegateAction {
		  private float scale;

		  @Override
		  protected boolean delegate (float delta) {
			  if (action == null) return true;
			  return action.act(delta * scale);
		  }

		  public float getScale () {
			  return scale;
		  }

		  public void setScale (float scale) {
			  this.scale = scale;
		  }
	  }

	  /** Sets the actor's {@link Actor#setVisible(boolean) visibility}.
	   * @author Nathan Sweet */
	  public static class VisibleAction extends Action3d {
		  private boolean visible;

		  @Override
		  public boolean act (float delta) {
			  actor3d.setVisible(visible);
			  return true;
		  }

		  public boolean isVisible () {
			  return visible;
		  }

		  public void setVisible (boolean visible) {
			  this.visible = visible;
		  }
	  }
}

abstract class Action3d implements Poolable {
    /** The actor this action is attached to, or null if it is not attached. */
    protected Actor3d actor3d;

    private Pool<Action3d> pool;

    /** Updates the action based on time. Typically this is called each frame by {@link Actor#act(float)}.
     * @param delta Time in seconds since the last frame.
     * @return true if the action is done. This method may continue to be called after the action is done. */
    abstract public boolean act (float delta);

    /** Sets the state of the action so it can be run again. */
    public void restart () {
    }

    /** @return null if the action is not attached to an actor. */
    public Actor3d getActor3d() {
            return actor3d;
    }

    /** Sets the actor this action will be used for. This is called automatically when an action is added to an actor. This is also
     * called with null when an action is removed from an actor. When set to null, if the action has a {@link #setPool(Pool) pool}
     * then the action is {@link Pool#free(Object) returned} to the pool (which calls {@link #reset()}) and the pool is set to null.
     * If the action does not have a pool, {@link #reset()} is not called.
     * <p>
     * This method is not typically a good place for a subclass to query the actor's state because the action may not be executed
     * for some time, eg it may be {@link DelayAction delayed}. The actor's state is best queried in the first call to
     * {@link #act(float)}. For a {@link TemporalAction}, use TemporalAction#begin(). */
    public void setActor3d(Actor3d actor) {
            this.actor3d = actor;
            if (actor == null) {
                    if (pool != null) {
                            pool.free(this);
                            pool = null;
                    }
            }
    }

    /** Resets the optional state of this action to as if it were newly created, allowing the action to be pooled and reused. State
     * required to be set for every usage of this action or computed during the action does not need to be reset.
     * <p>
     * The default implementation calls {@link #restart()}.
     * <p>
     * If a subclass has optional state, it must override this method, call super, and reset the optional state. */
    public void reset () {
            restart();
    }

    public Pool getPool () {
            return pool;
    }

    /** Sets the pool that the action will be returned to when removed from the actor.
     * @param pool May be null.
     * @see #setActor(Actor) */
    public void setPool (Pool pool) {
            this.pool = pool;
    }

    public String toString () {
            String name = getClass().getName();
            int dotIndex = name.lastIndexOf('.');
            if (dotIndex != -1) name = name.substring(dotIndex + 1);
            if (name.endsWith("Action")) name = name.substring(0, name.length() - 6);
            return name;
    }
}