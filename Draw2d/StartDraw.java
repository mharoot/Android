package com.example.michael.drawrectangle;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.graphics.Color;

public class StartDraw extends AppCompatActivity {
        DrawView drawView;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            drawView = new DrawView(this);
            drawView.setBackgroundColor(Color.WHITE);
            setContentView(drawView);

        }
}
