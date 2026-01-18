package com.THLight.BLE.USBeacon.Writer.Simple.ui.pager;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.THLight.BLE.USBeacon.Writer.Simple.ui.activity.base.BaseFragment;

import java.util.List;

/**
 * Created by Allen on 2020/3/4.
 */
public class BasePagerAdapter extends FragmentPagerAdapter {
    private List<BaseFragment> fragmentList;

    public BasePagerAdapter(FragmentManager fragmentManager, List<BaseFragment> fragmentList) {
        super(fragmentManager);
        this.fragmentList = fragmentList;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
