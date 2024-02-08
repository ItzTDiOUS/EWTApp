package com.example.ewtapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.res.ResourcesCompat;

import com.github.mikephil.charting.components.YAxis;
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


import java.util.ArrayList;
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
    private TextView ee, ww, user;
    private PopupWindow overlayPopup;

    private String Username;

    private String Electricity_data, Water_data;

    private static final String FIREBASE_ADMIN_NODE = "Admin";
    private static final String FIREBASE_USERNAME_FIELD = "username";
    private static final String FIREBASE_ELECTRICITY_FIELD = "electricity";
    private static final String FIREBASE_WATER_FIELD = "water";

    private static final String FIREBASE_ELECTRICITY_CHILD_NODE = "total_energy";
    private static final String FIREBASE_WATER_CHILD_NODE = "total_water_used";

    private static final String CHANNEL_ID = "My Channel";
    private static final int NOTIFICATION_ID = 100;

    private TextInputLayout ElecLimitText,WatLimText;


    int c;

    public MainActivity() {
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //removing app name from the top
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Objects.requireNonNull(getSupportActionBar()).hide();

        setContentView(R.layout.home);

        chartElectricity = findViewById(R.id.electricity_chart);
        electricityEntries = new ArrayList<>();
        chartWater = findViewById(R.id.water_chart);

        // Customize chart settings if needed

        customizeChart(chartElectricity);
        customizeChart(chartWater);

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





        // Notification
        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.alert, null);
        BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
        Bitmap largeIcon = (bitmapDrawable != null) ? bitmapDrawable.getBitmap() : null;

        createNotificationChannel();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setLargeIcon(largeIcon)
                .setSmallIcon(R.drawable.ewtlogo)
                .setContentText("ALERT")
                .setSubText("You Have Exceeded Your Electricity/Water Usage");

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            builder.setChannelId(CHANNEL_ID);
        }

        NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        nm.notify(NOTIFICATION_ID, builder.build());

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Username = getLoginStatus();
        Button refresh = findViewById(R.id.refresh_button);
        ee = findViewById(R.id.electricity_data);
        ww = findViewById(R.id.water_data);
        user=findViewById(R.id.naam);
        Button signoutButton = findViewById(R.id.logout_button);
        signoutButton.setOnClickListener(v -> logoutAndNavigateToLogin());

        refresh.setOnClickListener(v -> fetchDataFromFirebase());

        fetchDataFromFirebase(); // Initial data fetch

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    private void OpenLimPage() {
        Intent intent=new Intent(getApplicationContext(), LimPage.class);
        startActivity(intent);
        finish();
    }

    private void customizeChart(@NonNull LineChart chart) {
        // Customize chart settings if needed
        // For example, enable touch gestures, set X-axis position, etc.

        chart.setTouchEnabled(true);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        // Get the left Y-axis
        YAxis leftAxis = chart.getAxisLeft();

        // Enable the left Y-axis
        leftAxis.setEnabled(true);

        // Get the right Y-axis
        YAxis rightAxis = chart.getAxisRight();

        // Disable the right Y-axis
        rightAxis.setEnabled(false);


        // Add other customization settings if needed
    }


    private void updateCharts(long electricityValue, long waterValue) {
        // Update the LineCharts with new data points
        updateChart(chartElectricity, electricityValue, "Electricity");
        updateChart(chartWater, waterValue, "Water");
    }
    private void updateChart(LineChart chart, long value, String label) {
        LineData data = chart.getData();

        if (data == null) {
            data = new LineData();
            chart.setData(data);
        }

        ILineDataSet set = data.getDataSetByIndex(0);

        if (set != null && set.getEntryCount() > 6) {
            // Remove the oldest point
            Entry removedEntry = set.getEntryForIndex(0);
            set.removeEntry(removedEntry);

            // Adjust x-values of remaining entries
            List<Entry> entries = ((LineDataSet) set).getValues();

            for (Entry entry : entries) {
                entry.setX(entry.getX() - 1f);
            }
        }

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

    private LineDataSet createDataSet(String label) {
        // Create a LineDataSet with the provided label
        LineDataSet set = new LineDataSet(null, label);
        set.setDrawCircles(true);
        set.setDrawValues(true);
        set.setCircleRadius(4f);
        set.setLineWidth(2f);
        set.setColor(Color.BLUE);
        return set;
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

                    // Convert Long to String
                    Electricity_data = (electricityLong != null) ? String.valueOf(electricityLong) : "";
                    Water_data = (waterLong != null) ? String.valueOf(waterLong) : "";

                    // Check if the limits are exceeded
                    checkLimitsAndShowOverlay(electricityLong, waterLong);

                    // Update the UI
                    updateUI();

                     //Update the charts
                    updateCharts(electricityLong != null ? electricityLong : 0,
                            waterLong != null ? waterLong : 0);

                    chartElectricity.notifyDataSetChanged();
                    chartElectricity.invalidate();

                    chartWater.notifyDataSetChanged();
                    chartWater.invalidate();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle onCancelled if needed
            }
        });
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

        fetchDataFromFirebase();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

}