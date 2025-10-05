package com.arc_studio.brick_lib_api.core.json_function;

import com.google.gson.*;
import com.google.gson.JsonObject;
import com.google.gson.internal.LazilyParsedNumber;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author fho4565
 */
public final class JsonFunctionExecutor {
    private static final String[] FUNCTION_KEYS = new String[]{"function","func","fun","f"};
    private static final String[] VALUE_KEYS = new String[]{"value","var","val","v"};

    private JsonFunctionExecutor() {
    }

    public static Object execute(JsonElement json) {
        JsonObject object = json.getAsJsonObject();
        String functionKey = isFunction(object);
        if (functionKey != null) {
            return handleFunctionCall(object,functionKey);
        }
        String valueKey = isValue(object);
        if (valueKey != null) {
            return handleValueAssignment(object,valueKey);
        }
        throw new IllegalArgumentException("Unknown json function operation type");
    }

    public static Object execute(String json) {
        try {
            return execute(JsonParser.parseString(json));
        } catch (JsonSyntaxException e) {
            throw new JsonSyntaxException(e);
        } catch (RuntimeException e) {
            throw new RuntimeException(e);
        }
    }
    private static String isFunction(JsonObject jsonObject){
        for (String key : FUNCTION_KEYS) {
            if(jsonObject.has(key)){
                return key;
            }
        }
        return null;
    }

    private static String isValue(JsonObject jsonObject){
        for (String key : VALUE_KEYS) {
            if(jsonObject.has(key)){
                return key;
            }
        }
        return null;
    }

    private static Object handleFunctionCall(JsonObject jsonObject, String functionKey) {
        String id = jsonObject.get(functionKey).getAsString();
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("function name cannot be null");
        }

        JsonFunction function = JsonFunctionRegistry.of().get(id);
        if (function == null) {
            throw new IllegalArgumentException("Unregistered Function: " + id);
        }

        JsonElement argsElement = jsonObject.get("args");
        JsonArray args;
        if(argsElement == null){
            args = new JsonArray();
        }else{
            args = argsElement.getAsJsonArray();
        }
        JsonArray processed = new JsonArray();
        if (args != null) {
            ArrayList<JsonElement> copied = new ArrayList<>(args.asList());
            for (JsonElement value : copied) {
                if(value.isJsonObject()) {
                    final Object obj = execute(String.valueOf(value));
                    if (obj instanceof JsonElement jsonValue) {
                        processed.add(jsonValue);
                    } else {
                        String objString = obj.toString();
                        if (Boolean.parseBoolean(objString)) {
                            processed.add((boolean) obj);
                        } else if (NumberUtils.isCreatable(objString)) {
                            final int anInt = NumberUtils.toInt(objString);
                            if (anInt != 0) {
                                processed.add(anInt);
                            } else {
                                final double aDouble = NumberUtils.toDouble(objString);
                                if (aDouble != 0) {
                                    processed.add(aDouble);
                                }
                            }
                        } else {
                            processed.add(objString);
                        }
                    }
                } else if (value.isJsonPrimitive()) {
                    JsonPrimitive primitive = value.getAsJsonPrimitive();
                    if (primitive.isBoolean()) {
                        processed.add(primitive.getAsBoolean());
                    } else if (primitive.isString()) {
                        processed.add(primitive.getAsString());
                    } else if (primitive.isNumber()) {
                        Number number = primitive.getAsNumber();
                        if(number instanceof Short s){
                            processed.add(s);
                        } else if (number instanceof Integer i) {
                            processed.add(i);
                        } else if (number instanceof Long l) {
                            processed.add(l);
                        } else if (number instanceof Float f) {
                            processed.add(f);
                        } else if (number instanceof Double d) {
                            processed.add(d);
                        } else if (number instanceof Byte b) {
                            processed.add(b);
                        } else if (number instanceof BigInteger bi) {
                            processed.add(bi);
                        } else if (number instanceof BigDecimal bd) {
                            processed.add(bd);
                        } else if (number instanceof LazilyParsedNumber bd) {
                            processed.add(bd.doubleValue());
                        } else {
                            throw new UnsupportedOperationException("Unknown number type "+number+" class : "+number.getClass());
                        }
                    } else {
                        throw new IllegalArgumentException("Unknown json primitive type");
                    }
                }
            }
        }

        return function.execute(processed);
    }
    
    private static Object handleValueAssignment(JsonObject jsonObject, String valueKey) {
        JsonElement value = jsonObject.get(valueKey);
        if (value == null) {
            throw new IllegalArgumentException("Value cannot be null");
        }
        return jsonValueToJava(value);
    }
    
    // 将JsonValue转换为Java对象
    private static Object jsonValueToJava(JsonElement value) {
        if (value.isJsonPrimitive()) {
            JsonPrimitive primitive = value.getAsJsonPrimitive();
            if (primitive.isBoolean()) {
                return primitive.getAsBoolean();
            } else if (primitive.isString()) {
                return primitive.getAsString();
            } else if (primitive.isNumber()) {
                Number number = primitive.getAsNumber();
                if(number instanceof Short s){
                    return s;
                } else if (number instanceof Integer i) {
                    return i;
                } else if (number instanceof Long l) {
                    return l;
                } else if (number instanceof Float f) {
                    return f;
                } else if (number instanceof Double d) {
                    return d;
                } else if (number instanceof Byte b) {
                    return b;
                } else if (number instanceof BigInteger bi) {
                    return bi;
                } else if (number instanceof BigDecimal bd) {
                    return bd;
                } else {
                    throw new UnsupportedOperationException("Unknown number type "+number);
                }
            } else {
                throw new IllegalArgumentException("Unknown json primitive type");
            }
        } else if (value.isJsonObject()) {
            return value.getAsJsonObject();
        } else if (value.isJsonNull()) {
            return null;
        } else if (value.isJsonArray()) {
            return value.getAsJsonArray();
        }else {
            return new JsonParseException("Unknown Json type "+value);
        }
    }
    
    // 将JsonArray转换为Java数组
    private static Object[] jsonArrayToJava(JsonArray array) {
        return array.asList().stream()
            .map(JsonFunctionExecutor::jsonValueToJava)
            .toArray();
    }
    
    // 将JsonObject转换为Java Map
    private static Map<String, Object> jsonObjectToMap(JsonObject jsonObject) {
        Map<String, Object> map = new HashMap<>();
        jsonObject.asMap().forEach((key, value) ->
            map.put(key, jsonValueToJava(value)));
        return map;
    }
}