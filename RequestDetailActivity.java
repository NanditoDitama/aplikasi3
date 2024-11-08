package com.example.laporan2;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RequestDetailActivity extends AppCompatActivity {
    private ApprovalRequest currentRequest;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_detail);

        db = FirebaseFirestore.getInstance();

        // Ambil ID request dari intent
        String requestId = getIntent().getStringExtra("requestId");

        // Load detail request
        loadRequestDetail(requestId);

        // Setup tombol approve dan reject
        Button buttonApprove = findViewById(R.id.buttonApprove);
        Button buttonReject = findViewById(R.id.buttonReject);

        buttonApprove.setOnClickListener(v -> processRequest(true));
        buttonReject.setOnClickListener(v -> processRequest(false));
    }

    private void loadRequestDetail(String requestId) {
        db.collection("approvalRequests")
                .document(requestId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    currentRequest = documentSnapshot.toObject(ApprovalRequest.class);

                    if (currentRequest != null) {
                        // Sembunyikan semua view edit terlebih dahulu
                        hideEditViews();

                        // Tampilkan detail berdasarkan tipe request
                        if ("edit".equals(currentRequest.getType())) {
                            loadEditRequestDetail();
                        } else {
                            loadNewRequestDetail();
                        }
                    }
                });
    }

    private void hideEditViews() {
        // Sembunyikan view edit
        findViewById(R.id.textViewOriginalTitle).setVisibility(View.GONE);
        findViewById(R.id.textViewOriginalDescription).setVisibility(View.GONE);
        findViewById(R.id.textViewOriginalAmount).setVisibility(View.GONE);
        findViewById(R.id.textViewNewTitle).setVisibility(View.GONE);
        findViewById(R.id.textViewNewDescription).setVisibility(View.GONE);
        findViewById(R.id.textViewNewAmount).setVisibility(View.GONE);

        // Tampilkan view default
        findViewById(R.id.textViewTitle).setVisibility(View.VISIBLE);
        findViewById(R.id.textViewDescription).setVisibility(View.VISIBLE);
        findViewById(R.id.textViewAmount).setVisibility(View.VISIBLE);
        findViewById(R.id.imageViewReport).setVisibility(View.VISIBLE);
    }


    private void loadNewRequestDetail() {
        Map<String, Object> reportData = currentRequest.getReportData();

        // Tampilkan detail laporan baru
        TextView titleTextView = findViewById(R.id.textViewTitle);
        TextView descriptionTextView = findViewById(R.id.textViewDescription);
        TextView amountTextView = findViewById(R.id.textViewAmount);
        ImageView imageView = findViewById(R.id.imageViewReport);

        titleTextView.setText((String) reportData.get("title"));
        descriptionTextView.setText((String) reportData.get("description"));

        // Format amount
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("id", "ID"));
        amountTextView.setText(currencyFormat.format(
                Double.parseDouble(reportData.get("amount").toString())
        ));

        // Tampilkan gambar jika ada
        String imageUrl = (String) reportData.get("imageUrl");
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .into(imageView);
        }
    }


    private void loadEditRequestDetail() {
        Map<String, Object> reportData = currentRequest.getReportData();

        // Ambil data laporan asli
        db.collection("reports")
                .document((String) reportData.get("originalReportId"))
                .get()
                .addOnSuccessListener(originalDoc -> {
                    Report originalReport = originalDoc.toObject(Report.class);

                    // Tampilkan detail laporan asli
                    TextView originalTitleTextView = findViewById(R.id.textViewOriginalTitle);
                    TextView originalDescriptionTextView = findViewById(R.id.textViewOriginalDescription);
                    TextView originalAmountTextView = findViewById(R.id.textViewOriginalAmount);

                    originalTitleTextView.setText("Original: " + originalReport.getTitle());
                    originalDescriptionTextView.setText("Original: " + originalReport.getDescription());
                    originalAmountTextView.setText("Original: " +
                            NumberFormat.getCurrencyInstance(new Locale("id", "ID"))
                                    .format(originalReport.getAmount())
                    );

                    // Tampilkan detail perubahan
                    TextView newTitleTextView = findViewById(R.id.textViewNewTitle);
                    TextView newDescriptionTextView = findViewById(R.id.textViewNewDescription);
                    TextView newAmountTextView = findViewById(R.id.textViewNewAmount);

                    newTitleTextView.setText("New: " + reportData.get("title"));
                    newDescriptionTextView.setText("New: " + reportData.get("description"));
                    newAmountTextView.setText("New: " +
                            NumberFormat.getCurrencyInstance(new Locale("id", "ID"))
                                    .format(Double.parseDouble(reportData.get("amount").toString()))
                    );
                });
    }

    private void processEditRequest(boolean isApproved) {
        if (currentRequest == null) return;

        Map<String, Object> reportData = currentRequest.getReportData();
        String originalReportId = (String) reportData.get("originalReportId");

        if (isApproved) {
            // Update laporan asli
            Map<String, Object> updateData = new HashMap<>();
            updateData.put("title", reportData.get("title"));
            updateData.put("description", reportData.get("description"));
            updateData.put("amount", reportData.get("amount"));

            // Update gambar jika ada
            if (reportData.containsKey("imageUrl")) {
                updateData.put("imageUrl", reportData.get("imageUrl"));
            }

            db.collection("reports")
                    .document(originalReportId)
                    .update(updateData)
                    .addOnSuccessListener(aVoid -> {
                        // Hapus approval request
                        db.collection("approvalRequests")
                                .document(currentRequest.getId())
                                .delete();

                        Toast.makeText(this, "Perubahan disetujui", Toast.LENGTH_SHORT).show();
                        finish();
                    });
        } else {
            // Simpan status reject
            db.collection("rejectedEditRequests")
                    .document(currentRequest.getId())
                    .set(currentRequest)
                    .addOnSuccessListener(aVoid -> {
                        // Hapus approval request
                        db.collection("approvalRequests")
                                .document(currentRequest.getId())
                                .delete();

                        Toast.makeText(this, "Perubahan ditolak", Toast.LENGTH_SHORT).show(); finish();
                    });
        }
    }

    private void processRequest(boolean isApproved) {

        if (!isApproved) {
            // Tampilkan dialog input alasan
            showRejectionReasonDialog();
        } else {
            // Proses persetujuan
            approveRequest();
        }


        if (currentRequest == null) return;

        // Cek tipe request
        if ("edit".equals(currentRequest.getType())) {
            processEditRequest(isApproved);
            return;
        }

        // Update status request
        String status = isApproved ? "approved" : "rejected";

        db.collection("approvalRequests")
                .document(currentRequest.getId())
                .update("status", status)
                .addOnSuccessListener(aVoid -> {
                    // Jika approve, simpan ke reports
                    if (isApproved) {
                        saveApprovedReport();
                    } else {
                        // Untuk reject, kembalikan ke user dengan status
                        saveRejectedReport();
                    }
                });
    }

    private void saveApprovedReport() {
        Map<String, Object> reportData = currentRequest.getReportData();

        // Konversi data ke Report
        Report report = new Report(
                (String) reportData.get("id"),
                (String) reportData.get("title"),
                (String) reportData.get("description"),
                Double.parseDouble(reportData.get("amount").toString()),
                (Date) reportData.get("date"),
                (String) reportData.get("imageUrl"),
                (String) reportData.get("userId"),
                null
        );

        // Simpan ke reports
        db.collection("reports")
                .document(report.getId())
                .set(report)
                .addOnSuccessListener(aVoid -> {
                    // Redirect atau finish
                    Toast.makeText(this, "Report approved", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }
    private void showRejectionReasonDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Alasan Penolakan");

        // Buat EditText untuk alasan
        final EditText input = new EditText(this);
        input.setHint("Masukkan alasan penolakan");
        builder.setView(input);

        builder.setPositiveButton("Tolak", (dialog, which) -> {
            String reason = input.getText().toString().trim();
            rejectRequest(reason);
        });

        builder.setNegativeButton("Batal", null);
        builder.show();
    }

    private void rejectRequest(String reason) {
        // Simpan laporan ke rejected dengan alasan
        Map<String, Object> rejectedData = new HashMap<>(currentRequest.getReportData());
        rejectedData.put("rejectionReason", reason);

        db.collection("rejectedReports")
                .document(currentRequest.getId())
                .set(rejectedData)
                .addOnSuccessListener(aVoid -> {
                    // Hapus dari approval requests
                    db.collection("approvalRequests")
                            .document(currentRequest.getId())
                            .delete();

                    Toast.makeText(this, "Laporan ditolak", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }

    private void saveRejectedReport() {
        // Simpan status reject ke collection tersendiri
        Map<String, Object> rejectedReport = new HashMap<>(currentRequest.getReportData());
        rejectedReport.put("status", "rejected");

        db.collection("rejectedReports")
                .document(currentRequest.getId())
                .set(rejectedReport)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Report rejected", Toast.LENGTH_SHORT).show();
                    finish();
                });
    }
}