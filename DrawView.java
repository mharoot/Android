package com.example.michael.drawrectangle;

/**
 * Created by michael on 1/26/17.
 */
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class DrawView extends View {
    Paint paint = new Paint();

    public DrawView(Context context) {
        super(context);
    }

    @Override
    public void onDraw(Canvas canvas) {
        // Draw The Rectangle Frame
        paint.setStrokeWidth(3);
        paint.setColor(Color.BLACK);
        canvas.drawRect(30, 30, 80, 80, paint);

        // Coloring we first change stroke width
        paint.setStrokeWidth(0);
        paint.setColor(Color.CYAN);
        canvas.drawRect(33, 60, 77, 77, paint );

        paint.setColor(Color.YELLOW);
        canvas.drawRect(33, 33, 77, 60, paint );


        // Draw The Rectangle Frame
        paint.setStrokeWidth(3);
        paint.setColor(Color.BLACK);
        canvas.drawRect(90, 90, 190, 190, paint);

    }

}
