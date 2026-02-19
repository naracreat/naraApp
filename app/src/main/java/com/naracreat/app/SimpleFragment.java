package com.naracreat.app;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SimpleFragment extends Fragment {

    private final String text;

    public SimpleFragment(String text) {
        super();
        this.text = text;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull android.view.LayoutInflater inflater,
                             @Nullable android.view.ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        TextView tv = new TextView(requireContext());
        tv.setText(text);
        tv.setTextColor(getResources().getColor(R.color.text));
        tv.setTextSize(18f);
        tv.setGravity(Gravity.CENTER);
        tv.setBackgroundColor(getResources().getColor(R.color.bg));
        return tv;
    }
}
