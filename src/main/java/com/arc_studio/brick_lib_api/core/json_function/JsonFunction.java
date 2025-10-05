package com.arc_studio.brick_lib_api.core.json_function;

import com.google.gson.JsonArray;

/**
 * @author fho4565
 */
@FunctionalInterface
public interface JsonFunction {
    Object execute(JsonArray args);
}