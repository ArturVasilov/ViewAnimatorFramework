package ru.yota.android.animation;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.util.Property;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import ru.yota.android.R;

/**
 * @author Artur Vasilov
 */
public class ViewAnimatorFramework {

    /**
     * this class is for animating group of views
     */
    public static class GroupBuilder {

        private View[] mViews;
        private Property<View, Float>[] mProperties;
        private float[] mFromValues;
        private float[] mToValues;
        private int[] mDurations;
        private int[] mDelays;
        private Animator.AnimatorListener mLastCallback;

        @NonNull private final AnimatorSet mAnimatorSet;

        private boolean mWasAnimated = false;

        public GroupBuilder() {
            mAnimatorSet = new AnimatorSet();
        }

        /**
         * set views needed to be animated
         *
         * @param views - views to animate
         */
        public GroupBuilder views(View... views) {
            mViews = views;
            return this;
        }

        /**
         * set the properties of views to be animated;
         * Note: this method allows you to assign only one property for each view,
         * if you need to add more than one property,
         * use {@link ru.yota.android.animation.ViewAnimatorFramework.ViewBuilder} class and animator method
         *
         * @param properties - properties of each view to be animated.
         *                   If there are less properties than views, the first param will be used;
         *                   So, if you need to animate scaleY for all views, you can only pass in once;
         *                   Default property is aplha
         */
        @SafeVarargs
        public final GroupBuilder animationTypes(Property<View, Float>... properties) {
            mProperties = properties;
            return this;
        }

        public GroupBuilder fromValues(float... fromValues) {
            mFromValues = fromValues;
            return this;
        }

        public GroupBuilder toValues(float... toValues) {
            mToValues = toValues;
            return this;
        }

        public GroupBuilder durations(int... durations) {
            mDurations = durations;
            return this;
        }

        public GroupBuilder delays(int... delays) {
            mDelays = delays;
            return this;
        }

        /**
         * Assigns callback to animation, which will be the last in the group
         *
         * @param callback - callback, which will be assigned to last animation in group
         */
        public GroupBuilder callback(Animator.AnimatorListener callback) {
            mLastCallback = callback;
            return this;
        }

        public GroupBuilder animator(Animator animator) {
            mAnimatorSet.play(animator);
            return this;
        }

        public void animate() {
            if (mWasAnimated ||
                    (mViews == null || mViews.length == 0) ||
                    (mAnimatorSet.isRunning())) {
                return;
            }
            Resources resources = mViews[0].getResources();
            for (int i = 0; i < mViews.length; i++) {
                View view = mViews[i];
                Property<View, Float> property = View.ALPHA;
                if (mProperties != null && mProperties.length > i) {
                    property = mProperties[i];
                }
                float fromValue = 1f;
                if (mFromValues != null && mFromValues.length > i) {
                    fromValue = mFromValues[i];
                }
                float toValue = 0f;
                if (mToValues != null && mToValues.length > i) {
                    toValue = mToValues[i];
                }
                int duration = resources.getInteger(R.integer.appearing_animation_duration);
                if (mDurations != null && mDurations.length > i) {
                    duration = mDurations[i];
                }
                int delay = 0;
                if (mDelays != null && mDelays.length > i) {
                    delay = mDelays[i];
                }
                ObjectAnimator animator = ObjectAnimator.ofFloat(view, property, fromValue, toValue);
                animator.setDuration(duration);
                animator.setStartDelay(delay);
                animator.setInterpolator(new AccelerateDecelerateInterpolator());
                if (i == mViews.length - 1 && mLastCallback != null) {
                    animator.addListener(mLastCallback);
                }
                mAnimatorSet.play(animator);
            }
            mAnimatorSet.start();
            mWasAnimated = true;
            release();
        }

