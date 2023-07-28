package com.uas.dragdrop;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ActivityBerhasil extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_berhasil);

        Button btnKembali = findViewById(R.id.btnKembali);
        btnKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kembalikan hasil kembali ke MainActivity dengan nilai RESULT_CANCELED
                setResult(RESULT_CANCELED);
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Kembalikan hasil kembali ke MainActivity dengan nilai RESULT_CANCELED saat tombol kembali ditekan
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}
