package scene2d;
import com.badlogic.gdx.math.Interpolation;


public enum InterpolationType {
	Bounce, BounceIn, BounceOut, Circle, CircleIn, CircleOut, 
	Elastic, ElasticIn, ElasticOut, Exp10, Exp10In, Exp10Out, 
	Exp5, Exp5In, Exp5Out, Linear, Fade,
	Pow2, Pow2In, Pow2Out, Pow3, Pow3In, Pow3Out,
	Pow4, Pow4In, pow4Out, Pow5, Pow5In, Pow5Out, 
	Sine, SineIn, SineOut, Swing, SwingIn, SwingOut;
	
	public static InterpolationType getInterpolationType(Interpolation interpolation){
		for(int i=0;i<Effect.interpolationsValue.length;i++)
			if(Effect.interpolationsValue[i].equals(getInterpolation(InterpolationType.values()[i])))
				return InterpolationType.values()[i];
		return InterpolationType.Linear;
	}
	
	public static Interpolation getInterpolation(InterpolationType type){
		for(int i=0;i<InterpolationType.values().length;i++)
			if(type.equals(InterpolationType.values()[i]))
					return Effect.interpolationsValue[i];
		return Interpolation.linear;
	}
	
	public static Interpolation getInterpolation(String typename){
		return getInterpolation(InterpolationType.valueOf(typename));
	}
}