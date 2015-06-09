package ru.yota.android.animation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.Property;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;

import java.util.List;

import ru.yota.android.R;

/**
 * @author Artur Vasilov
 *
 * ViewAnimatorFramework class wraps the Android Animator Framework to
 * help you easier create standard animations or animate group of views.
 *
 * Readme is located here https://github.com/ArturVasilov/ViewAnimatorFramework/
 */
public class ViewAnimatorFramework {

    /**
     * This class is for animating group of views;
     * It uses the builder style.
     * The order of methods calls doesn't matter, animators created when you call build method.
     */
    public static class GroupBuilder {

        private View[] mViews;
        private Property<View, Float>[] mProperties;
        private float[] mFromValues;
        private float[] mToValues;
        private int[] mDurations;
        private int[] mDelays;
        private Interpolator[] mInterpolators;
        private Animator.AnimatorListener mCallback;

        @NonNull
        private final AnimatorSet mAnimatorSet;

        private boolean mWasAnimated = false;

        public GroupBuilder() {
            mAnimatorSet = new AnimatorSet();
        }

        /**
         * Set views needed to be animated;
         * This isn't the only way, you can use animators method to add animations.
         * @param views - views to animate.
         */
        public GroupBuilder views(View... views) {
            mViews = views;
            return this;
        }

        /**
         * Sets properties of views to be animated;
         * Note: this method allows you to assign only one property for each view,
         * if you need to animators more than one property,
         * use {@link ru.yota.android.animation.ViewAnimatorFramework.ViewBuilder} class and animators method.
         *
         * @param properties - properties of each view to be animated.
         *                   If there are less properties than views, the first param will be used;
         *                   So, if you need to animate scaleY for all views, you can only pass in once;
         *                   The default property (if you pass nothing) is alpha.
         */
        @SafeVarargs
        public final GroupBuilder animationTypes(Property<View, Float>... properties) {
            mProperties = properties;
            return this;
        }

        /**
         * Sets start values of animated properties;
         *
         * @param fromValues - start values of each view to be animated.
         *                   If there are less values than views, the first param will be used;
         *                   The default value (if you pass nothing) is 0.
         */
        public GroupBuilder fromValues(float... fromValues) {
            mFromValues = fromValues;
            return this;
        }

        /**
         * Sets end values of animated properties;
         *
         * @param toValues - end values of each view to be animated.
         *                 If there are less values than views, the first param will be used;
         *                 The default value (if you pass nothing) is 1.
         */
        public GroupBuilder toValues(float... toValues) {
            mToValues = toValues;
            return this;
        }

        /**
         * Sets duration of each animation;
         *
         * @param durations - duration of each animation.
         *                   If there are less values than views, the first param will be used;
         *                   The default value (if you pass nothing) is standard Android appear duration.
         */
        public GroupBuilder durations(int... durations) {
            mDurations = durations;
            return this;
        }

        /**
         * Sets delay of each animation;
         *
         * @param delays - delay of each animation.
         *               If there are less values than views, the first param will be used;
         *               The default value (if you pass nothing) is 0.
         */
        public GroupBuilder delays(int... delays) {
            mDelays = delays;
            return this;
        }

        /**
         * Sets interpolator of each animation;
         *
         * @param interpolators - interpolator of each animation.
         *               If there are less interpolators than views, the first param will be used;
         *               The default value (if you pass nothing) is AccelerateDecelerateInterpolator.
         */
        public GroupBuilder interpolators(Interpolator[] interpolators) {
            mInterpolators = interpolators;
            return this;
        }

        /**
         * Assigns callback to animation, which will be the last in the group.
         * It is guaranteed that callback will be assigned to the last animation.
         * @param callback - callback, which will be assigned to last animation in group.
         */
        public GroupBuilder callback(Animator.AnimatorListener callback) {
            mCallback = callback;
            return this;
        }

        /**
         * Add already defined animators to animators set.
         * There is no customization, except callback, you must configure this animators by yourself.
         * Perfectly fine to use {@link ru.yota.android.animation.ViewAnimatorFramework.ViewBuilder} class to create animators
         * @param animators - array of animators to add to animators set.
         */
        public GroupBuilder animators(Animator... animators) {
            if (animators == null || animators.length == 0) {
                return this;
            }
            for (Animator animator : animators) {
                mAnimatorSet.play(animator);
            }
            return this;
        }

