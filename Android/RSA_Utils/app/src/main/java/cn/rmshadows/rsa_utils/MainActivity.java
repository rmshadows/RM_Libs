package cn.rmshadows.rsa_utils;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import RSA_Utils.Main;
import RSA_Utils.RSA_PKCS8_Utils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("Hello world");
        final String TAG = "==>>";
        Log.d(TAG, "onCreate: Hello");
//        Main.loadPKCS1_key();





        setContentView(R.layout.activity_main);
    }
}