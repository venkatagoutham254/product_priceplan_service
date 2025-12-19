package aforo.productrateplanservice.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class JacksonConfig {

    private static final String DATETIME_FORMAT = "dd MMM, yyyy HH:mm z"; // e.g. 06 Jan, 2025 13:58 IST
    private static final ZoneId IST_ZONE = ZoneId.of("Asia/Kolkata");
    private static final ZoneId UTC_ZONE = ZoneId.of("UTC");
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATETIME_FORMAT);

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder -> builder
                .featuresToDisable(
                        DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                        DeserializationFeature.ACCEPT_FLOAT_AS_INT,
                        SerializationFeature.WRITE_DATES_AS_TIMESTAMPS
                )
                .serializers(new JsonSerializer<LocalDateTime>() {
                    @Override
                    public Class<LocalDateTime> handledType() {
                        return LocalDateTime.class;
                    }

                    @Override
                    public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                        if (value != null) {
                            // Convert UTC LocalDateTime to IST ZonedDateTime
                            ZonedDateTime utcTime = value.atZone(UTC_ZONE);
                            ZonedDateTime istTime = utcTime.withZoneSameInstant(IST_ZONE);
                            gen.writeString(FORMATTER.format(istTime));
                        }
                    }
                });
    }
}