        /**
         * Creates Animator object from all previously passed values.
         * @return animator which is ready to start.
         */
        public Animator build() {
            if ((mWasAnimated) || (mAnimatorSet.isRunning())) {
                return mAnimatorSet;
            }
            if ((mViews != null) && (mViews.length > 0)) {
                Resources resources = mViews[0].getResources();
                for (int i = 0; i < mViews.length; i++) {
                    createAnimator(resources, i);
                }
            }
            mWasAnimated = true;
            assignCallback();
            release();
            return mAnimatorSet;
        }

        private void createAnimator(Resources resources, int i) {
            View view = mViews[i];
            Property<View, Float> property = View.ALPHA;
            if (mProperties != null && mProperties.length > 0) {
                property = mProperties.length > i ? mProperties[i] : mProperties[0];
            }
            float fromValue = 0f;
            if (mFromValues != null && mFromValues.length > 0) {
                fromValue = mFromValues.length > i ? mFromValues[i] : mFromValues[0];
            }
            float toValue = 1f;
            if (mToValues != null && mToValues.length > 0) {
                toValue = mToValues.length > i ? mToValues[i] : mToValues[0];
            }
            int duration = resources.getInteger(R.integer.appearing_animation_duration);
            if (mDurations != null && mDurations.length > 0) {
                duration = mDurations.length > i ? mDurations[i] : mDurations[0];
            }
            int delay = 0;
            if (mDelays != null && mDelays.length > 0) {
                delay = mDelays.length > i ? mDelays[i] : mDelays[0];
            }
            Interpolator interpolator = new AccelerateDecelerateInterpolator();
            if (mInterpolators != null && mInterpolators.length > 0) {
                interpolator = mInterpolators.length > i ? mInterpolators[i] : mInterpolators[0];
            }
            ObjectAnimator animator = ObjectAnimator.ofFloat(view, property, fromValue, toValue);
            animator.setDuration(duration);
            animator.setStartDelay(delay);
            animator.setInterpolator(interpolator);
            mAnimatorSet.play(animator);
        }

        private void assignCallback() {
            List<Animator> animators = mAnimatorSet.getChildAnimations();
            if (mCallback == null || animators.isEmpty()) {
                return;
            }
            int max = 0;
            int maxIndex = -1;
            for (int i = 0; i < animators.size(); i++) {
                Animator animator = animators.get(i);
                int value = (int) (animator.getDuration() + animator.getStartDelay());
                if (value > max) {
                    max = value;
                    maxIndex = i;
                }
            }
            if (maxIndex > 0) {
                Animator animator = animators.get(maxIndex);
                animator.addListener(mCallback);
            }
        }

        private void release() {
            mViews = null;
            mProperties = null;
            mFromValues = null;
            mToValues = null;
            mDurations = null;
            mDelays = null;
        }
    }

    /**
     * This class allows you to create complex set of animations with one view.
     * Its' structure is the same as {@link ru.yota.android.animation.ViewAnimatorFramework.GroupBuilder} class.
     * And again, the order of methods calls doesn't matter, animators created when you call build method.
     */
    public static class ViewBuilder {

        @NonNull
        private final View mView;

        @NonNull
        private final AnimatorSet mAnimatorSet;

        private Property<View, Float>[] mProperties;
        private float[] mFromValues;
        private float[] mToValues;
        private int[] mDurations;
        private int[] mDelays;
        private Interpolator[] mInterpolators;
        private Animator.AnimatorListener mCallback;

        private boolean mWasBuilt = false;

        /**
         * @param view to be animated
         */
        public ViewBuilder(@NonNull View view) {
            this.mView = view;
            mAnimatorSet = new AnimatorSet();
        }

        /**
         * Sets the properties of view to be animated;
         *
         * @param properties - all properties of to be animated.
         */
        @SafeVarargs
        public final ViewBuilder animationTypes(Property<View, Float>... properties) {
            mProperties = properties;
            return this;
        }

        /**
         * Sets the start values of animated properties;
         *
         * @param fromValues - start values of each property to be animated.
         *                   If there are less values than properties, the first param will be used;
         *                   The default value (if you pass nothing) is 0.
         */
        public ViewBuilder fromValues(float... fromValues) {
            mFromValues = fromValues;
            return this;
        }

