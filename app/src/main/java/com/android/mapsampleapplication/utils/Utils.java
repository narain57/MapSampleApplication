package com.android.mapsampleapplication.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.android.mapsampleapplication.R;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Utils {

    public static AlertDialog mDialog;

    public static final int LOCATION_PERMISSION_CODE = 1;

    public static List<Address> getPlaceDetails(Context context, Double lat, Double longt){

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(lat, longt, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return addresses;
    }

    public static void showAlertDialogWithOkAndCancelButton(Context context, String title,String message, String positiveText, String negativeText, DialogInterface.OnClickListener okButtonOnClickListener, DialogInterface.OnClickListener cancelButtonOnClickListener, boolean dismiss) {

        try {
            if (mDialog != null && mDialog.isShowing())
                mDialog.dismiss();

            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(context,
                    android.R.style.Theme_DeviceDefault_Dialog_Alert);

            alertDialog.setMessage(message);
            alertDialog.setTitle(title);
            alertDialog.setCancelable(dismiss);

            if (okButtonOnClickListener == null) {
                okButtonOnClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                    }
                };
            }
            if (cancelButtonOnClickListener == null) {
                cancelButtonOnClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                    }
                };
            }
            alertDialog.setPositiveButton(positiveText, okButtonOnClickListener);
            alertDialog.setNegativeButton(negativeText, cancelButtonOnClickListener);
            mDialog = alertDialog.show();
            mDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
            mDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        } catch (Exception e) {
        }
    }

    public static void makeFragmentTransaction(Activity context,int id, Fragment fragment){
        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(id, fragment,"map");
        fragmentTransaction.commit();
    }
    public static boolean isLocationAllowed(Activity activity) {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;
        //If permission is not granted returning false
        return false;
    }

    //Requesting permission
    public static void requestStoragePermission(Activity activity, Fragment fragment) {

        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_COARSE_LOCATION)) {
        }
        fragment.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_CODE);
    }
}
