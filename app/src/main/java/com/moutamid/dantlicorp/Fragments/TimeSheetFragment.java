package com.moutamid.dantlicorp.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.dantlicorp.Adapter.VideoAdapter;
import com.moutamid.dantlicorp.Admin.Adapter.TimesheetAdapter;
import com.moutamid.dantlicorp.Model.TimeSheetModel;
import com.moutamid.dantlicorp.Model.VideoModel;
import com.moutamid.dantlicorp.R;
import com.moutamid.dantlicorp.helper.Config;
import com.moutamid.dantlicorp.helper.Constants;

import java.util.ArrayList;
import java.util.List;

public class TimeSheetFragment extends Fragment {

    RecyclerView content_rcv;
     public List<TimeSheetModel> productModelList = new ArrayList<>();
    TimesheetAdapter timesheetAdapter;
    TextView no_text;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_timesheet, container, false);
        content_rcv = view.findViewById(R.id.content_rcv);
        content_rcv.setLayoutManager(new GridLayoutManager(getContext(), 1));
        timesheetAdapter = new TimesheetAdapter(getContext(), productModelList);
        content_rcv.setAdapter(timesheetAdapter);
        no_text = view.findViewById(R.id.no_text);

        if (Config.isNetworkAvailable(getContext())) {
            getProducts();
        } else {
            Toast.makeText(getContext(), "No network connection available.", Toast.LENGTH_SHORT).show();
        }
        return view;
    }


    private void getProducts() {
//        Config.showProgressDialog(getContext());
        Constants.UserReference.child(Constants.auth().getUid()).child(Constants.TIME_SHEET).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                productModelList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    TimeSheetModel herbsModel = ds.getValue(TimeSheetModel.class);
                    productModelList.add(herbsModel);
                }
                if(productModelList.size()<1)
                {
                    no_text.setVisibility(View.VISIBLE);
                }
                else
                {
                    no_text.setVisibility(View.GONE);
                }
                timesheetAdapter.notifyDataSetChanged();
//                Config.dismissProgressDialog();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Config.dismissProgressDialog();

            }


        });
    }

}