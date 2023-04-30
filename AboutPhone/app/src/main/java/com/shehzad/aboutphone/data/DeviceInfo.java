package com.shehzad.aboutphone.data;

import static android.content.Context.ACTIVITY_SERVICE;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ConfigurationInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.fingerprint.FingerprintManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;

public class DeviceInfo {

    String cameraId;
    CameraManager cameraManager;
    CameraCharacteristics cameraCharacteristics;

    private final Context context;
    private final TelephonyManager telephonyManager;
    private final WindowManager windowManager;
    private final ActivityManager activityManager;
    private final ConnectivityManager connectivityManager;


    public DeviceInfo(Context context) {
        this.context = context;
        telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

    }

    public String getDeviceName() {
        return Build.MODEL;
    }

    public String getManufacturer() {
        return Build.MANUFACTURER;
    }

    public String getModelNumber() {
        return Build.DEVICE;
    }

    public String getOS() {
        return "Android";
    }

    public String getOSVersion() {
        return Build.VERSION.RELEASE;
    }

    public String getKernelVersion() {
        return System.getProperty("os.version");
    }

    public String getSDKVersion() {
        return String.valueOf(Build.VERSION.SDK_INT);
    }

    public String getBuildNumber() {
        return Build.DISPLAY;
    }

    public String getCPUInfo() {
        String arch = System.getProperty("os.arch");
        StringBuilder sb = new StringBuilder();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            String[] abis = Build.SUPPORTED_ABIS;
            sb.append("ABI: ").append(abis[0]).append("\n");
            sb.append("Architecture: ").append(arch).append("\n");

            try {
                Process process = Runtime.getRuntime().exec("cat /proc/cpuinfo");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    if (line.contains("processor") || line.contains("hardware") || line.contains("Revision")) {
                        sb.append(line).append("\n");
                    }
                }
                bufferedReader.close();
                try {
                    process.waitFor();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public String getGpuInfo() {
        String gpu = null;
        if (activityManager != null) {
            ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
            gpu = configurationInfo.getGlEsVersion();
        }
        return gpu;
    }

    public String getRAMInfo() {
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        long totalRam = memoryInfo.totalMem;
        return Formatter.formatFileSize(context, totalRam);
    }

    public String getInternalStorage() {
        long totalSize = 0L;
        File path = Environment.getDataDirectory();
        StatFs statFs = new StatFs(path.getPath());
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            long blockSize = statFs.getBlockSizeLong();
            long totalBlocks = statFs.getBlockCountLong();
            totalSize = blockSize * totalBlocks;
        }
        return Formatter.formatFileSize(context, totalSize);
    }

    public String getExternalStorage() {
        long totalSize = 0L;
        String externalStorageState = Environment.getExternalStorageState();
        if (externalStorageState.equals(Environment.MEDIA_MOUNTED)) {
            File path = Environment.getExternalStorageDirectory();
            StatFs statFs = new StatFs(path.getPath());
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                long blockSize = statFs.getBlockSizeLong();
                long totalBlocks = statFs.getBlockCountLong();
                totalSize = blockSize * totalBlocks;
            }
            return Formatter.formatFileSize(context, totalSize);
        } else return "Unavailable";
    }

    public String getBatteryLevel() {
        Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int battery = level * 100 / scale;
        return battery + "%";
    }

    public String getBatteryHealth() {
        Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int health = batteryIntent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
        switch (health) {
            case BatteryManager.BATTERY_HEALTH_GOOD:
                return "Good";
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                return "OverHeat";
            case BatteryManager.BATTERY_HEALTH_DEAD:
                return "Dead";
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                return "Over Voltage";
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                return "Unspecified Failure";
            case BatteryManager.BATTERY_HEALTH_COLD:
                return "Cold";
            default:
                return "Unknown";
        }
    }


    public String getChargingStatus() {
        Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        switch (status) {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                return "Charging";
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                return "Discharging";
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                return "Not Charging";
            case BatteryManager.BATTERY_STATUS_FULL:
                return "Full";
            default:
                return "Unknown";
        }
    }

    public String getBatteryTemperature() {
        Intent batteryIntent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int temp = batteryIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
        return temp/10 + "℃";
    }

    public String getNetworkInfo() {
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo == null) return "Disconnected";

        String typeName = networkInfo.getTypeName();
        if (typeName.equalsIgnoreCase("WIFI")) {
            WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            return typeName + " " + wifiInfo.getSSID();
        } else if (typeName.equalsIgnoreCase("MOBILE")) {
            return typeName + " " + telephonyManager.getNetworkOperatorName();
        } else return typeName;
    }

                                                            //display info
    public String getOrientation() {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT ? "Portrait Mode" : "LandScape Mode";

    }

    public String getResolution() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels + " x " + displayMetrics.widthPixels;
    }

    public String getTouchScreenType() {
        String type = "";
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH_DISTINCT))
            type += "Multi-touch ";
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN_MULTITOUCH_JAZZHAND))
            type += "JazzHand ";
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN))
            type += "Single-touch ";

        return type;
    }


    public String getCameraDetails() throws CameraAccessException {
        String sensor = null, aperture = null, focalLength = null;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);

            for (String id : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(id);
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_BACK) {
                    cameraId = id;
                    cameraCharacteristics = characteristics;
                    break;
                }
            }

            //camera sensor size
            Rect sensorSize = cameraCharacteristics.get(CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE);
            double sensorWidth = sensorSize.width();
            double sensorHeight = sensorSize.height();

            //camera aperture
            float apertureSize = cameraCharacteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_APERTURES)[0];

            //focal length
            float[] focalLengthArray = cameraCharacteristics.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS);
            float focalL = focalLengthArray[0];

            //display camera characteristic
            sensor = String.format(Locale.getDefault(), "Sensor Size: %.0f x %.0f", sensorWidth, sensorHeight);
            aperture = String.format(Locale.getDefault(), "Aperture: f/%.1f", apertureSize);
            focalLength = String.format(Locale.getDefault(), "Focal Length: %.0f mm", focalL);
        }
        return sensor + "\n" + aperture + "\n" + focalLength;
        
        
        
        
        
        
    }

    public String getSensors(int id) {
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> sensorList = sensorManager.getSensorList(Sensor.TYPE_ALL);
        for (Sensor sensor : sensorList) {
            if (sensor.getType() == Sensor.TYPE_GYROSCOPE && id == 1) return sensor.getName();
            else if (sensor.getType() == Sensor.TYPE_ACCELEROMETER && id == 2) return sensor.getName();
            else if (sensor.getType() == Sensor.TYPE_ROTATION_VECTOR && id == 3) return sensor.getName();
            else if (sensor.getType() == Sensor.TYPE_PROXIMITY && id == 4) return sensor.getName();
            else if (sensor.getType() == Sensor.TYPE_LIGHT && id == 5) return sensor.getName();
        }
        return null;
    }

    public String checkFaceRecognition() {
        PackageManager packageManager = context.getPackageManager();
        boolean isFaceRecognitionSupported = packageManager.hasSystemFeature(PackageManager.FEATURE_FACE);
        if (isFaceRecognitionSupported) return "Supported";
        else return "Not Supported";
    }

    public String checkFingerPrintLock() {
        FingerprintManager fingerprintManager;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            fingerprintManager = (FingerprintManager) context.getSystemService(Context.FINGERPRINT_SERVICE);
            if (fingerprintManager != null && fingerprintManager.isHardwareDetected())
                return "Supported";
        }
        return "Not Supported";
    }


}
