package com.arc_studio.brick_lib_api.codecs;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;

import java.util.Optional;

/**
 * @author fho4565
 */
public class CodecTools {
    public static<T,A> Optional<T> encodeAndGetOptional(Codec<A> codec, DynamicOps<T> ops, A input){
        return codec.encodeStart(ops, input).result();
    }
    public static<T,A> T encodeAndGet(Codec<A> codec, DynamicOps<T> ops, A input) {
        return codec.encodeStart(ops, input)
        //? if >= 1.20.6 {
        /*.getOrThrow()
        *///?} else {
        .get().orThrow()
        //?}
        ;
    }

    public static<T,A> A decodeAndGet(Codec<A> codec, DynamicOps<T> ops, T input){
        return codec.decode(ops, input).result().orElseThrow().getFirst();
    }

    public static<T,A> A decodeAndGet(Codec<A> codec, Dynamic<T> dynamic){
        return codec.decode(dynamic.getOps(), dynamic.getValue()).result().orElseThrow().getFirst();
    }

    public static<T,A> Optional<A> decodeAndGetOptional(Codec<A> codec, Dynamic<T> dynamic){
        Optional<Pair<A, T>> result = codec.decode(dynamic.getOps(), dynamic.getValue()).result();
        return result.map(Pair::getFirst);
    }

    public static<T,A> Optional<A> decodeAndGetOptional(Codec<A> codec, DynamicOps<T> ops, T input){
        Optional<Pair<A, T>> result = codec.decode(ops, input).result();
        return result.map(Pair::getFirst);
    }
}
