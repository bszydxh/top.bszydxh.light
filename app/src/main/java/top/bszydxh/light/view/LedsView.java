package top.bszydxh.light.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import top.bszydxh.light.R;

import java.util.List;

public class LedsView extends LinearLayout {
    boolean reverse = false;

    public LedsView(Context context) {
        super(context);
        initView(context);
    }


    public LedsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public LedsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public LedsView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    protected void initView(Context context) {
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    public void addLed() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_led_std, this);
    }

    public void addLeds(int num) {
        for (int i = 0; i < num; i++) {
            addLed();
        }
    }

    public void setColor(List<Integer> colors, int offset) {
        for (int i = offset; i < getChildCount() + offset; i++) {
            if (colors.get(i) == null) {
                continue;
            }
            int colorR = colors.get(i) >> 16 & 0xFF;
            int colorG = colors.get(i) >> 8 & 0xFF;
            int colorB = colors.get(i) & 0xFF;
            if (this.getChildAt(i - offset) == null) {
                continue;
            }
            if (reverse) {
                this.getChildAt(getChildCount() - 1 - i + offset).setBackgroundColor(Color.rgb(colorR, colorG, colorB));
            } else {
                this.getChildAt(i - offset).setBackgroundColor(Color.rgb(colorR, colorG, colorB));
            }
        }

    }

    public void setColor(List<Integer> colors) {
        setColor(colors, 0);
    }

    public void clear() {
        for (int i = 0; i < getChildCount(); i++) {
            this.getChildAt(getChildCount() - 1 - i).setBackgroundColor(Color.rgb(0, 0, 0));
        }
    }
}
