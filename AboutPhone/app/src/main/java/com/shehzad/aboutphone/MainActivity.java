package com.shehzad.aboutphone;

import android.hardware.camera2.CameraAccessException;
import android.os.Build;
import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.shehzad.aboutphone.adapter.InfoAdapter;
import com.shehzad.aboutphone.data.DeviceInfo;
import com.shehzad.aboutphone.model.InfoModel;

import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MainActivity extends AppCompatActivity {


    ArrayList<InfoModel> infoModels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DeviceInfo deviceInfo = new DeviceInfo(this);
        infoModels = new ArrayList<>();

        infoModels.add(new InfoModel("Device Name", deviceInfo.getDeviceName()));
        infoModels.add(new InfoModel("Manufacturer", deviceInfo.getManufacturer()));
        infoModels.add(new InfoModel("Model", deviceInfo.getModelNumber()));
        infoModels.add(new InfoModel("OS", deviceInfo.getOS()));
        infoModels.add(new InfoModel("OS Version", deviceInfo.getOSVersion()));
        infoModels.add(new InfoModel("Kernel Version", deviceInfo.getKernelVersion()));
        infoModels.add(new InfoModel("SDK Version", deviceInfo.getSDKVersion()));
        infoModels.add(new InfoModel("Build Number", deviceInfo.getBuildNumber()));
        infoModels.add(new InfoModel("CPU Info", deviceInfo.getCPUInfo()));
        infoModels.add(new InfoModel("GPU Info", deviceInfo.getGpuInfo()));
        infoModels.add(new InfoModel("RAM", deviceInfo.getRAMInfo()));
        infoModels.add(new InfoModel("Internal Storage", deviceInfo.getInternalStorage()));
        infoModels.add(new InfoModel("External Storage", deviceInfo.getExternalStorage()));
        infoModels.add(new InfoModel("Battery Level", deviceInfo.getBatteryLevel()));
        infoModels.add(new InfoModel("Battery Status", deviceInfo.getChargingStatus()));
        infoModels.add(new InfoModel("Battery Temperature", deviceInfo.getBatteryTemperature()));
        infoModels.add(new InfoModel("Battery Health", deviceInfo.getBatteryHealth()));
        infoModels.add(new InfoModel("Network Info", deviceInfo.getNetworkInfo()));
        infoModels.add(new InfoModel("Orientation", deviceInfo.getOrientation()));
        infoModels.add(new InfoModel("Resolution", deviceInfo.getResolution()));
        infoModels.add(new InfoModel("Screen Type", deviceInfo.getTouchScreenType()));
        infoModels.add(new InfoModel("Face Recognition", deviceInfo.checkFaceRecognition()));
        infoModels.add(new InfoModel("FingerPrint Lock", deviceInfo.checkFingerPrintLock()));
        try {
            infoModels.add(new InfoModel("Camera info", deviceInfo.getCameraDetails()));
        } catch (CameraAccessException e) {
            infoModels.add(new InfoModel("Error", e.getMessage()));
        }

        infoModels.add(new InfoModel("Gyroscope", deviceInfo.getSensors(1)));
        infoModels.add(new InfoModel("Accelerometer", deviceInfo.getSensors(2)));
        infoModels.add(new InfoModel("Rotation Vector", deviceInfo.getSensors(3)));
        infoModels.add(new InfoModel("Proximity", deviceInfo.getSensors(4)));
        infoModels.add(new InfoModel("Ambient light", deviceInfo.getSensors(5)));

        InfoAdapter adapter = new InfoAdapter(this, infoModels);
        ListView listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);
    }

}

