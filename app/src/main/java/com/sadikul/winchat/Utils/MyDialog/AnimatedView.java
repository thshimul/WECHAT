package com.sadikul.winchat.Utils.MyDialog;

import android.content.Context;
import android.view.View;

/**
 * Created by ASUS on 28-Nov-17.
 */

class AnimatedView extends View {

    private int target;

    public AnimatedView(Context context) {
        super(context);
    }

    public float getXFactor() {
        return getX() / target;
    }

    public void setXFactor(float xFactor) {
        setX(target * xFactor);
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public int getTarget() {
        return target;
    }
}