        /**
         * Sets the end values of animated properties;
         *
         * @param toValues - end values of each property to be animated.
         *                   If there are less values than properties, the first param will be used;
         *                   The default value (if you pass nothing) is 1.
         */
        public ViewBuilder toValues(float... toValues) {
            mToValues = toValues;
            return this;
        }

        /**
         * Sets duration of each property animation;
         *
         * @param durations - duration of each property animation.
         *                   If there are values than properties, the first param will be used;
         *                   The default value (if you pass nothing) is standard Android appear duration.
         */
        public ViewBuilder durations(int... durations) {
            mDurations = durations;
            return this;
        }

        /**
         * Sets delay of each animation;
         *
         * @param delays - delay of each animation.
         *               If there are less values than properties, the first param will be used;
         *               The default value (if you pass nothing) is 0.
         */
        public ViewBuilder delays(int... delays) {
            mDelays = delays;
            return this;
        }

        /**
         * Sets interpolator of each property animation;
         *
         * @param interpolators - interpolator of each property animation.
         *               If there are less interpolators than properties, the first param will be used;
         *               The default value (if you pass nothing) is AccelerateDecelerateInterpolator.
         */
        public ViewBuilder interpolators(Interpolator[] interpolators) {
            mInterpolators = interpolators;
            return this;
        }

        /**
         * Assigns callback to animation, which will be the last in the group.
         * It is guaranteed that callback will be assigned to the last animation.
         * @param callback - callback, which will be assigned to last animation in group.
         */
        public ViewBuilder callback(Animator.AnimatorListener callback) {
            mCallback = callback;
            return this;
        }

        /**
         * Add already defined animator to animator set.
         * There is no customization, except callback, you must configure this animator by yourself.
         * @param animators - array of animators to add to animator set.
         */
        public ViewBuilder animators(Animator... animators) {
            if (animators == null || animators.length == 0) {
                return this;
            }
            for (Animator animator : animators) {
                mAnimatorSet.play(animator);
            }
            return this;
        }

        /**
         * Creates Animator object from all previously passed values.
         * @return animator which is ready to start.
         */
        public Animator build() {
            if ((mWasBuilt) || (mAnimatorSet.isRunning())) {
                return mAnimatorSet;
            }
            if (mProperties != null && mProperties.length > 0) {
                for (int i = 0; i < mProperties.length; i++) {
                    addPropertyAnimator(i);
                }
            }
            assignCallback();
            release();
            mWasBuilt = true;
            return mAnimatorSet;
        }

        private void addPropertyAnimator(int i) {
            Property<View, Float> property = mProperties[i];
            float fromValue = 0f;
            if (mFromValues != null && mFromValues.length > 0) {
                fromValue = mFromValues.length > i ? mFromValues[i] : mFromValues[0];
            }
            float toValue = 1f;
            if (mToValues != null && mToValues.length > 0) {
                toValue = mToValues.length > i ? mToValues[i] : mToValues[0];
            }
            int duration = mView.getResources().getInteger(R.integer.appearing_animation_duration);
            if (mDurations != null && mDurations.length > 0) {
                duration = mDurations.length > i ? mDurations[i] : mDurations[0];
            }
            int delay = 0;
            if (mDelays != null && mDelays.length > 0) {
                delay = mDelays.length > i ? mDelays[i] : mDelays[0];
            }
            Interpolator interpolator = new AccelerateDecelerateInterpolator();
            if (mInterpolators != null && mInterpolators.length > 0) {
                interpolator = mInterpolators.length > i ? mInterpolators[i] : mInterpolators[0];
            }
            ObjectAnimator animator = ObjectAnimator.ofFloat(mView, property, fromValue, toValue);
            animator.setDuration(duration);
            animator.setStartDelay(delay);
            animator.setInterpolator(interpolator);
            mAnimatorSet.play(animator);
        }

