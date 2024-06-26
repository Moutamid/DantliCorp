package com.moutamid.dantlicorp.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.icu.lang.UCharacter;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.fxn.stash.Stash;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.dantlicorp.Activities.Home.AllUserLocationActivity;
import com.moutamid.dantlicorp.Activities.Home.ChatActivity;
import com.moutamid.dantlicorp.Activities.Home.NotificationsActivity;
import com.moutamid.dantlicorp.Dailogues.ChecksDialogClass;
import com.moutamid.dantlicorp.Dailogues.StartRouteDialogClass;
import com.moutamid.dantlicorp.MainActivity;
import com.moutamid.dantlicorp.Model.ChecksModel;
import com.moutamid.dantlicorp.Model.SocialModel;
import com.moutamid.dantlicorp.Model.UserModel;
import com.moutamid.dantlicorp.R;
import com.moutamid.dantlicorp.helper.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class HomeFragment extends Fragment {
    TextView admin;
    TextView date_txt;
    LinearLayout add_check_in_lyt, add_check_out_lyt, chat_Admin, start_journey_lyt;
    TextView show_map_lyt;
    FusedLocationProviderClient mFusedLocationClient;
    int PERMISSION_ID = 44;

    TextView journey_txt;
    ArrayList<UserModel> userArrayList = new ArrayList<>();
    ImageView menu;
    TextView location_txt;
    UserModel userNew;
    ImageView notification_icon;
    Context context;
    Resources resources;
    LinearLayout start_route, end_route;
    public static String address ="Can't fetch automatically";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for   getContext() fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
//        date_txt = view.findViewById(R.id.date);
        notification_icon = view.findViewById(R.id.notification_icon);
        add_check_in_lyt = view.findViewById(R.id.add_check_in_lyt);
        add_check_out_lyt = view.findViewById(R.id.add_check_out_lyt);
        location_txt = view.findViewById(R.id.location);
        menu = view.findViewById(R.id.language_icon);
        location_txt.setSelected(true);
        chat_Admin = view.findViewById(R.id.chat_Admin);
        start_route = view.findViewById(R.id.start_route);
        end_route = view.findViewById(R.id.end_route);
        userNew = (UserModel) Stash.getObject("UserDetails", UserModel.class);
        start_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StartRouteDialogClass cdd = new StartRouteDialogClass(getActivity(), add_check_in_lyt, add_check_out_lyt, start_route);
                cdd.show();


            }
        });
        end_route.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChecksModel checksModel = new ChecksModel();
                checksModel.status = "end";

                UserModel userModel = (UserModel) Stash.getObject("UserDetails", UserModel.class);
                Map<String, Object> values = new HashMap<>();
                values.put("status", "end");

                DatabaseReference child = Constants.UserReference.child(userModel.id).child("Routes").child(Stash.getString("route_key"));
                child.updateChildren(values).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isComplete())
                        {
                            add_check_in_lyt.setVisibility(View.GONE);
                            add_check_out_lyt.setVisibility(View.GONE);
                            start_route.setVisibility(View.GONE);
                            end_route.setVisibility(View.GONE);
                            Stash.put("route_start", "no");
                            Stash.put("check_in", "no");
                        }
                    }
                });

            }


        });
        notification_icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), NotificationsActivity.class));
            }
        });
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(getContext(), menu);

                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                popupMenu.setForceShowIcon(true);

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.spanish) {
                            setLocale("es");
                        } else if (menuItem.getItemId() == R.id.chinese) {
                            setLocale("zh");
                        } else if (menuItem.getItemId() == R.id.english) {
                            setLocale("en");
                        } else if (menuItem.getItemId() == R.id.russian) {
                            setLocale("ru");
                        } else if (menuItem.getItemId() == R.id.mandarin) {
                            setLocale("mdr");
                        } else if (menuItem.getItemId() == R.id.french) {
                            setLocale("fr");
                        }
                        return true;

                    }

                });

                popupMenu.show();

            }
        });

