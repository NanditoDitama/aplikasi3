package com.example.laporan2;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import java.util.List;

public class ApprovalAdapter extends ArrayAdapter<Report> {
    private Context context;
    private List<Report> reports;

    // Konstruktor dan metode standard adapter

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Report report = reports.get(position);

        // Buat tampilan dengan tombol approve dan reject
        View view = // inflate layout

                ButtonapproveButton = view.findViewById(R.id.buttonApprove);
        Button rejectButton = view.findViewById(R.id.buttonReject);

        approveButton.setOnClickListener(v -> {
            // Panggil method approve di activity
        });

        rejectButton.setOnClickListener(v -> {
            // Tampilkan dialog input alasan
        });

        return view;
    }
}