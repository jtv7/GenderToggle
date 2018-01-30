package com.jtv7.gendertoggle;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.jtv7.gendertogglelib.GenderToggle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GenderToggle genderToggle = findViewById(R.id.genderToggle);
        genderToggle.setChecked(GenderToggle.Checked.MALE); // Sets without animation
        genderToggle.useHardwareAcceleration(); // Provides smoother animation on slower devices. Glow will not work with this enabled!
        genderToggle.setCheckedChangeListener(new GenderToggle.GenderCheckedChangeListener() {
            @Override
            public void onCheckChanged(GenderToggle.Checked current) {
                if (current == GenderToggle.Checked.MALE) {
                    Toast.makeText(getApplicationContext(), "Male is checked", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Female is checked", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
