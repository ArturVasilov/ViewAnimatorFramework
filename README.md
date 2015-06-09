# ViewAnimatorFramework
This repository consist of one single class, which has a lot of different methods to help you build view animation with views using Animator framework.

This class has static utility methods, which makes creation of standard animations much easier. e.g. scale animation from 0 to 1 looks like:
```java
View view = ...
ViewAnimatorFramework.SCALE.getAnimator(view).start();
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
        .add(ViewAnimatorFramework.SCALE.getAnimator(view))
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
            }
         })
        .animate();
```
