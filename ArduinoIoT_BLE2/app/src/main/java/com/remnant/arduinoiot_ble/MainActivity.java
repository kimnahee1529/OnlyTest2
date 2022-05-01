package com.remnant.arduinoiot_ble;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    BluetoothManager btManager;
    BluetoothAdapter btAdapter=null;
    BluetoothLeScanner btScanner;
    BluetoothGatt btGatt;
    BluetoothGattService btGattservice;

    BluetoothGattCharacteristic ValueCharacteristic_write;
    BluetoothGattCharacteristic ValueCharacteristic_read;

    Button btnSwitchOn;
    Button btnSwitchOff;
    Button btnRead;

    TextView tvStatus;
    TextView tvValue;
    ImageSwitcher imageSwitcher;
    SeekBar seekBar;
    String strAddress;
    String strDevicename;

    // BLE.setLocalName("Demo Gyroscope") of Arduino Nano 33 IoT source Code
    private final static String TEST_BLE_DEVICE_NAME = "Demo Gyroscope";
    private final static int REQUEST_ENABLE_BT = 1;
    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    MenuItem scanitem;
    MenuItem stopscanitem;
    MenuItem connecteitem;
    MenuItem deconnectitem;


    //Serivce UUID와 같으면 안됨
    private static final UUID UUID_Service = UUID.fromString("66df5109-edde-4f8a-a5e1-02e02a69cbd5");
    private static final UUID UUID_VALUE_WRITE = UUID.fromString("AC9926CA-941E-4CE7-83A4-BBE77C726975");
    private static final UUID UUID_VALUE_READ = UUID.fromString("741c12b9-e13c-4992-8a5e-fce46dec0bff");

    boolean bScanON = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("This app needs location access");
                builder.setMessage("Please grant location access so this app can detect beacons.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.setOnDismissListener(new DialogInterface.OnDismissListener() {

                    public void onDismiss(DialogInterface dialog) {

                        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
                    }


                 });
                builder.show();
            }
        }


        seekBar = (SeekBar)findViewById(R.id.seek);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                               @Override
                                               public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

                                                   int iSeekValue = 0;
                                                   if(i>=0 && i<25)
                                                   {
                                                       iSeekValue = 1;
                                                   } else if(i>=25 && i<50) {
                                                       iSeekValue = 2;
                                                   } else if(i>=50 && i<75) {
                                                       iSeekValue = 3;
                                                   } else if(i>=75 && i<100) {
                                                       iSeekValue = 4;
                                                   } else {
                                                       iSeekValue = 0;
                                                   }

                                                   if(btGatt != null)
                                                   {
                                                        byte[] value=new byte[1];

                                                        value [0]=(byte)(iSeekValue);
                                                        ValueCharacteristic_write.setValue(value);
                                                        btGatt.writeCharacteristic(ValueCharacteristic_write);

                                                       (MainActivity.this).runOnUiThread(new Runnable(){
                                                           @Override
                                                           public void run() {
                                                               if(value[0] == 0) {
                                                                   imageSwitcher.setImageResource(R.drawable.off);
                                                               } else {
                                                                   imageSwitcher.setImageResource(R.drawable.on);
                                                               }
                                                           }
                                                       });

                                                   } else {
                                                       tvStatus.setText("BLE device not found");
                                                   }

                                               }

                                               @Override
                                               public void onStartTrackingTouch(SeekBar seekBar) {

                                               }

                                               @Override
                                               public void onStopTrackingTouch(SeekBar seekBar) {

                                               }

                                           }


        );

        imageSwitcher=(ImageSwitcher)findViewById(R.id.imageswitcher);
        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView=new ImageView(getApplicationContext());
                imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                return imageView;
            }
        });

        imageSwitcher.setImageResource(R.drawable.off);
        tvStatus = (TextView) findViewById(R.id.tvStatus);
        tvValue = (TextView) findViewById(R.id.tvValue);

        btnSwitchOn = (Button) findViewById(R.id.btnOn);
        btnSwitchOff = (Button) findViewById(R.id.btnOff);
        btnRead = (Button)findViewById(R.id.btnRead);

        btManager = (BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
        btAdapter = btManager.getAdapter();

        if (btAdapter == null || !btAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent,REQUEST_ENABLE_BT);
        }


    }// onCreate()

    @Override
    protected void onResume() {
        super.onResume();
        if (btAdapter == null || !btAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent,REQUEST_ENABLE_BT);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //return super.onCreateOptionsMenu(menu);

        MenuInflater menuInflater=getMenuInflater();
        menuInflater.inflate(R.menu.menu_main,menu);
        scanitem = menu.findItem(R.id.scan);
        stopscanitem = menu.findItem(R.id.stopscan);
        connecteitem = menu.findItem(R.id.connecter);
        deconnectitem = menu.findItem(R.id.deconnecter);

        return  true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.scan:
                if(btAdapter.isEnabled())
                { Toast.makeText(getApplicationContext(),"Scan Device ... ",Toast.LENGTH_SHORT).show();
                    btScanner = btAdapter.getBluetoothLeScanner();

                    startScanning();
                }
                else
                {
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent,REQUEST_ENABLE_BT);
                }
                return true;
            case R.id.stopscan:
                if(btAdapter.isEnabled()){
                    Toast.makeText(getApplicationContext(),"Stop Scan",Toast.LENGTH_SHORT).show();

                    stopScanning();

                } else {
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent,REQUEST_ENABLE_BT);
                }
                return true;
            case R.id.connecter:
                if(btAdapter.isEnabled()){
                    Toast.makeText(getApplicationContext(),"Connecting the BLE Device",Toast.LENGTH_SHORT).show();
                    /*scanitem.setVisible(false);
                    stopscanitem.setVisible(false);
                    connecteitem.setVisible(false);
                    deconnectitem.setVisible(true);*/
                    connection();
                }
                else
                {
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent,REQUEST_ENABLE_BT);
                }
                return true;
            case R.id.deconnecter:
                if(btAdapter.isEnabled()){
                    Toast.makeText(getApplicationContext(),"Disconnecting the BLE Device",Toast.LENGTH_SHORT).show();
                    scanitem.setVisible(true);
                    stopscanitem.setVisible(false);
                    connecteitem.setVisible(true);
                    deconnectitem.setVisible(false);
                    deconnection();
                } else {
                    Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableIntent,REQUEST_ENABLE_BT);
                }
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    // Device scan callback.
    private ScanCallback leScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {


            //strAddress = result.getDevice().getAddress();


            if (result.getScanRecord().getDeviceName() == null ) {
                strDevicename = "Unknown";
            } else {
                strDevicename = result.getScanRecord().getDeviceName();
            }


            if (bScanON) {
                if (strDevicename.equals(TEST_BLE_DEVICE_NAME)) {
                    tvStatus.setText("Device Name: " + strDevicename + "\n");
                    strAddress = result.getDevice().getAddress();
                    stopScanning();

                    if (btAdapter.isEnabled()) {
                        Toast.makeText(getApplicationContext(), "Stop Scan", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
                    }

                    connection();

                } else {
                    tvStatus.setText("Device not found");
                }
            }
            Log.d("LOG ", strAddress + "  : " + strDevicename);
        }

    };

    private BluetoothGattCallback connectecallabck = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
            btGatt=gatt;
            //gatt.discoverServices();

            if (newState == BluetoothProfile.STATE_CONNECTED) {
                gatt.discoverServices(); // onServicesDiscovered() 호출 (서비스 연결 위해 꼭 필요)

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // Can't see.
            }


        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);

            BluetoothGattService service = gatt.getService(UUID_Service);
            ValueCharacteristic_write = service.getCharacteristic(UUID_VALUE_WRITE);
            ValueCharacteristic_read = service.getCharacteristic(UUID_VALUE_READ);

            (MainActivity.this).runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    btnAllEnable();
                }
            });

        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,final BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);

            String strData = characteristic.getStringValue(0);
             /*
             String value = null;
             try {
                 value = new String (characteristic.getValue(), "UTF-8");
             } catch (UnsupportedEncodingException e) {
                 e.printStackTrace();
             }
            */

            (MainActivity.this).runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    tvValue.setText("Read Value : " + strData);
                }
            });

            super.onCharacteristicRead(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);

        }
    };

    public void startScanning() {
        btScanner.startScan(leScanCallback);
        scanitem.setVisible(false);
        stopscanitem.setVisible(true);
        connecteitem.setVisible(false);
        deconnectitem.setVisible(false);
        bScanON = true;
    }

    public void stopScanning() {
        btScanner.stopScan(leScanCallback);
        scanitem.setVisible(true);
        stopscanitem.setVisible(false);
        connecteitem.setVisible(true);
        deconnectitem.setVisible(false);
        bScanON = false;
    }
    public void connection()
    {
        if(strDevicename!=null)
        {   if (strDevicename.equals(TEST_BLE_DEVICE_NAME) )
            {
                tvStatus.setText("Arduino Nano 33 Iot device found");
                BluetoothDevice device=btAdapter.getRemoteDevice(strAddress);
                device.connectGatt(getApplicationContext(),false,connectecallabck);

                scanitem.setVisible(false);
                stopscanitem.setVisible(false);
                connecteitem.setVisible(false);
                deconnectitem.setVisible(true);

            } else {
                Toast.makeText(getApplicationContext(),"device not found",Toast.LENGTH_SHORT).show();
                tvStatus.setText("Arduino Nano 33 Iot device not found");
            }
        } else {
            tvStatus.setText("Can't detected the device!!");
            deconnectitem.setVisible(false);
            scanitem.setVisible(true);
        }

        //btnAllEnable();
    }

    public void deconnection()
    {
        if(strDevicename.equals(TEST_BLE_DEVICE_NAME) && btGatt!=null)
        {
            btGatt.disconnect();
            btGatt.close();
            btnAllDisable();

            if(btAdapter.isEnabled()){
                Toast.makeText(getApplicationContext(),"Disconnect the BLE Device.", Toast.LENGTH_SHORT).show();
                scanitem.setVisible(true);
                stopscanitem.setVisible(false);
                connecteitem.setVisible(true);
                deconnectitem.setVisible(false);
            }
            else
            {
                Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableIntent,REQUEST_ENABLE_BT);
            }

        } else {
            btnAllDisable();
            tvStatus.setText("BLE Device not found");
        }
    }
    public void readON(View v)
    {
                btGatt.readCharacteristic(ValueCharacteristic_read);

    }
    public void writeON(View v)
    {
        if(btGatt!=null)
        {   byte[] value=new byte[1];
            // value [0]=(byte)(01&0xff);
            value [0]=(byte)(01);

            (MainActivity.this).runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    imageSwitcher.setImageResource(R.drawable.on);
                }
            });



            ValueCharacteristic_write.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
            //ValueCharacteristic_write.setValue("String");
            ValueCharacteristic_write.setValue(value);
            btGatt.writeCharacteristic(ValueCharacteristic_write);
        } else {
            tvStatus.setText("Arduino Nano 33 Iot device not found");
        }
    }

    public void writeOff(View v)
    {
        if(btGatt!=null)
        { byte[] value=new byte[1];
            // value [0]=(byte)(00&0xff);
            value [0]=(byte)(00);

            ValueCharacteristic_write.setValue(value);
            btGatt.writeCharacteristic(ValueCharacteristic_write);
            (MainActivity.this).runOnUiThread(new Runnable(){
                @Override
                public void run() {
                    imageSwitcher.setImageResource(R.drawable.off);
                }
            });

        }else {
            tvStatus.setText("Arduino Nano 33 Iot device not found");
        }
    }

    void btnAllEnable() {
        btnSwitchOn.setEnabled(true);
        btnSwitchOff.setEnabled(true);
        btnRead.setEnabled(true);
        seekBar.setEnabled(true);

    }

    void btnAllDisable() {
        btnSwitchOn.setEnabled(false);
        btnSwitchOff.setEnabled(false);
        btnRead.setEnabled(false);
        seekBar.setEnabled(false);

    }

}