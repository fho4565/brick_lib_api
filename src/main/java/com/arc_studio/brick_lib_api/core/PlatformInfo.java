package com.arc_studio.brick_lib_api.core;

import java.util.Map;

/**
 * 可混合保存当前端位，加载器的类
 * @author fho4565
 */
public final class PlatformInfo {
    public static final int
            FALSE = 0,
            CLIENT = 1,
            SERVER = 2,
            CLIENT_AND_SERVER = 4,
            FORGE = 8,
            FABRIC = 16,
            NEOFORGE = 32,
            FORGELIKE = 64,
            TRUE = 128;
    private static final Map<Integer, String> MAP = Map.of(
            CLIENT, "CLIENT",
            SERVER, "SERVER",
            FORGE, "FORGE",
            FABRIC, "FABRIC",
            NEOFORGE, "NEOFORGE"
    );
    private int types = 0;

    public static PlatformInfo of(){
        return new PlatformInfo();
    }

    public PlatformInfo setClient() {
        this.types |= CLIENT;
        return this;
    }

    public PlatformInfo setOnlyClient() {
        removeServer();
        setClient();
        return this;
    }

    public PlatformInfo setServer() {
        this.types |= SERVER;
        return this;
    }

    public PlatformInfo setOnlyServer() {
        removeClient();
        setServer();
        return this;
    }

    public PlatformInfo setForge() {
        this.types |= FORGE;
        return this;
    }

    public PlatformInfo setOnlyForge() {
        removeFabric();
        removeNeoForge();
        setForge();
        return this;
    }

    public PlatformInfo setOnlyFabric() {
        removeForge();
        removeNeoForge();
        setFabric();
        return this;
    }

    public PlatformInfo setOnlyNeoForge() {
        removeForge();
        removeFabric();
        setNeoForge();
        return this;
    }

    public PlatformInfo setFabric() {
        this.types |= FABRIC;
        return this;
    }

    public PlatformInfo setNeoForge() {
        this.types |= NEOFORGE;
        return this;
    }

    public boolean isClient() {
        return (types & CLIENT) == CLIENT;
    }
    public boolean isOnlyClient(){
        return isClient() && !isServer();
    }

    public boolean isServer() {
        return (types & SERVER) == SERVER;
    }
    public boolean isOnlyServer(){
        return isServer() && !isClient();
    }

    public boolean isForge() {
        return (types & FORGE) == FORGE;
    }

    public boolean isFabric() {
        return (types & FABRIC) == FABRIC;
    }

    public boolean isNeoForge() {
        return (types & NEOFORGE) == NEOFORGE;
    }

    public PlatformInfo removeClient() {
        this.types &= ~CLIENT;
        return this;
    }

    public PlatformInfo removeServer() {
        this.types &= ~SERVER;
        return this;
    }

    public PlatformInfo removeForge() {
        this.types &= ~FORGE;
        return this;
    }

    public PlatformInfo removeFabric() {
        this.types &= ~FABRIC;
        return this;
    }

    public PlatformInfo removeNeoForge() {
        this.types &= ~NEOFORGE;
        return this;
    }

    public PlatformInfo removeAll() {
        this.types = 0;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Sides[");
        for (int i = 0; i < 5; i++) {
            if ((types & (1 << i)) == (1 << i)) {
                sb.append(MAP.get(1 << i)).append(",");
            }
        }
        if(sb.lastIndexOf(",") != -1){
            sb.deleteCharAt(sb.lastIndexOf(","));
        }
        return sb.append("]").toString();
    }

    @Override
    public int hashCode() {
        return types;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PlatformInfo platformInfo) {
            return types == platformInfo.types;
        }
        return false;
    }

    public int loaderEquals(PlatformInfo other){
        if (this.isFabric()) {
            if (other.isFabric()) {
                return FABRIC;
            } else {
                return FALSE;
            }
        } else if (this.isForge()) {
            if (other.isForge()) {
                return FORGE;
            } else {
                return FALSE;
            }
        } else if (this.isNeoForge()) {
            if (other.isNeoForge()) {
                return NEOFORGE;
            } else {
                return FALSE;
            }
        }
        return FALSE;
    }

    public int sideEquals(PlatformInfo other) {
        boolean c = this.isClient() && other.isClient();
        boolean s = this.isServer() && other.isServer();
        if (c && s) {
            return CLIENT_AND_SERVER;
        } else if (c) {
            return CLIENT;
        } else if (s) {
            return SERVER;
        } else {
            return FALSE;
        }
    }
}
