package com.example.recyle.PesanTiket;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.recyle.R;

public class CalenderActivity extends AppCompatActivity {
    private static final String TAG = "CalenderActivity";
    private CalendarView mCalendarView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_calender);
        mCalendarView = findViewById(R.id.calendarView);

        mCalendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                String date = (i1 + 1) + "/" + i2 + "/" + i;
                Log.d(TAG, "onSelectedDayChange: mm/yy/yyyy: " + date);

                Intent intent = new Intent(CalenderActivity.this, Tambah_Pesanan.class);
                intent.putExtra("date", date);
                startActivity(intent);
                finish();
            }
        });


    }
}