        public void stop() {
            mAnimatorSet.end();
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

    public static class ViewBuilder {

        @NonNull private final View mView;

        @NonNull private final AnimatorSet mAnimatorSet;

        private Property<View, Float>[] mProperties;
        private float[] mFromValues;
        private float[] mToValues;
        private int[] mDurations;
        private int[] mDelays;
        private Interpolator[] mInterpolators;
        private Animator.AnimatorListener mLastCallback;

        private boolean mWasBuilt = false;

        public ViewBuilder(@NonNull View view) {
            this.mView = view;
            mAnimatorSet = new AnimatorSet();
        }

        @SafeVarargs
        public final ViewBuilder animationTypes(Property<View, Float>... properties) {
            mProperties = properties;
            return this;
        }

        public ViewBuilder fromValues(float... fromValues) {
            mFromValues = fromValues;
            return this;
        }

        public ViewBuilder toValues(float... toValues) {
            mToValues = toValues;
            return this;
        }

        public ViewBuilder durations(int... durations) {
            mDurations = durations;
            return this;
        }

        public ViewBuilder delays(int... delays) {
            mDelays = delays;
            return this;
        }

        public ViewBuilder interpolator(Interpolator[] interpolators) {
            mInterpolators = interpolators;
            return this;
        }

        public ViewBuilder add(Animator animator) {
            mAnimatorSet.play(animator);
            return this;
        }

        public Animator build() {
            if ((mWasBuilt) || (mAnimatorSet.isRunning())
                    || (mProperties == null) || (mProperties.length == 0)) {
                return mAnimatorSet;
            }
            for (int i = 0; i < mProperties.length; i++) {
                Property<View, Float> property = mProperties[i];
                float fromValue = 1f;
                if (mFromValues != null && mFromValues.length > i) {
                    fromValue = mFromValues[i];
                }
                float toValue = 0f;
                if (mToValues != null && mToValues.length > i) {
                    toValue = mToValues[i];
                }
                int duration = mView.getResources().getInteger(R.integer.appearing_animation_duration);
                if (mDurations != null && mDurations.length > i) {
                    duration = mDurations[i];
                }
                int delay = 0;
                if (mDelays != null && mDelays.length > i) {
                    delay = mDelays[i];
                }
                Interpolator interpolator = new AccelerateDecelerateInterpolator();
                if (mInterpolators != null && mInterpolators.length > i) {
                    interpolator = mInterpolators[i];
                }
                ObjectAnimator animator = ObjectAnimator.ofFloat(mView, property, fromValue, toValue);
                animator.setDuration(duration);
                animator.setStartDelay(delay);
                animator.setInterpolator(interpolator);
                mAnimatorSet.play(animator);
                release();
                mWasBuilt = true;
            }
            return mAnimatorSet;
        }

        private void release() {
            mProperties = null;
            mFromValues = null;
            mToValues = null;
            mDurations = null;
            mDelays = null;
        }
    }


    public static class ALPHA {

        @NonNull
        public static Animator getAnimator(@NonNull View view, float fromValue, float toValue,
                                           int duration, int delay, Interpolator interpolator,
                                           Animator.AnimatorListener listener) {
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

    public static class SCALE_X {

        @NonNull
        public static Animator getAnimator(@NonNull View view, float fromValue, float toValue,
                                           int duration, int delay, Interpolator interpolator,
                                           Animator.AnimatorListener listener) {
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

    public static class SCALE_Y {
        @NonNull
        public static Animator getAnimator(@NonNull View view, float fromValue, float toValue,
                                           int duration, int delay, Interpolator interpolator,
                                           Animator.AnimatorListener listener) {
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

    public static class SCALE {
        @NonNull
        public static Animator getAnimator(@NonNull View view,
                                           float fromXValue, float toXValue,
                                           float fromYValue, float toYValue,
                                           int duration, int delay, Interpolator interpolator,
                                           Animator.AnimatorListener listener) {
            AnimatorSet animator = new AnimatorSet();
            Animator scaleX = ViewAnimatorFramework.getAnimator(View.SCALE_X, view,
                    fromXValue, toXValue, duration, delay, interpolator, listener);
            Animator scaleY = ViewAnimatorFramework.getAnimator(View.SCALE_Y, view,
                    fromYValue, toYValue, duration, delay, interpolator, listener);
            animator.playTogether(scaleX, scaleY);
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

    public static class ROTATION {
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
        public static Animator getAnimator(@NonNull View view) {
            int duration = view.getResources().getInteger(R.integer.appearing_animation_delay);
            float fromValue = 0f;
            float toValue = 1f;
            return getAnimator(view, fromValue, toValue, duration, 0, null, null);
        }
    }

    @NonNull
    private static Animator getAnimator(Property<View, Float> animatedProperty,
                                        @NonNull View view, float fromValue, float toValue,
                                        int duration, int delay, Interpolator interpolator,
                                        Animator.AnimatorListener listener) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, animatedProperty, fromValue, toValue);
        animator.setDuration(duration);
        animator.setStartDelay(delay);
        if (interpolator != null) {
            animator.setInterpolator(interpolator);
        }
        if (listener != null) {
            animator.addListener(listener);
        }
        return animator;
    }
}
