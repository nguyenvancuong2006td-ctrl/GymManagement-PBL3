package util;

import business.InvoiceBUS;
import data.MembershipPackageDAO;
import data.ProductDAO;
import data.PTServiceDAO;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import model.Invoice;
import model.InvoiceDetail;
import model.Payment;

import java.awt.Desktop;
import java.io.File;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class InvoicePDFExporter {

    private static final InvoiceBUS bus = new InvoiceBUS();

    // ✅ DAO để lấy tên item
    private static final ProductDAO productDAO = new ProductDAO();
    private static final MembershipPackageDAO packageDAO = new MembershipPackageDAO();
    private static final PTServiceDAO ptServiceDAO = new PTServiceDAO();

    public static void export(int invoiceID) {

        try {
            Invoice invoice = bus.getInvoiceByID(invoiceID);
            List<InvoiceDetail> details = bus.getInvoiceDetails(invoiceID);
            List<Payment> payments = bus.getPayments(invoiceID);

            String fileName = "Invoice_" + invoiceID + ".pdf";

            PdfWriter writer = new PdfWriter(fileName);
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

            DateTimeFormatter dtf =
                    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

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
            doc.add(new Paragraph("CHI TIẾT HÓA ĐƠN #" + invoiceID)
                    .setFont(bold)
                    .setFontSize(16)
                    .setTextAlignment(TextAlignment.CENTER));

            doc.add(new Paragraph("\n"));

            /* ================= INVOICE INFO ================= */
            Table info = new Table(2).setWidth(UnitValue.createPercentValue(100));

            info.addCell(infoCell("Ngày lập:", bold));
            info.addCell(infoCell(invoice.getInvoiceDate().format(dtf), normal));

            info.addCell(infoCell("Nhân viên:", bold));
            info.addCell(infoCell(invoice.getStaffName(), normal));

            info.addCell(infoCell("Khách hàng:", bold));
            info.addCell(infoCell(bus.getMemberName(invoiceID), normal));

            doc.add(info);
            doc.add(new Paragraph("\n"));

            /* ================= PAYMENT INFO ================= */
            if (!payments.isEmpty()) {
                Payment p = payments.get(0);

                Table payInfo = new Table(2).setWidth(UnitValue.createPercentValue(100));

                payInfo.addCell(infoCell("Phương thức:", bold));
                payInfo.addCell(infoCell(p.getPaymentMethod(), normal));

                payInfo.addCell(infoCell("Trạng thái:", bold));
                payInfo.addCell(infoCell(p.getStatus(), normal));

                doc.add(payInfo);
                doc.add(new Paragraph("\n"));
            }

            /* ================= DETAIL TABLE ================= */
            Table table = new Table(new float[]{1, 4, 1, 2, 2});
            table.setWidth(UnitValue.createPercentValue(100));

            table.addHeaderCell(header("STT", bold));
            table.addHeaderCell(header("Tên dịch vụ / sản phẩm", bold));
            table.addHeaderCell(header("SL", bold));
            table.addHeaderCell(header("Đơn giá", bold));
            table.addHeaderCell(header("Thành tiền", bold));

            int stt = 1;
            BigDecimal total = BigDecimal.ZERO;

            for (InvoiceDetail d : details) {
                BigDecimal lineTotal =
                        d.getPrice().multiply(BigDecimal.valueOf(d.getQuantity()));
                total = total.add(lineTotal);

                table.addCell(data(String.valueOf(stt++), normal));
                table.addCell(data(getItemName(d), normal));
                table.addCell(data(String.valueOf(d.getQuantity()), normal));
                table.addCell(data(formatMoney(d.getPrice()), normal));
                table.addCell(data(formatMoney(lineTotal), normal));
            }

            doc.add(table);
            doc.add(new Paragraph("\n"));

            /* ================= TOTAL ================= */
            doc.add(new Paragraph("TỔNG CỘNG: " + formatMoney(total))
                    .setFont(bold)
                    .setFontSize(14)
                    .setTextAlignment(TextAlignment.RIGHT));

            doc.add(new Paragraph("\n\n"));

            doc.add(new Paragraph("Người lập hóa đơn").setFont(normal));
            doc.add(new Paragraph(invoice.getStaffName()).setFont(bold));

            doc.close();

            Desktop.getDesktop().open(new File(fileName));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* ================= ITEM NAME ================= */
    private static String getItemName(InvoiceDetail d) {

        switch (d.getItemType()) {
            case "PRODUCT":
                return productDAO.getNameByID(d.getItemID());

            case "PACKAGE":
                return packageDAO.getNameByID(d.getItemID());

            case "PT":
                return ptServiceDAO.getNameByID(d.getItemID());

            default:
                return "Không xác định";
        }
    }

    /* ================= UTIL ================= */
    private static Cell infoCell(String text, PdfFont font) {
        return new Cell()
                .add(new Paragraph(text).setFont(font))
                .setBorder(Border.NO_BORDER);
    }

    private static Cell header(String text, PdfFont font) {
        return new Cell()
                .add(new Paragraph(text).setFont(font))
                .setTextAlignment(TextAlignment.CENTER);
    }

    private static Cell data(String text, PdfFont font) {
        return new Cell()
                .add(new Paragraph(text).setFont(font))
                .setTextAlignment(TextAlignment.CENTER);
    }

    private static String formatMoney(BigDecimal money) {
        return String.format("%,d đ", money.longValue());
    }
}
