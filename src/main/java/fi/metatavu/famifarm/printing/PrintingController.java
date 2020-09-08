package fi.metatavu.famifarm.printing;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.metatavu.famifarm.localization.LocalizedValueController;
import fi.metatavu.famifarm.packing.PackingController;
import fi.metatavu.famifarm.persistence.model.Packing;
import fi.metatavu.famifarm.rest.model.Printer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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

    public int printQrCode(String printerId, Packing packing, Locale locale) throws IOException {
        URL url = new URL("https://famifarm-print.metatavu.io/rest/v1/printers/"+printerId+"/raw");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        String productName = localizedValueController.getValue(packing.getProduct().getName(), locale);
        String packageSize = localizedValueController.getValue(packing.getPackageSize().getName(), locale);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String packingTime = formatter.format(packing.getTime());
        List<String> lines = new ArrayList<String>();
        lines.add("^XA");
        lines.add(String.format("^FO30,30,2^AfN,40,30^FD%s^FS", replaceUmlauts(productName)));
        lines.add(String.format("^FO30,85,2^AfN,40,30^FD%d * %s^FS", packing.getPackedCount(), replaceUmlauts(packageSize)));
        lines.add(String.format("^FO30,155,2^AfN,40,30^FD%s^FS", packingTime));
        lines.add(String.format("^FO480,155,2^BQN,2,10,H,0^FD:::%s^FS", packing.getId().toString()));
        lines.add("^XZ");
        String command = String.format("%s%s", lines.stream().collect( Collectors.joining("\r\n" ) ), "\r\n");
        Map<String, String> commandObject = new HashMap<>();
        commandObject.put("command",command);
        byte[] data = new ObjectMapper().writeValueAsBytes(commandObject);
        int length = data.length;
        connection.setFixedLengthStreamingMode(length);
        connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        connection.connect();
        try(OutputStream os = connection.getOutputStream()) {
            os.write(data);
        }
        int responseCode = connection.getResponseCode();
        connection.disconnect();
        return responseCode;
    }

    private String replaceUmlauts(String input) {
        return input.replaceAll("Ä", "A").replaceAll("Ö", "O").replaceAll("ä", "a").replaceAll("ö", "o").replaceAll("Å", "O").replaceAll("å", "o");
    }

    public List<Printer> getPrinters() throws IOException {
        URL url = new URL("https://famifarm-print.metatavu.io/rest/v1/printers/");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        int status = connection.getResponseCode();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        connection.disconnect();
        ObjectMapper objectMapper = new ObjectMapper();
        List<TranslatedPrinter> translatedPrinters = objectMapper.readValue(content.toString(), new TypeReference<List<TranslatedPrinter>>(){});
        List<Printer> printers = new ArrayList<>();
        for (TranslatedPrinter translatedPrinter: translatedPrinters) {
            Printer printer = new Printer();
            printer.setId(translatedPrinter.id);
            printer.setName(translatedPrinter.friendlyName);
            printers.add(printer);
        }

        return printers;
    }


}

class TranslatedPrinter {
    @JsonProperty("id")
    String id;
    @JsonProperty("friendlyName")
    String friendlyName;
}


