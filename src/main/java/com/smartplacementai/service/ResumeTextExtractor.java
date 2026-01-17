package com.smartplacementai.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class ResumeTextExtractor {

    public String extractText(MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();

            if (fileName == null) {
                throw new RuntimeException("Invalid file name");
            }

            if (fileName.endsWith(".pdf")) {
                return extractFromPdf(file.getInputStream());
            }

            if (fileName.endsWith(".docx")) {
                return extractFromDocx(file.getInputStream());
            }

            throw new RuntimeException("Unsupported file type");

        } catch (Exception e) {
            throw new RuntimeException("Failed to extract resume text", e);
        }
    }

    private String extractFromPdf(InputStream inputStream) throws Exception {
        PDDocument document = PDDocument.load(inputStream);
        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(document);
        document.close();
        return text;
    }

    private String extractFromDocx(InputStream inputStream) throws Exception {
        XWPFDocument document = new XWPFDocument(inputStream);
        XWPFWordExtractor extractor = new XWPFWordExtractor(document);
        String text = extractor.getText();
        extractor.close();
        return text;
    }
}