//        start_journey_lyt = view.findViewById(R.id.start_journey);
//        journey_txt = view.findViewById(R.id.journey_txt);
        show_map_lyt = view.findViewById(R.id.show_map_lyt);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        getLastLocation();
//        getDate();
//        start_journey_lyt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                Toast.makeText(getContext(), ""+Stash.getBoolean("journey_start"), Toast.LENGTH_SHORT).show();
////                if (Stash.getBoolean("journey_start")) {
////                    Stash.put("journey_start", false);
////                    journey_txt.setText("Journey Stop");
////                }
////                else
////                {
////                    Stash.put("journey_start", true);
////                    journey_txt.setText("Journey Start");
////
//                startActivity(new Intent(getContext(), AdminPanel.class));
////                }
//            }
//        });
        String route_start = Stash.getString("route_start");
        if (route_start != null) {
            if (route_start.equals("yes")) {
                add_check_in_lyt.setVisibility(View.GONE);
                add_check_out_lyt.setVisibility(View.GONE);
                end_route.setVisibility(View.GONE);
                start_route.setVisibility(View.GONE);

            } else {
                add_check_in_lyt.setVisibility(View.GONE);
                add_check_out_lyt.setVisibility(View.GONE);
                end_route.setVisibility(View.GONE);
                start_route.setVisibility(View.GONE);
            }
        }
        chat_Admin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getContext(), ChatActivity.class));
            }
        });
        add_check_in_lyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLastLocation();
                String check_in = Stash.getString("check_in");
                if (check_in != null) {
                    if (check_in.equals("yes")) {
                        Toast.makeText(getContext(), getString(R.string.please_check_out_before_check_in), Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = (new Intent(getContext(), ChecksDialogClass.class));
                        intent.putExtra("type", getString(R.string.check_in));
                        startActivity(intent);

                    }
                }
            }
        });
        add_check_out_lyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getLastLocation();
                String check_in = Stash.getString("check_in");
                if (check_in != null) {
                    if (check_in.equals("yes")) {
                        Intent intent = (new Intent(getContext(), ChecksDialogClass.class));
                        intent.putExtra("type", getString(R.string.check_out));
                        startActivity(intent);
                    } else {
                        Toast.makeText(getContext(), getString(R.string.please_check_in_before_check_out), Toast.LENGTH_SHORT).show();

                    }
                }
            }
        });
        show_map_lyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dialog lodingbar = new Dialog(getContext());
                lodingbar.setContentView(R.layout.loading);
                Objects.requireNonNull(lodingbar.getWindow()).setBackgroundDrawable(new ColorDrawable(UCharacter.JoiningType.TRANSPARENT));
                lodingbar.setCancelable(false);
                lodingbar.show();
                Constants.LocationReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot ds : snapshot.getChildren()) {
                            UserModel userModel = ds.getValue(UserModel.class);
                            userArrayList.add(new UserModel(ds.getKey(), userModel.lat, userModel.lng, userModel.image_url, userModel.name));
                        }
                        Stash.put("AllUserLocation", userArrayList);
                        startActivity(new Intent(getContext(), AllUserLocationActivity.class));
                        lodingbar.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        lodingbar.dismiss();
                    }
                });
            }
        });
        if (userNew.id != null) {
            Constants.UserReference.child(userNew.id).child("social_links").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        SocialModel socialModel = snapshot.getValue(SocialModel.class);
                        Stash.put("UserLinks", socialModel);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        return view;
    }

    //
