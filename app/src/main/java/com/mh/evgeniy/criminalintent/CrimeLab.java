package com.mh.evgeniy.criminalintent;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mh.evgeniy.criminalintent.database.CrimeBaseHelper;
import com.mh.evgeniy.criminalintent.database.CrimeDbSchema;
import com.mh.evgeniy.criminalintent.database.CrimeDbSchema.CrimeTable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by evgeniy on 09.07.2016.
 */
public class CrimeLab {

    private static CrimeLab sCrimeLab;
    //private ArrayList<Crime> mCrimes;
    private Context mContext;
    private SQLiteDatabase mDatabase;


    private CrimeLab(Context context){
        mContext= context.getApplicationContext();
        mDatabase=new CrimeBaseHelper(mContext).getWritableDatabase();

        //mCrimes=new ArrayList<>();
    }

    public List<Crime> getCrimes(){
        //return mCrimes;
        return new ArrayList<>();
    }

    public Crime getCrime(UUID id){
        /*for(Crime crime : mCrimes){
            if(crime.getId().equals(id)) return crime;
        }*/
        return null;
    }

    public static CrimeLab get(Context context){
        if(sCrimeLab==null){
            sCrimeLab= new CrimeLab(context);
        }
        return sCrimeLab;
    }

    public void addCrime(Crime c){
        //mCrimes.add(c);
        ContentValues values=getContentValues(c);
        mDatabase.insert(CrimeTable.NAME,null,values);
    }

    public void deleteCrime(Crime c) {
        //mCrimes.remove(c);
    }

    public void updateCrime(Crime c){
        String uuidString=c.getId().toString();
        ContentValues values=getContentValues(c);

        mDatabase.update(CrimeTable.NAME,values,CrimeTable.Cols.UUID+" = ?",new String[]{uuidString});

    }

    private static ContentValues getContentValues(Crime crime){
        ContentValues values=new ContentValues();
        values.put(CrimeTable.Cols.UUID,crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE,crime.getTitle().toString());
        values.put(CrimeTable.Cols.DATE,crime.getDate().toString());
        values.put(CrimeTable.Cols.SOLVED,crime.isSolved() ? 1:0);

        return values;
    }

    private Cursor queryCrimes(String whereClause, String[] whereArgs){
        Cursor cursor=mDatabase.query(
                CrimeTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null);
        return cursor;
    }

}
