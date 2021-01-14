package hd.wallpapers;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.smarteist.autoimageslider.SliderLayout;
import com.smarteist.autoimageslider.SliderView;
import com.squareup.picasso.Picasso;

import hd.wallpapers.categories.BeachActivity;
import hd.wallpapers.categories.LandActivity;
import hd.wallpapers.categories.MountainActivity;
import hd.wallpapers.categories.OceanActivity;
import hd.wallpapers.categories.SunriseActivity;
import hd.wallpapers.categories.TreeActivity;

public class DashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DrawerLayout drawerLayout;
    ActionBarDrawerToggle toggle;
    Toolbar toolbar;
    NavigationView navigationView;

    TextView beachTextView, treeTextView, oceanTextView, landTextView, sunriseTextView, mountainTextView;
    ImageView beachImageView, treeImageView, oceanImageView, landImageView, sunriseImageView, mountainImageView;
    Intent beachIntent, treeIntent, oceanIntent, landIntent, sunriseIntent, mountainIntent;
    String sunriseString, beachString, oceanString, treeString, mountainString, landString;

    SliderLayout sliderLayout;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("HD WALLPAPERS");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        firestore = FirebaseFirestore.getInstance();
        drawerLayout = findViewById(R.id.drawerLayout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        navigationView = findViewById(R.id.navigation_view);
        navigationView.bringToFront();
        navigationView.setCheckedItem(R.id.home);
        navigationView.setNavigationItemSelectedListener(this);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (!isConnected(this)) {
            showCustomDialog();
        }

        gettingImages();
        sliderLayout = findViewById(R.id.imageSlider);
        sliderLayout.setIndicatorAnimation(SliderLayout.Animations.FILL); //set indicator animation by using SliderLayout.Animations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderLayout.setScrollTimeInSec(5);
        setSliderViews();
    }

    private void setSliderViews() {
        for (int i = 0; i <= 5; i++) {
            SliderView sliderView = new SliderView(this);
            switch (i) {
                case 0:
                    sliderView.setImageUrl("https://cdn.pixabay.com/photo/2016/11/29/04/19/beach-1867285__340.jpg");
                    sliderView.setDescription("Sunrise");
                    sliderView.setDescriptionTextSize(36);
                    sliderView.setOnSliderClickListener(new SliderView.OnSliderClickListener() {
                        @Override
                        public void onSliderClick(SliderView sliderView) {
                            startActivity(sunriseIntent);
                        }
                    });
                    break;
                case 1:
                    sliderView.setImageUrl("https://cdn.pixabay.com/photo/2013/10/02/23/03/mountains-190055__340.jpg");
                    sliderView.setDescription("Mountain");
                    sliderView.setDescriptionTextSize(36);
                    sliderView.setOnSliderClickListener(new SliderView.OnSliderClickListener() {
                        @Override
                        public void onSliderClick(SliderView sliderView) {
                            startActivity(mountainIntent);
                        }
                    });
                    break;
                case 2:
                    sliderView.setImageUrl("https://cdn.pixabay.com/photo/2016/12/17/14/33/wave-1913559__340.jpg");
                    sliderView.setDescription("Ocean");
                    sliderView.setDescriptionTextSize(36);
                    sliderView.setOnSliderClickListener(new SliderView.OnSliderClickListener() {
                        @Override
                        public void onSliderClick(SliderView sliderView) {
                            startActivity(oceanIntent);
                        }
                    });
                    break;
                case 3:
                    sliderView.setImageUrl("https://cdn.pixabay.com/photo/2015/07/05/10/18/tree-832079__340.jpg");
                    sliderView.setDescription("Trees");
                    sliderView.setDescriptionTextSize(36);
                    sliderView.setOnSliderClickListener(new SliderView.OnSliderClickListener() {
                        @Override
                        public void onSliderClick(SliderView sliderView) {
                            startActivity(treeIntent);
                        }
                    });
                    break;

                case 4:
                    sliderView.setImageUrl("https://cdn.pixabay.com/photo/2014/11/16/15/15/field-533541__340.jpg");
                    sliderView.setDescription("Land");
                    sliderView.setDescriptionTextSize(36);
                    sliderView.setOnSliderClickListener(new SliderView.OnSliderClickListener() {
                        @Override
                        public void onSliderClick(SliderView sliderView) {
                            startActivity(landIntent);
                        }
                    });
                    break;

                case 5:
                    sliderView.setImageUrl("https://cdn.pixabay.com/photo/2017/03/31/15/34/sunset-2191645__340.jpg");
                    sliderView.setDescription("Beach");
                    sliderView.setDescriptionTextSize(36);
                    sliderView.setOnSliderClickListener(new SliderView.OnSliderClickListener() {
                        @Override
                        public void onSliderClick(SliderView sliderView) {
                            startActivity(beachIntent);
                        }
                    });
                    break;

            }
            sliderView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
            sliderLayout.addSliderView(sliderView);
        }
    }

    private void gettingImages() {
        beachTextView = findViewById(R.id.beach_text);
        oceanTextView = findViewById(R.id.ocean_text);
        treeTextView = findViewById(R.id.tree_text);
        mountainTextView = findViewById(R.id.mountian_text);
        sunriseTextView = findViewById(R.id.sunrise_text);
        landTextView = findViewById(R.id.land_text);

        beachTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(beachIntent);
            }
        });
        oceanTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(oceanIntent);
            }
        });
        treeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(treeIntent);
            }
        });
        mountainTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(mountainIntent);
            }
        });
        sunriseTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(sunriseIntent);
            }
        });
        landTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(landIntent);
            }
        });

        beachImageView = findViewById(R.id.beach_image);
        oceanImageView = findViewById(R.id.ocean_image);
        treeImageView = findViewById(R.id.tree_image);
        mountainImageView = findViewById(R.id.mountain_image);
        sunriseImageView = findViewById(R.id.sunrise_image);
        landImageView = findViewById(R.id.land_image);

        firestore.collection("DashboardActivity").document("Categories").get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot snapshot) {
                        sunriseString = snapshot.getString("sunrise");
                        beachString = snapshot.getString("beach");
                        oceanString = snapshot.getString("ocean");
                        treeString = snapshot.getString("tree");
                        mountainString = snapshot.getString("mountain");
                        landString = snapshot.getString("land");

                        Picasso.get().load(sunriseString).into(sunriseImageView);
                        Picasso.get().load(beachString).into(beachImageView);
                        Picasso.get().load(oceanString).into(oceanImageView);
                        Picasso.get().load(treeString).into(treeImageView);
                        Picasso.get().load(mountainString).into(mountainImageView);
                        Picasso.get().load(landString).into(landImageView);
                    }
                });

        beachIntent = new Intent(DashboardActivity.this, BeachActivity.class);
        oceanIntent = new Intent(DashboardActivity.this, OceanActivity.class);
        treeIntent = new Intent(DashboardActivity.this, TreeActivity.class);
        landIntent = new Intent(DashboardActivity.this, LandActivity.class);
        mountainIntent = new Intent(DashboardActivity.this, MountainActivity.class);
        sunriseIntent = new Intent(DashboardActivity.this, SunriseActivity.class);

    }

    private void showCustomDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Check Internet Connection...")
                .setMessage("It seems your Internet is On Turn it On...")
                .setCancelable(false)
                .setPositiveButton("TURN ON", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    }
                })
                .setNegativeButton("EXIT", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishAffinity();
                    }
                }).show();
    }

    private boolean isConnected(DashboardActivity dashboardActivity) {
        ConnectivityManager connectivityManager = (ConnectivityManager) dashboardActivity.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        return (wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void onShare(MenuItem item) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        String shareLink = "Get HD Wallpapers for Free from this HD WALLPAPERS APP" +
                "\n" +
                "https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName();
        intent.putExtra(Intent.EXTRA_TEXT, shareLink);
        startActivity(Intent.createChooser(intent, "Share Using"));
    }

    public void onRate(MenuItem item) {
        Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Exit Confirmation")
                    .setMessage("Are you sure you want to EXIT???")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finishAffinity();
                        }
                    })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setNeutralButton("RATE US!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName());
                            Intent rateIntent = new Intent(Intent.ACTION_VIEW, uri);
                            startActivity(rateIntent);
                        }
                    }).show();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                drawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.feedback:
                Intent feedbackIntent = new Intent(Intent.ACTION_SENDTO);
                feedbackIntent.setData(Uri.parse("mailto:"));
                String[] email = {"youremail@gmail.com"};
                String subject = "Feedback for your App";
                String text = "Give your feedback here:";
                feedbackIntent.putExtra(Intent.EXTRA_EMAIL, email);
                feedbackIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
                feedbackIntent.putExtra(Intent.EXTRA_TEXT, text);
                if (feedbackIntent.resolveActivity(getPackageManager()) != null) {
                    startActivity(feedbackIntent);
                }
                break;

            case R.id.share:
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String shareLink = "Your Message \n" +
                        "https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName();
                intent.putExtra(Intent.EXTRA_TEXT, shareLink);
                startActivity(Intent.createChooser(intent, "Share using"));
                break;
            case R.id.rate:
                Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName());
                Intent rateIntent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(rateIntent);
                break;
            case R.id.disclaimer:
                drawerLayout.closeDrawer(GravityCompat.START);
                MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
                builder.setTitle("Disclaimer")
                        .setMessage("Disclaimer")
                        .setPositiveButton("Share App", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                intent.setType("text/plain");
                                String shareLink = "your Message \n" +
                                        "https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName();
                                intent.putExtra(Intent.EXTRA_TEXT, shareLink);
                                startActivity(Intent.createChooser(intent, "Share using"));
                            }
                        })
                        .setNegativeButton("Rate App", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=" + getApplicationContext().getPackageName());
                                Intent rateIntent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(rateIntent);
                            }
                        }).setNeutralButton("Okay...", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}