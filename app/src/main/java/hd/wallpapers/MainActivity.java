package hd.wallpapers;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final long SPLASH_TIMER = 3000;
    Animation sideAnim, bottomAnim;
    TextView wallpaperLogo, wallpaperTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        wallpaperLogo = findViewById(R.id.wallpaper_logo);
        wallpaperTitle = findViewById(R.id.wallpaper_title);

        sideAnim = AnimationUtils.loadAnimation(this, R.anim.side_animation);
        bottomAnim = AnimationUtils.loadAnimation(this, R.anim.bottom_animation);

        wallpaperLogo.setAnimation(sideAnim);
        wallpaperTitle.setAnimation(bottomAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_TIMER);

    }
}