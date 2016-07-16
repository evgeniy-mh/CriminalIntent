package com.mh.evgeniy.criminalintent;

import android.support.v4.app.Fragment;
import android.util.Log;

/**
 * Created by evgeniy on 09.07.2016.
 */
public class CrimeListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment(){

        return new CrimeListFragment();
    }
}