        private void assignCallback() {
            List<Animator> animators = mAnimatorSet.getChildAnimations();
            if (mCallback == null || animators.isEmpty()) {
                return;
            }
            int max = 0;
            int maxIndex = -1;
            for (int i = 0; i < animators.size(); i++) {
                Animator animator = animators.get(i);
                int value = (int) (animator.getDuration() + animator.getStartDelay());
                if (value > max) {
                    max = value;
                    maxIndex = i;
                }
            }
            if (maxIndex > 0) {
                Animator animator = animators.get(maxIndex);
                animator.addListener(mCallback);
            }
        }

        private void release() {
            mProperties = null;
            mFromValues = null;
            mToValues = null;
            mDurations = null;
            mDelays = null;
        }
    }

    /**
     * This class provides group of static helper method to create single view alpha animations.
     */
    public static class ALPHA {

        /**
         * The fully parametrized method. Other methods just call it with default values of params.
         * @param view - view to be animated.
         * @param fromValue - start alpha; default value is 0.
         * @param toValue - end alpha; default values is 1.
         * @param duration - duration of the animation; default value is standard Android appearance time.
         * @param delay - start delay of the animation; default value is 0.
         * @param interpolator - interpolator for the animation. Default is AccelerateDecelerateInterpolator.
         * @param listener - callback for the animation.
         * @return animator, which is read to start.
         */
        @NonNull
        public static Animator getAnimator(@NonNull View view, float fromValue, float toValue,
                                           int duration, int delay, Interpolator interpolator,
                                           Animator.AnimatorListener listener) {
            view.setAlpha(fromValue);
            return ViewAnimatorFramework.getAnimator(View.ALPHA, view, fromValue, toValue,
                    duration, delay, interpolator, listener);
        }

        @NonNull
        public static Animator getAnimator(@NonNull View view, float fromValue, float toValue,
                                           int duration, int delay) {
            return getAnimator(view, fromValue, toValue, duration, delay, null, null);
        }

        @NonNull
        public static Animator getAnimator(@NonNull View view, float fromValue, float toValue) {
            int duration = view.getResources().getInteger(R.integer.appearing_animation_delay);
            return getAnimator(view, fromValue, toValue, duration, 0, null, null);
        }

        @NonNull
        public static Animator getAnimator(@NonNull View view, int duration, int delay) {
            float fromValue = 0f;
            float toValue = 1f;
            return getAnimator(view, fromValue, toValue, duration, delay, null, null);
        }

        @NonNull
        public static Animator getAnimator(@NonNull View view, float fromValue, float toValue, Interpolator interpolator) {
            int duration = view.getResources().getInteger(R.integer.appearing_animation_delay);
            return getAnimator(view, fromValue, toValue, duration, 0, interpolator, null);
        }

        @NonNull
        public static Animator getAnimator(@NonNull View view, int duration, int delay, Interpolator interpolator) {
            float fromValue = 0f;
            float toValue = 1f;
            return getAnimator(view, fromValue, toValue, duration, delay, interpolator, null);
        }

        @NonNull
        public static Animator getAnimator(@NonNull View view, boolean appear) {
            int duration = view.getResources().getInteger(R.integer.appearing_animation_delay);
            float fromValue;
            float toValue;
            if (appear) {
                fromValue = 0f;
                toValue = 1f;
            }
            else {
                fromValue = 1f;
                toValue = 0f;
            }
            return getAnimator(view, fromValue, toValue, duration, 0, null, null);
        }

        @NonNull
        public static Animator getAnimator(@NonNull View view) {
            int duration = view.getResources().getInteger(R.integer.appearing_animation_delay);
            float fromValue = 0f;
            float toValue = 1f;
            return getAnimator(view, fromValue, toValue, duration, 0, null, null);
        }

    }

    /**
     * This class provides group of static helper method to create single view SCALE_X animations.
     */
    public static class SCALE_X {

        /**
         * The fully parametrized method. Other methods just call it with default values of params.
         * @param view - view to be animated.
         * @param fromValue - start scaleX value; default value is 0.
         * @param toValue - end scaleX value; default values is 1.
         * @param duration - duration of the animation; default value is standard Android appearance time.
         * @param delay - start delay of the animation; default value is 0.
         * @param interpolator - interpolator for the animation. Default is AccelerateDecelerateInterpolator.
         * @param listener - callback for the animation.
         * @return animator, which is read to start.
         */
        @NonNull
        public static Animator getAnimator(@NonNull View view, float fromValue, float toValue,
                                           int duration, int delay, Interpolator interpolator,
                                           Animator.AnimatorListener listener) {
            view.setScaleX(fromValue);
            return ViewAnimatorFramework.getAnimator(View.SCALE_X, view, fromValue, toValue,
                    duration, delay, interpolator, listener);
        }

