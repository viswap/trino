/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.trino.plugin.elasticsearch.decoders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.airlift.json.ObjectMapperProvider;
import io.airlift.slice.Slices;
import io.trino.spi.TrinoException;
import io.trino.spi.block.BlockBuilder;
import org.elasticsearch.search.SearchHit;

import java.util.function.Supplier;

import static io.trino.spi.StandardErrorCode.TYPE_MISMATCH;
import static io.trino.spi.type.VarcharType.VARCHAR;
import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

public class RawJsonDecoder
        implements Decoder
{
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapperProvider().get();
    private final String path;

    public RawJsonDecoder(String path)
    {
        this.path = requireNonNull(path, "path is null");
    }

    @Override
    public void decode(SearchHit hit, Supplier<Object> getter, BlockBuilder output)
    {
        Object value = getter.get();
        if (value == null) {
            output.appendNull();
        }
        else {
            try {
                String rawJsonValue = OBJECT_MAPPER.writeValueAsString(value);
                VARCHAR.writeSlice(output, Slices.utf8Slice(rawJsonValue));
            }
            catch (JsonProcessingException e) {
                throw new TrinoException(
                        TYPE_MISMATCH,
                        format("Expected valid json for field '%s' marked to be rendered as JSON: %s [%s]", path, value, value.getClass().getSimpleName()),
                        e);
            }
        }
    }
}
