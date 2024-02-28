package com.cinntra.ledger.graph;

import com.github.mikephil.charting.formatter.ValueFormatter;

public class MonthAxisValueFormatter extends ValueFormatter {
    private final String[] months = new String[] {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    };

    @Override
    public String getFormattedValue(float value) {
        int index = (int) value;
        if (index >= 0 && index < months.length) {
            return months[index];
        }
        return "";
    }
}
