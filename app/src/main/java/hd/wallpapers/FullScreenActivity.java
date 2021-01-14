package hd.wallpapers;

import android.Manifest;
import android.app.DownloadManager;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.chrisbanes.photoview.PhotoView;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FullScreenActivity extends AppCompatActivity {

    String originalUrl;
    PhotoView photoView;
    Intent intent;
    Intent shareIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen);

        intent = getIntent();
        originalUrl = intent.getStringExtra("originalUrl");
        photoView = findViewById(R.id.photoView);
        Picasso.get().load(originalUrl).into(photoView);

        Button setWallpaper = findViewById(R.id.set_wallpaper);
        setWallpaper.setOnClickListener(v -> {
            setWallpaper();
        });

        Button downloadWallpaperbtn = findViewById(R.id.download_wallpaper);
        downloadWallpaperbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                downloadWallpaper(v);
            }
        });

        Button shareWallpaperbtn = findViewById(R.id.share_wallpaper);
        shareWallpaperbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareWallpaperEvent(v);
            }
        });
    }

    public void ShareWallpaperEvent(View view) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        File file = new File(getExternalCacheDir() + "/" + "LoveQuoteImage.png");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            image(photoView).compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);

            fileOutputStream.flush();
            fileOutputStream.close();

            shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/*");
            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Get More HD Wallpapers on " + "https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName());
            shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        } catch (IOException e) {
            e.printStackTrace();
        }

        startActivity(Intent.createChooser(shareIntent, "Send Using"));
    }

    private Bitmap image(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        ImageView imageView = new ImageView(this);
        imageView.setImageBitmap(bitmap);
        return bitmap;
    }

    public void downloadWallpaper(View view) {
        Dexter.withContext(this)
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                        Uri uri = Uri.parse(originalUrl);
                        DownloadManager.Request request = new DownloadManager.Request(uri);
                        String subPath = "/HD WALLPAPERS/" + System.currentTimeMillis() + ".jpg";
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, subPath);
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        downloadManager.enqueue(request);
                        Toast.makeText(FullScreenActivity.this, "Downloading Start", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startActivity(new Intent(Settings.EXTRA_APP_PACKAGE));
                        } else {
                            Toast.makeText(FullScreenActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                })
                .check();

    }

    public void setWallpaper() {
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        Bitmap bitmap = ((BitmapDrawable) photoView.getDrawable()).getBitmap();
        try {
            wallpaperManager.setBitmap(bitmap);
            Toast.makeText(this, "Wallpaper Set Successfully", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}