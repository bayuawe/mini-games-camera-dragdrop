package com.uas.dragdrop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ActivityBerhasil extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 101;
    private static final int REQUEST_ACTIVITY_BERHASIL = 1;
    private static final int REQUEST_CAMERA_PERMISSION = 102;

    private ImageView imageView;
    private List<Step> steps = new ArrayList<>();
    private StepAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_berhasil);

        // Kode yang sudah ada sebelumnya
        Button btnKembali = findViewById(R.id.btnKembali);
        btnKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Kembalikan hasil kembali ke MainActivity dengan nilai RESULT_CANCELED
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        Button btnCaptureImage = findViewById(R.id.btnCaptureImage);
        imageView = findViewById(R.id.imageView);

        btnCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        Button btnShareImage = findViewById(R.id.btnShareImage);
        btnShareImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareImage();
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StepAdapter(steps);
        recyclerView.setAdapter(adapter);

//        prepareData();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {
            @Override
            public boolean onMove(RecyclerView recyclerView,
                                  RecyclerView.ViewHolder viewHolder,
                                  RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();

                Collections.swap(steps, fromPosition, toPosition);
                adapter.notifyItemMoved(fromPosition, toPosition);

//                if (isUrutanBenar()) {
//                    Intent intent = new Intent(ActivityBerhasil.this, ActivityBerhasil.class);
//                    startActivityForResult(intent, REQUEST_ACTIVITY_BERHASIL);
//                    finish();
//                }

                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

            }
        });

        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Memeriksa izin kamera
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                // Jika izin belum diberikan, tampilkan permintaan izin
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.CAMERA},
                        REQUEST_CAMERA_PERMISSION);
            } else {
                // Jika izin sudah diberikan, mulai intent kamera
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            if (extras != null) {
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                imageView.setImageBitmap(imageBitmap);
            }
        } else if (requestCode == REQUEST_ACTIVITY_BERHASIL && resultCode == RESULT_OK) {
            resetGame();
        }
    }

    private void shareImage() {
        // Simpan gambar ke penyimpanan internal
        String fileName = "shared_image.jpg";
        File imagePath = new File(getFilesDir(), "images");
        if (!imagePath.exists()) {
            imagePath.mkdirs();
        }
        File imageFile = new File(imagePath, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(imageFile);
            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Bagikan gambar ke aplikasi lain
        Uri contentUri = FileProvider.getUriForFile(this, "com.uas.dragdrop.fileprovider", imageFile);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
        shareIntent.setType("image/jpeg");
        startActivity(Intent.createChooser(shareIntent, "Bagikan gambar dengan"));
    }

//    private void prepareData() {
//        steps.add(new Step("1. Rebus air hingga mendidih"));
//        steps.add(new Step("2. Masukkan mie ke dalam air mendidih selama 3 menit"));
//        steps.add(new Step("3. Campurkan bumbu mie ke dalam mangkuk"));
//        steps.add(new Step("4. Tambahkan air panas ke dalam mangkuk"));
//        steps.add(new Step("5. Aduk hingga bumbu larut dan mie siap dinikmati"));
//        Collections.shuffle(steps);
//        adapter.notifyDataSetChanged();
//    }
//
//    private boolean isUrutanBenar() {
//        for (int i = 0; i < steps.size(); i++) {
//            String stepText = steps.get(i).getStepText();
//            if (!stepText.matches("\\d+.*")) {
//                return false;
//            }
//            int stepNumber = Integer.parseInt(stepText.split("\\.")[0]);
//            if (i + 1 != stepNumber) {
//                return false;
//            }
//        }
//        return true;
//    }

    public void resetGame() {
        steps.clear();
//        prepareData();
    }
}
