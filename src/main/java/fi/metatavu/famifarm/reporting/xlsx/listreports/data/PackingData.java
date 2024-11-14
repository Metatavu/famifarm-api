package fi.metatavu.famifarm.reporting.xlsx.listreports.data;

import fi.metatavu.famifarm.persistence.model.Packing;
import fi.metatavu.famifarm.persistence.model.PackingBasket;

import java.util.List;

public class PackingData {

  private final Packing packing;

  private final List<PackingBasket> packingBaskets;

  public PackingData(Packing packing, List<PackingBasket> packingBaskets) {
    this.packing = packing;
    this.packingBaskets = packingBaskets;
  }

  public Packing getPacking() {
    return packing;
  }

  public List<PackingBasket> getPackingBaskets() {
    return packingBaskets;
  }
}
