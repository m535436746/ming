package com.telstar.launcher.input.util;

import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

public class AnimationFactory {
	
	/**
	 * 加载动画.
	 */
	public static LayoutAnimationController loadAnimation() {
		/*
		 * 创建动画的集合
		 */
		AnimationSet set = new AnimationSet(false);
		Animation animation;
		/*
		 * 创建旋转动画
		 */
//		animation = new RotateAnimation(180, 10);
//		animation.setDuration(500);
//		set.addAnimation(animation);
		
		animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(500);
		set.addAnimation(animation);
		
		animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF,
				0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		animation.setDuration(500);
		set.addAnimation(animation);

		LayoutAnimationController controller = new LayoutAnimationController(set, 1);
		controller.setInterpolator(new AccelerateInterpolator());
		controller.setAnimation(set);
		return controller;
	}
	
	/**
	 * 从左到右显示菜单.
	 */
	public static Animation getLtRAnimation() {
		Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				1.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		animation.setInterpolator(new AccelerateInterpolator());
		animation.setDuration(500);
		animation.setFillAfter(true);
		return animation;
	}
	
	/**
	 * 从右到左显示菜单.
	 */
	public static Animation getRtLAnimation() {
		Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				-1.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		animation.setInterpolator(new AccelerateInterpolator());
		AnimationSet as = new AnimationSet(true);
		as.addAnimation(animation);
		as.setDuration(500);
		as.setFillAfter(true);
		return as;
	}
	
	/**
	 * 放大的动画
	 * @return
	 */
	public static AnimationSet getZoomInAnimationSet() {
		ScaleAnimation animationZoomIn = new ScaleAnimation(1.0f, 1.4f, 1.0f, 1.4f, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		animationZoomIn.setInterpolator(new AccelerateInterpolator());
		AnimationSet as = new AnimationSet(true);
		as.setFillAfter(true);
		as.addAnimation(animationZoomIn);
		as.setDuration(500);
		return as;
	}

	/**
	 * 缩小的动画
	 * @return
	 */
	public static AnimationSet getZoomOutAnimationSet() {
		ScaleAnimation animationZoomOut = new ScaleAnimation(1.4f, 1.0f, 1.4f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
				Animation.RELATIVE_TO_SELF, 0.5f);
		animationZoomOut.setInterpolator(new AccelerateInterpolator());
		AnimationSet as = new AnimationSet(true);
		as.setFillAfter(true);
		as.addAnimation(animationZoomOut);
		as.setDuration(500);
		return as;
	}

}
