package dev.didelfo.shadowWarden.manager.connections.qr;


import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class QRMapRender extends MapRenderer {

    private final BitMatrix qrCode;
    private boolean rendered = false;

    public QRMapRender(String jsonString) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            this.qrCode = qrCodeWriter.encode(jsonString, BarcodeFormat.QR_CODE, 128, 128);
        } catch (WriterException e) {
            throw new RuntimeException("Error al generar QR code", e);
        }
    }

    public void render(MapView map, MapCanvas canvas, Player player) {
        if (!this.rendered) {
            for(int x = 0; x < 128; ++x) {
                for(int y = 0; y < 128; ++y) {
                    canvas.setPixel(x, y, (byte)(this.qrCode.get(x, y) ? 0 : 119));
                }
            }

            this.rendered = true;
        }
    }

}
