package top.bszydxh.light.activity;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import top.bszydxh.light.adapter.RootFragmentPagerAdapter;
import top.bszydxh.light.databinding.ActivityMainBinding;
import top.bszydxh.light.fragment.ConfigFragment;
import top.bszydxh.light.fragment.ControlFragment;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private ViewPager2 rootViewPager;
    private RootFragmentPagerAdapter rootFragmentPagerAdapter;
    private ActivityMainBinding binding;
    ControlFragment controlFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);//声明onCreate,方法继承之前的状态
        binding = ActivityMainBinding.inflate(getLayoutInflater());//赋值阶段,inflate为调用生成的绑定类中包含的静态方法。这将为要使用的活动创建一个绑定类的实例。
        setContentView(binding.getRoot());//binding中getRoot()方法是对binding根视图的引用,也相当于创建视图
        setSupportActionBar(binding.toolbar);//设置toolbar
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
        rootFragmentPagerAdapter = new RootFragmentPagerAdapter(
                getSupportFragmentManager(), getLifecycle()
        );
        rootFragmentPagerAdapter.addFragment(getControlFragment());
        rootFragmentPagerAdapter.addFragment(new ConfigFragment());
        rootViewPager = binding.pager;
        rootViewPager.setAdapter(rootFragmentPagerAdapter);
        rootViewPager.setOffscreenPageLimit(5);
        BottomNavigationView bottomNavigationView = binding.btmNav;
        rootViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
            }
        });
        //导航栏绑定根ViewPager2适配器事件
        bottomNavigationView.setOnItemSelectedListener(item -> {
            String nav_name = item.getTitle().toString();
            switch (nav_name) {
                case "控制":
                    rootViewPager.setCurrentItem(0, true);
                    break;
                case "配网":
                    rootViewPager.setCurrentItem(1, true);
                    break;
            }
            return true;
        });

    }

    public void pageTo(int position) {
        rootViewPager.setCurrentItem(position, false);
    }

    public void setTitle(String str1, String str2) {
        binding.toolbarUserName.setText(str1);
        binding.toolbarUserSignature.setText(str2);
    }

    @NonNull
    public ControlFragment getControlFragment() {
        if (controlFragment == null) {
            controlFragment = new ControlFragment();
        }
        return controlFragment;
    }
}