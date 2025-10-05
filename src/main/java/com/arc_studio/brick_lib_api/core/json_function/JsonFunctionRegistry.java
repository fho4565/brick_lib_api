package com.arc_studio.brick_lib_api.core.json_function;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fho4565
 */
public class JsonFunctionRegistry {
    private static final JsonFunctionRegistry FUNCTION_REGISTRY = new JsonFunctionRegistry();
    private final Map<String, JsonFunction> functions = new HashMap<>();
    private JsonFunctionRegistry(){}
    public static JsonFunctionRegistry of(){
        return FUNCTION_REGISTRY;
    }
    
    public void register(String id, JsonFunction function) {
        functions.put(id, function);
    }
    
    public JsonFunction get(String id) {
        return functions.get(id);
    }
}