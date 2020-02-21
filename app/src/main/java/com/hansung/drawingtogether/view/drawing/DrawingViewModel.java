package com.hansung.drawingtogether.view.drawing;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.hansung.drawingtogether.R;
import com.hansung.drawingtogether.view.BaseViewModel;
import com.hansung.drawingtogether.view.SingleLiveEvent;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DrawingViewModel extends BaseViewModel {
    public final SingleLiveEvent<DrawingCommand> drawingCommands = new SingleLiveEvent<>();

    private final int PICK_FROM_GALLERY = 0;
    private final int PICK_FROM_CAMERA = 1;
    private String photoPath;

    public void clickPen(View view) {
        drawingCommands.postValue(new DrawingCommand.PenMode(view));
    }

    public void clickEraser(View view) {
        drawingCommands.postValue(new DrawingCommand.EraserMode(view));
    }

    public void clickText(View view) {
        drawingCommands.postValue(new DrawingCommand.TextMode(view));
    }

    public void clickShape(View view) {
        drawingCommands.postValue(new DrawingCommand.ShapeMode(view));
    }

    public void clickSearch(View view) {
        navigate(R.id.action_drawingFragment_to_searchFragment);
    }

    public void clickExit(View view) {
        back();
    }

    public void getImageFromGallery(Fragment fragment) {
        checkPermission(fragment.getContext());

        Intent galleryIntent = new Intent(Intent.ACTION_PICK);
        galleryIntent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        fragment.startActivityForResult(galleryIntent, PICK_FROM_GALLERY);
    }

    public void getImageFromCamera(Fragment fragment) {
        checkPermission(fragment.getContext());

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(fragment.getContext().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile(fragment);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (photoFile != null) {
                Uri uri = FileProvider.getUriForFile(fragment.getContext(), "com.hansung.drawingtogether.fileprovider", photoFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                fragment.startActivityForResult(cameraIntent, PICK_FROM_CAMERA);
            }
        }
    }

    public File createImageFile(Fragment fragment) throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = fragment.getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File storageDir = new File(Environment.getExternalStorageDirectory() + "/Pictures", "drawingtogether");
        if (!storageDir.exists()) storageDir.mkdirs();
        File  image = File.createTempFile(imageFileName, ".jpg", storageDir);
        photoPath = image.getAbsolutePath();
        Log.e("kkankkan", photoPath);
        return image;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    private void checkPermission(Context context) {
        PermissionListener permissionListener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                //
            }
            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                //
            }
        };

        TedPermission.with(context)
                .setPermissionListener(permissionListener)
                .setDeniedMessage(context.getResources().getString(R.string.permission_camera))
                .setPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)
                .check();
    }
}