        @NonNull
        public static Animator getAnimator(@NonNull View view, float fromValue, float toValue,
                                           int duration, int delay) {
            return getAnimator(view, fromValue, toValue, duration, delay, null, null);
        }

        @NonNull
        public static Animator getAnimator(@NonNull View view, float fromValue, float toValue) {
            int duration = view.getResources().getInteger(R.integer.appearing_animation_delay);
            return getAnimator(view, fromValue, toValue, duration, 0, null, null);
        }

        @NonNull
        public static Animator getAnimator(@NonNull View view, int duration, int delay) {
            float fromValue = 0f;
            float toValue = 1f;
            return getAnimator(view, fromValue, toValue, duration, delay, null, null);
        }

        @NonNull
        public static Animator getAnimator(@NonNull View view, float fromValue, float toValue, Interpolator interpolator) {
            int duration = view.getResources().getInteger(R.integer.appearing_animation_delay);
            return getAnimator(view, fromValue, toValue, duration, 0, interpolator, null);
        }

        @NonNull
        public static Animator getAnimator(@NonNull View view, int duration, int delay, Interpolator interpolator) {
            float fromValue = 0f;
            float toValue = 1f;
            return getAnimator(view, fromValue, toValue, duration, delay, interpolator, null);
        }

        @NonNull
        public static Animator getAnimator(@NonNull View view, boolean appear) {
            int duration = view.getResources().getInteger(R.integer.appearing_animation_delay);
            float fromValue;
            float toValue;
            if (appear) {
                fromValue = 0f;
                toValue = 1f;
            }
            else {
                fromValue = 1f;
                toValue = 0f;
            }
            return getAnimator(view, fromValue, toValue, duration, 0, null, null);
        }

        @NonNull
        public static Animator getAnimator(@NonNull View view) {
            int duration = view.getResources().getInteger(R.integer.appearing_animation_delay);
            float fromValue = 0f;
            float toValue = 1f;
            return getAnimator(view, fromValue, toValue, duration, 0, null, null);
        }
    }

    /**
     * This class provides group of static helper method to create single view SCALE_Y animations.
     */
    public static class SCALE_Y {

        /**
         * The fully parametrized method. Other methods just call it with default values of params.
         * @param view - view to be animated.
         * @param fromValue - start scaleY value; default value is 0.
         * @param toValue - end scaleY value; default values is 1.
         * @param duration - duration of the animation; default value is standard Android appearance time.
         * @param delay - start delay of the animation; default value is 0.
         * @param interpolator - interpolator for the animation. Default is AccelerateDecelerateInterpolator.
         * @param listener - callback for the animation.
         * @return animator, which is read to start.
         */
        @NonNull
        public static Animator getAnimator(@NonNull View view, float fromValue, float toValue,
                                           int duration, int delay, Interpolator interpolator,
                                           Animator.AnimatorListener listener) {
            view.setScaleY(fromValue);
            return ViewAnimatorFramework.getAnimator(View.SCALE_Y, view, fromValue, toValue,
                    duration, delay, interpolator, listener);
        }

        @NonNull
        public static Animator getAnimator(@NonNull View view, float fromValue, float toValue,
                                           int duration, int delay) {
            return getAnimator(view, fromValue, toValue, duration, delay, null, null);
        }

        @NonNull
        public static Animator getAnimator(@NonNull View view, float fromValue, float toValue) {
            int duration = view.getResources().getInteger(R.integer.appearing_animation_delay);
            return getAnimator(view, fromValue, toValue, duration, 0, null, null);
        }

        @NonNull
        public static Animator getAnimator(@NonNull View view, int duration, int delay) {
            float fromValue = 0f;
            float toValue = 1f;
            return getAnimator(view, fromValue, toValue, duration, delay, null, null);
        }

        @NonNull
        public static Animator getAnimator(@NonNull View view, float fromValue, float toValue, Interpolator interpolator) {
            int duration = view.getResources().getInteger(R.integer.appearing_animation_delay);
            return getAnimator(view, fromValue, toValue, duration, 0, interpolator, null);
        }

