# ViewAnimatorFramework
This repository consist of one single class, which has a lot of different methods to help you build view animation with views using Animator framework.

This class has static utility methods, which makes creation of standard animations much easier. 
Lets look at some more examples.
Scale animation from 0 to 1 looks like:
```java
View view = ...
ViewAnimatorFramework.SCALE.getAnimator(view).start();
```
Appering image with alpha animation:
```java
ViewAnimatorFramework.ALPHA.getAnimator(mImage, 0f, 1f, duration, 0, new AccelerateInterpolator(), null).start();
```

It also helps you to create complex set of animations in builder-style:
```java
View view = ...
ViewAnimatorFramework.ViewBuilder viewBuilder = new ViewAnimatorFramework.ViewBuilder(view);
Animator imageAnimator = viewBuilder
        .animationTypes(View.ALPHA, View.ROTATION)
        .durations(200, 400)
        .fromValues(0, 0.5f)
        .toValues(1, 0.8f)
        .build();
imageAnimator.start();
```
Or for more complex animations use animators method:
```java
ViewAnimatorFramework.ViewBuilder imageBuilder = new ViewAnimatorFramework.ViewBuilder(mImage);
Animator imageAnimator = imageBuilder
        .animators(ViewAnimatorFramework.ALPHA.getAnimator(mImage, 0f, 1f, duration, 0, new AccelerateInterpolator(), null))
        .animators(ViewAnimatorFramework.EFFECTS.bounceAnimator(mImage, duration, duration))
        .build();
imageAnimator.start();
```

And more, you can use your another builder to animate group of views:
```java
ViewAnimatorFramework.GroupBuilder builder = new ViewAnimatorFramework.GroupBuilder();
builder.views(view1, view2, view3)
        .animationTypes(View.ALPHA)
        .durations(2000)
        .delays(0, 100, 200)
        .fromValues(0.5f, 0.3f, 0.2f)
        .toValues(1f)
        .callback(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(@NonNull Animator animation) {
                super.onAnimationEnd(animation);
                //do something here
            }
         })
        .build().start();
```
And again you can use you animators if you want something more complicated:
```java
ViewAnimatorFramework.ViewBuilder imageBuilder = new ViewAnimatorFramework.ViewBuilder(mImage);
Animator imageAnimator = imageBuilder
        .animators(ViewAnimatorFramework.ALPHA.getAnimator(mImage, 0f, 1f, duration, 0, new AccelerateInterpolator(), null))
        .animators(ViewAnimatorFramework.EFFECTS.bounceAnimator(mImage, duration, duration))
        .build();
        
Animator.AnimatorListener listener = new AnimatorListenerAdapter() {
    @Override
    public void onAnimationStart(@NonNull Animator animation) {
        super.onAnimationStart(animation);
        //do something here
    }
};

Animator animator1 = ViewAnimatorFramework.ALPHA.getAnimator(view1, duration, delay * 2);
Animator animator2 = ViewAnimatorFramework.ALPHA.getAnimator(view2, duration, delay * 3);

ViewAnimatorFramework.GroupBuilder groupBuilder = new ViewAnimatorFramework.GroupBuilder();
groupBuilder
        .animators(imageAnimator, animator1, animator2)
        .callback(listener)
        .build()
        .start();
```

With such API it's much easier to create new beautiful animations. Here is an example from API itself:
```java
@NonNull
public static Animator bounceAnimator(View view, int duration, int delay) {
    return new GroupBuilder()
            .animators(SCALE_X.getAnimator(view, 0.9f, 1f, duration, delay, new BounceInterpolator(), null))
            .animators(SCALE_Y.getAnimator(view, 0.9f, 1f, duration, delay, new BounceInterpolator(), null))
            .build();
}
```
Or even simplier:
```java
@NonNull
public static Animator bounceAnimator(View view, int duration, int delay) {
    return new GroupBuilder()
            .animators(SCALE.getAnimator(view, 0.9f, 1f, duration, delay, new BounceInterpolator(), null))
            .build();
}
```

Feel free to use it and enjoy!
