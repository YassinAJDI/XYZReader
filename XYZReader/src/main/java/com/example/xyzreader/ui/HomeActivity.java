package com.example.xyzreader.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;

import com.example.xyzreader.R;
import com.example.xyzreader.utils.Injection;
import com.example.xyzreader.utils.ViewModelFactory;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public static ArticlesViewModel obtainViewModel(FragmentActivity activity) {
        ViewModelFactory factory = Injection.provideViewModelFactory(activity);
        return ViewModelProviders.of(activity, factory).get(ArticlesViewModel.class);
    }
}
