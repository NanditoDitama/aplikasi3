package com.example.laporan2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;

public class AdminDashboardActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private ImageButton buttonLogout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);

        // Setup ViewPager dengan FragmentStateAdapter
        AdminPagerAdapter adapter = new AdminPagerAdapter(this);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> {
                    tab.setText(position == 0 ? "New Requests" : "Edit Requests");
                }
        ).attach();
        buttonLogout = findViewById(R.id.buttonLogout);

        // Set listener untuk tombol logout
        buttonLogout.setOnClickListener(v -> showLogoutConfirmationDialog());
    }
    private void showLogoutConfirmationDialog() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
        Toast.makeText(this, "Logout berhasil", Toast.LENGTH_SHORT).show();
    }
}