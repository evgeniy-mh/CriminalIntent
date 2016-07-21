package com.mh.evgeniy.criminalintent;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.MediaStore;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ShareCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.mh.evgeniy.criminalintent.database.PictureUtils;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.UUID;

/**
 * Created by evgeniy on 09.07.2016.
 */
public class CrimeFragment extends Fragment {

    private static final String ARG_CRIME_ID="crime_id";
    private static final String DIALOG_DATE = "DialogDate";
    private static final String DIALOG_TIME = "DialogTime";
    private static final String DIALOD_DATE_CODE="DialogDate";
    private static final String DIALOG_SHOW_PHOTO="DialogPhoto";
    private static final int REQUEST_DATE = 0;
    private static final int REQUEST_TIME = 1;
    private static final int REQUEST_CONTACT=2;
    private static final int REQUEST_PHOTO=3;

    private Crime mCrime;
    private EditText mTitleField;
    private Button mDateButton;
    private Button mTimeButton;
    private CheckBox mSolvedCheckBox;
    private Button mReportButton;
    private Button mSuspectButton;
    private Button mCallSuspectButtom;
    private String mSuspectID;
    private Uri mSuspectContactUri;
    private ImageButton mPhotoButton;
    private ImageView mPhotoView;
    private File mPhotoFile;
    private Point mPhotoViewSize;

    public static CrimeFragment newInstance(UUID crimeId){
        Bundle args=new Bundle();
        args.putSerializable(ARG_CRIME_ID,crimeId);

        CrimeFragment fragment=new CrimeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceBundle){
        super.onCreate(savedInstanceBundle);
        UUID crimeId=(UUID) getArguments().getSerializable(ARG_CRIME_ID);
        mCrime=CrimeLab.get(getActivity()).getCrime(crimeId);
        mPhotoFile=CrimeLab.get(getActivity()).getPhotoFile(mCrime);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v=inflater.inflate(R.layout.fragment_crime,container,false);
        mTitleField=(EditText) v.findViewById(R.id.crime_title);
        mTitleField.setText(mCrime.getTitle());
        mTitleField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mCrime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        mDateButton=(Button)v.findViewById(R.id.crime_date);
        updateDate();
        mDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager=getActivity().getSupportFragmentManager();
                DatePickerFragment dialog=DatePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this,REQUEST_DATE);
                dialog.show(manager,DIALOG_DATE);

