package com.example.edenappbeta;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
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
    private static String name = "ESP32_LED_Control";

    //rodzaj bledu polaczania BT
    int rodzaj_bledu=-1;

    //zmienne do BT
    BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket btSocket = null;
    private OutputStream outStream = null;
    private static final String TAG = "BT";
    //zmienne do obslugi handlera i BT
    private StringBuilder sb = new StringBuilder();
    private ConnectedThread mConnectedThread;
    Handler h;
    final int RECIEVE_MESSAGE = 1;

    public ArrayList<String> arrayList=new ArrayList<>();

    StringBuilder idAL = new StringBuilder("AL");
    StringBuilder idSO = new StringBuilder("SO");
    StringBuilder idLI = new StringBuilder("LI");
    StringBuilder idHU = new StringBuilder("HU");
    StringBuilder idWA = new StringBuilder("WA");
    StringBuilder idTE = new StringBuilder("TE");
    StringBuilder idOW = new StringBuilder("OW");


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

        h = new Handler() {
            public void handleMessage(android.os.Message msg) {
                switch (msg.what) {
                    case RECIEVE_MESSAGE:
                        sb.append(msg.obj);
                        int endOfLineIndex=-1;
                        endOfLineIndex = sb.indexOf("\n");
                        if(endOfLineIndex>0)
                        {
                            StringBuilder sbID = new StringBuilder(sb);
                            sbID.setLength(2);
                            if ((sbID.toString()).compareTo((idAL.toString()))==0) {
                                sb.delete(0,4); //format w ESP -> AL: TUTAJ TEKST WIADOMOSCI
                                Collections.reverse(arrayList);
                                arrayList.add(sb.toString());
                                ListView idAlerty = findViewById(R.id.alerty);
                                Collections.reverse(arrayList);
                                ArrayAdapter arrayAdapter = new ArrayAdapter(MainActivity.this, android.R.layout.simple_list_item_1, arrayList);
                                idAlerty.setAdapter(arrayAdapter);
                                sb.setLength(0);
                            }
                            if ((sbID.toString()).compareTo((idSO.toString()))==0) {

                            }
                            if ((sbID.toString()).compareTo((idLI.toString()))==0) {

                            }
                            if ((sbID.toString()).compareTo((idHU.toString()))==0) {

                            }
                            if ((sbID.toString()).compareTo((idWA.toString()))==0) {

                            }
                            if ((sbID.toString()).compareTo((idTE.toString()))==0) {

                            }
                            if ((sbID.toString()).compareTo((idOW.toString()))==0) {

                            }
                        }
                }
            };
        };

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

    //sprawdzanie czy łączy się z ESP32 - dowolny model - pierwsze 3 segmenty z MAC lub po nazwie
    public int check_connected()
    {
        if(bluetoothAdapter.isEnabled())
        {
            for (BluetoothDevice bt : bluetoothAdapter.getBondedDevices())
            {
                if (bt.getName().compareTo(name)==0)
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


    //sterowanie przyciskami w zakladce manual
    public void btnOnWaterManual(View v)
    {
        if (check_connected()==1) {
            mConnectedThread.write("g");
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
            mConnectedThread.write("h");
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
            mConnectedThread.write("j");
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
            mConnectedThread.write("k");
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
            mConnectedThread.write("m");
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
            mConnectedThread.write("n");
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

        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            errorExit("Fatal Error", "In onResume() and socket create failed: " + e.getMessage() + ".");
        }

        bluetoothAdapter.cancelDiscovery();

        try {
            btSocket.connect();
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                errorExit("Fatal Error", "In onResume() and unable to close socket during connection failure" + e2.getMessage() + ".");
            }
        }

        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "...In onPause()...");

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

    private class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[256];
            int bytes;


            while (true) {
                try {

                    bytes = mmInStream.read(buffer);
                    String readMessage = new String(buffer,0,bytes);
                    h.obtainMessage(RECIEVE_MESSAGE,readMessage).sendToTarget();
                   // h.obtainMessage(RECIEVE_MESSAGE, bytes, -1, buffer).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }


        public void write(String message) {
            Log.d(TAG, "...Data to send: " + message + "...");
            byte[] msgBuffer = message.getBytes();
            try {
                mmOutStream.write(msgBuffer);
            } catch (IOException e) {
                Log.d(TAG, "...Error data send: " + e.getMessage() + "...");
            }
        }
    }





}