//    public void getDate() {
//        String date = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
//        Calendar calendar = Calendar.getInstance();
//        int day = calendar.get(Calendar.DAY_OF_WEEK);
//        switch (day) {
//            case Calendar.SUNDAY:
//                date_txt.setText("Sunday  " + date);
//                break;
//            case Calendar.MONDAY:
//                date_txt.setText("Monday  " + date);
//                break;
//            case Calendar.TUESDAY:
//                date_txt.setText("Tuesday  " + date);
//                break;
//
//            case Calendar.WEDNESDAY:
//                date_txt.setText("Wednesday  " + date);
//                break;
//
//            case Calendar.THURSDAY:
//                date_txt.setText("Thursday  " + date);
//                break;
//
//            case Calendar.FRIDAY:
//                date_txt.setText("Friday  " + date);
//                break;
//
//            case Calendar.SATURDAY:
//                date_txt.setText("Saturday  " + date);
//                break;
//        }
//
//    }
//
//
    private void getLastLocation() {
        // check if permissions are given
        if (checkPermissions()) {

            // check if location is enabled
            if (isLocationEnabled()) {

                // getting last
                // location from
                // FusedLocationClient
                // object
                if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        Location location = task.getResult();
                        if (location == null) {
                            requestNewLocationData();
                        } else {
                            UserModel userNew = (UserModel) Stash.getObject("UserDetails", UserModel.class);
//                            Geocoder geocoder;
//                            List<Address> addresses = null;
//                            geocoder = new Geocoder(getActivity(), Locale.getDefault());
//                            try {
//                                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
//                            } catch (IOException e) {
//                                Log.d("data", e.getMessage() + "");
//                            }
//                            address = Objects.requireNonNull(addresses).get(0).getAddressLine(0);
//                          location_txt.setText(address);
                            Constants.cur_lat = location.getLatitude();
                            Constants.cur_lng = location.getLongitude();
                            UserModel userModel = new UserModel();
                            userModel.lat = Constants.cur_lat;
                            userModel.lng = Constants.cur_lng;
                            userModel.name = userNew.name;
                            userModel.image_url = userNew.image_url;
                            if (userNew.id != null) {
                                Stash.put("userID", userNew.id);

                                Constants.LocationReference.child(userNew.id).setValue(userModel);
                            }
                        }
                    }
                });
            } else {
                Toast.makeText(getContext(), "Please turn on" + " your location...", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        } else {
            // if permissions aren't available,
            // request for permissions
            requestPermissions();
        }
    }

    @SuppressLint("MissingPermission")
    private void requestNewLocationData() {

        // Initializing LocationRequest
        // object with appropriate methods
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(5);
        mLocationRequest.setFastestInterval(0);
        mLocationRequest.setNumUpdates(1);

        // setting LocationRequest
        // on FusedLocationClient
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getContext());
        mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
    }

    private LocationCallback mLocationCallback = new LocationCallback() {

        @Override
        public void onLocationResult(LocationResult locationResult) {
            Location location = locationResult.getLastLocation();
            Constants.cur_lat = location.getLatitude();
            Constants.cur_lng = location.getLongitude();
        }
    };

    // method to check for permissions
    private boolean checkPermissions() {
        return ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        // If we want background location
        // on Android 10.0 and higher,
        // use:
        // ActivityCompat.checkSelfPermission(  getContext(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // method to request for permissions
    private void requestPermissions() {
        ActivityCompat.requestPermissions((Activity) getContext(), new String[]{
                android.Manifest.permission.ACCESS_COARSE_LOCATION,
                android.Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_ID);
    }

    // method to check
    // if location is enabled
    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    // If everything is alright then
    @Override
    public void
    onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        String route_start = Stash.getString("route_start");
        if (route_start != null) {
            if (route_start.equals("yes")) {
                end_route.setVisibility(View.GONE);
                add_check_in_lyt.setVisibility(View.GONE);
                add_check_out_lyt.setVisibility(View.GONE);
                start_route.setVisibility(View.GONE);

            } else {
                add_check_in_lyt.setVisibility(View.GONE);
                add_check_out_lyt.setVisibility(View.GONE);
                end_route.setVisibility(View.GONE);
                start_route.setVisibility(View.GONE);

            }
        }
        if (checkPermissions()) {
            getLastLocation();
        }
    }

    public void setLocale(String lang) {
        Stash.put("language", lang);
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(getContext(), MainActivity.class);
        startActivity(refresh);
        getActivity().finishAffinity();
    }

}