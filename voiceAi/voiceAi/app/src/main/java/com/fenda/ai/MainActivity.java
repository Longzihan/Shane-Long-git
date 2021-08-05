package com.fenda.ai;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Process;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.fenda.ai.authz.DDSService;
import com.fenda.ai.authz.ObService;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.SYSTEM_ALERT_WINDOW;

public class MainActivity extends Activity {

    public static final int PERMISSION_REQ = 0x123456;

    private String[] mPermission = new String[] {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.RECORD_AUDIO,
            SYSTEM_ALERT_WINDOW


    };

    private List<String> mRequestPermission = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){


            for (String one : mPermission) {
                if (PackageManager.PERMISSION_GRANTED != this.checkPermission(one, Process.myPid(), Process.myUid())) {
                    mRequestPermission.add(one);
                }
            }
            if (!mRequestPermission.isEmpty()) {
                this.requestPermissions(mRequestPermission.toArray(new String[mRequestPermission.size()]), PERMISSION_REQ);
            }else {
                startService(new Intent(this, DDSService.class));
                startService(new Intent(this, ObService.class));
            }
        }else {
            startService(new Intent(this, DDSService.class));
            startService(new Intent(this, ObService.class));
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
//        this.finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
            return;
        }
        if (requestCode == PERMISSION_REQ) {
            for (int i = 0; i < grantResults.length; i++) {
                for (String one : mPermission) {
                    if (permissions[i].equals(one) && grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        mRequestPermission.remove(one);
                    }
                }
            }
            startService(new Intent(this, DDSService.class));
            startService(new Intent(this, ObService.class));

        }
    }
}
