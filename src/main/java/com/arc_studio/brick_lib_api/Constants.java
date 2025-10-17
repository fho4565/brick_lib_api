package com.arc_studio.brick_lib_api;


import com.arc_studio.brick_lib_api.core.PlatformInfo;
import com.arc_studio.brick_lib_api.platform.Platform;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelResource;
import org.jetbrains.annotations.ApiStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Brick Lib提供的一些变量，这些变量在某些情况下不保证一定是初始化的，在调用前需要检查值
 */
public class Constants {
    private static String[] launchArgs;
    private static boolean initiatedGeneral = false;
    private static boolean initiatedWorld = false;
    private static Path versionFolderPath;
    private static Path globalConfigFolderPath;
    private static Path defaultConfigFolderPath;
    private static Path brickLibWorldFolder;
    private static Path versionDataFolder;
    private static Path worldFolder;
    private static Path serverConfigFolder;
    private static MinecraftServer currentServer;
    private static boolean isInDevelopEnvironment = false;
    private static PlatformInfo platform;

    /**
     * <p>获取当前游戏版本文件夹，在模组初始化时调用</p>
     *
     * @return 游戏版本文件夹，如果在开发环境，会返回build文件夹的位置
     */
    public static Path versionFolderPath() {
        return versionFolderPath;
    }

    /**
     * 获取Brick Lib在世界文件夹根目录的数据文件夹
     * */
    public static Path brickLibWorldFolder() {
        return brickLibWorldFolder;
    }

    /**
     * 获取当前的服务器
     */
    public static MinecraftServer currentServer() {
        return currentServer;
    }

    /**
     * 获取当前服务器存档的文件夹路径
     */
    public static Path worldFolder() {
        return worldFolder;
    }

    /**
     * 游戏的启动参数，不包括main参数
     */
    public static String[] launchArgs() {
        return launchArgs;
    }

    /**
     * <p>用于检查变量是否已经初始化，只检查在模组初始化时进行初始化的变量</p>
     */
    public static boolean isGeneralInitiated() {
        return initiatedGeneral;
    }

    /**
     * 世界变量是否初始化
     */
    public static boolean isWorldInitiated() {
        return initiatedWorld;
    }

    /**
     * 检查当前游戏环境是否是开发环境
     */
    public static Boolean isInDevelopEnvironment() {
        return isInDevelopEnvironment;
    }

    @ApiStatus.Internal
    public static void initGeneral() {
        if(!initiatedGeneral) {
            launchArgs = Platform.launchArgs();
            platform = Platform.platform();
            isInDevelopEnvironment = Platform.isDev();
            versionFolderPath = Platform.versionPath();
            globalConfigFolderPath = versionFolderPath.resolve("config");
            defaultConfigFolderPath = versionFolderPath.resolve("defaultconfigs");
            versionDataFolder = versionFolderPath.resolve("brickLib");
            initiatedGeneral = true;
        }
    }

    @ApiStatus.Internal
    public static void installWorldVariables(MinecraftServer server) {
        if (!initiatedWorld) {
            currentServer = server;
            worldFolder = currentServer.getWorldPath(LevelResource.ROOT).toAbsolutePath();
            brickLibWorldFolder = Constants.worldFolder().resolve("brickLib");
            serverConfigFolder = worldFolder.resolve("serverconfig");
            if (!Files.isDirectory(serverConfigFolder)) {
                try {
                    Files.createDirectories(serverConfigFolder);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            File dir = brickLibWorldFolder.toFile();
            if (!dir.exists()) {
                if (!dir.mkdirs()) {
                    BrickLibAPI.LOGGER.error("Failed to create brick lib folder");
                }
            }
            File versionDataDir = versionDataFolder.toFile();
            if (!versionDataDir.exists()) {
                if (!versionDataDir.mkdirs()) {
                    BrickLibAPI.LOGGER.error("Failed to create brick lib version data folder");
                }
            }
            initiatedWorld = true;
        }
    }

    @ApiStatus.Internal
    public static void uninstallWorldVariables() {
        brickLibWorldFolder = null;
        currentServer = null;
        serverConfigFolder = null;
        initiatedWorld = false;
    }

    /**
     * 获取模组运行的平台
     * */
    public static PlatformInfo platform() {
        return platform;
    }

    /**
     * 获取Brick Lib在版本文件夹根目录的数据文件夹
     * */
    public static Path versionDataFolder() {
        return versionDataFolder;
    }

    public static Path globalConfigFolderPath() {
        return globalConfigFolderPath;
    }

    public static Path serverConfigFolder() {
        return serverConfigFolder;
    }

    public static Path defaultConfigFolderPath() {
        return defaultConfigFolderPath;
    }
}
