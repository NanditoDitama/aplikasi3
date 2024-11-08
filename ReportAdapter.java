package com.example.laporan2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class ReportAdapter extends BaseAdapter {

    private Context context;
    private List<Report> reportList;
    private OnItemClickListener listener;

    public ReportAdapter(Context context, List<Report> reportList) {
        this.context = context;
        this.reportList = reportList;
    }

    @Override
    public int getCount() {
        return reportList.size();
    }

    @Override
    public Report getItem(int position) {
        return reportList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_report, parent, false);
            holder = new ViewHolder();
            holder.textViewTitle = convertView.findViewById(R.id.textViewTitle);
            holder.textViewDate = convertView.findViewById(R.id.textViewDate);
            holder.buttonDelete = convertView.findViewById(R.id.buttonDelete);
            holder.buttonEdit = convertView.findViewById(R.id.buttonEdit);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Report report = reportList.get(position);

        if (report != null) {
            // Tampilkan status
            if ("pending".equals(report.getStatus())) {
                holder.textViewTitle.setText("Menunggu Persetujuan");
                holder.textViewDate.setText("Status: Pending");
                holder.buttonEdit.setVisibility(View.GONE);
            } else if ("approved".equals(report.getStatus())) {
                holder.textViewTitle.setText(report.getTitle());
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                holder.textViewDate.setText(sdf.format(report.getDate()));
                holder.buttonEdit.setVisibility(View.GONE);
            } else if ("rejected".equals(report.getStatus())) {
                holder.textViewTitle.setText(report.getTitle());
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                holder.textViewDate.setText(sdf.format(report.getDate()));
                holder.buttonEdit.setVisibility(View.VISIBLE);
            }
        }

        return convertView;
    }

    public void updateData(List<Report> newReportList) {
        this.reportList = newReportList;
        notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView textViewTitle, textViewDate;
        ImageButton buttonDelete, buttonEdit;
    }

    public interface OnItemClickListener {
        void onItemClick(Report report);
        void onDeleteClick(Report report);
        void onEditClick(Report report);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

}