                /*Intent i=DatePickerActivity.newIntent(getActivity(),mCrime.getDate());
                startActivityForResult(i,REQUEST_DATE);*/


            }
        });

        mTimeButton=(Button)v.findViewById(R.id.crime_time);
        updateTime();
        mTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager=getActivity().getSupportFragmentManager();
                TimePickerFragment dialog=TimePickerFragment.newInstance(mCrime.getDate());
                dialog.setTargetFragment(CrimeFragment.this,REQUEST_TIME);
                dialog.show(manager,DIALOG_TIME);

            }
        });

        mSolvedCheckBox=(CheckBox)v.findViewById(R.id.crime_solved);
        mSolvedCheckBox.setChecked(mCrime.isSolved());
        mSolvedCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mCrime.setSolved(isChecked);
            }
        });

        mReportButton=(Button)v.findViewById(R.id.crime_report);
        mReportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*Intent i=new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT,getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.crime_report_subject));
                i=Intent.createChooser(i,getString(R.string.send_report));*/
                Intent i=ShareCompat.IntentBuilder.from(getActivity())
                        .setType("text/plain").getIntent();
                i.putExtra(Intent.EXTRA_TEXT,getCrimeReport());
                i.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.crime_report_subject));
                i=Intent.createChooser(i,getString(R.string.send_report));

                startActivity(i);
            }
        });

        final Intent pickContact=new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        mSuspectButton=(Button)v.findViewById(R.id.crime_suspect);
        mSuspectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(pickContact,REQUEST_CONTACT);
            }
        });

        if(mCrime.getSuspect()!=null){
            mSuspectButton.setText(mCrime.getSuspect());
        }

        mCallSuspectButtom=(Button)v.findViewById(R.id.call_suspect);
        if(mCrime.getSuspect()==null) mCallSuspectButtom.setEnabled(false);
        mCallSuspectButtom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =new Intent(Intent.ACTION_DIAL);
                String phoneNumber=getSuspectPhoneNumber();
                if(phoneNumber!=null) i.setData(Uri.parse("tel:"+phoneNumber));
                startActivity(i);
            }
        });


        PackageManager packageManager=getActivity().getPackageManager();
        if(packageManager.resolveActivity(pickContact,PackageManager.MATCH_DEFAULT_ONLY)==null){
            mSuspectButton.setEnabled(false);
            mCallSuspectButtom.setEnabled(false);
        }

        mPhotoButton=(ImageButton)v.findViewById(R.id.crime_camera);
        final Intent captureImage=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        boolean canTakePhoto=mPhotoFile!=null && captureImage.resolveActivity(packageManager)!=null;
        mPhotoButton.setEnabled(canTakePhoto);

        if(canTakePhoto){
            Uri uri=Uri.fromFile(mPhotoFile);
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT,uri);
        }

        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(captureImage,REQUEST_PHOTO);
            }
        });

        mPhotoView=(ImageView)v.findViewById(R.id.crime_photo);

        ViewTreeObserver observer=mPhotoView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                boolean isFirstPass=(mPhotoViewSize==null);

                mPhotoViewSize=new Point(mPhotoView.getWidth(),mPhotoView.getHeight());
                //Log.d("viewSize",mPhotoViewSize.toString());
                if(isFirstPass) updatePhotoView();

            }
        });

        mPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager=getActivity().getSupportFragmentManager();
                ShowPhotoDialog dialog=ShowPhotoDialog.newInstance(mPhotoFile.getPath());
                dialog.show(manager,DIALOG_SHOW_PHOTO);

            }
        });

        return v;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){

        if(resultCode!= Activity.RESULT_OK) return;

        if(requestCode==REQUEST_DATE){
            Date date=(Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);
            date.setHours(mCrime.getDate().getHours());
            date.setMinutes(mCrime.getDate().getMinutes());

            mCrime.setDate(date);
            updateDate();
        }
        if(requestCode==REQUEST_TIME){
            Date date=(Date)data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);
            Calendar calendar=new GregorianCalendar();
            calendar.setTimeInMillis(date.getTime());
            calendar.set(Calendar.YEAR,mCrime.getDate().getYear()+1900);
            calendar.set(Calendar.MONTH,mCrime.getDate().getMonth());
            mCrime.setDate(calendar.getTime());
            updateTime();
        }
        if(requestCode==REQUEST_CONTACT && data!=null){
            mSuspectContactUri=data.getData(); //указатель на контакт который выбрал пользователь

            String[] queryFields=new String[]{ ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts._ID};

            Cursor c=getActivity().getContentResolver().query(mSuspectContactUri,queryFields,null,null,null);
            try {
                if(c.getCount()==0) return;

                c.moveToFirst();
                String suspect=c.getString(0);
                mCrime.setSuspect(suspect);
                mSuspectButton.setText(suspect);

                mSuspectID=c.getString(1);
                Log.d("suspectID",mSuspectID);

            }finally {
                c.close();
            }
            getSuspectPhoneNumber();
        }
        if(requestCode==REQUEST_PHOTO){
            updatePhotoView();
        }

    }

    @Override
    public void onPause(){
        super.onPause();

        CrimeLab.get(getActivity()).updateCrime(mCrime);
    }

    private void updateDate(){
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        mDateButton.setText(DateFormat.format("EEEE, MMMM dd, yyyy",mCrime.getDate()).toString());
    }

    private void updateTime(){
        java.text.DateFormat df = android.text.format.DateFormat.getTimeFormat(getActivity());
        if(mCrime.getDate()!=null)
        mTimeButton.setText("at "+df.format(mCrime.getDate()));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.fragment_crime,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_item_delete_crime:
                CrimeLab crimeLab=CrimeLab.get(getActivity());
                crimeLab.deleteCrime(mCrime);
                getActivity().finish();
            return true;
         default: return super.onOptionsItemSelected(item);
        }
    }

    private String getCrimeReport(){
        String solvedString=null;
        if(mCrime.isSolved()){
            solvedString=getString(R.string.crime_report_solved);
        }else {
            solvedString=getString(R.string.crime_report_unsolved);
        }

        String dateFormat="EEE, MMM dd";
        String dateString= DateFormat.format(dateFormat,mCrime.getDate()).toString();

        String suspect=mCrime.getSuspect();
        if(suspect==null){
            suspect=getString(R.string.crime_report_no_suspect);
        }else {
            suspect=getString(R.string.crime_report_suspect,suspect);
        }

        String report=getString(R.string.crime_report,
                mCrime.getTitle(),dateString,solvedString,suspect);

        return report;
    }

    private String getSuspectPhoneNumber(){
        String phoneNumber;
        Cursor c=getActivity().getContentResolver().query(
                Phone.CONTENT_URI,null,
                Phone.CONTACT_ID+" = "+mSuspectID,null,null);
        try {
            if(c.getCount()==0) return null;
            c.moveToFirst();
            phoneNumber=c.getString(c.getColumnIndex(Phone.NUMBER));

            Log.d("suspectID",phoneNumber);

        }finally {
            c.close();
        }

        return phoneNumber;
    }

    private void updatePhotoView(){
        if(mPhotoFile==null|| !mPhotoFile.exists()){
            mPhotoView.setImageDrawable(null);
        }else{
            //Bitmap bitmap= PictureUtils.getScaledBitmap(mPhotoFile.getPath(),getActivity());

            /*Bitmap bitmap= (mPhotoViewSize==null) ? PictureUtils.getScaledBitmap(mPhotoFile.getPath(),getActivity())
                    : PictureUtils.getScaledBitmap(mPhotoFile.getPath(),mPhotoViewSize.x,mPhotoViewSize.y);*/

            Bitmap bitmap;

            if(mPhotoViewSize==null){
                Log.d("viewSize","getScaledBitmap(mPhotoFile.getPath(),getActivity())");
                bitmap=PictureUtils.getScaledBitmap(mPhotoFile.getPath(),getActivity());
            }else {
                Log.d("viewSize","getScaledBitmap(mPhotoFile.getPath(),mPhotoViewSize.x,mPhotoViewSize.y)");
                bitmap=PictureUtils.getScaledBitmap(mPhotoFile.getPath(),mPhotoViewSize.x,mPhotoViewSize.y);
            }

            mPhotoView.setImageBitmap(bitmap);
        }


    }


}
