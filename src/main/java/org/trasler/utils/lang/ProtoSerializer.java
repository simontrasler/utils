package org.trasler.utils.lang;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;
import java.io.IOException;

public class ProtoSerializer extends JsonSerializer<Message> {
    private static final JsonFormat.Printer printer = JsonFormat.printer().omittingInsignificantWhitespace();

    @Override
    public void serialize(Message message, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeRawValue(printer.print(message));
    }
}
