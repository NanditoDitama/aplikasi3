package com.example.laporan2;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class EditRequestsFragment extends Fragment {
    private RecyclerView recyclerView;
    private RequestAdapter adapter;
    private List<ApprovalRequest> requests = new ArrayList<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_requests, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewRequests);
        adapter = new RequestAdapter(requests, this::onRequestItemClick);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        loadEditRequests();
        return view;
    }

    private void loadEditRequests() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("approvalRequests")
                .whereEqualTo("status", "pending")
                .whereEqualTo("type", "edit")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    requests.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        ApprovalRequest request = doc.toObject(ApprovalRequest.class);
                        requests.add(request);
                    }
                    adapter.notifyDataSetChanged();
                });
    }

    private void onRequestItemClick(ApprovalRequest request) {
        Intent intent = new Intent(getContext(), RequestDetailActivity.class);
        intent.putExtra("requestId", request.getId());
        startActivity(intent);
    }
}