        @NonNull
        public static Animator getAnimator(@NonNull View view, int duration, int delay, Interpolator interpolator) {
            float fromValue = 0f;
            float toValue = 1f;
            return getAnimator(view, fromValue, toValue, duration, delay, interpolator, null);
        }

        @NonNull
        public static Animator getAnimator(@NonNull View view, boolean appear) {
            int duration = view.getResources().getInteger(R.integer.appearing_animation_delay);
            float fromValue;
            float toValue;
            if (appear) {
                fromValue = 0f;
                toValue = 1f;
            }
            else {
                fromValue = 1f;
                toValue = 0f;
            }
            return getAnimator(view, fromValue, toValue, duration, 0, null, null);
        }

        @NonNull
        public static Animator getAnimator(@NonNull View view) {
            int duration = view.getResources().getInteger(R.integer.appearing_animation_delay);
            float fromValue = 0f;
            float toValue = 1f;
            return getAnimator(view, fromValue, toValue, duration, 0, null, null);
        }
    }

    /**
     * This class provides group of static helper method to create single view scale animations.
     * Usual view animators has no scale property, only scale_x and scale_y;
     * this class unites them to provide just simple scale animation.
     */
    public static class SCALE {

        /**
         * The fully parametrized method. Other methods just call it with default values of params.
         * @param view - view to be animated.
         * @param fromXValue - start scaleX value; default value is 0.
         * @param toXValue - end scaleX value; default values is 1.
         * @param fromYValue - start scaleY value; default value is 0.
         * @param toYValue - end scaleY value; default values is 1.
         * @param duration - duration of the animation; default value is standard Android appearance time.
         * @param delay - start delay of the animation; default value is 0.
         * @param interpolator - interpolator for the animation. Default is AccelerateDecelerateInterpolator.
         * @param listener - callback for the animation.
         * @return animator, which is read to start.
         */
        @NonNull
        public static Animator getAnimator(@NonNull View view,
                                           float fromXValue, float toXValue,
                                           float fromYValue, float toYValue,
                                           int duration, int delay, Interpolator interpolator,
                                           Animator.AnimatorListener listener) {
            view.setScaleX(fromXValue);
            view.setScaleY(fromYValue);
            AnimatorSet animator = new AnimatorSet();
            Animator scaleX = SCALE_X.getAnimator(view, fromXValue, toXValue,
                    duration, delay, interpolator, listener);
            Animator scaleY = SCALE_Y.getAnimator(view, fromYValue, toYValue,
                    duration, delay, interpolator, listener);
            animator.play(scaleX);
            animator.play(scaleY);
            return animator;
        }

        @NonNull
        public static Animator getAnimator(@NonNull View view,
                                           float fromValue, float toValue,
                                           int duration, int delay, Interpolator interpolator,
                                           Animator.AnimatorListener listener) {
            return getAnimator(view, fromValue, toValue, fromValue, toValue, duration, delay, interpolator, listener);
        }

        @NonNull
        public static Animator getAnimator(@NonNull View view, float fromValue, float toValue,
                                           int duration, int delay) {
            return getAnimator(view, fromValue, toValue, duration, delay, null, null);
        }

        @NonNull
        public static Animator getAnimator(@NonNull View view, float fromValue, float toValue) {
            int duration = view.getResources().getInteger(R.integer.appearing_animation_delay);
            return getAnimator(view, fromValue, toValue, duration, 0, null, null);
        }

        @NonNull
        public static Animator getAnimator(@NonNull View view, int duration, int delay) {
            float fromValue = 0f;
            float toValue = 1f;
            return getAnimator(view, fromValue, toValue, duration, delay, null, null);
        }

        @NonNull
        public static Animator getAnimator(@NonNull View view, float fromValue, float toValue, Interpolator interpolator) {
            int duration = view.getResources().getInteger(R.integer.appearing_animation_delay);
            return getAnimator(view, fromValue, toValue, duration, 0, interpolator, null);
        }

        @NonNull
        public static Animator getAnimator(@NonNull View view, int duration, int delay, Interpolator interpolator) {
            float fromValue = 0f;
            float toValue = 1f;
            return getAnimator(view, fromValue, toValue, duration, delay, interpolator, null);
        }

