/**
 * The MIT License
 *
 * Copyright 2025 Simon Trasler
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.trasler.utils.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;

/**
 *
 * @author Simon Trasler
 */
public class TrieConfigTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testJsonFile() throws IOException {
        URL url = getClass().getClassLoader().getResource("config.json");
        String string = Files.readString(Paths.get(url.getPath()));

        try {
            TrieConfig trieConfig = objectMapper.readValue(string, TrieConfig.class);

            assertEquals(1, trieConfig.get(Map.of("a", "a1", "b", "b1")));
            assertEquals(2, trieConfig.get(Map.of("a", "a1", "b", "b2")));
            assertEquals(3, trieConfig.get(Map.of("a", "a2", "b", "b1")));
            assertEquals(4, trieConfig.get(Map.of("a", "a2", "b", "b2")));
        }
        catch (JsonProcessingException e) {
            fail("Unexpected exception thrown");
        }
    }

    @Test
    public void testTextFile() {
        URL url = getClass().getClassLoader().getResource("config.txt");

        try {
            TrieConfig trieConfig = TrieConfig.from(Paths.get(url.getPath()), Integer.class);

            assertEquals(1, trieConfig.get(Map.of("a", "a1", "b", "b1")));
            assertEquals(2, trieConfig.get(Map.of("a", "a1", "b", "b2")));
            assertEquals(3, trieConfig.get(Map.of("a", "a2", "b", "b1")));
            assertEquals(4, trieConfig.get(Map.of("a", "a2", "b", "b2")));
        }
        catch (IOException e) {
            fail("Unexpected exception thrown");
        }
    }
}
