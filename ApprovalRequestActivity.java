package com.example.laporan2;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApprovalRequestActivity extends AppCompatActivity {
    private List<Map<String, Object>> approvalRequests;
    private ApprovalRequestAdapter adapter;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval_request);

        db = FirebaseFirestore.getInstance();
        approvalRequests = new ArrayList<>();

        // Setup RecyclerView atau ListView
        fetchPendingRequests();
    }

    private void fetchPendingRequests() {
        db.collection("approvalRequests")
                .whereEqualTo("status", "pending")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    approvalRequests.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Map<String, Object> requestData = doc.getData();
                        approvalRequests.add(requestData);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void approveRequest(Map<String, Object> requestData) {
        String requestId = (String) requestData.get("id");

        // Buat dokumen baru di collection reports
        Map<String, Object> reportData = new HashMap<>(requestData);
        reportData.put("status", "approved");

        db.collection("reports")
                .document(requestId)
                .set(reportData)
                .addOnSuccessListener(aVoid -> {
                    // Hapus approval request
                    db.collection("approvalRequests")
                            .document(requestId)
                            .delete();
                });
    }

    private void rejectRequest(Map<String, Object> requestData, String reason) {
        String requestId = (String) requestData.get("id");

        // Update status di approvalRequests
        Map<String, Object> rejectionData = new HashMap<>(requestData);
        rejectionData.put("status", "rejected");
        rejectionData.put("rejectionReason", reason);

        db.collection("rejectedReports")
                .document(requestId)
                .set(rejectionData)
                .addOnSuccessListener(aVoid -> {
                    // Hapus approval request
                    db.collection("approvalRequests")
                            .document(requestId)
                            .delete();
                });
    }
}