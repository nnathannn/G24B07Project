package com.example.smartair;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class PDFExportButton {


    public static void exportHistory(
            Context context,
            String childName,
            String filterType,
            String startDate,
            String endDate,
            List<Item> items
    ) {
        if (context == null) return;

        if (items == null || items.isEmpty()) {
            Toast.makeText(context, "No data to export", Toast.LENGTH_SHORT).show();
            return;
        }

        final int pageWidth = 595;
        final int pageHeight = 842;
        final int margin = 40;

        PdfDocument pdfDocument = new PdfDocument();

        Paint titlePaint = new Paint();
        titlePaint.setTextSize(18f);
        titlePaint.setFakeBoldText(true);

        Paint subtitlePaint = new Paint();
        subtitlePaint.setTextSize(12f);

        Paint textPaint = new Paint();
        textPaint.setTextSize(11f);

        int currentY = margin + 20;
        int pageNumber = 1;

        PdfDocument.PageInfo pageInfo =
                new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        //header
        canvas.drawText("History Export", margin, currentY, titlePaint);
        currentY += 20;

        if (childName != null && !childName.isEmpty()) {
            canvas.drawText("Child: " + childName, margin, currentY, subtitlePaint);
            currentY += 16;
        }

        if (filterType != null && !filterType.isEmpty()) {
            canvas.drawText("Filter: " + filterType, margin, currentY, subtitlePaint);
            currentY += 16;
        }

        canvas.drawText("Date range: " + startDate + " to " + endDate,
                margin, currentY, subtitlePaint);
        currentY += 24;


        int tableWidth = pageWidth - 2 * margin;
        int colWidth = tableWidth / 4;

        int colX0 = margin;
        int colX1 = margin + colWidth;
        int colX2 = margin + 2 * colWidth;
        int colX3 = margin + 3 * colWidth;


        String header0 = "Date";
        String header1 = "Col 2";
        String header2 = "Col 3";
        String header3 = "Col 4";

        int startIndex = 0;
        Item first = items.get(0);
        if (first instanceof AdapterHistory.HistoryItem) {
            AdapterHistory.HistoryItem h = (AdapterHistory.HistoryItem) first;
            header0 = safe(h.getDate());      //Date
            header1 = safe(h.getChildId());   //PEF
            header2 = safe(h.field1);         //PB
            header3 = safe(h.field2);         //Status

            startIndex = 1;
        }


        textPaint.setFakeBoldText(true);
        canvas.drawText(header0, colX0, currentY, textPaint);
        canvas.drawText(header1, colX1, currentY, textPaint);
        canvas.drawText(header2, colX2, currentY, textPaint);
        canvas.drawText(header3, colX3, currentY, textPaint);
        textPaint.setFakeBoldText(false);

        currentY += 18;
        int rowHeight = 18;

        try {
            for (int i = startIndex; i < items.size(); i++) {
                Item baseItem = items.get(i);
                if (!(baseItem instanceof AdapterHistory.HistoryItem)) {
                    continue;
                }

                AdapterHistory.HistoryItem item = (AdapterHistory.HistoryItem) baseItem;

                //new page
                if (currentY + rowHeight > pageHeight - margin) {
                    pdfDocument.finishPage(page);
                    pageNumber++;
                    pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create();
                    page = pdfDocument.startPage(pageInfo);
                    canvas = page.getCanvas();
                    currentY = margin;


                    canvas.drawText("History Export", margin, currentY, titlePaint);
                    currentY += 24;


                    textPaint.setFakeBoldText(true);
                    canvas.drawText(header0, colX0, currentY, textPaint);
                    canvas.drawText(header1, colX1, currentY, textPaint);
                    canvas.drawText(header2, colX2, currentY, textPaint);
                    canvas.drawText(header3, colX3, currentY, textPaint);
                    textPaint.setFakeBoldText(false);

                    currentY += 18;
                }

                String col0 = safe(item.getDate());
                String col1 = safe(item.getChildId());
                String col2 = safe(item.field1);
                String col3 = safe(item.field2);

                canvas.drawText(col0, colX0, currentY, textPaint);
                canvas.drawText(col1, colX1, currentY, textPaint);
                canvas.drawText(col2, colX2, currentY, textPaint);
                canvas.drawText(col3, colX3, currentY, textPaint);

                currentY += rowHeight;
            }

            pdfDocument.finishPage(page);

            //saving PDF
            String safeChild = (childName == null || childName.isEmpty())
                    ? "all"
                    : childName.replaceAll("\\s+", "_");
            String safeFilter = (filterType == null || filterType.isEmpty())
                    ? "all"
                    : filterType.replaceAll("\\s+", "_");

            String fileName = "history_"
                    + safeChild + "_"
                    + safeFilter + "_"
                    + startDate + "_to_" + endDate
                    + ".pdf";

            fileName = fileName.replaceAll("[^A-Za-z0-9._-]", "_");

            File downloadsDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS
            );
            File dir = new File(downloadsDir, "exports");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            File pdfFile = new File(dir, fileName);

            Log.d("PDF_EXPORT", "Saving to: " + pdfFile.getAbsolutePath());
            Toast.makeText(context,
                    "Exported to:\n" + pdfFile.getAbsolutePath(),
                    Toast.LENGTH_LONG
            ).show();


            FileOutputStream out = new FileOutputStream(pdfFile);
            pdfDocument.writeTo(out);
            out.close();
            pdfDocument.close();


            Toast.makeText(context,
                    "Exported to: " + pdfFile.getAbsolutePath(),
                    Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            try { pdfDocument.close(); } catch (Exception ignored) {}
            Toast.makeText(context,
                    "Failed to export PDF: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }
    public static void exportProviderReport(Context context, ProviderReportData data) {
        if (context == null || data == null) return;

        final int pageWidth = 595;
        final int pageHeight = 842;
        final int margin = 40;

        PdfDocument pdf = new PdfDocument();

        Paint titlePaint = new Paint();
        titlePaint.setTextSize(18f);
        titlePaint.setFakeBoldText(true);

        Paint subtitlePaint = new Paint();
        subtitlePaint.setTextSize(13f);
        subtitlePaint.setFakeBoldText(true);

        Paint textPaint = new Paint();
        textPaint.setTextSize(11f);

        int pageNumber = 1;
        PdfDocument.PageInfo pageInfo =
                new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create();
        PdfDocument.Page page = pdf.startPage(pageInfo);
        Canvas canvas = page.getCanvas();
        int y = margin;



        canvas.drawText("Provider Report", margin, y, titlePaint);
        y += 20;
        canvas.drawText("Child: " + safe(data.childName), margin, y, textPaint);
        y += 14;
        canvas.drawText("Provider: " + safe(data.providerName), margin, y, textPaint);
        y += 14;
        canvas.drawText("Range: " + safe(data.startDate) + " to " + safe(data.endDate),
                margin, y, textPaint);
        y += 24;

        pdf.finishPage(page);

        try {
            String safeChild = (data.childName == null || data.childName.isEmpty())
                    ? "child"
                    : data.childName.replaceAll("\\s+", "_");

            String timeStamp = String.valueOf(System.currentTimeMillis());

            String fileName = "provider_report_"
                    + safeChild + "_"
                    + safe(data.startDate) + "_to_" + safe(data.endDate) + "_"
                    + timeStamp
                    + ".pdf";

            fileName = fileName.replaceAll("[^A-Za-z0-9._-]", "_");

            File downloadsDir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS
            );
            File dir = new File(downloadsDir, "exports");
            if (!dir.exists()) {
                boolean ok = dir.mkdirs();
                Log.d("PDF_EXPORT_PROVIDER", "mkdirs: " + ok + " at " + dir.getAbsolutePath());
            }

            File pdfFile = new File(dir, fileName);
            Log.d("PDF_EXPORT_PROVIDER", "Writing file: " + pdfFile.getAbsolutePath());

            FileOutputStream out = new FileOutputStream(pdfFile);
            pdf.writeTo(out);
            out.close();
            pdf.close();

            Toast.makeText(context,
                    "Exported to:\n" + pdfFile.getAbsolutePath(),
                    Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e("PDF_EXPORT_PROVIDER", "Error writing PDF", e);
            try { pdf.close(); } catch (Exception ignored) {}
            Toast.makeText(context,
                    "Failed to export PDF: " + e.getMessage(),
                    Toast.LENGTH_LONG).show();
        }
    }



    private static String safe(String s) {
        return s == null ? "" : s;
    }

    private static int drawSectionTitle(Canvas canvas,
                                        String title,
                                        int x,
                                        int y,
                                        Paint paint) {
        paint.setFakeBoldText(true);
        canvas.drawText(title, x, y, paint);
        paint.setFakeBoldText(false);
        return y + 18;
    }

    private static int drawSymptomBarChart(Canvas canvas,
                                           int startX,
                                           int startY,
                                           int width,
                                           int height,
                                           List<ProviderReportData.CategoryCount> data) {
        if (data == null || data.isEmpty()) return startY;

        Paint axisPaint = new Paint();
        axisPaint.setColor(Color.BLACK);
        axisPaint.setStrokeWidth(1.5f);

        Paint barPaint = new Paint();
        barPaint.setColor(Color.rgb(80, 140, 220));

        Paint labelPaint = new Paint();
        labelPaint.setTextSize(9f);

        int bottomY = startY + height;
        int leftX = startX;
        int rightX = startX + width;

        canvas.drawLine(leftX, bottomY, rightX, bottomY, axisPaint); // X-axis
        canvas.drawLine(leftX, bottomY, leftX, startY, axisPaint);   // Y-axis

        int max = 0;
        for (ProviderReportData.CategoryCount c : data) {
            if (c.count > max) max = c.count;
        }
        if (max == 0) max = 1;

        int n = data.size();
        float barSpace = width / (float) (n * 2);
        float barWidth = barSpace;

        for (int i = 0; i < n; i++) {
            ProviderReportData.CategoryCount c = data.get(i);

            float centerX = leftX + barSpace * (1 + i * 2);
            float barTop = bottomY - (c.count / (float) max) * (height - 20);
            float halfBar = barWidth / 2f;

            canvas.drawRect(centerX - halfBar, barTop,
                    centerX + halfBar, bottomY, barPaint);

            String label = c.label;
            if (label.length() > 6) label = label.substring(0, 6);

            canvas.drawText(label,
                    centerX - (labelPaint.measureText(label) / 2f),
                    bottomY + 10,
                    labelPaint);
        }

        return bottomY + 24;
    }

    private static int drawZoneTimeSeriesChart(Canvas canvas,
                                               int startX,
                                               int startY,
                                               int width,
                                               int height,
                                               List<ProviderReportData.DailyZonePoint> data) {
        if (data == null || data.isEmpty()) return startY;

        Paint axisPaint = new Paint();
        axisPaint.setColor(Color.BLACK);
        axisPaint.setStrokeWidth(1.5f);

        Paint linePaint = new Paint();
        linePaint.setColor(Color.rgb(80, 140, 220));
        linePaint.setStrokeWidth(1.7f);

        Paint pointPaint = new Paint();
        pointPaint.setColor(Color.rgb(20, 100, 200));

        Paint labelPaint = new Paint();
        labelPaint.setTextSize(9f);

        int bottomY = startY + height;
        int leftX = startX;
        int rightX = startX + width;

        canvas.drawLine(leftX, bottomY, rightX, bottomY, axisPaint); // X-axis
        canvas.drawLine(leftX, bottomY, leftX, startY, axisPaint);   // Y-axis

        int n = data.size();
        if (n == 1) n = 2;

        float xStep = width / (float) (n - 1);
        float maxYValue = 100f;

        float prevX = -1;
        float prevY = -1;

        for (int i = 0; i < data.size(); i++) {
            ProviderReportData.DailyZonePoint p = data.get(i);
            float x = leftX + i * xStep;
            float y = bottomY - (float) (p.greenPercent / maxYValue) * (height - 20);

            if (prevX >= 0) {
                canvas.drawLine(prevX, prevY, x, y, linePaint);
            }
            canvas.drawCircle(x, y, 2.5f, pointPaint);

            prevX = x;
            prevY = y;
        }

        ProviderReportData.DailyZonePoint first = data.get(0);
        ProviderReportData.DailyZonePoint last = data.get(data.size() - 1);
        String firstLabel = safe(first.date);
        String lastLabel = safe(last.date);

        canvas.drawText(firstLabel, leftX, bottomY + 10, labelPaint);
        float lastLabelWidth = labelPaint.measureText(lastLabel);
        canvas.drawText(lastLabel,
                rightX - lastLabelWidth,
                bottomY + 10,
                labelPaint);

        canvas.drawText("0%", leftX - 25, bottomY, labelPaint);
        canvas.drawText("100%", leftX - 30, startY + 5, labelPaint);

        return bottomY + 28;
    }
}