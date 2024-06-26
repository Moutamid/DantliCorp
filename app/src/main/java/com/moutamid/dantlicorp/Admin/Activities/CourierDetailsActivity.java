package com.moutamid.dantlicorp.Admin.Activities;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.icu.lang.UCharacter;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.fxn.stash.Stash;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.dantlicorp.Activities.Home.EditProfileActivity;
import com.moutamid.dantlicorp.Admin.Adapter.MyAdapter;
//import com.moutamid.dantlicorp.Dailogues.UserDetailsDialogClass;
import com.moutamid.dantlicorp.R;
import com.moutamid.dantlicorp.helper.Config;
import com.moutamid.dantlicorp.helper.Constants;

import java.util.Objects;

public class CourierDetailsActivity extends AppCompatActivity {


    TabLayout tabLayout;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager = (ViewPager) findViewById(R.id.viewPager);

        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.profile)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.social_links)));
//        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.routes)));
//        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.check_out)));

         tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final MyAdapter adapter = new MyAdapter(this, getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    public void map(View view) {

        String userID = Stash.getString("userID");
        Dialog lodingbar = new Dialog(CourierDetailsActivity.this);
        lodingbar.setContentView(R.layout.loading);
        Objects.requireNonNull(lodingbar.getWindow()).setBackgroundDrawable(new ColorDrawable(UCharacter.JoiningType.TRANSPARENT));
        lodingbar.setCancelable(false);
        lodingbar.show();

        Constants.LocationReference.child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Intent intent = new Intent(CourierDetailsActivity.this, UserLocationActivity.class);
                    intent.putExtra("lat", snapshot.child("lat").getValue().toString());
                    intent.putExtra("lng", snapshot.child("lng").getValue().toString());
                    intent.putExtra("name", snapshot.child("name").getValue().toString());
lodingbar.dismiss();                    startActivity(intent);
                }
                else
                {
lodingbar.dismiss();                    Toast.makeText(CourierDetailsActivity.this, "User location is not active", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
lodingbar.dismiss();                Toast.makeText(CourierDetailsActivity.this, "User location is not active", Toast.LENGTH_SHORT).show();



            }
        });
//        if (model.lat > -90 && model.lat < 90 && model.lng > -180 && model.lng < 180) {


//        } else {
//            Toast.makeText(context, "Invalid Coordinates to show marker", Toast.LENGTH_SHORT).show();
//        }
    }

    public void timeline(View view) {
        startActivity(new Intent(CourierDetailsActivity.this, AllTimeSheetsActivity.class));

//        UserDetailsDialogClass cdd = new UserDetailsDialogClass(CourierDetailsActivity.this);
//        cdd.show();
    }
}