package com.luvsic.demo.fragment;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.luvsic.demo.R;

public class ContainerActivity extends AppCompatActivity {
    private AFragment fragment_a;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);

        fragment_a = AFragment.newInstance("new instance a fragment");
        fragment_a.getArguments().getString("title");
        getSupportFragmentManager().beginTransaction().add(R.id.container, fragment_a, "a").commitAllowingStateLoss();
    }
}