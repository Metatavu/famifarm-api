package fi.metatavu.famifarm.reporting.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import fi.metatavu.famifarm.reporting.AbstractReport;

/**
 * Abstract class for all json reports
 */
public abstract class AbstractJsonReport extends AbstractReport {

  /**
   * Returns report's content type
   *
   * @return report's content type
   */
  @Override
  public String getContentType() {
    return "application/json";
  }

  /**
   * Gets object mapper with parameters
   *
   * @return object mapper
   */
  public ObjectMapper getObjectMapper() {
    return JsonMapper.builder()
      .addModule(new ParameterNamesModule())
      .addModule(new Jdk8Module())
      .addModule(new JavaTimeModule())
      .configure(SerializationFeature.FLUSH_AFTER_WRITE_VALUE, true)
      .build();
  }
}
