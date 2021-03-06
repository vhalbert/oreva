package org.odata4j.format.jsonlite;

import java.io.Reader;

import org.odata4j.core.OEntity;
import org.odata4j.format.FormatParser;
import org.odata4j.format.Settings;
import org.odata4j.format.json.JsonStreamReaderFactory;

/**
 * A FormatParser that can be used for parsing the result of functions that
 * return an OEntity
 * 
 * Design Note:
 * 
 * This class does basically the same thing as JsonEntryFormatParser with two exceptions:
 * - it returns an object that derives from OObject because that is what functions return.
 * - it resolves the EdmEntitySet by looking up the function by name in the metadata.
 * 
 * @author <a href="mailto:shantanu@synerzip.com">Shantanu Dindokar</a>
 */
public class JsonLiteEntityFormatParser extends JsonLiteFormatParser implements FormatParser<OEntity> {

  public JsonLiteEntityFormatParser(Settings settings) {
    super(settings);
  }

  @Override
  public OEntity parse(Reader reader) {
    JsonStreamReaderFactory.JsonStreamReader jsr = JsonStreamReaderFactory.createJsonStreamReader(reader);
    try {
      ensureNext(jsr);

      // skip the StartObject event
      ensureStartObject(jsr.nextEvent());

      if (isResponse) {
        ensureNext(jsr);
      }
      return parseEntry(parseFunction.getEntitySet(), jsr).getEntity();
      // no interest in the closing events
    } finally {
      jsr.close();
    }
  }

  /**
   * Helper function to parse a OEntity out of the stream, used in function parameter parsing.
   * @param jsr the json stream reader.
   * @return the OEntity
   */
  public OEntity parseSingleEntity(JsonStreamReaderFactory.JsonStreamReader jsr) {
    return parseEntry(parseFunction.getEntitySet(), jsr).getEntity();
  }

}
