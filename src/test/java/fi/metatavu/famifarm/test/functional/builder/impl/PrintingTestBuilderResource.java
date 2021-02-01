package fi.metatavu.famifarm.test.functional.builder.impl;

import fi.metatavu.famifarm.client.ApiClient;
import fi.metatavu.famifarm.client.api.PrintersApi;

import fi.metatavu.famifarm.client.model.PrintData;
import fi.metatavu.famifarm.client.model.Printer;
import fi.metatavu.famifarm.test.functional.builder.AbstractTestBuilderResource;

import java.util.List;
import java.util.UUID;

/**
 * Test builder resource for printing packing qr codes
 */
public class PrintingTestBuilderResource extends AbstractTestBuilderResource<PrintData, PrintersApi> {


    /**
     * Constructor
     *
     * @param apiClient API client
     */
    public PrintingTestBuilderResource(ApiClient apiClient) {
        super(apiClient);
    }

    /**
     * Prints a qr code of a specified packing id using a specified printer
     *
     * @param packingId packing id
     * @param printerId printer id
     */
    public void print(UUID packingId, String printerId) {
        PrintData printData = new PrintData();
        printData.setPackingId(packingId);
        getApi().print(printData, printerId);
    }

    /**
     * Lists connected printers
     *
     * @return connected printers
     */
    public List<Printer> getPrinters() {
        return getApi().listPrinters();
    }

    @Override
    public void clean(PrintData printData) {
    }
}
