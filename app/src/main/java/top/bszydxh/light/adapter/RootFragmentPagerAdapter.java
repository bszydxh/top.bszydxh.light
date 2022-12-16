package top.bszydxh.light.adapter;

import android.util.SparseArray;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class RootFragmentPagerAdapter extends FragmentStateAdapter {
    final FragmentManager fragmentManager;

    private final SparseArray<Fragment> fragmentList = new SparseArray<>();

    public RootFragmentPagerAdapter(@NonNull FragmentManager fm,
                                    @NonNull Lifecycle lifecycle) {
        super(fm, lifecycle);
        fragmentManager = fm;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragmentList.get(position);
    }

    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }



    @Override
    public int getItemCount() {
        return fragmentList.size();
    }

    public void addFragment(Fragment fragment) {
        fragmentList.put(fragmentList.size(), fragment);
    }
}
