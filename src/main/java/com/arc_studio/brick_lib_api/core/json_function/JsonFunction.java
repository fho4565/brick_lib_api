package com.arc_studio.brick_lib_api.core.json_function;

import com.google.gson.JsonArray;

/**
 * JsonFunction可以在Json中被解析调用，且拥有返回值
 * */
@FunctionalInterface
public interface JsonFunction {
    Object execute(JsonArray args);
}
