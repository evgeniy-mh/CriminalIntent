package com.mh.evgeniy.criminalintent;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import java.util.Date;

/**
 * Created by evgeniy on 16.07.2016.
 */
public class DatePickerActivity extends SingleFragmentActivity{

    @Override
    protected Fragment createFragment(){
        return new DatePickerFragment();
    }

    public static Intent newIntent(Context packageContext, Date date){
        Intent intent=new Intent(packageContext,DatePickerActivity.class);
        intent.putExtra(DatePickerFragment.ARG_DATE,date);
        return intent;
    }

}
