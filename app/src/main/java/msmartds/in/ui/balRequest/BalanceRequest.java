package msmartds.in.ui.balRequest;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import  msmartds.in.util.BaseActivity;

import java.util.Objects;

public class BalanceRequest extends BaseActivity {

    ViewPager viewPager = null;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( msmartds.in.R.layout.balance_request_activity);

        Toolbar toolbar = (Toolbar) findViewById( msmartds.in.R.id.toolbar1);
        toolbar.setTitle("Balance Request");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById( msmartds.in.R.id.pagerr);
        viewPager.setAdapter(new Myadapter(getSupportFragmentManager(), BalanceRequest.this));    // Set up the ViewPager with the sections adapter.
        tabLayout = (TabLayout) findViewById( msmartds.in.R.id.tabss);
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

                if (position == 0) {
                    fragment = new BalanceRequestFragment();
                }
                if (position == 1) {
                    fragment = new BalanceHistoryFragemnt();
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
    }
}

