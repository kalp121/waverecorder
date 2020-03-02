package com.danielkim.soundrecorder.activities;

import android.Manifest;
import android.app.Activity;
//import android.app.Fragment;
//import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;
import com.danielkim.soundrecorder.MySharedPreferences;
import com.danielkim.soundrecorder.R;
import com.danielkim.soundrecorder.fragments.FileViewerFragment;
import com.danielkim.soundrecorder.fragments.Grouping;
import com.danielkim.soundrecorder.fragments.RecordFragment;
import com.danielkim.soundrecorder.listeners.FolderListener;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.security.ProviderInstaller;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements FolderListener {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private PagerSlidingTabStrip tabs;
    private ViewPager pager;
    private String selectedFolder = "";
    private Boolean folderChecked = false;
    private MyAdapter adapter;
    private BroadcastReceiver mReceiver;
    String[] permissions = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String name = MySharedPreferences.getUserName(this);
        if (name.isEmpty()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        pager = (ViewPager) findViewById(R.id.pager);
        adapter = new MyAdapter(getSupportFragmentManager());
        pager.setAdapter(adapter);
        tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabs.setViewPager(pager);
        adapter.notifyDataSetChanged();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setPopupTheme(R.style.ThemeOverlay_AppCompat_Light);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

//        updateAndroidSecurityProvider(this);
        checkPermissions();
        //mReceiver = new BroadcastReceiver() {
//
        //    @Override
        //    public void onReceive(Context context, Intent intent) {
        //        //extract our message from intent
        //        String msg_for_me = intent.getStringExtra("some_msg");
        //        //log our message value
        //
//
        //    }
        //};
    }

    private boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(this, p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
            return false;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (!selectedFolder.isEmpty() || folderChecked) {
            adapter.goBackFolder();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void folderSelected(String folder) {
        selectedFolder = folder;
    }

    @Override
    public void folderChecked(Boolean displayOptions) {
        folderChecked = displayOptions;
        checkFolderStatus();
    }

    private void checkFolderStatus() {
        adapter.setFolderChecked(folderChecked);
    }

    public class MyAdapter extends FragmentStatePagerAdapter {
        private final FileViewerFragment fragment;
        private FragmentManager fragmentManager;
        private Fragment mFragmentAtPos0;
        private String[] titles = {getString(R.string.tab_title_record),
                getString(R.string.tab_title_saved_recordings)};

        private final class MainPageListener implements MainPageFragmentListener{
            public void onSwitchToNextFragment(int index) {
                fragmentManager.beginTransaction().remove(mFragmentAtPos0)
                        .commit();
                if (mFragmentAtPos0 instanceof Grouping){
                    mFragmentAtPos0 = RecordFragment.newInstance(0,listener,index);
                }else{
                    mFragmentAtPos0 = Grouping.newInstance(0,listener);
                }
                adapter.notifyDataSetChanged();
            }
        }
        MainPageListener listener =new MainPageListener();


        public MyAdapter(FragmentManager fm) {
            super(fm);
            fragmentManager=fm;
            fragment = FileViewerFragment.newInstance(0);
        }

        @Override
        public int getItemPosition(Object object) {
            if (object instanceof Grouping && mFragmentAtPos0 instanceof RecordFragment)
                return POSITION_NONE;
            if (object instanceof RecordFragment && mFragmentAtPos0 instanceof Grouping)
                return POSITION_NONE;
            return POSITION_UNCHANGED;
        }

        public Fragment getItem(int position) {
            switch (position) {
                case 0: {
                    if (mFragmentAtPos0 == null) {
                        mFragmentAtPos0 = Grouping.newInstance(position,listener);
                    }
                    return mFragmentAtPos0;
                }
                case 1: {
                    return fragment;
                }
            }
            return null;
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        public void goBackFolder() {
            fragment.goBackFolder();
        }

        public void setFolderChecked(Boolean folderChecked) {
            fragment.setFolderChecked(folderChecked);
        }
    }

    public interface MainPageFragmentListener {
        void onSwitchToNextFragment(int index);
    }


    public MainActivity() {
    }

   private void updateAndroidSecurityProvider(Activity callingActivity) {
       try {
           ProviderInstaller.installIfNeeded(this);
       } catch (GooglePlayServicesRepairableException e) {
           // Thrown when Google Play Services is not installed, up-to-date, or enabled
           // Show dialog to allow users to install, update, or otherwise enable Google Play services.
           GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), callingActivity, 0);
       } catch (GooglePlayServicesNotAvailableException e) {
       }

   }


}

