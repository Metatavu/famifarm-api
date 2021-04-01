package fi.metatavu.famifarm.reporting.json.models;

/**
 * Model for json event
 */
public class Event {
  private String lineNumber;
  private String startTime;
  private String endTime;
  private String user;
  private String productName;
  private String eventPhase;
  private String wastageReason;
  private String additionalInformation;
  private Integer amount;

  public String getLineNumber() {
    return lineNumber;
  }

  public void setLineNumber(String lineNumber) {
    this.lineNumber = lineNumber;
  }

  public String getEndTime() {
    return endTime;
  }

  public void setEndTime(String endTime) {
    this.endTime = endTime;
  }

  public String getUser() {
    return user;
  }

  public void setUser(String user) {
    this.user = user;
  }

  public String getProductName() {
    return productName;
  }

  public void setProductName(String productName) {
    this.productName = productName;
  }

  public String getEventPhase() {
    return eventPhase;
  }

  public void setEventPhase(String eventPhase) {
    this.eventPhase = eventPhase;
  }

  public String getWastageReason() {
    return wastageReason;
  }

  public void setWastageReason(String wastageReason) {
    this.wastageReason = wastageReason;
  }

  public String getAdditionalInformation() {
    return additionalInformation;
  }

  public void setAdditionalInformation(String additionalInformation) {
    this.additionalInformation = additionalInformation;
  }

  public Integer getAmount() {
    return amount;
  }

  public String getStartTime() {
    return startTime;
  }

  public void setStartTime(String startTime) {
    this.startTime = startTime;
  }

  public void setAmount(Integer amount) {
    this.amount = amount;
  }
}
