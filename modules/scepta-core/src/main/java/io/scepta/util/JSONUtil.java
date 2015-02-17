/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.scepta.util;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This class provides helper functions for dealing with JSON.
 *
 */
public class JSONUtil {

    private static final ObjectMapper MAPPER=new ObjectMapper();

    /**
     * This method serializes the supplied object into a JSON string representation.
     *
     * @param obj The object to serialize
     * @return The serialized JSON
     */
    public static String serialize(Object obj) {
        try {
            return MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            // TODO: REPORT ERROR
            e.printStackTrace();
        }
        return null;
    }

    /**
     * This method serializes the supplied object into a JSON string representation.
     *
     * @param obj The object to serialize
     * @param os The output stream
     * @throws IOException Failed to serialize
     */
    public static void serialize(Object obj, java.io.OutputStream os) throws IOException {
        MAPPER.writeValue(os, obj);
    }

    /**
     * This method deserializes the supplied JSON as an object of the supplied
     * class.
     *
     * @param json The JSON representation
     * @param cls The class
     * @return The deserialized object
     */
    public static Object deserialize(String json, Class<?> cls) {
        try {
            return MAPPER.readValue(json, cls);
        } catch (IOException e) {
            // TODO: REPORT ERROR
            e.printStackTrace();
        }

        return null;
    }

    /**
     * This method deserializes the supplied JSON as an object of the supplied
     * class.
     *
     * @param json The JSON representation
     * @param cls The class
     * @return The deserialized object
     */
    public static Object deserialize(java.io.InputStream is, Class<?> cls) {
        try {
            return MAPPER.readValue(is, cls);
        } catch (IOException e) {
            // TODO: REPORT ERROR
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (Exception e) {
                // TODO: REPORT ERROR
                e.printStackTrace();
           }
        }

        return null;
    }

}
