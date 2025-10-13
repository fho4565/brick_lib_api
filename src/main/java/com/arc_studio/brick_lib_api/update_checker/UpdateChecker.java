package com.arc_studio.brick_lib_api.update_checker;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * @author fho4565
 */
public final class UpdateChecker {
    private static final Gson GSON = new Gson();
    private static final Logger LOGGER = LoggerFactory.getLogger(UpdateChecker.class);
    public static final String MODRINTH_URL = "https://api.modrinth.com/v2/project/%s/version";
    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    public record Entry(String url,boolean isModrinth,Consumer<List<ModrinthModInfo>> modrinth,Consumer<String> custom) {
        public static Entry modrinth(String projectId,Consumer<List<ModrinthModInfo>> modrinth){
            return new Entry(projectId,true,modrinth,null);
        }

        public static Entry custom(String url,Consumer<String> custom){
            return new Entry(url,false,null,custom);
        }
    }
    public static List<ModrinthModInfo> checkFromModrinth(String projectId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(MODRINTH_URL.formatted(projectId)))
                    .GET()
                    .build();
            LOGGER.info("Try to check update of {} from modrinth", projectId);
            HttpResponse<String> res = null;
            res = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            LOGGER.info("data got");
            if (res.statusCode() < 200 || res.statusCode() >= 300) {
                LOGGER.warn("Received non-success status code {} from Modrinth for project {}", res.statusCode(), projectId);
                return List.of();
            }

            String body = res.body();
            if (body == null) {
                body = "";
            }

            TypeToken<List<ModrinthModInfo>> typeAdapter = new TypeToken<>() {};
            List<ModrinthModInfo> infos = GSON.fromJson(body, typeAdapter
                    //? if <= 1.19.2 {
                    /*.getType()
                    *///?}
            );
            return infos != null ? infos : List.of();
        } catch (Exception e) {
            LOGGER.error("Unexpected error during Modrinth update check for project {}", projectId, e);
            return List.of();
        }
    }

    public static CompletableFuture<List<ModrinthModInfo>> checkFromModrinthAsync(String projectId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(MODRINTH_URL.formatted(projectId)))
                    .GET()
                    .build();
            LOGGER.info("Try to check update of {} from modrinth", projectId);

            return HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .handle((res, throwable) -> {
                        if (throwable != null) {
                            LOGGER.warn("Failed to check mod update from Modrinth", throwable);
                            return List.of();
                        }

                        if (res.statusCode() < 200 || res.statusCode() >= 300) {
                            LOGGER.warn("Received non-success status code {} from Modrinth for project {}", res.statusCode(), projectId);
                            return List.of();
                        }

                        String body = res.body();
                        if (body == null) {
                            body = "";
                        }

                        try {
                            TypeToken<List<ModrinthModInfo>> typeAdapter = new TypeToken<>() {};
                            List<ModrinthModInfo> infos = GSON.fromJson(body, typeAdapter
                                    //? if <= 1.19.2 {
                                    /*.getType()
                                    *///?}
                            );
                            LOGGER.debug("UpdateChecker.checkFromModrinth result {}",
                                    infos != null ? "not null" : "is null");
                            return infos != null ? infos : List.of();
                        } catch (Exception e) {
                            LOGGER.error("Failed to parse Modrinth response for project {}", projectId, e);
                            return List.of();
                        }
                    });
        } catch (Exception e) {
            LOGGER.error("Failed to create Modrinth update check request for project {}", projectId, e);
            return CompletableFuture.completedFuture(List.of());
        }
    }

    public static CompletableFuture<String> checkFromCustomAsync(String checkUrl) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(checkUrl))
                    .GET()
                    .build();
            LOGGER.info("Try to check update from {}", checkUrl);

            return HTTP_CLIENT.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .handle((res, throwable) -> {
                        if (throwable != null) {
                            LOGGER.warn("Failed to check mod update from {}", checkUrl, throwable);
                            return "";
                        }

                        if (res.statusCode() < 200 || res.statusCode() >= 300) {
                            LOGGER.warn("Received non-success status code {} from {}",
                                    res.statusCode(), checkUrl);
                            return "";
                        }

                        return res.body() != null ? res.body() : "";
                    });
        } catch (Exception e) {
            LOGGER.error("Failed to create custom update check request for URL {}", checkUrl, e);
            return CompletableFuture.completedFuture("");
        }
    }


    public static String checkFromCustom(String checkUrl){
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(checkUrl)).GET().build();
            LOGGER.info("Try to check update from {}",checkUrl);
            HttpResponse<String> res = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            if (res.statusCode() < 200 || res.statusCode() >= 300) {
                return null;
            }
            return res.body() != null ? res.body() : "";
        } catch (IOException | InterruptedException e) {
            LOGGER.warn("Failed to check mod update from {}", checkUrl);
            LOGGER.warn(e.getLocalizedMessage());
            return "";
        }
    }
}
