package org.tensorflow.lite.examples.detection.QRProduct;

public interface QRCodeFoundListener {
    void onQRCodeFound(String qrCode);
    void qrCodeNotFound();
}
