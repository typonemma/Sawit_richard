package com.example.myapplication;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;

public class formkebunSawit extends AppCompatActivity {

    private UsbManager usbManager;
    private UsbDevice arduinoDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formkebun_sawit);

        usbManager = (UsbManager) getSystemService(USB_SERVICE);

        Button btnSend = findViewById(R.id.submitbtn);
        Button btnReceive = findViewById(R.id.receivedbtn);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToArduino("Hello from Android!");
            }
        });

        btnReceive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                receiveFromArduino();
            }
        });

        // Daftarkan penerima untuk mendeteksi perangkat yang terhubung atau terputus
        IntentFilter filter = new IntentFilter(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(usbReceiver, filter);

        // Periksa perangkat yang sudah terhubung
        checkConnectedDevices();
    }

    private void sendToArduino(String data) {
        // Mengirim data ke Arduino melalui USB
        // Implementasikan kodenya sesuai dengan kebutuhan Anda
    }

    private void receiveFromArduino() {
        // Menerima data dari Arduino melalui USB
        // Implementasikan kodenya sesuai dengan kebutuhan Anda
    }

    private void checkConnectedDevices() {
        // Periksa perangkat yang sudah terhubung
        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
        for (UsbDevice device : usbDevices.values()) {
            if (isArduino(device)) {
                arduinoDevice = device;
                Toast.makeText(this, "Arduino connected", Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }

    private boolean isArduino(UsbDevice device) {
        // Tentukan kriteria untuk mengenali perangkat Arduino
        // Misalnya, periksa apakah perangkat mendukung USB CDC (Communication Device Class)
        return device.getInterfaceCount() > 0 && device.getInterface(0).getInterfaceClass() == 0x02; // CDC class
    }

    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action != null) {
                if (action.equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                    // Perangkat USB terhubung
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (isArduino(device)) {
                        arduinoDevice = device;
                        Toast.makeText(context, "Arduino connected", Toast.LENGTH_SHORT).show();
                    }
                } else if (action.equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                    // Perangkat USB terputus
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (device.equals(arduinoDevice)) {
                        arduinoDevice = null;
                        Toast.makeText(context, "Arduino disconnected", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(usbReceiver);
    }
}
