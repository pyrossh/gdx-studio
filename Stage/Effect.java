import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

/*
 *  <p>
 *  The EffectFactory allows to do magic effects on your actors.
 *  Effects like scale in/out, shake, fade in/out are supported.
 *  <p>
 */
public class Effect {

	public static void createEffect(Actor actor, EffectType effectType, float value,
			float duration, InterpolationType type){
		if(actor == null)
			return;
		Interpolation interp = InterpolationType.getInterpolation(type);
		switch(effectType){
			case FadeInOut:
				actor.addAction(fadeInOut(value, duration, interp));
				break;
			case FadeInOutLooping:
				actor.addAction(Actions.forever(fadeInOut(value, duration, interp)));
				break;
			case FadeInOutRemoveActor:
				break;
			case ScaleInOut:
				actor.addAction(scaleInOut(value, duration, interp));
				break;
			case ScaleInOutLooping:
				actor.addAction(Actions.forever(scaleInOut(value, duration, interp)));
				break;
			case ScaleInOutRemoveActor:
				actor.addAction(Actions.sequence(scaleInOut(value, duration, interp), Actions.removeActor(actor)));
				break;
			case ShakeInOut:
				actor.addAction(shakeInOut(value, duration, interp));
				break;
			case ShakeInOutLooping:
				actor.addAction(Actions.forever(shakeInOut(value, duration, interp)));
				break;
			case ShakeInOutRemoveActor:
				actor.addAction(Actions.sequence(shakeInOut(value, duration, interp), Actions.removeActor(actor)));
				break;
				
			case ScaleToThenFadeOut:
				actor.addAction(Actions.sequence(Actions.scaleTo(value, value, duration, interp)));
				break;
				
			case PatrolX:
				actor.addAction(Actions.forever(Actions.sequence(Actions.moveBy(value, 0, duration, interp),
						Actions.moveBy(-value, 0, duration, interp))));
				break;
			
			case PatrolY:
				actor.addAction(Actions.forever(Actions.sequence(Actions.moveBy(0, value, duration, interp),
						Actions.moveBy(0, -value, duration, interp))));
				break;
				
			case None:
				break;
			default:
				break;
		}
	}
	
	public static void createEffect(ImageJson imageJson){
		createEffect(imageJson, imageJson.effectType, imageJson.effectValue, imageJson.effectDuration, 
				imageJson.interpolationType);
	}

	
	public static Action scaleInOut(float value, float duration, Interpolation interp){
		return Actions.sequence(Actions.scaleTo(value, value, duration, interp), Actions.scaleTo(1, 1, duration, interp));
	}
	
	public static Action shakeInOut(float value, float duration, Interpolation interp){
		return Actions.sequence(Actions.rotateTo(value, duration, interp), Actions.rotateTo(-value, duration, interp),
				Actions.rotateTo(0, duration, interp));
	}
	
	public static Action fadeInOut(float value, float duration, Interpolation interp){
		return Actions.sequence(Actions.fadeIn(duration, interp), Actions.fadeOut(duration, interp));
	}
	
	public static Action fadeOutIn(float value, float duration, Interpolation interp){
		return Actions.sequence(Actions.fadeOut(duration, interp), Actions.fadeIn(duration, interp));
	}

	/**
	 * Scale effect, Shake effect (SC, SHK)
	 * */
	public static void create_SC_SHK(Actor actor, float scaleRatioX,
			float scaleRatioY, float shakeAngle, float originalAngle,
			float duration, final boolean isRemoveActor) {
		if (actor != null) {
			actor.addAction(Actions.sequence(
					Actions.scaleTo(scaleRatioX, scaleRatioY, duration),
					Actions.rotateTo(shakeAngle, duration),
					Actions.rotateTo(-shakeAngle, duration),
					Actions.rotateTo(originalAngle, duration)));
		}
	}

	//
	// TRIPLE EFFECTS (Sequence - Chain reaction)
	// ################################################################
	/**
	 * Scale effect, Back To Normal, Fade Out (SC, BTN, FO)
	 * */
	public static void create_SC_BTN_FO(Actor actor, float scaleRatioX,
			float scaleRatioY, float duration, float delayBeforeFadeOut,
			final boolean isRemoveActor) {
		if (actor != null) {
			actor.addAction(Actions.sequence(
					Actions.scaleTo(scaleRatioX, scaleRatioY, duration),
					Actions.scaleTo(1, 1, duration),
					Actions.delay(delayBeforeFadeOut),
					Actions.fadeOut(duration)));
		}
	}

