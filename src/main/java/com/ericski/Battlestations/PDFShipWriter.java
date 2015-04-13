package com.ericski.Battlestations;

import com.ericski.graphics.BetterImageScaler;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.imageio.ImageIO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PDFShipWriter
{

    static final Logger logger = LogManager.getLogger(PDFShipWriter.class);

    public static void drawPDF(Ship ship, OutputStream outStream)
        throws IOException
    {
        drawPDF(ship, outStream, new PDFShipWriterOptions());
    }

    public static void drawPDF(List<Ship> ships, OutputStream outStream,
                               PDFShipWriterOptions options) throws IOException
    {
        BufferedImage logoImage = null;
        InputStream imageStream = Ship.class
            .getResourceAsStream("/com/ericski/Battlestations/Images/batstationlogo.gif");
        try
        {
            logoImage = ImageIO.read(imageStream);
        }
        catch (IOException e)
        {
            logger.warn("Couldn't load logo",e);
        }
        Rectangle pageSize;
        switch (options.getPageSize())
        {
            case 1:
                pageSize = PageSize.LEGAL;
                // imageSize = 567;
                break;
            default:
                pageSize = PageSize.LETTER;
                break;
        }
        Document document = new Document(pageSize);
        document.addAuthor("Eric's Shipbuilder");
        document.setMargins(25, 25, 10, 10);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try
        {

            PdfWriter w = PdfWriter.getInstance(document, baos);

            w.setFullCompression();
            document.open();

            for (Ship ship : ships)
            {
                System.out.println("\t" + ship.getName() + " " + ship.getSpecies() + " " + ship.getSize());
                int imageSize = 567;

                switch (options.getPageSize())
                {
                    default:
                        if (options.isShowGuns())
                        {
                            imageSize -= 14;
                        }
                        if (options.isShowSpeed())
                        {
                            imageSize -= 14;
                        }
                        if (options.isShowHelm())
                        {
                            imageSize -= 14;
                        }
                        if (options.isShowShield())
                        {
                            imageSize -= 14;
                        }
                        if (options.isShowOCC())
                        {
                            imageSize -= 14;
                        }
                        if (options.isDamageTrack())
                        {
                            imageSize -= 32;
                        }
                        if (options.isShowNotes() && ship.getNotes().size() > 0)
                        {
                            imageSize -= 64;
                        }
                        break;
                }
                BufferedImage shipImage = ship.generateImage();

                java.awt.Image scaledShipImage = shipImage;
                float qualReduction = options.getOutputQualityReduction();
                if (qualReduction > 0.0)
                {
                    int size = (int) (shipImage.getHeight() * (1.0 - qualReduction));
                    size = Math.max(size, imageSize); // don't scale smaller
                    // than what we're going
                    // to rescale it to
                    // anyhow
                    // System.out.println("Scaling ship image to " + size);
                    scaledShipImage = BetterImageScaler
                        .getFasterScaledInstance(shipImage, size, size,
                                                 RenderingHints.VALUE_INTERPOLATION_BICUBIC,
                                                 true);
                }

                try
                {
                    Paragraph p;

                    PdfPTable table = new PdfPTable(4);
                    table.setWidthPercentage(100);

                    p = new Paragraph("Name: " + ship.getName(), FontFactory
                                      .getFont(FontFactory.HELVETICA, 10, Font.NORMAL,
                                               Color.BLACK));
                    p.setAlignment(Paragraph.ALIGN_CENTER);
                    PdfPCell cell = new PdfPCell(p);
                    cell.setBorder(Rectangle.NO_BORDER);
                    table.addCell(cell);

                    p = new Paragraph("Registry: " + ship.getSpecies(),
                                      FontFactory.getFont(FontFactory.HELVETICA, 10,
                                                          Font.NORMAL, Color.BLACK));
                    p.setAlignment(Paragraph.ALIGN_CENTER);
                    cell = new PdfPCell(p);
                    cell.setBorder(Rectangle.NO_BORDER);
                    table.addCell(cell);

                    p = new Paragraph("Size: " + ship.getSize(), FontFactory
                                      .getFont(FontFactory.HELVETICA, 10, Font.NORMAL,
                                               Color.BLACK));
                    p.setAlignment(Paragraph.ALIGN_CENTER);
                    cell = new PdfPCell(p);
                    cell.setBorder(Rectangle.NO_BORDER);
                    table.addCell(cell);

                    if (logoImage != null)
                    {
                        com.lowagie.text.Image logoPdfImage = com.lowagie.text.Image
                            .getInstance(logoImage, null);
                        logoPdfImage.scalePercent(12f);
                        cell = new PdfPCell(logoPdfImage);
                        cell.setBorder(Rectangle.NO_BORDER);
                        table.addCell(cell);
                    }
                    table.setSpacingAfter(2);
                    document.add(table);

                    if (options.isShowNotes() && ship.getNotes().size() > 0)
                    {
                        p = new Paragraph("Notes: " + ship.getNotesAsString(),
                                          FontFactory.getFont(FontFactory.HELVETICA, 10,
                                                              Font.NORMAL, Color.BLACK));
                        document.add(p);
                    }

                    com.lowagie.text.Image shipPdfImage = com.lowagie.text.Image
                        .getInstance(scaledShipImage, null);

                    shipPdfImage.scaleAbsolute(imageSize, imageSize);
                    shipPdfImage.setAlignment(com.lowagie.text.Image.MIDDLE);

                    shipPdfImage.setSpacingAfter(2);
                    document.add(shipPdfImage);

                    document.add(new Paragraph(" "));

                    if (options.isShowSpeed())
                    {
                        Color speedColor = Color.CYAN.brighter().brighter();
                        table = new PdfPTable(13);
                        table.setWidthPercentage(100);
                        p = new Paragraph("Speed", FontFactory.getFont(
                                          FontFactory.HELVETICA, 10, Font.NORMAL,
                                          speedColor));
                        p.setAlignment(Paragraph.ALIGN_CENTER);
                        cell = new PdfPCell(p);
                        cell.setColspan(1);
                        cell.setBorder(Rectangle.NO_BORDER);
                        table.addCell(cell);

                        for (int i = 0; i < 12; i++)
                        {
                            p = new Paragraph(Integer.toString(i), FontFactory
                                              .getFont(FontFactory.HELVETICA, 15,
                                                       Font.NORMAL, Color.BLACK));
                            p.setAlignment(Paragraph.ALIGN_CENTER);
                            cell = new PdfPCell(p);
                            // cell.setPadding(2);
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setBackgroundColor(speedColor);
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cell.setBorder(Rectangle.BOX);
                            table.addCell(cell);
                        }
                        if (options.isShowOCC())
                        {
                            table.setSpacingAfter(2);
                        }
                        else
                        {
                            table.setSpacingAfter(10);
                        }
                        document.add(table);
                    }

                    //
                    // OOC
                    //
                    if (options.isShowOCC())
                    {
                        table = new PdfPTable(13);
                        table.setWidthPercentage(100);
                        p = new Paragraph("OOC", FontFactory.getFont(
                                          FontFactory.HELVETICA, 10, Font.NORMAL,
                                          Color.BLACK));
                        p.setAlignment(Paragraph.ALIGN_CENTER);
                        cell = new PdfPCell(p);
                        cell.setColspan(2);
                        cell.setBorder(Rectangle.NO_BORDER);
                        table.addCell(cell);

                        int maxOOC = 5;
                        if (RuleSetVersion.getInstance().getCurrentVersion() == RuleSetEnum.Version1)
                        {
                            maxOOC = 7;
                        }
                        for (int i = 0; i < maxOOC; i++)
                        {
                            p = new Paragraph(Integer.toString(i), FontFactory
                                              .getFont(FontFactory.HELVETICA, 15,
                                                       Font.NORMAL, Color.BLACK));
                            p.setAlignment(Paragraph.ALIGN_CENTER);
                            cell = new PdfPCell(p);
                            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                            cell.setBackgroundColor(Color.GRAY);
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cell.setBorder(Rectangle.BOX);
                            table.addCell(cell);
                        }
                        p = new Paragraph(" ", FontFactory.getFont(
                                          FontFactory.HELVETICA, 10, Font.NORMAL,
                                          Color.WHITE));
                        p.setAlignment(Paragraph.ALIGN_CENTER);
                        cell = new PdfPCell(p);
                        cell.setColspan(11 - maxOOC);
                        cell.setBorder(Rectangle.NO_BORDER);
                        table.addCell(cell);

                        table.setSpacingAfter(10);
                        document.add(table);
                    }

                    //
                    // Helm
                    //
                    if (options.isShowHelm())
                    {
                        table = new PdfPTable(13);
                        table.setWidthPercentage(100);
                        p = new Paragraph("Helm Power", FontFactory.getFont(
                                          FontFactory.HELVETICA, 10, Font.NORMAL,
                                          BattlestationColors.Piloting.getColor()));
                        p.setAlignment(Paragraph.ALIGN_CENTER);
                        cell = new PdfPCell(p);
                        cell.setColspan(2);
                        cell.setBorder(Rectangle.NO_BORDER);
                        table.addCell(cell);

                        for (int i = 0; i < 11; i++)
                        {
                            if (i > ship.getDamageSize())
                            {
                                p = new Paragraph(" ", FontFactory.getFont(
                                                  FontFactory.HELVETICA, 15, Font.NORMAL,
                                                  Color.BLACK));
                                p.setAlignment(Paragraph.ALIGN_CENTER);
                                cell = new PdfPCell(p);
                                cell
                                    .setHorizontalAlignment(Element.ALIGN_CENTER);
                                cell.setBorder(Rectangle.NO_BORDER);
                            }
                            else
                            {
                                p = new Paragraph(Integer.toString(i),
                                                  FontFactory.getFont(
                                                      FontFactory.HELVETICA, 15,
                                                      Font.NORMAL, Color.BLACK));
                                p.setAlignment(Paragraph.ALIGN_CENTER);
                                cell = new PdfPCell(p);
                                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                cell
                                    .setBackgroundColor(BattlestationColors.Piloting.getColor());
                                cell
                                    .setHorizontalAlignment(Element.ALIGN_CENTER);
                                cell.setBorder(Rectangle.BOX);
                            }

                            table.addCell(cell);
                        }
                        table.setSpacingAfter(2);
                        document.add(table);
                    }

                    if (options.isShowGuns())
                    {
                        table = new PdfPTable(13);
                        table.setWidthPercentage(100);
                        p = new Paragraph("Gun Power", FontFactory.getFont(
                                          FontFactory.HELVETICA, 10, Font.NORMAL,
                                          BattlestationColors.Combat.getColor()));
                        p.setAlignment(Paragraph.ALIGN_CENTER);
                        cell = new PdfPCell(p);
                        cell.setColspan(2);
                        cell.setBorder(Rectangle.NO_BORDER);
                        table.addCell(cell);

                        for (int i = 0; i < 11; i++)
                        {
                            if (i > ship.getDamageSize())
                            {
                                p = new Paragraph(" ", FontFactory.getFont(
                                                  FontFactory.HELVETICA, 15, Font.NORMAL,
                                                  Color.BLACK));
                                p.setAlignment(Paragraph.ALIGN_CENTER);
                                cell = new PdfPCell(p);
                                cell
                                    .setHorizontalAlignment(Element.ALIGN_CENTER);
                                cell.setBorder(Rectangle.NO_BORDER);
                            }
                            else
                            {
                                p = new Paragraph(Integer.toString(i),
                                                  FontFactory.getFont(
                                                      FontFactory.HELVETICA, 15,
                                                      Font.NORMAL, Color.BLACK));
                                p.setAlignment(Paragraph.ALIGN_CENTER);
                                cell = new PdfPCell(p);
                                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                                cell
                                    .setBackgroundColor(BattlestationColors.Combat.getColor());
                                cell
                                    .setHorizontalAlignment(Element.ALIGN_CENTER);
                                cell.setBorder(Rectangle.BOX);
                            }

                            table.addCell(cell);
                        }
                        table.setSpacingAfter(2);
                        document.add(table);
                    }

                    if (options.isShowShield())
                    {
                        table = new PdfPTable(13);
                        table.setWidthPercentage(100);
                        p = new Paragraph("Shield Power", FontFactory.getFont(
                                          FontFactory.HELVETICA, 10, Font.NORMAL,
                                          BattlestationColors.Science.getColor()));
                        p.setAlignment(Paragraph.ALIGN_CENTER);
                        cell = new PdfPCell(p);
                        cell.setColspan(2);
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cell.setBorder(Rectangle.NO_BORDER);
                        table.addCell(cell);

                        int shld = ship.getDamageSize();
                        for (int i = 0; i < 11; i++)
                        {
                            if (options.isReverseShield())
                            {
                                if (shld > -1)
                                {
                                    p = new Paragraph(Integer.toString(shld--),
                                                      FontFactory.getFont(
                                                          FontFactory.HELVETICA, 15,
                                                          Font.NORMAL, Color.BLACK));
                                    p.setAlignment(Paragraph.ALIGN_CENTER);
                                    cell = new PdfPCell(p);
                                    cell
                                        .setBackgroundColor(BattlestationColors.Science.getColor());
                                    cell
                                        .setHorizontalAlignment(Element.ALIGN_CENTER);
                                    cell.setBorder(Rectangle.BOX);
                                }
                                else
                                {
                                    p = new Paragraph(" ", FontFactory.getFont(
                                                      FontFactory.HELVETICA, 15,
                                                      Font.NORMAL, Color.BLACK));
                                    p.setAlignment(Paragraph.ALIGN_CENTER);
                                    cell = new PdfPCell(p);
                                    cell
                                        .setHorizontalAlignment(Element.ALIGN_CENTER);
                                    cell.setBorder(Rectangle.NO_BORDER);
                                }
                            }
                            else
                            {
                                if (i > ship.getDamageSize())
                                {
                                    p = new Paragraph(" ", FontFactory.getFont(
                                                      FontFactory.HELVETICA, 15,
                                                      Font.NORMAL, Color.BLACK));
                                    p.setAlignment(Paragraph.ALIGN_CENTER);
                                    cell = new PdfPCell(p);
                                    cell
                                        .setHorizontalAlignment(Element.ALIGN_CENTER);
                                    cell.setBorder(Rectangle.NO_BORDER);
                                }
                                else
                                {
                                    p = new Paragraph(Integer.toString(i),
                                                      FontFactory.getFont(
                                                          FontFactory.HELVETICA, 15,
                                                          Font.NORMAL, Color.BLACK));
                                    p.setAlignment(Paragraph.ALIGN_CENTER);
                                    cell = new PdfPCell(p);
                                    cell
                                        .setBackgroundColor(BattlestationColors.Science.getColor());
                                    cell
                                        .setHorizontalAlignment(Element.ALIGN_CENTER);
                                    cell.setBorder(Rectangle.BOX);
                                }
                            }
                            table.addCell(cell);
                        }

                        table.setSpacingAfter(2);
                        document.add(table);
                    }

                    int shipSize = ship.getDamageSize();
                    if (options.isDamageTrack())
                    {
                        table = new PdfPTable(2);
                        table.setWidthPercentage(100);
                        p = new Paragraph("Damage Track", FontFactory.getFont(
                                          FontFactory.HELVETICA, 10, Font.UNDERLINE,
                                          Color.BLACK));
                        p.setAlignment(Paragraph.ALIGN_CENTER);
                        p.setSpacingAfter(2);
                        document.add(p);

                        p = new Paragraph(makeCircleString(shipSize * 4),
                                          FontFactory.getFont(FontFactory.HELVETICA, 9,
                                                              Font.BOLD, Color.BLACK));
                        p.setAlignment(Paragraph.ALIGN_LEFT);
                        // document.add(p);
                        cell = new PdfPCell(p);
                        cell.setBorder(Rectangle.NO_BORDER);
                        cell.setColspan(2);
                        table.addCell(cell);

                        table.setWidthPercentage(100);

                        for (int i = 3; i < 12; i++)
                        {
                            // damageIncrement += shipSize * 2;
                            p = new Paragraph(makeCircleString(shipSize * 2)
                                + " (" + i + "+)", FontFactory.getFont(
                                                  FontFactory.HELVETICA, 9, Font.BOLD,
                                                  Color.BLACK));
                            p.setAlignment(Paragraph.ALIGN_LEFT);
                            // document.add(p);
                            cell = new PdfPCell(p);
                            cell.setBorder(Rectangle.NO_BORDER);
                            table.addCell(cell);
                        }

                        // damageIncrement += shipSize * 2;
                        p = new Paragraph(makeCircleString(shipSize * 2)
                            + " (12)", FontFactory.getFont(
                                              FontFactory.HELVETICA, 9, Font.BOLD,
                                              Color.BLACK));
                        p.setAlignment(Paragraph.ALIGN_LEFT);
                        // document.add(p);
                        cell = new PdfPCell(p);
                        cell.setBorder(Rectangle.NO_BORDER);
                        table.addCell(cell);

                        table.setSpacingAfter(2);
                        document.add(table);

                    }
                    else if (options.isDamageChart())
                    {
                        table = new PdfPTable(17);
                        table.setWidthPercentage(100);
                        p = new Paragraph("Hull Damage", FontFactory.getFont(
                                          FontFactory.HELVETICA, 10, Font.NORMAL,
                                          Color.WHITE));
                        p.setAlignment(Paragraph.ALIGN_CENTER);
                        // document.add(p);
                        cell = new PdfPCell(p);
                        cell.setBackgroundColor(Color.BLACK);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setBorder(Rectangle.BOX);
                        cell.setColspan(2);
                        table.addCell(cell);

                        p = new Paragraph("Size " + shipSize
                            + " Hull Rating Survival Chart", FontFactory
                                          .getFont(FontFactory.HELVETICA, 12, Font.BOLD,
                                                   Color.BLACK));
                        p.setAlignment(Paragraph.ALIGN_CENTER);
                        cell = new PdfPCell(p);
                        cell.setBackgroundColor(Color.GRAY);
                        cell.setBorder(Rectangle.BOX);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setColspan(13);
                        table.addCell(cell);

                        p = new Paragraph("Chasing Missiles", FontFactory
                                          .getFont(FontFactory.HELVETICA, 10,
                                                   Font.NORMAL, Color.WHITE));
                        p.setAlignment(Paragraph.ALIGN_CENTER);
                        cell = new PdfPCell(p);
                        cell.setBackgroundColor(Color.BLACK);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setBorder(Rectangle.BOX);
                        cell.setColspan(2);
                        table.addCell(cell);

                        p = new Paragraph(" ", FontFactory.getFont(
                                          FontFactory.HELVETICA, 13, Font.NORMAL,
                                          Color.BLACK));
                        p.setAlignment(Paragraph.ALIGN_CENTER);
                        // document.add(p);
                        cell = new PdfPCell(p);
                        cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
                        cell.setColspan(2);
                        table.addCell(cell);

                        p = new Paragraph("Hull Damage", FontFactory.getFont(
                                          FontFactory.HELVETICA, 10, Font.NORMAL,
                                          Color.WHITE));
                        p.setAlignment(Paragraph.ALIGN_CENTER);
                        cell = new PdfPCell(p);
                        cell.setBackgroundColor(Color.BLACK);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setBorder(Rectangle.NO_BORDER);
                        cell.setColspan(2);
                        table.addCell(cell);

                        int val = shipSize * 2;
                        for (int i = 0; i < 10; i++)
                        {
                            val += (shipSize * 2);
                            p = new Paragraph(Integer.toString(val),
                                              FontFactory.getFont(FontFactory.HELVETICA,
                                                                  10, Font.NORMAL, Color.WHITE));
                            p.setAlignment(Paragraph.ALIGN_CENTER);
                            cell = new PdfPCell(p);
                            cell.setBackgroundColor(Color.BLACK);
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cell.setBorder(Rectangle.NO_BORDER);
                            table.addCell(cell);
                        }
                        val += (shipSize * 2);
                        p = new Paragraph(val + "+", FontFactory.getFont(
                                          FontFactory.HELVETICA, 10, Font.NORMAL,
                                          Color.WHITE));
                        p.setAlignment(Paragraph.ALIGN_CENTER);
                        cell = new PdfPCell(p);
                        cell.setBackgroundColor(Color.BLACK);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setBorder(Rectangle.NO_BORDER);
                        table.addCell(cell);

                        p = new Paragraph(" ", FontFactory.getFont(
                                          FontFactory.HELVETICA, 13, Font.NORMAL,
                                          Color.BLACK));
                        p.setAlignment(Paragraph.ALIGN_CENTER);
                        // document.add(p);
                        cell = new PdfPCell(p);
                        cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
                        cell.setColspan(2);
                        table.addCell(cell);

                        p = new Paragraph(" ", FontFactory.getFont(
                                          FontFactory.HELVETICA, 13, Font.NORMAL,
                                          Color.BLACK));
                        p.setAlignment(Paragraph.ALIGN_CENTER);
                        // document.add(p);
                        cell = new PdfPCell(p);
                        cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT
                            | Rectangle.BOTTOM);
                        cell.setColspan(2);
                        table.addCell(cell);

                        p = new Paragraph("Hull Check (2D6):", FontFactory
                                          .getFont(FontFactory.HELVETICA, 6, Font.NORMAL,
                                                   Color.BLACK));
                        p.setAlignment(Paragraph.ALIGN_CENTER);
                        cell = new PdfPCell(p);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setBorder(Rectangle.BOX);
                        cell.setColspan(2);
                        table.addCell(cell);

                        for (int i = 3; i < 12; i++)
                        {
                            p = new Paragraph(i + "+", FontFactory.getFont(
                                              FontFactory.HELVETICA, 10, Font.NORMAL,
                                              Color.BLACK));
                            p.setAlignment(Paragraph.ALIGN_CENTER);
                            cell = new PdfPCell(p);
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cell.setBorder(Rectangle.BOX);
                            table.addCell(cell);
                        }

                        p = new Paragraph("12", FontFactory.getFont(
                                          FontFactory.HELVETICA, 10, Font.NORMAL,
                                          Color.BLACK));
                        p.setAlignment(Paragraph.ALIGN_CENTER);
                        cell = new PdfPCell(p);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setBorder(Rectangle.BOX);
                        table.addCell(cell);

                        p = new Paragraph("X", FontFactory.getFont(
                                          FontFactory.HELVETICA, 10, Font.BOLD,
                                          Color.BLACK));
                        p.setAlignment(Paragraph.ALIGN_CENTER);
                        cell = new PdfPCell(p);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setBorder(Rectangle.BOX);
                        table.addCell(cell);

                        p = new Paragraph(" ", FontFactory.getFont(
                                          FontFactory.HELVETICA, 13, Font.NORMAL,
                                          Color.BLACK));
                        p.setAlignment(Paragraph.ALIGN_CENTER);
                        // document.add(p);
                        cell = new PdfPCell(p);
                        cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT
                            | Rectangle.BOTTOM);
                        cell.setColspan(2);
                        table.addCell(cell);

                        table.setSpacingAfter(2);
                        document.add(table);
                    }

                    p = new Paragraph("www.battlestations.info", FontFactory.getFont(FontFactory.HELVETICA, 7, Font.BOLDITALIC, Color.BLUE));
                    /*
                     p = new Paragraph("Created with Eric's Ship Creator on "
                     + new Date().toString(), FontFactory.getFont(
                     FontFactory.HELVETICA, 7, Font.BOLDITALIC,
                     Color.MAGENTA));
                     */
                    p.setAlignment(Paragraph.ALIGN_CENTER);
                    document.add(p);

                    document.newPage();

                }
                catch (DocumentException e)
                {
                    logger.warn("Error creating pdf",e);
                }
            }
            document.close();
            // Prepare to write to the file
            baos.writeTo(outStream);
            outStream.close();
        }
        catch (DocumentException e1)
        {
            logger.warn("Error writing pdf",e1);
        }

    }

    public static void drawPDF(Ship ship, OutputStream outStream,
                               PDFShipWriterOptions options) throws IOException
    {
        BufferedImage logoImage = null;
        InputStream imageStream = Ship.class
            .getResourceAsStream("/com/ericski/Battlestations/Images/batstationlogo.gif");
        try
        {
            logoImage = ImageIO.read(imageStream);
        }
        catch (IOException e)
        {
            logger.warn("Couldn't load logo",e);            
        }

        BufferedImage shipImage = ship.generateImage();

        int imageSize = 567;
        Rectangle pageSize;
        switch (options.getPageSize())
        {
            case 1:
                pageSize = PageSize.LEGAL;
                // imageSize = 567;
                break;
            default:
                pageSize = PageSize.LETTER;
                if (options.isShowGuns())
                {
                    imageSize -= 14;
                }
                if (options.isShowSpeed())
                {
                    imageSize -= 14;
                }
                if (options.isShowHelm())
                {
                    imageSize -= 14;
                }
                if (options.isShowShield())
                {
                    imageSize -= 14;
                }
                if (options.isShowOCC())
                {
                    imageSize -= 14;
                }
                if (options.isDamageTrack())
                {
                    imageSize -= 32;
                }
                if (options.isShowNotes() && ship.getNotes().size() > 0)
                {
                    imageSize -= 64;
                }
                break;
        }

        java.awt.Image scaledShipImage = shipImage;
        float qualReduction = options.getOutputQualityReduction();
        if (qualReduction > 0.0)
        {
            int size = (int) (shipImage.getHeight() * (1.0 - qualReduction));
            size = Math.max(size, imageSize); // don't scale smaller than what
            // we're going to rescale it to
            // anyhow
            // System.out.println("Scaling ship image to " + size);
            scaledShipImage = BetterImageScaler.getFasterScaledInstance(
                shipImage, size, size,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC, true);
        }

        Document document = new Document(pageSize);
        document.addAuthor("Eric's Shipbuilder");
        document.setMargins(25, 25, 10, 10);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try
        {
            PdfWriter w = PdfWriter.getInstance(document, baos);
            w.setFullCompression();
            document.open();
            Paragraph p;

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);

            p = new Paragraph("Name: " + ship.getName(), FontFactory.getFont(
                              FontFactory.HELVETICA, 10, Font.NORMAL, Color.BLACK));
            p.setAlignment(Paragraph.ALIGN_CENTER);
            PdfPCell cell = new PdfPCell(p);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            p = new Paragraph("Registry: " + ship.getSpecies(), FontFactory
                              .getFont(FontFactory.HELVETICA, 10, Font.NORMAL,
                                       Color.BLACK));
            p.setAlignment(Paragraph.ALIGN_CENTER);
            cell = new PdfPCell(p);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            p = new Paragraph("Size: " + ship.getSize(), FontFactory.getFont(
                              FontFactory.HELVETICA, 10, Font.NORMAL, Color.BLACK));
            p.setAlignment(Paragraph.ALIGN_CENTER);
            cell = new PdfPCell(p);
            cell.setBorder(Rectangle.NO_BORDER);
            table.addCell(cell);

            if (logoImage != null)
            {
                com.lowagie.text.Image logoPdfImage = com.lowagie.text.Image
                    .getInstance(logoImage, null);
                logoPdfImage.scalePercent(12f);
                cell = new PdfPCell(logoPdfImage);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);
            }
            table.setSpacingAfter(2);
            document.add(table);

            if (options.isShowNotes() && ship.getNotes().size() > 0)
            {
                p = new Paragraph("Notes: " + ship.getNotesAsString(),
                                  FontFactory.getFont(FontFactory.HELVETICA, 10,
                                                      Font.NORMAL, Color.BLACK));
                document.add(p);
            }

            com.lowagie.text.Image shipPdfImage = com.lowagie.text.Image
                .getInstance(scaledShipImage, null);

            shipPdfImage.scaleAbsolute(imageSize, imageSize);
            shipPdfImage.setAlignment(com.lowagie.text.Image.MIDDLE);

            shipPdfImage.setSpacingAfter(2);
            document.add(shipPdfImage);

            document.add(new Paragraph(" "));

            if (options.isShowSpeed())
            {
                Color speedColor = Color.CYAN.brighter().brighter();
                table = new PdfPTable(13);
                table.setWidthPercentage(100);
                p = new Paragraph("Speed", FontFactory.getFont(
                                  FontFactory.HELVETICA, 10, Font.NORMAL, speedColor));
                p.setAlignment(Paragraph.ALIGN_CENTER);
                cell = new PdfPCell(p);
                cell.setColspan(1);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                for (int i = 0; i < 12; i++)
                {
                    p = new Paragraph(Integer.toString(i), FontFactory
                                      .getFont(FontFactory.HELVETICA, 15, Font.NORMAL,
                                               Color.BLACK));
                    p.setAlignment(Paragraph.ALIGN_CENTER);
                    cell = new PdfPCell(p);
                    // cell.setPadding(2);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setBackgroundColor(speedColor);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBorder(Rectangle.BOX);
                    table.addCell(cell);
                }
                if (options.isShowOCC())
                {
                    table.setSpacingAfter(2);
                }
                else
                {
                    table.setSpacingAfter(10);
                }
                document.add(table);
            }

            //
            // OOC
            //
            if (options.isShowOCC())
            {
                table = new PdfPTable(13);
                table.setWidthPercentage(100);
                p = new Paragraph("OOC", FontFactory.getFont(
                                  FontFactory.HELVETICA, 10, Font.NORMAL, Color.BLACK));
                p.setAlignment(Paragraph.ALIGN_CENTER);
                cell = new PdfPCell(p);
                cell.setColspan(2);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                int maxOOC = 5;
                if (RuleSetVersion.getInstance().getCurrentVersion() == RuleSetEnum.Version1)
                {
                    maxOOC = 7;
                }
                for (int i = 0; i < maxOOC; i++)
                {
                    p = new Paragraph(Integer.toString(i), FontFactory
                                      .getFont(FontFactory.HELVETICA, 15, Font.NORMAL,
                                               Color.BLACK));
                    p.setAlignment(Paragraph.ALIGN_CENTER);
                    cell = new PdfPCell(p);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setBackgroundColor(Color.GRAY);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBorder(Rectangle.BOX);
                    table.addCell(cell);
                }
                p = new Paragraph(" ", FontFactory.getFont(
                                  FontFactory.HELVETICA, 10, Font.NORMAL, Color.WHITE));
                p.setAlignment(Paragraph.ALIGN_CENTER);
                cell = new PdfPCell(p);
                cell.setColspan(11 - maxOOC);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                table.setSpacingAfter(10);
                document.add(table);
            }

            //
            // Helm
            //
            if (options.isShowHelm())
            {
                table = new PdfPTable(13);
                table.setWidthPercentage(100);
                p = new Paragraph("Helm Power", FontFactory.getFont(
                                  FontFactory.HELVETICA, 10, Font.NORMAL,
                                  BattlestationColors.Piloting.getColor()));
                p.setAlignment(Paragraph.ALIGN_CENTER);
                cell = new PdfPCell(p);
                cell.setColspan(2);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                for (int i = 0; i < 11; i++)
                {
                    if (i > ship.getDamageSize())
                    {
                        p = new Paragraph(" ", FontFactory.getFont(
                                          FontFactory.HELVETICA, 15, Font.NORMAL,
                                          Color.BLACK));
                        p.setAlignment(Paragraph.ALIGN_CENTER);
                        cell = new PdfPCell(p);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setBorder(Rectangle.NO_BORDER);
                    }
                    else
                    {
                        p = new Paragraph(Integer.toString(i), FontFactory
                                          .getFont(FontFactory.HELVETICA, 15,
                                                   Font.NORMAL, Color.BLACK));
                        p.setAlignment(Paragraph.ALIGN_CENTER);
                        cell = new PdfPCell(p);
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cell.setBackgroundColor(BattlestationColors.Piloting.getColor());
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setBorder(Rectangle.BOX);
                    }

                    table.addCell(cell);
                }
                table.setSpacingAfter(2);
                document.add(table);
            }

            if (options.isShowGuns())
            {
                table = new PdfPTable(13);
                table.setWidthPercentage(100);
                p = new Paragraph("Gun Power", FontFactory.getFont(
                                  FontFactory.HELVETICA, 10, Font.NORMAL,
                                  BattlestationColors.Combat.getColor()));
                p.setAlignment(Paragraph.ALIGN_CENTER);
                cell = new PdfPCell(p);
                cell.setColspan(2);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                for (int i = 0; i < 11; i++)
                {
                    if (i > ship.getDamageSize())
                    {
                        p = new Paragraph(" ", FontFactory.getFont(
                                          FontFactory.HELVETICA, 15, Font.NORMAL,
                                          Color.BLACK));
                        p.setAlignment(Paragraph.ALIGN_CENTER);
                        cell = new PdfPCell(p);
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setBorder(Rectangle.NO_BORDER);
                    }
                    else
                    {
                        p = new Paragraph(Integer.toString(i), FontFactory
                                          .getFont(FontFactory.HELVETICA, 15,
                                                   Font.NORMAL, Color.BLACK));
                        p.setAlignment(Paragraph.ALIGN_CENTER);
                        cell = new PdfPCell(p);
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cell.setBackgroundColor(BattlestationColors.Combat.getColor());
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        cell.setBorder(Rectangle.BOX);
                    }

                    table.addCell(cell);
                }
                table.setSpacingAfter(2);
                document.add(table);
            }

            if (options.isShowShield())
            {
                table = new PdfPTable(13);
                table.setWidthPercentage(100);
                p = new Paragraph("Shield Power", FontFactory.getFont(
                                  FontFactory.HELVETICA, 10, Font.NORMAL,
                                  BattlestationColors.Science.getColor()));
                p.setAlignment(Paragraph.ALIGN_CENTER);
                cell = new PdfPCell(p);
                cell.setColspan(2);
                cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                int shld = ship.getDamageSize();
                for (int i = 0; i < 11; i++)
                {
                    if (options.isReverseShield())
                    {
                        if (shld > -1)
                        {
                            p = new Paragraph(Integer.toString(shld--),
                                              FontFactory.getFont(FontFactory.HELVETICA,
                                                                  15, Font.NORMAL, Color.BLACK));
                            p.setAlignment(Paragraph.ALIGN_CENTER);
                            cell = new PdfPCell(p);
                            cell
                                .setBackgroundColor(BattlestationColors.Science.getColor());
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cell.setBorder(Rectangle.BOX);
                        }
                        else
                        {
                            p = new Paragraph(" ", FontFactory.getFont(
                                              FontFactory.HELVETICA, 15, Font.NORMAL,
                                              Color.BLACK));
                            p.setAlignment(Paragraph.ALIGN_CENTER);
                            cell = new PdfPCell(p);
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cell.setBorder(Rectangle.NO_BORDER);
                        }
                    }
                    else
                    {
                        if (i > ship.getDamageSize())
                        {
                            p = new Paragraph(" ", FontFactory.getFont(
                                              FontFactory.HELVETICA, 15, Font.NORMAL,
                                              Color.BLACK));
                            p.setAlignment(Paragraph.ALIGN_CENTER);
                            cell = new PdfPCell(p);
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cell.setBorder(Rectangle.NO_BORDER);
                        }
                        else
                        {
                            p = new Paragraph(Integer.toString(i), FontFactory
                                              .getFont(FontFactory.HELVETICA, 15,
                                                       Font.NORMAL, Color.BLACK));
                            p.setAlignment(Paragraph.ALIGN_CENTER);
                            cell = new PdfPCell(p);
                            cell
                                .setBackgroundColor(BattlestationColors.Science.getColor());
                            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            cell.setBorder(Rectangle.BOX);
                        }
                    }
                    table.addCell(cell);
                }

                table.setSpacingAfter(2);
                document.add(table);
            }

            int shipSize = ship.getDamageSize();
            if (options.isDamageTrack())
            {
                table = new PdfPTable(2);
                table.setWidthPercentage(100);
                p = new Paragraph("Damage Track", FontFactory.getFont(
                                  FontFactory.HELVETICA, 10, Font.UNDERLINE, Color.BLACK));
                p.setAlignment(Paragraph.ALIGN_CENTER);
                p.setSpacingAfter(2);
                document.add(p);

                p = new Paragraph(makeCircleString(shipSize * 4), FontFactory
                                  .getFont(FontFactory.HELVETICA, 9, Font.BOLD,
                                           Color.BLACK));
                p.setAlignment(Paragraph.ALIGN_LEFT);
                // document.add(p);
                cell = new PdfPCell(p);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setColspan(2);
                table.addCell(cell);

                table.setWidthPercentage(100);

                for (int i = 3; i < 12; i++)
                {
                    // damageIncrement += shipSize * 2;
                    p = new Paragraph(makeCircleString(shipSize * 2) + " (" + i
                        + "+)", FontFactory.getFont(FontFactory.HELVETICA,
                                                    9, Font.BOLD, Color.BLACK));
                    p.setAlignment(Paragraph.ALIGN_LEFT);
                    // document.add(p);
                    cell = new PdfPCell(p);
                    cell.setBorder(Rectangle.NO_BORDER);
                    table.addCell(cell);
                }

                // damageIncrement += shipSize * 2;
                p = new Paragraph(makeCircleString(shipSize * 2) + " (12)",
                                  FontFactory.getFont(FontFactory.HELVETICA, 9,
                                                      Font.BOLD, Color.BLACK));
                p.setAlignment(Paragraph.ALIGN_LEFT);
                // document.add(p);
                cell = new PdfPCell(p);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                table.setSpacingAfter(2);
                document.add(table);

            }
            else if (options.isDamageChart())
            {
                table = new PdfPTable(17);
                table.setWidthPercentage(100);
                p = new Paragraph("Hull Damage", FontFactory.getFont(
                                  FontFactory.HELVETICA, 10, Font.NORMAL, Color.WHITE));
                p.setAlignment(Paragraph.ALIGN_CENTER);
                // document.add(p);
                cell = new PdfPCell(p);
                cell.setBackgroundColor(Color.BLACK);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorder(Rectangle.BOX);
                cell.setColspan(2);
                table.addCell(cell);

                p = new Paragraph("Size " + shipSize
                    + " Hull Rating Survival Chart", FontFactory.getFont(
                                      FontFactory.HELVETICA, 12, Font.BOLD, Color.BLACK));
                p.setAlignment(Paragraph.ALIGN_CENTER);
                cell = new PdfPCell(p);
                cell.setBackgroundColor(Color.GRAY);
                cell.setBorder(Rectangle.BOX);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setColspan(13);
                table.addCell(cell);

                p = new Paragraph("Chasing Missiles", FontFactory.getFont(
                                  FontFactory.HELVETICA, 10, Font.NORMAL, Color.WHITE));
                p.setAlignment(Paragraph.ALIGN_CENTER);
                cell = new PdfPCell(p);
                cell.setBackgroundColor(Color.BLACK);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorder(Rectangle.BOX);
                cell.setColspan(2);
                table.addCell(cell);

                p = new Paragraph(" ", FontFactory.getFont(
                                  FontFactory.HELVETICA, 13, Font.NORMAL, Color.BLACK));
                p.setAlignment(Paragraph.ALIGN_CENTER);
                // document.add(p);
                cell = new PdfPCell(p);
                cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
                cell.setColspan(2);
                table.addCell(cell);

                p = new Paragraph("Hull Damage", FontFactory.getFont(
                                  FontFactory.HELVETICA, 10, Font.NORMAL, Color.WHITE));
                p.setAlignment(Paragraph.ALIGN_CENTER);
                cell = new PdfPCell(p);
                cell.setBackgroundColor(Color.BLACK);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorder(Rectangle.NO_BORDER);
                cell.setColspan(2);
                table.addCell(cell);

                int val = shipSize * 2;
                for (int i = 0; i < 10; i++)
                {
                    val += (shipSize * 2);
                    p = new Paragraph(Integer.toString(val), FontFactory
                                      .getFont(FontFactory.HELVETICA, 10, Font.NORMAL,
                                               Color.WHITE));
                    p.setAlignment(Paragraph.ALIGN_CENTER);
                    cell = new PdfPCell(p);
                    cell.setBackgroundColor(Color.BLACK);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBorder(Rectangle.NO_BORDER);
                    table.addCell(cell);
                }
                val += (shipSize * 2);
                p = new Paragraph(val + "+", FontFactory.getFont(
                                  FontFactory.HELVETICA, 10, Font.NORMAL, Color.WHITE));
                p.setAlignment(Paragraph.ALIGN_CENTER);
                cell = new PdfPCell(p);
                cell.setBackgroundColor(Color.BLACK);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorder(Rectangle.NO_BORDER);
                table.addCell(cell);

                p = new Paragraph(" ", FontFactory.getFont(
                                  FontFactory.HELVETICA, 13, Font.NORMAL, Color.BLACK));
                p.setAlignment(Paragraph.ALIGN_CENTER);
                // document.add(p);
                cell = new PdfPCell(p);
                cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT);
                cell.setColspan(2);
                table.addCell(cell);

                p = new Paragraph(" ", FontFactory.getFont(
                                  FontFactory.HELVETICA, 13, Font.NORMAL, Color.BLACK));
                p.setAlignment(Paragraph.ALIGN_CENTER);
                // document.add(p);
                cell = new PdfPCell(p);
                cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT
                    | Rectangle.BOTTOM);
                cell.setColspan(2);
                table.addCell(cell);

                p = new Paragraph("Hull Check (2D6):", FontFactory.getFont(
                                  FontFactory.HELVETICA, 6, Font.NORMAL, Color.BLACK));
                p.setAlignment(Paragraph.ALIGN_CENTER);
                cell = new PdfPCell(p);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorder(Rectangle.BOX);
                cell.setColspan(2);
                table.addCell(cell);

                for (int i = 3; i < 12; i++)
                {
                    p = new Paragraph(i + "+", FontFactory
                                      .getFont(FontFactory.HELVETICA, 10, Font.NORMAL,
                                               Color.BLACK));
                    p.setAlignment(Paragraph.ALIGN_CENTER);
                    cell = new PdfPCell(p);
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBorder(Rectangle.BOX);
                    table.addCell(cell);
                }

                p = new Paragraph("12", FontFactory.getFont(
                                  FontFactory.HELVETICA, 10, Font.NORMAL, Color.BLACK));
                p.setAlignment(Paragraph.ALIGN_CENTER);
                cell = new PdfPCell(p);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorder(Rectangle.BOX);
                table.addCell(cell);

                p = new Paragraph("X", FontFactory.getFont(
                                  FontFactory.HELVETICA, 10, Font.BOLD, Color.BLACK));
                p.setAlignment(Paragraph.ALIGN_CENTER);
                cell = new PdfPCell(p);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBorder(Rectangle.BOX);
                table.addCell(cell);

                p = new Paragraph(" ", FontFactory.getFont(
                                  FontFactory.HELVETICA, 13, Font.NORMAL, Color.BLACK));
                p.setAlignment(Paragraph.ALIGN_CENTER);
                // document.add(p);
                cell = new PdfPCell(p);
                cell.setBorder(Rectangle.LEFT | Rectangle.RIGHT
                    | Rectangle.BOTTOM);
                cell.setColspan(2);
                table.addCell(cell);

                table.setSpacingAfter(2);
                document.add(table);
            }

            p = new Paragraph("Created with Eric's Ship Creator on "
                + new Date().toString(), FontFactory.getFont(
                                  FontFactory.HELVETICA, 7, Font.BOLDITALIC, Color.MAGENTA));
            p.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(p);

            document.close();
        }
        catch (DocumentException e)
        {
            logger.warn("Error creating pdf",e);
        }

        // Prepare to write to the file
        baos.writeTo(outStream);
        outStream.close();

    }

    private static String makeCircleString(int number)
    {
        StringBuilder buf = new StringBuilder(number);
        for (int i = 0; i < number; i++)
        {
            if (i % 5 == 0)
            {
                buf.append(' ');
            }
            buf.append('O');

        }
        return buf.toString();
    }

    public static void main(String[] args) throws Exception
    {
        System.out.println("Starting");
        List<Ship> ships = ShipFactory.INSTANCE.getStandardTemplates();
        PDFShipWriterOptions options = new PDFShipWriterOptions();
        //options.setOutputQualityReduction(.6F);
        options.setShowHelm(false);
        options.setShowGuns(true);
        options.setShowOCC(false);
        options.setShowSpeed(true);
        options.setShowShield(true);
        options.setReverseShield(true);
        options.setDamageChart(true);

        //System.out.println("Removing Generic Ships");
        List<Ship> newList = new ArrayList<>();
        for (Ship ship : ships)
        {
            if (ship.getSpecies() != null && ship.getSpecies().length() > 0 && !ship.getSpecies().equalsIgnoreCase("Dr Moreau") && !ship.getSpecies().equalsIgnoreCase("Doids") && !ship.getSpecies().equalsIgnoreCase("Eugene"))
            {
                newList.add(ship);
            }
        }
        System.out.println("Creating File");
        OutputStream outStream = new FileOutputStream("N:\\FAShips_high.pdf");
        PDFShipWriter.drawPDF(newList, outStream, options);
        System.out.println("Finished");

    }
}
