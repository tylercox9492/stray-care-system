package com.straycare.stray_care_system.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.straycare.stray_care_system.model.Dog;
import com.straycare.stray_care_system.model.QrCode;
import com.straycare.stray_care_system.repository.QrCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

@Service
public class QRCodeService {

    @Autowired
    private QrCodeRepository qrCodeRepository;

    public String generateQRCode(Dog dog) throws WriterException, IOException {
        String url = "http://localhost:8080/api/dogs/" + dog.getId();

        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix = qrCodeWriter.encode(url, BarcodeFormat.QR_CODE, 300, 300);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        String base64Image = Base64.getEncoder().encodeToString(outputStream.toByteArray());

        QrCode qrCode = new QrCode();
        qrCode.setDog(dog);
        qrCode.setQrData(url);
        qrCodeRepository.save(qrCode);

        return base64Image;
    }
}