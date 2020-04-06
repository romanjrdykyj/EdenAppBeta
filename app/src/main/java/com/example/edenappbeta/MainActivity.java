package com.example.edenappbeta;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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


import org.w3c.dom.Text;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {


    //zmienne do przesuwanych okienek
    private    ViewPager pager;
    private    PagerAdapter pagerAdapter;

    // UUID do połącznie BT
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // MAC do połączenia BT
    private static String address = "24:6F:28:AE:0D:86";

    //rodzaj bledu polaczania BT
    int rodzaj_bledu=-1;




    BluetoothAdapter bluetoothAdapter;          //nie wiem co z tym ??? do samego dolu
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;
    private static final String TAG = "BT";





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //właczenie BT przy samym starcie
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(i, 1);
        }
        //utworzenie widoku itp.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //dodanie dolnego paska nawigacyjnego
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        //dodanie przesuwanych okien
        bottomNav.setOnNavigationItemSelectedListener(navListner);
        //ustawienie przy włączeniu przesuwanych okien
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
        //ustawienie jako strony startowej SENSORFRAGMENT
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SensorFragment()).commit();

        //automatyczne łączenie z ESP32 i sprawdzanie łącza
        if(check_connected()==1)
        {
            Toast.makeText(getApplicationContext(), "Connected!", Toast.LENGTH_SHORT).show();
        }
        if(check_connected()==2)
        {
            Toast.makeText(getApplicationContext(), "Please pair device!", Toast.LENGTH_SHORT).show();
        }

    }

    //dodanie do layoutu gornego menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu, menu);
        return true;
    }

    //dodanie do przesuwania gornego menu
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

    //dodanie do przesuwania dolnego menu oraz layoutu
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



    //status połączenia -> potrzebny w public void onBT
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        if(requestCode == 1)
        {
            if (resultCode == RESULT_OK)
            {
                Toast.makeText(getApplicationContext(), "The bluetooth is enabled!", Toast.LENGTH_SHORT).show();
            }
        }
        if(requestCode == 2)
        {
            if (resultCode == RESULT_OK)
            {
                Toast.makeText(getApplicationContext(), "The bluetooth is enabled!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //obsluga przycisku  w zakladce settings BT
    public void onBt (View view)
    {

        if (!bluetoothAdapter.isEnabled())
        {
            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(i,1);
        }
        if (bluetoothAdapter.isEnabled())
        {
            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(i,2);
        }



    }

    //obsluga przycisku list device w zakladce settings BT
    public void show_listdevice (View view)
    {
        Intent intentOpenBluetoothSettings = new Intent();
        intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
        startActivity(intentOpenBluetoothSettings);
    }

    //nie wiem czemu nie moglem tego zrobic prosciej ale pozniej spróbować
    //sprawdzanie czy łączy się z ESP32 - dowolny model - pierwsze 3 segmenty z MAC
    public int check_connected()
    {
        if(bluetoothAdapter.isEnabled())
        {
            for (BluetoothDevice bt : bluetoothAdapter.getBondedDevices())
            {
                char[] pobrane = bt.getAddress().toCharArray();
                char[] wzor = new char[6];
                wzor[0] = '2';
                wzor[1] = '4';
                wzor[2] = ':';
                wzor[3] = '6';
                wzor[4] = 'F';
                wzor[5] = '8';

                if (wzor[0] == pobrane[0] &
                        wzor[1] == pobrane[1] &
                        wzor[2] == pobrane[2] &
                        wzor[3] == pobrane[3] &
                        wzor[4] == pobrane[4] &
                        wzor[2] == pobrane[5] &
                        wzor[0] == pobrane[6] &
                        wzor[5] == pobrane[7])
                {
                    return 1;
                }
            }
            return 2;
        }
        else
        {
            return 3;
        }
    }

    //osbluga przycisku Connect z zakladce Settings BT
    public void connect (View view)
    {
        if(check_connected()==1)
        {
            Toast.makeText(getApplicationContext(), "Connected!", Toast.LENGTH_SHORT).show();
        }
        if(check_connected()==2)
        {
            Toast.makeText(getApplicationContext(), "Please pair device!", Toast.LENGTH_SHORT).show();
        }
        if(check_connected()==3)
        {
            Toast.makeText(getApplicationContext(), "Please power BT!", Toast.LENGTH_SHORT).show();
        }
    }

    // pozmieniac sendy
    //sterowanie przyciskami w zakladce manual
    public void btnOnWaterManual(View v)
    {
        if (check_connected()==1) {
            sendData("f");
            Toast.makeText(getBaseContext(), "Turn on!", Toast.LENGTH_SHORT).show();
        }
        else if (check_connected()==2){
            Toast.makeText(getApplicationContext(), "Please pair device!", Toast.LENGTH_SHORT).show();
        }
        else if (check_connected()==3)
        {
            Toast.makeText(getApplicationContext(), "Please power BT!", Toast.LENGTH_SHORT).show();
        }
    }
    public void btnOffWaterManual(View v)
    {
        if (check_connected()==1) {
            sendData("b");
            Toast.makeText(getBaseContext(), "Turn off!", Toast.LENGTH_SHORT).show();
        }
        else if (check_connected()==2){
            Toast.makeText(getApplicationContext(), "Please pair device!", Toast.LENGTH_SHORT).show();
        }
        else if (check_connected()==3)
        {
            Toast.makeText(getApplicationContext(), "Please power BT!", Toast.LENGTH_SHORT).show();
        }
    }
    public void btnOnLightManual(View v)
    {
        if (check_connected()==1) {
            //sendData("g");
            Toast.makeText(getBaseContext(), "Turn on!", Toast.LENGTH_SHORT).show();
        }
        else if (check_connected()==2){
            Toast.makeText(getApplicationContext(), "Please pair device!", Toast.LENGTH_SHORT).show();
        }
        else if (check_connected()==3)
        {
            Toast.makeText(getApplicationContext(), "Please power BT!", Toast.LENGTH_SHORT).show();
        }
    }
    public void btnOffLightManual(View v)
    {
        if (check_connected()==1) {
            //sendData("g");
            Toast.makeText(getBaseContext(), "Turn off!", Toast.LENGTH_SHORT).show();
        }
        else if (check_connected()==2){
            Toast.makeText(getApplicationContext(), "Please pair device!", Toast.LENGTH_SHORT).show();
        }
        else if (check_connected()==3)
        {
            Toast.makeText(getApplicationContext(), "Please power BT!", Toast.LENGTH_SHORT).show();
        }
    }
    public void btnOnSpritManual(View v)
    {
        if (check_connected()==1) {
            //sendData("g");
            Toast.makeText(getBaseContext(), "Turn on!", Toast.LENGTH_SHORT).show();
        }
        else if (check_connected()==2){
            Toast.makeText(getApplicationContext(), "Please pair device!", Toast.LENGTH_SHORT).show();
        }
        else if (check_connected()==3)
        {
            Toast.makeText(getApplicationContext(), "Please power BT!", Toast.LENGTH_SHORT).show();
        }
    }
    public void btnOffSpritManual(View v)
    {
        if (check_connected()==1) {
            //sendData("g");
            Toast.makeText(getBaseContext(), "Turn off!", Toast.LENGTH_SHORT).show();
        }
        else if (check_connected()==2){
            Toast.makeText(getApplicationContext(), "Please pair device!", Toast.LENGTH_SHORT).show();
        }
        else if (check_connected()==3)
        {
            Toast.makeText(getApplicationContext(), "Please power BT!", Toast.LENGTH_SHORT).show();
        }
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        if(Build.VERSION.SDK_INT >= 10){
            try {
                final Method  m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[] { UUID.class });
                return (BluetoothSocket) m.invoke(device, MY_UUID);
            } catch (Exception e) {
                Log.e(TAG, "Could not create Insecure RFComm Connection",e);
            }
        }
        return  device.createRfcommSocketToServiceRecord(MY_UUID);
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "...onResume - try connect...");

        // Set up a pointer to the remote node using it's address.
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);

        // Two things are needed to make a connection:
        //   A MAC address, which we got above.
        //   A Service ID or UUID.  In this case we are using the
        //     UUID for SPP.

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e1) {
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e1.getMessage() + ".");
        }

        // Discovery is resource intensive.  Make sure it isn't going on
        // when you attempt to connect and pass your message.
        bluetoothAdapter.cancelDiscovery();

        // Establish the connection.  This will block until it connects.
        Log.d(TAG, "...Connecting...");
        try {
            btSocket.connect();
            Log.d(TAG, "...Connection ok...");
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }

        // Create a data stream so we can talk to server.
        Log.d(TAG, "...Create Socket...");

        try {
            outStream = btSocket.getOutputStream();
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and output stream creation failed:" + e.getMessage() + ".");
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "...In onPause()...");

        if (outStream != null) {
            try {
                outStream.flush();
            } catch (IOException e) {
                errorExit("Fatal Error", "In onPause() and failed to flush output stream: " + e.getMessage() + ".");
            }
        }

        try     {
            btSocket.close();
        } catch (IOException e2) {
            errorExit("Fatal Error", "In onPause() and failed to close socket." + e2.getMessage() + ".");
        }
    }


    private void errorExit(String title, String message){
        Toast.makeText(getBaseContext(), title + " - " + message, Toast.LENGTH_LONG).show();
        finish();
    }

    private void sendData(String message) {
        byte[] msgBuffer = message.getBytes();

        Log.d(TAG, "...Send data: " + message + "...");

        try {
            outStream.write(msgBuffer);
        } catch (IOException e) {
            String msg = "In onResume() and an exception occurred during write: " + e.getMessage();
            if (address.equals("00:00:00:00:00:00"))
                msg = msg + ".\n\nUpdate your server address from 00:00:00:00:00:00 to the correct address on line 35 in the java code";
            msg = msg +  ".\n\nCheck that the SPP UUID: " + MY_UUID.toString() + " exists on server.\n\n";

            errorExit("Fatal Error", msg);
        }
    }

}
