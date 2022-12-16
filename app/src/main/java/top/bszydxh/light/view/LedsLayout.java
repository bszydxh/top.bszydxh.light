package top.bszydxh.light.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import top.bszydxh.light.R;

import java.util.List;

public class LedsLayout extends LinearLayout {
    public LedsLayout(Context context) {
        super(context);
        initView(context);
    }


    public LedsLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public LedsLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    public LedsLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    protected void initView(Context context) {
        setOrientation(LinearLayout.VERTICAL);
    }

    public void addLedView(int num,boolean reverse) {
        LayoutInflater.from(getContext()).inflate(R.layout.leds_layout_std, this);
        LedsView ledsView = getChildAt(getChildCount() - 1).findViewById(R.id.ledsViewIndex);
        ledsView.addLeds(num);
        ledsView.setReverse(reverse);
    }

    public void setColor(List<Integer> colors) {
        for (int i = 0; i < getChildCount(); i++) {
            LedsView ledsView = getChildAt(i).findViewById(R.id.ledsViewIndex);
            int offset = 0;
            for (int k = 0; k < i; k++) {
                LedsView ledsViewIndex = getChildAt(k).findViewById(R.id.ledsViewIndex);
                offset += ledsViewIndex.getChildCount();
            }
            ledsView.setColor(colors, offset);
        }
    }

    public void clear() {
        for (int i = 0; i < getChildCount(); i++) {
            LedsView ledsView = getChildAt(i).findViewById(R.id.ledsViewIndex);
            ledsView.clear();
        }
    }
}
