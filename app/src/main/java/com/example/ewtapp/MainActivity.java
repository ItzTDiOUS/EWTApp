package com.example.ewtapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.example.ewtapp.R;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.view.menu.MenuView;
import androidx.core.app.NotificationCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;



import java.util.List;


public class MainActivity extends AppCompatActivity {
    private boolean doubleBackToExitPressedOnce = false;
    private LineChart chartElectricity;
    private LineChart chartWater;

    public long electricityLimit;
    public long waterLimit;
    private List<Entry> electricityEntries;
    private List<Entry> waterEntries;
    private long lastEntryTimestamp;
    private TextView ee, ww, user,displayUsername,ve,fw;
    private PopupWindow overlayPopup;
    private long startTime;

    private String Username;

    private String Electricity_data, Water_data, Electricity_voltage,Water_flow;

    private static final String FIREBASE_ADMIN_NODE = "Admin";
    private static final String FIREBASE_USERNAME_FIELD = "username";
    private static final String FIREBASE_ELECTRICITY_FIELD = "electricity";
    private static final String FIREBASE_WATER_FIELD = "water";
    private long lastEntryTimestamp2;
    private static final String FIREBASE_ELECTRICITY_CHILD_NODE_VOLTAGE="voltage";
    private static final String FIREBASE_WATER_CHILD_NODE_FLOW="flow_rate_lps";


    private static final String FIREBASE_ELECTRICITY_CHILD_NODE = "total_energy";
    private static final String FIREBASE_WATER_CHILD_NODE = "total_water_used";

    private static final String CHANNEL_ID = "My Channel";
    private static final int NOTIFICATION_ID = 100;

    private TextInputLayout ElecLimitText,WatLimText;

    public static int home_i;

    int c;

    public MainActivity() {
    }
    private ElectricityFragment electricityFragment;
    private WaterFragment waterFragment;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //removing app name from the top
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();

        setContentView(R.layout.home);

        electricityFragment = new ElectricityFragment();
        waterFragment = new WaterFragment();





//



        displayUsername=findViewById(R.id.naam);

        Username = getLoginStatus();

