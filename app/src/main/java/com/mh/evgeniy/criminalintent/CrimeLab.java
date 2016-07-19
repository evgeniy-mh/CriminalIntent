package com.mh.evgeniy.criminalintent;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mh.evgeniy.criminalintent.database.CrimeBaseHelper;
import com.mh.evgeniy.criminalintent.database.CrimeCursorWrapper;
import com.mh.evgeniy.criminalintent.database.CrimeDbSchema;
import com.mh.evgeniy.criminalintent.database.CrimeDbSchema.CrimeTable;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
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
        mContext = context.getApplicationContext();
        mDatabase = new CrimeBaseHelper(mContext).getWritableDatabase();

        //mCrimes=new ArrayList<>();
    }

    public List<Crime> getCrimes(){
        //return mCrimes;
        List<Crime> crimes=new ArrayList<>();

        CrimeCursorWrapper cursorWrapper=queryCrimes(null,null);

        try{
            cursorWrapper.moveToFirst();
            while(!cursorWrapper.isAfterLast()){
                crimes.add(cursorWrapper.getCrime());
                cursorWrapper.moveToNext();
            }

        }finally {
            cursorWrapper.close();
        }
        return crimes;
    }

    public Crime getCrime(UUID id){
        /*for(Crime crime : mCrimes){
            if(crime.getId().equals(id)) return crime;
        }*/

        CrimeCursorWrapper cursorWrapper=queryCrimes(CrimeTable.Cols.UUID+" = ?",
                new String[]{id.toString()});

        try{
            if(cursorWrapper.getCount()==0) return null;
            cursorWrapper.moveToFirst();
            return cursorWrapper.getCrime();
        }finally {
            cursorWrapper.close();
        }

    }

    public static CrimeLab get(Context context){
        if (sCrimeLab == null) {
            sCrimeLab = new CrimeLab(context);
        }
        return sCrimeLab;
    }

    public void addCrime(Crime c){
        //mCrimes.add(c);
        ContentValues values = getContentValues(c);

        mDatabase.insert(CrimeTable.NAME, null, values);
    }

    public void deleteCrime(Crime c) {
        //mCrimes.remove(c);
        ContentValues values=getContentValues(c);
        mDatabase.delete(CrimeTable.NAME,CrimeTable.Cols.UUID+" = ?",
                new String[] {c.getId().toString()});
    }

    public void updateCrime(Crime c){
        String uuidString = c.getId().toString();
        ContentValues values = getContentValues(c);

        mDatabase.update(CrimeTable.NAME, values,
                CrimeTable.Cols.UUID + " = ?",
                new String[] { uuidString });

    }

    private static ContentValues getContentValues(Crime crime){
        ContentValues values = new ContentValues();
        values.put(CrimeTable.Cols.UUID, crime.getId().toString());
        values.put(CrimeTable.Cols.TITLE, crime.getTitle());
        values.put(CrimeTable.Cols.DATE, crime.getDate().getTime());
        values.put(CrimeTable.Cols.SOLVED, crime.isSolved() ? 1 : 0);
        values.put(CrimeTable.Cols.SUSPECT,crime.getSuspect());

        return values;
    }

    private CrimeCursorWrapper queryCrimes(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                CrimeTable.NAME,
                null, // Columns - null selects all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null  // orderBy
        );

        return new CrimeCursorWrapper(cursor);
    }

}
