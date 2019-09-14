package com.example.simeon_dee.mobileattendance;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapRegionDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.graphics.BitmapCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PassportUploadActivity extends AppCompatActivity {

    SQLiteDatabase mDatabase;
    public static final int REQUEST_CODE = 1;
    private ImageView imgPassport;
    private TextView tvUsername;
    Bitmap bitmapImage;
    byte[] imgBytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.passport_upload_layout);

        imgPassport = (ImageView) findViewById(R.id.imgPassport);
        tvUsername = (TextView) findViewById(R.id.username);
        tvUsername.setText(LoginActivity.currentUser.getID());

    }

    public void nextButtonClickHandler(View view) {
        try{
            if(imgPassport.getBackground() != null){

                //Bitmap bitmap = ((BitmapDrawable) imgPassport.getDrawable()).getBitmap();
                if(mDatabase == null || !mDatabase.isOpen()){
                    mDatabase = openOrCreateDatabase(LoginActivity.DBASE_NAME,MODE_PRIVATE,null);
                }

               //******NOTE*********
                //******SOMETHING WRONG WITH GETING BITMAP IMAGE FROM IMAGEVIEW***

                imgBytes = DbImageBitmapConverterUtility.getBytes(bitmapImage);
                //String updateSQl = "UPDATE userAccounts \n" +
                //        "SET passport = '" + imgBytes + "' " +
                //        "WHERE username = '" + LoginActivity.currentUser.getID() + "';";
                ContentValues cv = new ContentValues();
                cv.put("passport",imgBytes);
                mDatabase.update("userAccounts",cv,
                        "username = '" + LoginActivity.currentUser.getID() + "'",
                        null);

                displayDialog("Success Report","Passport Saved Successfully");
                Intent intent = new Intent(PassportUploadActivity.this,
                        StudentDetailsRegActivity.class);
                startActivity(intent);
            }

        } catch (Exception e){
            displayDialog("Something Happened","Err. Description: \n" + e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_passport_upload_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId;
        itemId = item.getItemId();
        switch (itemId){
            case R.id.itmNext:
                Intent intent = new Intent(PassportUploadActivity.this,
                        StudentDetailsRegActivity.class);
                startActivity(intent);
        }
        return true;
    }

    public void browsePassportClickHandler(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try{
            if(resultCode == RESULT_OK){
                if(requestCode == REQUEST_CODE && data != null){
                    Uri imgUri = data.getData();
                    bitmapImage = MediaStore.Images.Media.getBitmap(
                            this.getContentResolver(),imgUri);
                    imgPassport.setImageBitmap(bitmapImage);

                }
                else if(requestCode == REQUEST_CODE && data == null){
                    Toast.makeText(PassportUploadActivity.this,R.string.error_no_passport_selected,
                            Toast.LENGTH_LONG).show();
                }
            }

        } catch (Exception ex){
            displayDialog("Oops!...", "Description:\n" +ex.getMessage());
        }

    }

    private void displayDialog(String dialogTitle, String dialogMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(PassportUploadActivity.this);
        builder.setTitle(dialogTitle)
                .setMessage(dialogMessage)
                .setPositiveButton("Ok",null);
        Dialog dialog = builder.create();
        dialog.show();
    }

}
