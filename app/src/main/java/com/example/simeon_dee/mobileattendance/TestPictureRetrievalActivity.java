package com.example.simeon_dee.mobileattendance;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class TestPictureRetrievalActivity extends AppCompatActivity {

    public static final int REQUEST_CODE = 100;
    byte[] imgByte;
    Button btBrowseImg,btConvToByte, btConvToImg;
    ImageView imgSrc,imgDest;
    TextView tvImgBytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_picture_retrieval);
        btBrowseImg = (Button) findViewById(R.id.btBrowseImage);
        btConvToByte = (Button)findViewById(R.id.btConvertToByteArray);
        btConvToImg = (Button) findViewById(R.id.btConvertToByteArray);

        imgSrc = (ImageView) findViewById(R.id.imgSrc);
        imgDest = (ImageView) findViewById(R.id.imgDestination);
        tvImgBytes = (TextView) findViewById(R.id.tvByteArray);

    }


    public void btConvertToByteArray(View view) {
        tvImgBytes.setText(imgByte.toString());
    }

    public void btBrowseImage(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try{
            if(resultCode == RESULT_OK){
                if(requestCode == REQUEST_CODE && data !=null){
                    //imgSrc.setImageURI(data.getData());


                    Uri imgUri = data.getData();
                    //imgSrc.setImageURI(imgUri);
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(
                            this.getContentResolver(),imgUri);

                    imgSrc.setImageBitmap(bitmap);
                    //ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    //bitmap.compress(Bitmap.CompressFormat.JPEG,50,stream);
                    imgByte = DbImageBitmapConverterUtility.getBytes(bitmap);


                }
            }

        } catch (Exception ex){
            displayDialog("Oops!...", "Description:\n" +ex.getMessage());
        }

    }

    private void displayDialog(String dialogTitle, String dialogMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                TestPictureRetrievalActivity.this);
        builder.setTitle(dialogTitle)
                .setMessage(dialogMessage)
                .setPositiveButton("Ok",null);
        Dialog dialog = builder.create();
        dialog.show();
    }

    public void btConvertByteToImage(View view) {
        imgDest.setImageBitmap(DbImageBitmapConverterUtility.getBitmapImage(imgByte));
    }
}
