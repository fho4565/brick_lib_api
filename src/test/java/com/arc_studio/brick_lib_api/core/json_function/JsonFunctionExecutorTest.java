package com.arc_studio.brick_lib_api.core.json_function;

import com.google.gson.JsonElement;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class JsonFunctionExecutorTest {

    public static JsonFunctionRegistry registry = JsonFunctionRegistry.of();

    @BeforeAll
    static void beforeAll() {
        registry.register("add", jsonValues -> {
            double num1 = toDouble(jsonValues.get(0));
            double num2 = toDouble(jsonValues.get(1));
            return num1 + num2;
        });
        registry.register("sum", jsonValues -> {
            double sum = 0;
            for (JsonElement jsonValue : jsonValues) {
                sum += toDouble(jsonValue);
            }
            return sum;
        });

        registry.register("greet", jsonValues -> "Hello, " + toText(jsonValues.get(0)));

        registry.register("currentTime", jsonValues -> System.currentTimeMillis());
    }

    @Test
    void complexAdd() {
        System.out.println("Result : " + JsonFunctionExecutor.execute("""
                {"function":"add","args":[{"fun":"add","args":[{"f":"add","args":[{"value":"9"},3]},4]},3]}
                """));
    }

    @Test
    void greet() {
        System.out.println("Greet : " + JsonFunctionExecutor.execute("""
                {"func":"greet","args":["John"]}
                """));
    }

    @Test
    void sum() {
        System.out.println("Sum : " + JsonFunctionExecutor.execute("""
                {
                    "func": "sum",
                    "args": [
                        5,
                        8
                    ]
                }
                """));
    }

    @Test
    void currentTime() {
        System.out.println("Current Time : " + JsonFunctionExecutor.execute("""
                {
                    "func": "currentTime",
                    "args": [
                        5,
                        8
                    ]
                }
                """));
    }

    @Test
    void currentTimeNoArg() {
        System.out.println("Current Time : " + JsonFunctionExecutor.execute("""
                {
                    "func": "currentTime"
                }
                """));
    }

    @Test
    void valueTest() {
        System.out.println("Value : " + JsonFunctionExecutor.execute("""
                {"value":"Direct Value"}
                """));

        System.out.println("Complex Value : " + JsonFunctionExecutor.execute("""
                {"type":"value","value":{"name":"John","age":30,"scores":[90,85,95]}}
                """));
    }

    private static double toDouble(JsonElement value) {
        if (value.isJsonPrimitive()) {
            if (value.getAsJsonPrimitive().isNumber()) {
                return value.getAsNumber().doubleValue();
            }
        }
        return Double.parseDouble(value.toString());
    }

    private static String toText(JsonElement value) {
        if (value.isJsonPrimitive()) {
            if (value.getAsJsonPrimitive().isString()) {
                return value.getAsString();
            }
        }
        return value.toString();
    }

}