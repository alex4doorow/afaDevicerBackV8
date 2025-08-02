package com.afa.devicer.back.utils;

import com.afa.devicer.back.components.ExcludeFromJacocoGeneratedReport;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public final class MaskHelper {
    private static final Set<String> MASKING_KEYS = new HashSet<>(Arrays.asList(
            "password",
            "access_token",
            "refresh_token"
    ));
    private static final int MIN_LENGTH = 4;

    private MaskHelper() {
    }

    /**
     * Mask secret string
     */
    public static String maskSecret(final String secret) {
        if (StringUtils.isBlank(secret)) {
            return secret;
        }

        final int valueLength = secret.length();
        if (valueLength <= MIN_LENGTH) {
            return "*".repeat(valueLength);
        } else {
            final int visibleLength = Math.min(valueLength / MIN_LENGTH, MIN_LENGTH);
            return secret.substring(0, visibleLength) +
                    "*".repeat(MIN_LENGTH) +
                    secret.substring(valueLength - visibleLength);
        }
    }

    /**
     * Mask JSON string with secret keys
     */
    public static String maskJson(final String json) {
        if (json == null) {
            return null;
        }

        try {
            final Object jsonObject = new JSONTokener(json).nextValue();
            if (jsonObject instanceof JSONObject input) {
                return maskJsonObject(input).toString();
            } else if (jsonObject instanceof JSONArray inputArray) {
                return maskJsonArray(inputArray).toString();
            } else {
                return json;
            }
        } catch (JSONException e) {
            return null;
        }
    }

    @ExcludeFromJacocoGeneratedReport
    private static JSONArray maskJsonArray(final JSONArray array) {
        final JSONArray result = new JSONArray();
        for (int i = 0; i < array.length(); i++) {
            final Object item = array.get(i);
            if (item instanceof JSONObject) {
                result.put(new JSONObject(maskJson(item.toString())));
            } else if (item instanceof JSONArray) {
                result.put(maskJsonArray((JSONArray) item));
            } else {
                result.put(item);
            }
        }
        return result;
    }

    private static JSONObject maskJsonObject(final JSONObject input) {
        final JSONObject output = new JSONObject();

        for (final String key : input.keySet()) {
            final Object value = input.get(key);
            if (value instanceof JSONArray jsonArray) {
                output.put(key, maskJsonArray(jsonArray));
            } else if (value instanceof JSONObject jsonObject) {
                output.put(key, maskJsonObject(jsonObject));
            } else if (value instanceof String secret && MASKING_KEYS.contains(key)) {
                output.put(key, maskSecret(secret));
            } else {
                output.put(key, value);
            }
        }
        return output;
    }
}
