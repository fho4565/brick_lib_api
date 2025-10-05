package com.arc_studio.brick_lib_api.core;

import java.util.Map;

/**
 * @author fho4565
 */
public final class EnvType {
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

    public static EnvType of(){
        return new EnvType();
    }

    public EnvType setClient() {
        this.types |= CLIENT;
        return this;
    }

    public EnvType setOnlyClient() {
        removeServer();
        setClient();
        return this;
    }

    public EnvType setServer() {
        this.types |= SERVER;
        return this;
    }

    public EnvType setOnlyServer() {
        removeClient();
        setServer();
        return this;
    }

    public EnvType setForge() {
        this.types |= FORGE;
        return this;
    }

    public EnvType setOnlyForge() {
        removeFabric();
        removeNeoForge();
        setForge();
        return this;
    }

    public EnvType setOnlyFabric() {
        removeForge();
        removeNeoForge();
        setFabric();
        return this;
    }

    public EnvType setOnlyNeoForge() {
        removeForge();
        removeFabric();
        setNeoForge();
        return this;
    }

    public EnvType setFabric() {
        this.types |= FABRIC;
        return this;
    }

    public EnvType setNeoForge() {
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

    public EnvType removeClient() {
        this.types &= ~CLIENT;
        return this;
    }

    public EnvType removeServer() {
        this.types &= ~SERVER;
        return this;
    }

    public EnvType removeForge() {
        this.types &= ~FORGE;
        return this;
    }

    public EnvType removeFabric() {
        this.types &= ~FABRIC;
        return this;
    }

    public EnvType removeNeoForge() {
        this.types &= ~NEOFORGE;
        return this;
    }

    public EnvType removeAll() {
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
        if (obj instanceof EnvType envType) {
            return types == envType.types;
        }
        return false;
    }

    public int loaderEquals(EnvType other){
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

    public int sideEquals(EnvType other) {
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