package com.example.myfood.activities;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.myfood.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        ThemeHelper.applyTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.settings_title);
        }

        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        RadioGroup radioGroup = findViewById(R.id.radio_group_theme);
        RadioButton radioSystem = findViewById(R.id.radio_theme_system);
        RadioButton radioLight = findViewById(R.id.radio_theme_light);
        RadioButton radioDark = findViewById(R.id.radio_theme_dark);

        int currentMode = ThemeHelper.getSavedThemeMode(this);
        if (currentMode == ThemeHelper.MODE_LIGHT) {
            radioLight.setChecked(true);
        } else if (currentMode == ThemeHelper.MODE_DARK) {
            radioDark.setChecked(true);
        } else {
            radioSystem.setChecked(true);
        }

        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_theme_system) {
                ThemeHelper.saveThemeMode(this, ThemeHelper.MODE_SYSTEM);
            } else if (checkedId == R.id.radio_theme_light) {
                ThemeHelper.saveThemeMode(this, ThemeHelper.MODE_LIGHT);
            } else if (checkedId == R.id.radio_theme_dark) {
                ThemeHelper.saveThemeMode(this, ThemeHelper.MODE_DARK);
            }
        });
    }
}

