package kg.founders.bff.config.settings.gson;

import com.google.gson.*;
import org.springframework.data.domain.Page;

import java.lang.reflect.Type;

public class PageSerializer implements JsonSerializer<Page<?>> {

    @Override
    public JsonElement serialize(Page<?> page, Type type, JsonSerializationContext context) {
        JsonObject json = new JsonObject();
        json.add("content", context.serialize(page.getContent()));
        json.addProperty("totalElements", page.getTotalElements());
        json.addProperty("totalPages", page.getTotalPages());
        json.addProperty("number", page.getNumber());
        json.addProperty("size", page.getSize());
        json.addProperty("first", page.isFirst());
        json.addProperty("last", page.isLast());
        json.addProperty("numberOfElements", page.getNumberOfElements());
        json.addProperty("empty", page.isEmpty());
        return json;
    }
}

