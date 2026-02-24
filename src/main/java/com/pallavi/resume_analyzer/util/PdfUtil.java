package com.pallavi.resume_analyzer.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public class PdfUtil {

    public static String extractText(MultipartFile file){

        try (PDDocument document = PDDocument.load(file.getInputStream())){
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);

        }catch (IOException e){
            throw new RuntimeException("Error reading PDF file", e);
        }
    }
}
