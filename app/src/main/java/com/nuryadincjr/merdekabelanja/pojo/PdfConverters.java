package com.nuryadincjr.merdekabelanja.pojo;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PdfConverters {
    @SuppressLint("StaticFieldLeak")
    public static PdfConverters pdfDocument;
    public Context context;
    private final String pdfPAth = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();

    public PdfConverters(Context context ) {
        this.context = context;
    }

    public static PdfConverters getInstance(Context context ) {
        if (pdfDocument == null) {
            pdfDocument = new PdfConverters(context);
        }
        return pdfDocument;
    }


    public void getDataToPdf(LinearLayout layout, String fileName) {
        DisplayMetrics displaymetrics = context.getResources().getDisplayMetrics();
        int convertHeight = displaymetrics.heightPixels;
        int convertWidth = displaymetrics.widthPixels;

        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(
                convertWidth, convertHeight, 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);

        Bitmap bitmap = Bitmap.createBitmap(layout.getWidth(), layout.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        layout.draw(canvas);

        Bitmap bitmaps = Bitmap.createScaledBitmap(bitmap, convertWidth, convertHeight, true);

        Paint paint = new Paint();
        paint.setColor(Color.WHITE);

        canvas = page.getCanvas();
        canvas.drawPaint(paint);
        canvas.drawBitmap(bitmaps, 0, 0, null);

        document.finishPage(page);

        File file = new File(pdfPAth + "/" + fileName +".pdf");

        try {
            document.writeTo(new FileOutputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "Something wrong: " + e.toString(), Toast.LENGTH_LONG).show();
        }

        document.close();

        Snackbar.make(layout, "Location: "+ file.getPath(), Snackbar.LENGTH_LONG)
                .setAction("Open", view -> openPdf(fileName)).setDuration(30000).show();
    }

    private void openPdf(String fileName) {

        File file = new File(pdfPAth + "/" + fileName +".pdf");
        if (file.exists()) {
            Intent intent = new Intent(Intent.ACTION_VIEW);

            Uri uri = FileProvider.getUriForFile(context, context.getPackageName()  + ".provider", file);
            intent.setDataAndType(uri, "application/pdf");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            try {
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context, "No Application for pdf view", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
