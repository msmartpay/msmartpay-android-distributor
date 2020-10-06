package msmartds.in.balRequest;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import msmartds.in.URL.BaseActivity;

public class BalanceRequest extends BaseActivity implements BalanceRequestFragment.Communication, BalanceHistoryFragemnt.Communicator2 {

    ViewPager viewPager = null;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(msmartds.in.R.layout.balance_request_activity);

        Toolbar toolbar = (Toolbar) findViewById(msmartds.in.R.id.toolbar1);
        toolbar.setTitle("Balance Request");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(msmartds.in.R.id.pagerr);
        viewPager.setAdapter(new Myadapter(getSupportFragmentManager(), BalanceRequest.this));    // Set up the ViewPager with the sections adapter.
        tabLayout = (TabLayout) findViewById(msmartds.in.R.id.tabss);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);

    }

    class Myadapter extends FragmentStatePagerAdapter {
        private Context ctx;
        private boolean conn = false;

        public Myadapter(FragmentManager fm, Context ctx) {
            super(fm);
            this.ctx = ctx;
        }

        @Override
        public Fragment getItem(int position) {

            Fragment fragment = null;
            if (isConnectionAvailable()) {
                if (position == 0) {
                    fragment = new BalanceRequestFragment();
                }
                if (position == 1) {
                    fragment = new BalanceHistoryFragemnt();
                }
            } else {
                Toast.makeText(ctx, "No Internet Connection!", Toast.LENGTH_SHORT).show();
            }
            return fragment;
        }

        @Override
        public int getCount() {
            return 2;
        }


        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Balance Request";
                case 1:
                    return "Request History";
            }
            return null;
        }

        public boolean isConnectionAvailable() {

            if (isOnline() == false) {
                return false;
            } else {
                return true;
            }
        }

        public boolean isOnline() {
            boolean haveConnectedWifi = false;
            boolean haveConnectedMobile = false;

            ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(ctx.CONNECTIVITY_SERVICE);
            NetworkInfo[] netInfo = cm.getAllNetworkInfo();
            for (NetworkInfo ni : netInfo) {
                if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                    if (ni.isConnected())
                        haveConnectedWifi = true;
                if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                    if (ni.isConnected())
                        haveConnectedMobile = true;
            }
            if (haveConnectedWifi == true || haveConnectedMobile == true) {
                Log.e("Log-Wifi", String.valueOf(haveConnectedWifi));
                Log.e("Log-Mobile", String.valueOf(haveConnectedMobile));
                conn = true;
            } else {
                Log.e("Log-Wifi", String.valueOf(haveConnectedWifi));
                Log.e("Log-Mobile", String.valueOf(haveConnectedMobile));
                conn = false;
            }

            return conn;
        }
    }
}

