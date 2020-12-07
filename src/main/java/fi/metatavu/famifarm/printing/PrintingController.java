package fi.metatavu.famifarm.printing;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fi.metatavu.famifarm.campaigns.CampaignController;
import fi.metatavu.famifarm.localization.LocalizedValueController;
import fi.metatavu.famifarm.packings.PackingController;
import fi.metatavu.famifarm.persistence.model.CutPacking;
import fi.metatavu.famifarm.persistence.model.Packing;
import fi.metatavu.famifarm.rest.model.PackingType;
import fi.metatavu.famifarm.rest.model.Printer;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
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
    private CampaignController campaignController;
    @Inject
    private LocalizedValueController localizedValueController;

    /**
     * Prints a QR code
     *
     * @param printerId the id of a printer to use
     * @param packing a packing to print
     * @param locale locale
     *
     * @return HTTP status returned by the printing server
     * @throws IOException thrown when printing goes wrong
     */
    public int printQrCode(String printerId, Packing packing, Locale locale) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String packingTime = formatter.format(packing.getTime());
        if (packing.getType() == PackingType.BASIC) {
            String productName = localizedValueController.getValue(packing.getProduct().getName(), locale);
            String packageSize = localizedValueController.getValue(packing.getPackageSize().getName(), locale);
            List<String> lines = new ArrayList<String>();
            lines.add("^XA");
            lines.add(String.format("^FO30,30,2^AfN,40,30^FD%s^FS", replaceUmlauts(productName)));
            lines.add(String.format("^FO30,85,2^AfN,40,30^FD%d * %s^FS", packing.getPackedCount(), replaceUmlauts(packageSize)));
            lines.add(String.format("^FO30,155,2^AfN,40,30^FD%s^FS", packingTime));
            lines.add(String.format("^FO480,155,2^BQN,2,10,H,0^FD:::%s^FS", packing.getId().toString()));
            lines.add("^XZ");
            return executePrintCommands(lines, printerId);
        } else {
            String campaignName = packing.getCampaign().getName();
            List<String> lines = new ArrayList<String>();
            lines.add("^XA");
            lines.add(String.format("^FO30,30,2^AfN,40,30^FD%s^FS", replaceUmlauts(campaignName)));
            lines.add("^FO30,85,2^AfN,40,30^FD^FS");
            lines.add(String.format("^FO30,155,2^AfN,40,30^FD%s^FS", packingTime));
            lines.add(String.format("^FO480,155,2^BQN,2,10,H,0^FD:::%s^FS", packing.getId().toString()));
            lines.add("^XZ");
            return executePrintCommands(lines, printerId);
        }
    }

    /**
     * Prints a QR code
     *
     * @param printerId the id of a printer to use
     * @param cutPacking a packing to print
     * @param locale locale
     *
     * @return HTTP status returned by the printing server
     * @throws IOException thrown when printing goes wrong
     */
    public int printQrCode(String printerId, CutPacking cutPacking, Locale locale) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        String sowingTime = formatter.format(cutPacking.getSowingDay());
        String cuttingTime = formatter.format(cutPacking.getCuttingDay());
        DecimalFormat df2 = new DecimalFormat("#.##");
        String kilos = df2.format(cutPacking.getWeight());
        String productName = localizedValueController.getValue(cutPacking.getProduct().getName(), locale);
        List<String> lines = new ArrayList<>();
        lines.add("^XA");
        lines.add("^CF0,80");
        lines.add(String.format("^FO15,30^FD%s^FS", replaceUmlauts(productName)));
        lines.add("^CF0,60");
        lines.add(String.format("^FO15,170^FDWeight: %s kg^FS", kilos));
        lines.add(String.format("^FO15,240^FDSowed: %s^FS", sowingTime));
        lines.add(String.format("^FO15,310^FDCut: %s^FS", cuttingTime));
        lines.add("^CF0,30");
        lines.add(String.format("^FO15,500^FDStorage conditions: %s^FS", replaceUmlauts(cutPacking.getStorageCondition())));
        lines.add(String.format("^FO15,550^FDManufacturer: %s^FS", replaceUmlauts(cutPacking.getProducer())));
        lines.add(String.format("^FO15,600^FDContact: %s^FS", replaceUmlauts(cutPacking.getContactInformation())));
        lines.add("^FO50,700^GB700,1,2,B,0^FS");
        lines.add(String.format("^FO250,800^BQN,2,10,H^FD:::%s^FS", cutPacking.getId().toString()));
        lines.add("^CF0,30");
        lines.add(String.format("^FO100,1130^FD%s^FS", cutPacking.getId().toString()));
        lines.add("^XZ");

        return executePrintCommands(lines, printerId);
    }

    /**
     * Executes printing commands
     *
     * @param lines strings that contain printing commands
     * @param printerId the id of a printer to use
     * @return HTTP status returned by the printing server
     * @throws IOException thrown when printing goes wrong
     */
    private int executePrintCommands (List<String> lines, String printerId) throws IOException {
        URL url = new URL("https://famifarm-print.metatavu.io/rest/v1/printers/"+printerId+"/raw");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
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


