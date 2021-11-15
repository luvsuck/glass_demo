package com.luvsic.demo.fragment;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.luvsic.demo.R;

/**
 * @param
 * @author zyy
 * @description
 * @return
 * @time 2021/11/2 12:04
 */
public class AFragment extends Fragment {
    private Button btn1;
    private BFragment bFragment;

    public static AFragment newInstance(String title) {
        Bundle bundle = new Bundle();
        AFragment fragment = new AFragment();
        bundle.putString("title", title);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_a, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        btn1 = view.findViewById(R.id.chg);
        btn1.setOnClickListener(v -> {
            System.out.println("change event");
            if (bFragment == null)
                bFragment = new BFragment();
            assert getFragmentManager() != null;
            getFragmentManager().beginTransaction().replace(R.id.container, bFragment).addToBackStack(null).commitAllowingStateLoss();
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
    }
}