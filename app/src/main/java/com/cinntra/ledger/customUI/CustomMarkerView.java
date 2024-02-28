package com.cinntra.ledger.customUI;

import android.content.Context;
import android.graphics.Canvas;
import android.widget.TextView;

import com.cinntra.ledger.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;

import java.util.List;

public class CustomMarkerView extends MarkerView {

    private TextView tvContent;
    private List<String> dayList;

    public CustomMarkerView(Context context, int layoutResource, List<String> dayList) {
        super(context, layoutResource);
        this.dayList = dayList;
        tvContent = findViewById(R.id.tvContent);
    }

    // This method will be called every time the MarkerView is redrawn
    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        // You can customize the content based on the Entry object
        if (e instanceof BarEntry) {
            BarEntry barEntry = (BarEntry) e;
            int index = (int) barEntry.getX();

            // Check if the index is within bounds
            if (index >= 0 && index < dayList.size()) {
                tvContent.setText("Venue: " + dayList.get(index) + "\nValue: " + barEntry.getY());
            } else {
                tvContent.setText("Value: " + barEntry.getY());
            }
        }

        // Call the super method for default implementation
        super.refreshContent(e, highlight);
    }

    // This method will be called when the MarkerView is drawn on the chart
    @Override
    public void draw(Canvas canvas, float posX, float posY) {
        // Ensure the MarkerView is drawn within the chart's bounds
        float offset = -getWidth() / 2f;

        // Call the super method for default implementation
        super.draw(canvas, posX + offset, posY);
    }
}
