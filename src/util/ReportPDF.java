package util;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import javax.swing.table.TableModel;
import java.awt.Desktop;
import java.io.File;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ReportPDF {

    public static void exportTable(
            String filePath,
            String reportTitle,
            String reportScope,
            TableModel model
    ) {

        try {
            PdfWriter writer = new PdfWriter(filePath);
            PdfDocument pdf = new PdfDocument(writer);
            Document doc = new Document(pdf);

            PdfFont normal = PdfFontFactory.createFont(
                    "c:/windows/fonts/times.ttf",
                    PdfEncodings.IDENTITY_H
            );
            PdfFont bold = PdfFontFactory.createFont(
                    "c:/windows/fonts/timesbd.ttf",
                    PdfEncodings.IDENTITY_H
            );

            NumberFormat moneyFmt =
                    NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
            SimpleDateFormat exportTime =
                    new SimpleDateFormat("dd/MM/yyyy HH:mm");

            /* ================= HEADER ================= */

            doc.add(new Paragraph("PHÒNG GYM ABC")
                    .setFont(bold)
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.CENTER));

            doc.add(new Paragraph("Địa chỉ: 123 Nguyễn Văn Cừ, Quận 5, TP.HCM")
                    .setFont(normal)
                    .setTextAlignment(TextAlignment.CENTER));

            doc.add(new Paragraph("Điện thoại: 0909 123 456")
                    .setFont(normal)
                    .setTextAlignment(TextAlignment.CENTER));

            doc.add(new Paragraph()
                    .setBorderBottom(new SolidBorder(1))
                    .setMarginTop(5)
                    .setMarginBottom(10));

            /* ================= TITLE ================= */

            doc.add(new Paragraph(reportTitle)
                    .setFont(bold)
                    .setFontSize(16)
                    .setTextAlignment(TextAlignment.CENTER));

            doc.add(new Paragraph("Phạm vi báo cáo: " + reportScope)
                    .setFont(normal)
                    .setTextAlignment(TextAlignment.CENTER));

            doc.add(new Paragraph("Thời điểm xuất: " + exportTime.format(new Date()))
                    .setFont(normal)
                    .setTextAlignment(TextAlignment.CENTER));

            doc.add(new Paragraph("\n"));

            /* ================= TABLE ================= */

            Table table = new Table(model.getColumnCount())
                    .setWidth(UnitValue.createPercentValue(100));

            // Header
            for (int c = 0; c < model.getColumnCount(); c++) {
                table.addHeaderCell(
                        new Cell()
                                .add(new Paragraph(model.getColumnName(c))
                                        .setFont(bold))
                                .setTextAlignment(TextAlignment.CENTER)
                );
            }

            double totalRevenue = 0;

            // Data
            for (int r = 0; r < model.getRowCount(); r++) {
                for (int c = 0; c < model.getColumnCount(); c++) {
                    Object value = model.getValueAt(r, c);
                    String display;

                    if (value instanceof Number && c == model.getColumnCount() - 1) {
                        totalRevenue += ((Number) value).doubleValue();
                        display = moneyFmt.format(value);
                    } else {
                        display = value == null ? "" : value.toString();
                    }

                    table.addCell(new Cell()
                            .add(new Paragraph(display).setFont(normal))
                            .setTextAlignment(TextAlignment.CENTER));
                }
            }

            doc.add(table);
            doc.add(new Paragraph("\n"));

            /* ================= SUMMARY ================= */

            doc.add(new Paragraph("Tổng số dòng: " + model.getRowCount())
                    .setFont(normal));

            if (totalRevenue > 0) {
                doc.add(new Paragraph("TỔNG DOANH THU: " + moneyFmt.format(totalRevenue))
                        .setFont(bold)
                        .setFontSize(14)
                        .setTextAlignment(TextAlignment.RIGHT));
            }

            doc.add(new Paragraph("\n\n"));
            doc.add(new Paragraph("Người lập báo cáo").setFont(normal));
            doc.add(new Paragraph("______________________").setFont(normal));

            doc.close();
            Desktop.getDesktop().open(new File(filePath));

        } catch (Exception e) {
            throw new RuntimeException("Lỗi xuất PDF báo cáo", e);
        }
    }
}
