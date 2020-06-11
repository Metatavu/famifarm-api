package fi.metatavu.famifarm.printing;

import fi.metatavu.famifarm.localization.LocalizedValueController;
import fi.metatavu.famifarm.packing.PackingController;
import fi.metatavu.famifarm.persistence.model.Packing;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * A controller class for printing packing id qr code
 */
@ApplicationScoped
public class PrintingController {
    @Inject
    private PackingController packingController;
    @Inject
    private LocalizedValueController localizedValueController;

    public int printQrCode(UUID printerId, UUID packingId) throws IOException {
        URL url = new URL("https://famifarm-print.metatavu.io/rest/v1/printers/"+printerId+"/raw");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        Packing packing = packingController.findById(packingId);
        String productName = localizedValueController.getValue(packing.getProduct().getName(), Locale.ENGLISH);
        String packingTime = packing.getTime().toString();
        List<String> lines = new ArrayList<String>();
        lines.add("^XA");
        lines.add(String.format("^FO100,200,2^AfN,40,30^FD%s^FS", productName));
        lines.add(String.format("^FO280,270,2^BQN,2,10,H,0^%s^FS", packingId));
        lines.add(String.format("^^FO250,700,2^AfN,40,30^FD%s^FS", packingTime));
        lines.add("^XZ");
        String command = String.format("%s%s", lines.stream().collect( Collectors.joining("\r\n" ) ), "\r\n");
        int length = command.length();
        connection.setFixedLengthStreamingMode(length);
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connection.connect();
        try(OutputStream os = connection.getOutputStream()) {
            os.write(command.getBytes());
        }
        connection.disconnect();
        return connection.getResponseCode();
    }
}
