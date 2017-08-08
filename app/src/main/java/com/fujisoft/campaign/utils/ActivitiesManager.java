package com.fujisoft.campaign.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;

import java.util.Iterator;
import java.util.Stack;

/**
 * activity堆栈管理
 */
public class ActivitiesManager {

    /**
     * stack<Activity>存放activity
     */
    private static Stack<Activity> mActivityStack;

    /**
     * 管理activity
     */
    private static ActivitiesManager mActivitiesManager;

    /**
     * construct
     */
    private ActivitiesManager() {
    }

    /**
     * 单例
     *
     * @return
     */
    public static ActivitiesManager getInstance() {
        if (null == mActivitiesManager) {
            mActivitiesManager = new ActivitiesManager();
            if (null == mActivityStack) {
                mActivityStack = new Stack<Activity>();
            }
        }
        return mActivitiesManager;
    }

    /**
     * 栈大小
     *
     * @return
     */
    public int stackSize() {
        return mActivityStack.size();
    }

    /**
     * 获取当前Activity（栈顶activity）
     *
     * @return
     */
    public Activity getCurrentActivity() {
        Activity activity = null;
        try {
            activity = mActivityStack.lastElement();
        } catch (Exception e) {
            return null;
        }
        return activity;
    }

    /**
     * 移除栈顶activity
     */
    public void moveActivity() {
        Activity activity = mActivityStack.lastElement();
        if (null != activity) {
            activity.finish();
            mActivityStack.remove(activity);
            activity = null;
        }
    }

    /**
     * 结束指定的Activity
     *
     * @param activity
     */
    public void moveActivity(Activity activity) {
        if (null != activity) {
            activity.finish();
            mActivityStack.remove(activity);
            activity = null;
        }
    }

    /**
     * 添加Activity到堆栈
     *
     * @param activity
     */
    public void addActivity(Activity activity) {
        mActivityStack.add(activity);
    }

    /**
     * 结束所有Activity
     */
    public void moveAllActivities() {
        while (!mActivityStack.isEmpty()) {
            Activity activity = getCurrentActivity();
            if (null == activity) {
                break;
            }
            activity.finish();
            moveActivity(activity);
        }
    }

    /**
     * 结束指定类名的Activity
     *
     * @param cls
     */
    public void moveSpecialActivity(Class<?> cls) {
        try {
            Iterator<Activity> iterator = mActivityStack.iterator();
            Activity activity = null;
            while (iterator.hasNext()) {
                activity = iterator.next();
                if (activity.getClass().equals(cls)) {
                    activity.finish();
                    iterator.remove();
                    activity = null;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 退出应用程序
     */
    @SuppressWarnings("deprecation")
    public void appExit(Context context) {
        try {
            moveAllActivities();
            ActivityManager activityMgr = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            activityMgr.restartPackage(context.getPackageName());
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}