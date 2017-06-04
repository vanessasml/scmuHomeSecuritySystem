package com.p1.scmu.home_security_system;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;

import java.util.List;

public class ActivityMainMenu extends AppCompatActivity {

    private final static String TAG = ActivityMainMenu.class.getSimpleName();
    private static int request_code_settings = 1;
    private static int request_code_user_settings=2;
    private final static String MEMBER = "Member";
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private LocalService localService;
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    protected List<Member> memberListI;
    protected List<Member> memberListIO;
    protected List<Member> memberList;
    protected Member appOwner;
    private Settings currSettings;
    private WifiManager wifi;
    private LocalService.LocalBinder mBoundService;
    private boolean mIsBound;

    private int[] imageResId={R.mipmap.icon_members,
            R.mipmap.icon_at_home,
            R.mipmap.icon_history};

    private  ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
         //   mBoundService = (LocalService.LocalBinder)service;
            Log.d("Lol","LOL");
            System.err.println("Entrei aqui");
            LocalService.LocalBinder binder = (LocalService.LocalBinder) service;
            localService = binder.getService();
            mIsBound = true;
          //  int statusCode = mBoundService.getStatusCode();
           // Log.d("Binding.java","called onServiceConnected. statusCode: " + statusCode);
            if(localService == null)
            {
                Log.d("scasda","NULL");
            }

            localService.SendRequest();
            refreshMembersLists();
        }
        @Override
        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            mBoundService = null;
            mIsBound = false;
            Log.d("Binding", "called onServiceDisconnected");

        }
    };

    void doBindService() {
        // Establish a connection with the service.  We use an explicit
        // class name because we want a specific service implementation that
        // we know will be running in our own process (and thus won't be
        // supporting component replacement by other applications).
        bindService(new Intent(this, LocalService.class), mConnection, Context.BIND_AUTO_CREATE);
        Toast.makeText(this, "Connected from service", Toast.LENGTH_SHORT).show();
        mIsBound = true;
    }

    void doUnbindService() {
        if (mIsBound) {
           // int statusCode = mBoundService.getStatusCode();
          //  if (statusCode != 0) Log.d("doUnbindService", "Binding.java statusCode: " + statusCode);

            // Tell the user we did an unbind
            Toast.makeText(this, "Disconnected from service", Toast.LENGTH_SHORT).show();

            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        Log.d("OnStart","kjdasdhasidhasi");

        Intent intent = new Intent(this, LocalService.class);
        startService(intent);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        System.err.println(localService);
    }
    @Override
    protected void onResume(){
        Log.d("OnResume","kjdasdhasidhasi");
        super.onResume();
        mIsBound = true;


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setIcon(imageResId[0]);
        tabLayout.getTabAt(1).setIcon(imageResId[1]);
        tabLayout.getTabAt(2).setIcon(imageResId[2]);


}
    @Override
    protected void onStop() {
        super.onStop();
        // Unbind from the service
        if (mIsBound) {
            unbindService(mConnection);
            mIsBound = false;
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("OnCreate","kjdasdhasidhasi");
        setContentView(R.layout.activity_main_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //enableWifiService();
        Intent intent = new Intent(this, LocalService.class);
        startService(intent);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);


    }

    public void refreshMembersLists(){
        Log.d("Refresh","Refresh members list");
        memberList = localService.getMembers();
        memberListI = localService.getMembersAtHome();
        memberListIO = localService.getAllMembersHistory();
    }

    private void enableWifiService(){
        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifi.setWifiEnabled(true);
    }

    public void sendToInsertMemberToServer(Member m){
        localService.sendResponse(m, Request.Method.POST, LocalService.INSERT_MEMBER);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startSettingsActivity(appOwner.rfid);
            return true;
        }else if(id== R.id.action_user_profile){
            startUserSettingsActivity(appOwner);
        }

        return super.onOptionsItemSelected(item);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("ActivitySettings", "receivingDataInMainActivity");
        if (requestCode == request_code_settings) {
            if (resultCode == RESULT_OK) {
                Log.i("ActivitySettings", "guardando");
                Settings settings = (Settings) data.getExtras().get(ActivitySettings.SETTINGS);
                currSettings = settings;
                sendSettingsToServer(settings);
            }
        }else if(requestCode == request_code_user_settings){
            if(resultCode == RESULT_OK) {
                Log.i("ActivityAddUser", "guardando");
                Member member = (Member) data.getExtras().get(ActivityAddUser.MEMBER);
                int index = memberList.indexOf(appOwner);
                memberList.add(index, member);
                memberList.remove(appOwner);
                appOwner=member;
                sendToInsertMemberToServer(member);
            }
        }
    }

    private void sendSettingsToServer(Settings settings) {
        localService.sendSettings(settings, Request.Method.POST, LocalService.INSERT_SETTINGS);
    }

    private void startSettingsActivity(String auth) {
        Intent intent = new Intent(this, ActivitySettings.class);
        intent.putExtra(ActivitySettings.PREVIOUS_SETTINGS, currSettings);
        startActivityForResult(intent, request_code_settings);
    }

    private void startUserSettingsActivity(Member member) {
        Intent intent = new Intent(this, ActivityUserSettings.class);
        intent.putExtra(MEMBER, member);
        startActivityForResult(intent, request_code_user_settings);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        private MembersListAdapter membersAdapter;
        private View rootView;
        private ActivityMainMenu activityMainMenu;

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            rootView = inflater.inflate(R.layout.tab_members, container, false);
            activityMainMenu = (ActivityMainMenu) getActivity();
            return rootView;
        }

        @Override
        public void onActivityCreated(@Nullable Bundle savedInstanceState){
            super.onActivityCreated(savedInstanceState);

            ListView listView = (ListView) rootView.findViewById(R.id.list_view_members);
            membersAdapter = new MembersListAdapter(rootView.getContext(), activityMainMenu.memberList);
            listView.setAdapter(membersAdapter);
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    ActivityTabMembers m = new ActivityTabMembers();
                    return m;
                case 1:
                    ActivityTabAtHome ah = new ActivityTabAtHome();
                    return ah;
                case 2:
                    ActivityTabHistory h = new ActivityTabHistory();
                    return h;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Members";
                case 1:
                    return "At Home";
                case 2:
                    return "History";
            }
            return null;
        }
    }
}
