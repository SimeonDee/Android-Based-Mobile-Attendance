package com.example.simeon_dee.mobileattendance;

import android.app.Application;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by SIMEON_DEE on 5/16/2018.
 */

public class DbImageBitmapConverterUtility {

    //convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) throws IOException{

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,50,stream);
            return stream.toByteArray();
    }

    //convert from byte array to bitmap
    public static Bitmap getBitmapImage(byte[] bytearray){
        //BitmapFactory.Options options = new BitmapFactory.Options();
        try{
            int i = bytearray.length;
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytearray,0,bytearray.length);
            return bitmap;
        } catch (Exception e){
            return null;
        }

    }

}
