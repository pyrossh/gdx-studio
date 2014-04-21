package scene2d;

public enum EffectType {
	None,
	ScaleIn, ScaleOut, ScaleInOut, ScaleTo, ScaleToBack,
	ShakeIn, ShakeOut, ShakeInOut, ShakeTo, ShakeToBack,
	FadeIn, FadeOut, FadeInOut, FadeTo, FadeToBack,
	ScaleToThenFadeOut, ScaleAndFadeOut, ScaleAndFadeIn,
	
	SlideRight, SlideLeft, SlideUp, SlideDown,
	PatrolX, PatrolY;
}

enum EffectDuration {
	Once,
	OnceToAndBack,
	Looping,
	LoopingToAndBack
}