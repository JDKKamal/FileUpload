package com.jdkgroup.fileupload;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import lolodev.permissionswrapper.callback.OnRequestPermissionsCallBack;
import lolodev.permissionswrapper.wrapper.PermissionWrapper;

public class FileUploadActivity extends AppCompatActivity implements View.OnClickListener {

    private AppCompatActivity appCompatActivity;
    private CircleImageView circleivProfileImage;

    private Intent intent;
    private String mediaPath;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_upload);

        appCompatActivity = this;

        circleivProfileImage = (CircleImageView) findViewById(R.id.circleivProfileImage);

        circleivProfileImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.circleivProfileImage:

                new PermissionWrapper.Builder(this)
                        .addPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE})
                        //enable rationale message with a custom message
                        .addPermissionRationale("Permission check!")
                        //show settings dialog,in this case with default message base on requested permission/s
                        .addPermissionsGoSettings(true)
                        //enable callback to know what option was choosed
                        .addRequestPermissionsCallBack(new OnRequestPermissionsCallBack() {
                            @Override
                            public void onGrant() {
                                dialogTakeImageCameraGallery();
                            }

                            @Override
                            public void onDenied(String permission) {

                            }
                        }).build().request();
                break;
        }
    }

    private void galleryIntent() {
        intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, 0);
    }

    private void cameraIntent() {
        intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 1);
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            mediaPath = destination.getAbsolutePath();

            System.out.println("Tag" + ":: Camera Path " + destination.getAbsolutePath());
            circleivProfileImage.setImageBitmap(null);
            circleivProfileImage.setImageBitmap(BitmapFactory.decodeFile(mediaPath));

            fo.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void dialogTakeImageCameraGallery() {
        final CharSequence[] items = {"Take Photo", "Choose from Gallery", "Cancel"};
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(appCompatActivity);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {

                if (items[item].equals("Take Photo")) {
                    cameraIntent();

                } else if (items[item].equals("Choose from Gallery")) {
                    galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 0 && resultCode == RESULT_OK && null != data) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                assert cursor != null;
                cursor.moveToFirst();

                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                mediaPath = cursor.getString(columnIndex);
                circleivProfileImage.setImageBitmap(null);
                System.out.println("Tag" + ":: Gallery Path " + mediaPath);
                circleivProfileImage.setImageBitmap(BitmapFactory.decodeFile(mediaPath));
                cursor.close();

            } else if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
                onCaptureImageResult(data);
            }
        } catch (Exception e) {

        }

    }
}
