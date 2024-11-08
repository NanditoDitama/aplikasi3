package com.example.laporan2;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ApprovalActivity extends AppCompatActivity {
    private List<Report> pendingReports;
    private ApprovalAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval);

        fetchPendingReports();
    }

    private void fetchPendingReports() {
        db.collection("reports")
                .whereEqualTo("status", "pending")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    pendingReports.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Report report = doc.toObject(Report.class);
                        pendingReports.add(report);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void approveReport(Report report) {
        report.setStatus("approved");
        db.collection("reports")
                .document(report.getId())
                .update("status", "approved")
                .addOnSuccessListener(aVoid -> {
                    // Update history
                    updateReportHistory(report, "approved");
                });
    }

    private void rejectReport(Report report, String reason) {
        report.setStatus("rejected");
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "rejected");
        updates.put("rejectionReason", reason);

        db.collection("reports")
                .document(report.getId())
                .update(updates)
                .addOnSuccessListener(aVoid -> {
                    // Update history
                    updateReportHistory(report, "rejected");
                });
    }

    private void updateReportHistory(Report report, String status) {
        History history = new History(
                UUID.randomUUID().toString(),
                report.getId(),
                getCurrentUserId(),
                status,
                new Date()
        );

        db.collection("history")
                .add(history);
    }
}
