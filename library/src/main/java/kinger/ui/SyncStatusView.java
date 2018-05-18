package kinger.ui;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.ColorRes;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import kinger.ui.syncstatusview.R;


/**
 * Created by Sunny Kinger on 18-05-2018
 */
public class SyncStatusView extends RelativeLayout {

    ImageView ivSync;
    TextView tvSyncStatus;
    ObjectAnimator roundAnimation;
    ObjectAnimator bounceAnimation;
    String mSyncingMsg;
    String mSyncedMsg;
    String mNotSyncedMsg;
    int mSyncingTextColor;
    int mNotSyncedTextColor;
    int mSyncedTextColor;
    int mSyncingColor;
    int mNotSyncedColor;
    int mSyncedColor;

    public SyncStatusView(Context context) {
        super(context);
        init(null);
    }

    public SyncStatusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public SyncStatusView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SyncStatusView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        int status;
        boolean loading;
        inflate(getContext(), R.layout.view_sync_status, this);
        initView();
        setAnimatorForLoading();
        setAnimatorForBounce();
        TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.SyncStatusView);
        try {
            mNotSyncedMsg = array.getString(R.styleable.SyncStatusView_not_synced_msg);
            mSyncedMsg = array.getString(R.styleable.SyncStatusView_synced_msg);
            mSyncingMsg = array.getString(R.styleable.SyncStatusView_syncing_msg);
            mNotSyncedTextColor = array.getColor(R.styleable.SyncStatusView_not_synced_text_color, getResources().getColor(R.color.default_not_synced));
            mSyncedTextColor = array.getColor(R.styleable.SyncStatusView_synced_text_color, getResources().getColor(R.color.default_synced));
            mSyncingTextColor = array.getColor(R.styleable.SyncStatusView_syncing_text_color, getResources().getColor(R.color.default_syncing));
            mNotSyncedColor = array.getColor(R.styleable.SyncStatusView_not_synced_color, getResources().getColor(android.R.color.black));
            mSyncedColor = array.getColor(R.styleable.SyncStatusView_synced_color, getResources().getColor(android.R.color.black));
            mSyncingColor = array.getColor(R.styleable.SyncStatusView_syncing_color, getResources().getColor(android.R.color.black));
            status = array.getInt(R.styleable.SyncStatusView_sync_status, -1);
            loading = array.getBoolean(R.styleable.SyncStatusView_loading, false);
        } finally {
            array.recycle();
        }
        setStatus(status);
        setLoading(loading);
    }

    public void setSyncingMessage(String message) {
        this.mSyncingMsg = message;
    }

    public void setNotSyncedMessage(String message) {
        this.mNotSyncedMsg = message;
    }

    public void setSyncedMessage(String message) {
        this.mSyncedMsg = message;
    }

    public void setSyncedTextColor(@ColorRes int color) {
        this.mSyncedTextColor = getResources().getColor(color);
    }

    public void setSyncedColor(@ColorRes int color) {
        this.mSyncedColor = getResources().getColor(color);
    }

    public void setNotSyncedColor(@ColorRes int color) {
        this.mNotSyncedColor = getResources().getColor(color);
    }

    public void setNotSyncedTextColor(@ColorRes int color) {
        this.mNotSyncedTextColor = getResources().getColor(color);
    }

    public void setSyncingColor(@ColorRes int color) {
        this.mSyncingColor = color;
    }

    public void setSyncingTextColor(@ColorRes int color) {
        this.mSyncingTextColor = color;
    }

    private void initView() {
        ivSync = findViewById(R.id.iv_sync);
        tvSyncStatus = findViewById(R.id.tv_sync_status);
    }

    private void setAnimatorForBounce() {
        bounceAnimation = ObjectAnimator.ofPropertyValuesHolder(
                ivSync,
                PropertyValuesHolder.ofFloat("scaleX", 1.2f),
                PropertyValuesHolder.ofFloat("scaleY", 1.2f));
        bounceAnimation.setDuration(310);
        bounceAnimation.setRepeatCount(ObjectAnimator.INFINITE);
        bounceAnimation.setRepeatMode(ObjectAnimator.REVERSE);

    }

    private void setAnimatorForLoading() {
        roundAnimation = ObjectAnimator.ofFloat(ivSync,
                "rotation", 0f, 360f);
        roundAnimation.setRepeatCount(ObjectAnimator.INFINITE);
        roundAnimation.setRepeatMode(ObjectAnimator.RESTART);
        roundAnimation.setInterpolator(new AccelerateInterpolator());
    }

    public void setLoading(boolean loading) {
        if (loading) {
            ivSync.post(new Runnable() {
                @Override
                public void run() {
                    if (!roundAnimation.isRunning()) {
                        roundAnimation.start();
                    }

                    if (bounceAnimation.isRunning()) {
                        bounceAnimation.cancel();
                    }
                }
            });
        } else {
            ivSync.post(new Runnable() {
                @Override
                public void run() {
                    if (roundAnimation.isRunning()) {
                        roundAnimation.cancel();
                    }
                }
            });
        }

    }

    public void setStatus(@Status int status) {
        if (status == Status.NOT_SYNCED) {
            ivSync.post(new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(200);
                    if (roundAnimation.isRunning()) {
                        roundAnimation.cancel();
                    }
                    if (!bounceAnimation.isRunning()) {
                        bounceAnimation.start();
                    }
                }
            });
            ivSync.setColorFilter(mNotSyncedColor, PorterDuff.Mode.SRC_IN);
            tvSyncStatus.setTextColor(mNotSyncedTextColor);
            tvSyncStatus.setText(mNotSyncedMsg);
        } else if (status == Status.SYNCED) {
            ivSync.post(new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(200);
                    if (roundAnimation.isRunning()) {
                        roundAnimation.cancel();
                    }
                    if (bounceAnimation.isRunning()) {
                        bounceAnimation.cancel();
                    }
                }
            });
            ivSync.setColorFilter(mSyncedColor, PorterDuff.Mode.SRC_IN);
            tvSyncStatus.setTextColor(mSyncedTextColor);
            tvSyncStatus.setText(mSyncedMsg);
        } else {
            ivSync.post(new Runnable() {
                @Override
                public void run() {
                    SystemClock.sleep(200);
                    if (!roundAnimation.isRunning()) {
                        roundAnimation.start();
                    }

                    if (bounceAnimation.isRunning()) {
                        bounceAnimation.cancel();
                    }
                }
            });
            ivSync.setColorFilter(mSyncingColor, PorterDuff.Mode.SRC_IN);
            tvSyncStatus.setTextColor(mSyncingTextColor);
            tvSyncStatus.setText(mSyncingMsg);
        }
    }

    @IntDef({Status.NOT_SYNCED, Status.SYNCED, Status.SYNCING, Status.ALREADY_SYNCED})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Status {
        int NOT_SYNCED = -1;
        int SYNCED = 0;
        int SYNCING = 1;
        int ALREADY_SYNCED = 2;
    }
}