        @NonNull
        public static Animator getAnimator(@NonNull View view, boolean appear) {
            int duration = view.getResources().getInteger(R.integer.appearing_animation_delay);
            float fromValue;
            float toValue;
            if (appear) {
                fromValue = 0f;
                toValue = 1f;
            }
            else {
                fromValue = 1f;
                toValue = 0f;
            }
            return getAnimator(view, fromValue, toValue, duration, 0, null, null);
        }

        @NonNull
        public static Animator getAnimator(@NonNull View view) {
            int duration = view.getResources().getInteger(R.integer.appearing_animation_delay);
            float fromValue = 0f;
            float toValue = 1f;
            return getAnimator(view, fromValue, toValue, duration, 0, null, null);
        }
    }

    /**
     * This class provides group of static helper method to create single view rotate animations.
     */
    public static class ROTATION {

        /**
         * The fully parametrized method. Other methods just call it with default values of params.
         * @param view - view to be animated.
         * @param fromValue - start angle value; default value is 0.
         * @param toValue - end angle value; default values is 1.
         * @param duration - duration of the animation; default value is standard Android appearance time.
         * @param delay - start delay of the animation; default value is 0.
         * @param interpolator - interpolator for the animation. Default is AccelerateDecelerateInterpolator.
         * @param listener - callback for the animation.
         * @return animator, which is read to start.
         */
        @NonNull
        public static Animator getAnimator(@NonNull View view, float fromValue, float toValue,
                                           int duration, int delay, Interpolator interpolator,
                                           Animator.AnimatorListener listener) {
            return ViewAnimatorFramework.getAnimator(View.ROTATION, view, fromValue, toValue,
                    duration, delay, interpolator, listener);
        }

        @NonNull
        public static Animator getAnimator(@NonNull View view, float fromValue, float toValue,
                                           int duration, int delay) {
            return getAnimator(view, fromValue, toValue, duration, delay, null, null);
        }

        @NonNull
        public static Animator getAnimator(@NonNull View view, float fromValue, float toValue) {
            int duration = view.getResources().getInteger(R.integer.appearing_animation_delay);
            return getAnimator(view, fromValue, toValue, duration, 0, null, null);
        }

        @NonNull
        public static Animator getAnimator(@NonNull View view, float fromValue, float toValue, Interpolator interpolator) {
            int duration = view.getResources().getInteger(R.integer.appearing_animation_delay);
            return getAnimator(view, fromValue, toValue, duration, 0, interpolator, null);
        }

        @NonNull
        public static Animator getAnimator(@NonNull View view, int duration, int delay, Interpolator interpolator) {
            float fromValue = 0f;
            float toValue = 1f;
            return getAnimator(view, fromValue, toValue, duration, delay, interpolator, null);
        }

        @NonNull
        public static Animator getAnimator(@NonNull View view) {
            int duration = view.getResources().getInteger(R.integer.appearing_animation_delay);
            float fromValue = 0f;
            float toValue = 1f;
            return getAnimator(view, fromValue, toValue, duration, 0, null, null);
        }
    }

    /**
     * This class provides methods to create some standard effect animations, like bounce.
     */
    public static class EFFECTS {

        /**
         * @param view - view to be bounced.
         * @param duration - duration of the animation.
         * @param delay - start delay of the animation.
         * @return animator, which is ready to start.
         */
        @NonNull
        public static Animator bounceAnimator(View view, int duration, int delay) {
            return new GroupBuilder()
                    .animators(SCALE.getAnimator(view, 0.9f, 1f, duration, delay, new BounceInterpolator(), null))
                    .build();
        }

    }

    /**
     * Method for creating any view property animation.
     */
    @NonNull
    public static Animator getAnimator(Property<View, Float> animatedProperty,
                                       @NonNull View view, float fromValue, float toValue,
                                       int duration, int delay, Interpolator interpolator,
                                       Animator.AnimatorListener listener) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, animatedProperty, fromValue, toValue);
        animator.setDuration(duration);
        animator.setStartDelay(delay);
        animator.setInterpolator(interpolator == null ? new AccelerateDecelerateInterpolator() : interpolator);
        if (listener != null) {
            animator.addListener(listener);
        }
        return animator;
    }
}