        getLimitsFromSharedPreferences();


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.home_icon) {
                    openMainActivity();
                    // Do nothing or add logic for refreshing your activity
                } else if (itemId == R.id.electricity_icon) {
                    replaceFragment(new ElectricityFragment());
                } else if (itemId == R.id.blur_icon) {
                    replaceFragment(new WaterFragment());
                } else if (itemId == R.id.settings_icon) {
                    showOverlayLayout(item);
                }
                return true;
            }
        });






        chartElectricity = findViewById(R.id.electricity_chart);
        chartWater = findViewById(R.id.water_chart);

        // Customize chart settings if needed
        customizeChart(chartElectricity);
        customizeChart(chartWater);

        // Customize chart settings if needed

        TextView videosTextView = findViewById(R.id.logout_button);
        videosTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openVideosActivity();
            }
        });

        // Fetch data from Firebase and update charts



        View LL = LayoutInflater.from(this).inflate(R.layout.limit_layout, null);




        TextView analyzeElec=findViewById(R.id.kw_per_year_ek4);
        TextView analyzeWat=findViewById(R.id.kw_per_year_ek7);


        analyzeElec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenLimPage();
            }
        });

        analyzeWat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenLimPage();
            }
        });



        // Notification
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.alert, null);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        Bitmap largeIcon = (bitmapDrawable != null) ? bitmapDrawable.getBitmap() : null;

        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setLargeIcon(largeIcon)
                .setSmallIcon(R.drawable.ewtlogo)
                .setContentText("You Have Exceeded Your Electricity/Water Usage")
                .setSubText("ALERT!");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID);
        }

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(NOTIFICATION_ID, builder.build());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);



        ee = findViewById(R.id.electricity_data);
        ww = findViewById(R.id.water_data);
        ve=findViewById(R.id.voltage);
        fw=findViewById(R.id.flow);
        user=findViewById(R.id.naam);



        fetchDataFromFirebase(); // Initial data fetch

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }
    private void openMainActivity() {
        // Create an intent to open the MainActivity
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        startActivity(intent);
    }



    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.frame_layout, fragment); // Assuming R.id.frame_layout is your fragment container
        fragmentTransaction.commit();
    }



    private void openVideosActivity() {
            Intent intent = new Intent(getApplicationContext(), VideoPlayer.class);
            startActivity(intent);
        }



    private void OpenLimPage() {
        Intent intent=new Intent(getApplicationContext(), LimPage.class);
        startActivity(intent);
        finish();
    }





    private void createNotificationChannel() {
        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "EWT App Notification Channel", NotificationManager.IMPORTANCE_HIGH);
            nm.createNotificationChannel(channel);
        }
    }

    private void fetchDataFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(FIREBASE_ADMIN_NODE);
        Query checkUsername = databaseReference.orderByChild(FIREBASE_USERNAME_FIELD).equalTo(Username);

        checkUsername.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();

                    // Convert Long values to String
                    Long electricityLong = userSnapshot.child(FIREBASE_ELECTRICITY_FIELD).child(FIREBASE_ELECTRICITY_CHILD_NODE).getValue(Long.class);
                    Long waterLong = userSnapshot.child(FIREBASE_WATER_FIELD).child(FIREBASE_WATER_CHILD_NODE).getValue(Long.class);
                    Long electricityvoltageLong = userSnapshot.child(FIREBASE_ELECTRICITY_FIELD).child(FIREBASE_ELECTRICITY_CHILD_NODE_VOLTAGE).getValue(Long.class);
                    Long waterLong_flow = userSnapshot.child(FIREBASE_WATER_FIELD).child(FIREBASE_WATER_CHILD_NODE_FLOW).getValue(Long.class);
                    // Convert Long to String
                    Electricity_data = (electricityLong != null) ? String.valueOf(electricityLong) : "";
                    Water_data = (waterLong != null) ? String.valueOf(waterLong) : "";
                    Electricity_voltage= (electricityvoltageLong != null) ? String.valueOf(electricityvoltageLong) : "";
                    Water_flow = (waterLong_flow != null) ? String.valueOf(waterLong_flow) : "";
                    // Check if the limits are exceeded
                    checkLimitsAndShowOverlay(electricityLong, waterLong);

                    // Update the UI
                    updateUI();

                     //Update the charts
                    updateCharts(electricityLong != null ? electricityLong : 0,
                            waterLong != null ? waterLong : 0);




                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled if needed
                // Handle onCancelled if needed
            }
        });
    }
    private String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // You can customize the date format
        return sdf.format(new Date());
    }





    private void updateCharts(long electricityValue, long waterValue) {
        // Check if 24 hours have passed since the last entry
        if (System.currentTimeMillis() - lastEntryTimestamp >= 24 * 60 * 60 * 1000) {
            // Clear all data points if 24 hours have passed
            clearDataSets(chartElectricity);
            clearDataSets(chartWater);

            // Update the last entry timestamp
            lastEntryTimestamp = System.currentTimeMillis();
        }

        // Update the LineCharts with new data points
        updateChart(chartElectricity, electricityValue, "Electricity");
        updateChart(chartWater, waterValue, "Water");

        // Notify the chart that the data has changed
        chartElectricity.notifyDataSetChanged();
        chartElectricity.invalidate();

        chartWater.notifyDataSetChanged();
        chartWater.invalidate();
    }

    private void clearDataSets(LineChart chart) {
        LineData data = chart.getData();
        if (data != null) {
            data.clearValues();
        }
    }

    private void updateChart(LineChart chart, long value, String label) {
        // Update the LineChart with a new data point

        LineData data = chart.getData();

        if (data == null) {
            data = new LineData();
            chart.setData(data);
        }

        ILineDataSet set = data.getDataSetByIndex(0);

        if (set == null) {
            set = createDataSet(label);
            data.addDataSet(set);
        }

        // Add a new entry to the dataset
        data.addEntry(new Entry(set.getEntryCount(), value), 0);

        // Notify the chart that the data has changed
        chart.notifyDataSetChanged();
        chart.invalidate();
    }

    private void customizeChart(LineChart chart) {
        // Customize chart settings if needed
        // For example, enable touch gestures, set X-axis position, etc.
        chart.setTouchEnabled(true);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.animateX(1000);
        chart.getAxisRight().setEnabled(false);
        chart.getDescription().setText("Time ->");

        // Add other customization settings if needed
    }

    private LineDataSet createDataSet(String label) {
        // Create a LineDataSet with default settings
        LineDataSet set = new LineDataSet(null, label);
        set.setDrawCircles(false);
        set.setDrawValues(false);
        set.setCircleRadius(0f);
        set.setLineWidth(1f);
        set.setColor(Color.BLUE);
        return set;
    }


    private void checkLimitsAndShowOverlay(Long electricity, Long water) {

        if (electricity != null && electricity > electricityLimit) {
            // Electricity limit exceeded, show the overlay
            showOverWarninglayLayout();
        }


        else if (water != null && water > waterLimit) {
            // Water limit exceeded, show the overlay
            showOverWarninglayLayout();
        }
    }

    private void updateUI() {
        ee.setText(String.format("%s Kwh", Electricity_data != null ? Electricity_data : ""));
        ww.setText(String.format("%s L", Water_data != null ? Water_data : ""));
        ve.setText(String.format("%s V", Electricity_voltage != null ? Electricity_voltage : ""));
        fw.setText(String.format("%s L/min", Water_flow != null ? Water_flow : ""));
        displayUsername.setText(String.format("Hello, %s", Username != null ? Username : ""));
        fetchDataFromFirebase();
    }

    @Override
    public void onBackPressed() {
                if (doubleBackToExitPressedOnce) {
            finishAffinity();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        showToastOnExit();
        resetDoubleBackFlagAfterDelay();
    }

    private void showToastOnExit() {
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
    }

    private void resetDoubleBackFlagAfterDelay() {
        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

    private void logoutAndNavigateToLogin() {
        clearLoginStatus();
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void clearLoginStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("loginStatus");
        editor.apply();
    }

    private String getLoginStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        return sharedPreferences.getString("loginStatus", null);
    }


    public void showOverlayLayout(MenuItem item) {
        // If the overlay is already showing, close it
        if (overlayPopup != null && overlayPopup.isShowing()) {
            overlayPopup.dismiss();
            return;
        }




        // Inflate the overlay layout
        View overlayView = LayoutInflater.from(this).inflate(R.layout.overlay_layout, null);

        // Find the logout button in the overlay
        Button logoutButton = overlayView.findViewById(R.id.btnLogoutOverlay);

        // Set an OnClickListener for the logout button
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Call the logout method when the logout button is clicked
                logoutAndNavigateToLogin();

                // Dismiss the overlay after logging out
                if (overlayPopup != null && overlayPopup.isShowing()) {
                    overlayPopup.dismiss();
                }
            }
        });

        // Create a PopupWindow
        overlayPopup = new PopupWindow(overlayView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, true);

        // Remove the background color setting

        // Enable touch events for the underlying activity
        overlayPopup.setTouchable(true);

        // Set dismiss listener to remove the dim background when the popup is dismissed
        overlayPopup.setOnDismissListener(() -> {
            // Remove the background color when the overlay is dismissed
        });

        // Show the PopupWindow
        overlayPopup.showAtLocation(overlayView, 0, 0, 0);
    }




    public void resetWater(View view) {
        // Handle Reset Water button click
        resetWaterInFirebase();
    }

    public void showOverWarninglayLayout() {

        if(c==0){
            // If the overlay is already showing, do nothing
            if (overlayPopup != null && overlayPopup.isShowing()) {
                return;
            }

            // Inflate the warning overlay layout
            View warningOverlayView = LayoutInflater.from(this).inflate(R.layout.warning_layout, null);

            // Find the close button in the warning overlay using the correct ID
            Button closeButton = warningOverlayView.findViewById(R.id.btnCloseWarningOverlay);

            // Set an OnClickListener for the close button
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    c=1;
                    // Dismiss the warning overlay when the close button is clicked
                    if (overlayPopup != null && overlayPopup.isShowing()) {
                        overlayPopup.dismiss();
                    }
                }
            });

            // Create a PopupWindow for the warning overlay
            PopupWindow newPopup = new PopupWindow(warningOverlayView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, true);

            // Enable touch events for the underlying activity
            newPopup.setTouchable(true);

            // Set dismiss listener to remove the dim background when the popup is dismissed
            newPopup.setOnDismissListener(() -> {
                // Remove the background color when the overlay is dismissed
                overlayPopup = null;
            });

            // Show the PopupWindow for the warning overlay
            newPopup.showAtLocation(warningOverlayView, 0, 0, 0);

            // Update the reference to the new popup
            overlayPopup = newPopup;
        }

    }


    private void resetWaterInFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(FIREBASE_ADMIN_NODE);
        Query checkUsername = databaseReference.orderByChild(FIREBASE_USERNAME_FIELD).equalTo(Username);

        checkUsername.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();

                    // Reset water value to zero
                    userSnapshot.child(FIREBASE_WATER_FIELD).child(FIREBASE_WATER_CHILD_NODE).getRef().setValue(0);

                    // Fetch updated data from Firebase
                    fetchDataFromFirebase();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled if needed
            }
        });
    }




    public void resetElectricity(View view) {
        // Handle Reset Electricity button click

        // Call a method to reset electricity value to zero in Firebase
        resetElectricityInFirebase();
    }

    private void resetElectricityInFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(FIREBASE_ADMIN_NODE);
        Query checkUsername = databaseReference.orderByChild(FIREBASE_USERNAME_FIELD).equalTo(Username);

        checkUsername.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();

                    // Reset electricity value to zero
                    userSnapshot.child(FIREBASE_ELECTRICITY_FIELD).child(FIREBASE_ELECTRICITY_CHILD_NODE).getRef().setValue(0L);

                    // Fetch updated data from Firebase
                    fetchDataFromFirebase();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled if needed
            }
        });
    }

    private void getLimitsFromSharedPreferences() {
        // Get the SharedPreferences instance
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        // Retrieve electricitylimvalue, default value is 0 if not found
        electricityLimit = sharedPreferences.getLong("electricitylimvalue", 0);

        // Retrieve waterlimvalue, default value is 0 if not found
        waterLimit = sharedPreferences.getLong("waterlimvalue", 0);

        // Now you have both values
        // You can use savedElectricityLimit and savedWaterLimit as needed
        // For example, you can display them, perform calculations, etc.
    }


}