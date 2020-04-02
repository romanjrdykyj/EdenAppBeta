package com.example.edenappbeta;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.edenappbeta.AutoSlide.NawadnianieFragment;
import com.example.edenappbeta.AutoSlide.OswietlenieFragment;
import com.example.edenappbeta.AutoSlide.ZraszanieFragment;
import com.example.edenappbeta.FragmentBottom.AutomaticFragment;
import com.example.edenappbeta.FragmentBottom.ManualFragment;
import com.example.edenappbeta.FragmentBottom.SensorFragment;
import com.example.edenappbeta.FragmentTop.NotifiFragment;
import com.example.edenappbeta.FragmentTop.SettingsFragment;
import com.example.edenappbeta.ManualSlide.ManualNawadnianieFragment;
import com.example.edenappbeta.ManualSlide.ManualOswietlenieFragment;
import com.example.edenappbeta.ManualSlide.ManualZraszanieFragment;
import com.example.edenappbeta.NotifiSlide.NotifiSlideFragment;
import com.example.edenappbeta.SensorSlide.SensorHumaFragment;
import com.example.edenappbeta.SensorSlide.SensorLightFragment;
import com.example.edenappbeta.SensorSlide.SensorOwnFragment;
import com.example.edenappbeta.SensorSlide.SensorSoilFragment;
import com.example.edenappbeta.SensorSlide.SensorTempFragment;
import com.example.edenappbeta.SensorSlide.SensorWaterFragment;
import com.example.edenappbeta.SettingsSlide.SettingsBluetoothFragment;
import com.example.edenappbeta.SettingsSlide.SettingsFirebaseFragment;
import com.example.edenappbeta.SettingsSlide.SettingsFlowerFragment;


import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

private    ViewPager pager;
private    PagerAdapter pagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListner);



        List<Fragment> list_start = new ArrayList<>();
        list_start.add(new SensorSoilFragment());
        list_start.add(new SensorTempFragment());
        list_start.add(new SensorHumaFragment());
        list_start.add(new SensorLightFragment());
        list_start.add(new SensorOwnFragment());
        list_start.add(new SensorWaterFragment());
        pager = findViewById(R.id.view_pager);
        pagerAdapter = new SlidePagerAdapter(getSupportFragmentManager(), list_start);
        pager.setAdapter(pagerAdapter);




        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SensorFragment()).commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;

        switch (item.getItemId()) {
            case R.id.notifi1:
                selectedFragment = new NotifiFragment();
                List<Fragment> list_notifi = new ArrayList<>();
                list_notifi.add(new NotifiSlideFragment());
                pager = findViewById(R.id.view_pager);
                pagerAdapter = new SlidePagerAdapter(getSupportFragmentManager(), list_notifi);
                pager.setAdapter(pagerAdapter);
                break;
            case R.id.settings1:
                selectedFragment = new SettingsFragment();
                List<Fragment> list_settings = new ArrayList<>();
                list_settings.add(new SettingsBluetoothFragment());
                list_settings.add(new SettingsFirebaseFragment());
                list_settings.add(new SettingsFlowerFragment());
                pager = findViewById(R.id.view_pager);
                pagerAdapter = new SlidePagerAdapter(getSupportFragmentManager(), list_settings);
                pager.setAdapter(pagerAdapter);
                break;



        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
        return true;
    }


    public BottomNavigationView.OnNavigationItemSelectedListener navListner =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;

                    switch (menuItem.getItemId()){
                        case R.id.nav_dashboard:
                            selectedFragment = new SensorFragment();
                            List<Fragment> list_sensor = new ArrayList<>();
                            list_sensor.add(new SensorSoilFragment());
                            list_sensor.add(new SensorTempFragment());
                            list_sensor.add(new SensorHumaFragment());
                            list_sensor.add(new SensorLightFragment());
                            list_sensor.add(new SensorOwnFragment());
                            list_sensor.add(new SensorWaterFragment());

                            pager = findViewById(R.id.view_pager);
                            pagerAdapter = new SlidePagerAdapter(getSupportFragmentManager(), list_sensor);
                            pager.setAdapter(pagerAdapter);
                            break;
                        case R.id.nav_automatic:
                            selectedFragment = new AutomaticFragment();
                            List<Fragment> list_auto = new ArrayList<>();
                            list_auto.add(new NawadnianieFragment());
                            list_auto.add(new OswietlenieFragment());
                            list_auto.add(new ZraszanieFragment());
                            pager = findViewById(R.id.view_pager);
                            pagerAdapter = new SlidePagerAdapter(getSupportFragmentManager(), list_auto);
                            pager.setAdapter(pagerAdapter);
                            break;
                        case R.id.nav_manual:
                            selectedFragment = new ManualFragment();
                            List<Fragment> list_manual = new ArrayList<>();
                            list_manual.add(new ManualNawadnianieFragment());
                            list_manual.add(new ManualOswietlenieFragment());
                            list_manual.add(new ManualZraszanieFragment());
                            pager = findViewById(R.id.view_pager);
                            pagerAdapter = new SlidePagerAdapter(getSupportFragmentManager(), list_manual);
                            pager.setAdapter(pagerAdapter);
                            break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                    return true;
            };

    };




}