	/**
	 * Scale effect, Shake effect, Back To Normal (SC, SHK, BTN)
	 * */
	public static void create_SC_SHK_BTN(Actor actor, float scaleRatioX,
			float scaleRatioY, float shakeAngle, float originalAngle,
			float duration, final boolean isRemoveActor) {
		if (actor != null) {
			actor.addAction(Actions.sequence(
					Actions.scaleTo(scaleRatioX, scaleRatioY, duration),
					Actions.rotateTo(shakeAngle, duration),
					Actions.rotateTo(-shakeAngle, duration),
					Actions.rotateTo(originalAngle, duration),
					Actions.scaleTo(1, 1, duration)));
		}
	}

	static Interpolation[] interpolationsValue = {
		Interpolation.bounce, Interpolation.bounceIn,  Interpolation.bounceOut,  
		Interpolation.circle,  Interpolation.circleIn,  Interpolation.circleOut, 
		Interpolation.elastic,  Interpolation.elasticIn,  Interpolation.elasticOut, 
		Interpolation.exp10,  Interpolation.exp10In,  Interpolation.exp10Out, 
		Interpolation.exp5,  Interpolation.exp5In,  Interpolation.exp5Out, 
		Interpolation.linear,  Interpolation.fade,
		Interpolation.pow2,  Interpolation.pow2In,  Interpolation.pow2Out, 
		Interpolation.pow3,  Interpolation.pow3In,  Interpolation.pow3Out, 
		Interpolation.pow4,  Interpolation.pow4In,  Interpolation.pow4Out, 
		Interpolation.pow5,  Interpolation.pow5In,  Interpolation.pow5Out, 
		Interpolation.sine,  Interpolation.sineIn,  Interpolation.sineOut, 
		Interpolation.swing,  Interpolation.swingIn,  Interpolation.swingOut, 
	};
	
	/***********************************************************************************************************
	 * 					Transition Related Functions												   	       *
	 ************************************************************************************************************/		

	public static void transition(TransitionType transitionType, Actor actor, float duration, 
			InterpolationType interptype){
		switch(transitionType){
			case None: break;
			case leftToRight: transitionLeftToRight(actor, duration, interptype);break;
			case rightToLeft: transitionRightToLeft(actor, duration, interptype);break;
			case upToDown: transitionUpToDown(actor, duration, interptype);break;
			case downToUp: transitionDownToUp(actor, duration, interptype);break;
			case FadeIn: transitionFadeIn(actor, duration, interptype);break;
			case FadeOut: transitionFadeOut(actor, duration, interptype);break;
			case ScaleIn: transitionScaleIn(actor, duration, interptype);break;
			case ScaleOut: transitionScaleOut(actor, duration, interptype);break;
		}
	}

	private static void transitionLeftToRight(Actor actor, float duration, InterpolationType interptype){
		actor.setPosition(-999, 0);
		actor.addAction(Actions.moveTo(0, 0, duration, InterpolationType.getInterpolation(interptype)));
	}

	private static void transitionRightToLeft(Actor actor, float duration, InterpolationType interptype){
		actor.setPosition(999, 0);
		actor.addAction(Actions.moveTo(0, 0, duration, InterpolationType.getInterpolation(interptype)));
	}

	private static void transitionUpToDown(Actor actor, float duration, InterpolationType interptype){
		actor.setPosition(0, 999);
		actor.addAction(Actions.moveTo(0, 0, duration, InterpolationType.getInterpolation(interptype)));
	}

	private static void transitionDownToUp(Actor actor, float duration, InterpolationType interptype){
		actor.setPosition(0, -999);
		actor.addAction(Actions.moveTo(0, 0, duration, InterpolationType.getInterpolation(interptype)));
	}

	private static void transitionFadeIn(Actor actor, float duration, InterpolationType interptype){
		Color color = actor.getColor();
		color.a = 0f;
		actor.setColor(color);
		actor.addAction(Actions.fadeIn(duration, InterpolationType.getInterpolation(interptype)));
	}

	private static void transitionFadeOut(Actor actor, float duration, InterpolationType interptype){
		Action action2 = new Action(){
			@Override 
			public boolean act(float delta){
				Color color = actor.getColor();
				color.a = 1f;
				actor.setColor(color);
				return true;
			}
		};
		actor.addAction(Actions.sequence(Actions.fadeOut(duration, InterpolationType.getInterpolation(interptype)), action2));
	}

	private static void transitionScaleIn(Actor actor, float duration, InterpolationType interptype){
		actor.setScale(0, 0);
		actor.addAction(Actions.scaleTo(1, 1, duration, InterpolationType.getInterpolation(interptype)));
	}

	private static void transitionScaleOut(Actor actor, float duration, InterpolationType interptype){
		Action action2 = new Action(){
			@Override 
			public boolean act(float delta){
				actor.scale(1f);
				return true;
			}
		};
		actor.addAction(Actions.sequence(Actions.scaleTo(1, 1, duration, InterpolationType.getInterpolation(interptype)), action2));
	}
}