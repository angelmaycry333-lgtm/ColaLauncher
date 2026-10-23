package com.cola.launcher;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private int dp(float value) {
        return (int) (value * getResources()
                .getDisplayMetrics().density);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER);
        layout.setPadding(dp(24), dp(24), dp(24), dp(24));
        layout.setBackgroundColor(Color.rgb(22, 24, 28));

        TextView title = new TextView(this);
        title.setText("🥤 ColaLauncher");
        title.setTextSize(30);
        title.setTextColor(Color.WHITE);
        title.setGravity(Gravity.CENTER);

        TextView version = new TextView(this);
        version.setText("Minecraft Java Edition 1.8.9");
        version.setTextSize(18);
        version.setTextColor(Color.LTGRAY);
        version.setGravity(Gravity.CENTER);

        Button launch = new Button(this);
        launch.setText("ЗАПУСТИТЬ MINECRAFT");
        launch.setOnClickListener(v ->
            Toast.makeText(this,
                "Игровой движок пока разрабатывается!",
                Toast.LENGTH_LONG).show()
        );

        layout.addView(title, new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, dp(70)));
        layout.addView(version, new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, dp(60)));
        layout.addView(launch, new LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, dp(60)));

        setContentView(layout);
    }
}
