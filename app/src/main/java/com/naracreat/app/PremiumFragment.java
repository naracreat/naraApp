package com.naracreat.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PremiumFragment extends Fragment {

    public PremiumFragment() { super(R.layout.fragment_premium); }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Button btn = view.findViewById(R.id.btnOpenPremium);
        btn.setOnClickListener(v -> startActivity(new Intent(requireContext(), PremiumActivity.class)));
    }
}
