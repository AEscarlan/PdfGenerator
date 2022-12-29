package com.example.generatepdf;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.w3c.dom.Document;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    Button btn;
    Bitmap bmp, scaledBitmap;

    private static final int PERMISSION_REQUEST_CODE = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn = findViewById(R.id.btn);

        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.villa_filomena_logo);
        scaledBitmap = Bitmap.createScaledBitmap(bmp, 140, 140, false);

        if (checkPermission()) {
            Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
        } else {
            requestPermission();
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generatePDF();
            }
        });

    }

    private void generatePDF(){

        //1 inch = 72 points so 1 * 72
        int width = 612;
        int height = 792;

        String Fname = "/Receipts";

        PdfDocument document = new PdfDocument();

        Paint paint = new Paint();
        Paint titlePaint = new Paint();

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(width, height, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        Matrix matrix = new Matrix();
        float scaleFactor = Math.min((float) 140 / bmp.getWidth(), (float) 140 / bmp.getHeight());
        bmp.getHeight();
        matrix.setScale(scaleFactor, scaleFactor);
        matrix.postTranslate(10, 25);

        canvas.drawBitmap(bmp, matrix, paint);

        titlePaint.setTextAlign(Paint.Align.LEFT);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        titlePaint.setTextSize(16f);
        canvas.drawText("Villa Filomena Natural Spring Resort", 130, 80, titlePaint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(12f);
        paint.setColor(Color.BLACK);
        canvas.drawText("Purok 2 Kaytimbog, Indang, Cavite", 130, 100, paint);
        canvas.drawText("09391136357", 130, 115, paint);

        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setTextSize(12f);
        paint.setColor(Color.BLACK);
        canvas.drawText("Receipt Date", width-20, 80, paint);
        canvas.drawText("Online Booking", width-20, 100, paint);

        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        titlePaint.setTextSize(20f);
        canvas.drawText("Receipt", width/2, 160, titlePaint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(14f);
        paint.setColor(Color.BLACK);
        canvas.drawText("Guest Name", 50, 200, paint);
        canvas.drawText("No. of People", 50, 220, paint);
        canvas.drawText("Check-in: (date and time)", 50, 240, paint);
        canvas.drawText("Check-out: (date and time)", 50, 260, paint);

        titlePaint.setTextAlign(Paint.Align.LEFT);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        titlePaint.setTextSize(14f);
        canvas.drawText("Description", 50, 300, titlePaint);

        titlePaint.setTextAlign(Paint.Align.CENTER);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        titlePaint.setTextSize(14f);
        canvas.drawText("Quantity", width/2, 300, titlePaint);

        titlePaint.setTextAlign(Paint.Align.RIGHT);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        titlePaint.setTextSize(14f);
        canvas.drawText("Price", width-50, 300, titlePaint);

        paint.setTextAlign(Paint.Align.LEFT);
        paint.setTextSize(14f);
        paint.setColor(Color.BLACK);
        canvas.drawText("Adult", 50, 320, paint);
        canvas.drawText("Kid", 50, 340, paint);
        canvas.drawText("Room Details", 50, 360, paint);
        canvas.drawText("Cottage Details", 50, 380, paint);

        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTextSize(14f);
        paint.setColor(Color.BLACK);
        canvas.drawText("0", width/2, 320, paint);
        canvas.drawText("0", width/2, 340, paint);
        canvas.drawText("0", width/2, 360, paint);
        canvas.drawText("0", width/2, 380, paint);

        paint.setTextAlign(Paint.Align.RIGHT);
        paint.setTextSize(14f);
        paint.setColor(Color.BLACK);
        canvas.drawText("0", width-50, 320, paint);
        canvas.drawText("0", width-50, 340, paint);
        canvas.drawText("0", width-50, 360, paint);
        canvas.drawText("0", width-50, 380, paint);

        titlePaint.setTextAlign(Paint.Align.LEFT);
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        titlePaint.setTextSize(16f);
        canvas.drawText("Total Amount: ", 50, 500, titlePaint);
        canvas.drawText("Payment Method: GCash", 50, 520, titlePaint);
        canvas.drawText("Reference Number", 50, 540, titlePaint);
        canvas.drawText("Paid: ", 50, 560, titlePaint);
        canvas.drawText("Balance: ", 50, 580, titlePaint);

        document.finishPage(page);

        String folder_name = "Receipts";
        File f = new File(Environment.getExternalStorageDirectory(), folder_name);
        if (!f.exists()) {
            f.mkdirs();
        }

        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/"+folder_name;

        File file = new File(path, "Receipt1.pdf");

        try {
            document.writeTo(new FileOutputStream(file));
            Toast.makeText(MainActivity.this, "successful", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        document.close();

    }

    private boolean checkPermission() {
        // checking of permissions.
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        // requesting permissions if not provided.
        ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0) {

                // after requesting permissions we are showing
                // users a toast message of permission granted.
                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                if (writeStorage && readStorage) {
                    Toast.makeText(this, "Permission Granted..", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denied.", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }
}