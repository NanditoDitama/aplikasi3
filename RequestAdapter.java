package com.example.laporan2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.RequestViewHolder> {
    private List<ApprovalRequest> requests;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ApprovalRequest request);
    }

    public RequestAdapter(List<ApprovalRequest> requests, OnItemClickListener listener) {
        this.requests = requests;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        ApprovalRequest request = requests.get(position);

        // Ambil data dari reportData
        Map<String, Object> reportData = request.getReportData();

        holder.titleTextView.setText(reportData.get("title").toString());
        holder.userNameTextView.setText(request.getUserName());

        // Format tanggal
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());
        holder.dateTextView.setText(dateFormat.format(request.getTimestamp()));

        holder.itemView.setOnClickListener(v -> listener.onItemClick(request));
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        TextView userNameTextView;
        TextView dateTextView;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.textViewTitle);
            userNameTextView = itemView.findViewById(R.id.textViewUserName);
            dateTextView = itemView.findViewById(R.id.textViewDate);
        }
    }
}
