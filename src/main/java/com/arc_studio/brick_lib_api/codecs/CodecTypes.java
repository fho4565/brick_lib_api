package com.arc_studio.brick_lib_api.codecs;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.*;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import it.unimi.dsi.fastutil.floats.FloatList;
import net.minecraft.Util;
import net.minecraft.core.HolderSet;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.NotNull;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
//? if >= 1.20.6 {
/*import net.minecraft.network.chat.ComponentSerialization;
*///?}

import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;


/**
 * @author fho4565
 */
@SuppressWarnings({"unused","unchecked"})
public class CodecTypes {
    public static final Codec<Integer> NON_NEGATIVE_INT = intRangeWithMessage(
            0, Integer.MAX_VALUE, integer -> "Value must be non-negative: " + integer
    );
    public static final Codec<Integer> POSITIVE_INT = intRangeWithMessage(1, Integer.MAX_VALUE, integer -> "Value must be positive: " + integer);
    public static final Codec<Float> POSITIVE_FLOAT = floatRangeMinExclusiveWithMessage(
            0.0F, Float.MAX_VALUE, aFloat -> "Value must be positive: " + aFloat
    );
    public static PrimitiveCodec<Boolean> BOOL = new PrimitiveCodec<>() {
        @Override
        public <T> DataResult<Boolean> read(final DynamicOps<T> ops, final T input) {
            return ops
                    .getBooleanValue(input);
        }

        @Override
        public <T> T write(final DynamicOps<T> ops, final Boolean value) {
            return ops.createBoolean(value);
        }

        @Override
        public String toString() {
            return "Bool";
        }
    };
    public static PrimitiveCodec<Byte> BYTE = new PrimitiveCodec<>() {
        @Override
        public <T> DataResult<Byte> read(final DynamicOps<T> ops, final T input) {
            return ops
                    .getNumberValue(input)
                    .map(Number::byteValue);
        }

        @Override
        public <T> T write(final DynamicOps<T> ops, final Byte value) {
            return ops.createByte(value);
        }

        @Override
        public String toString() {
            return "Byte";
        }
    };
    public static PrimitiveCodec<Short> SHORT = new PrimitiveCodec<>() {
        @Override
        public <T> DataResult<Short> read(final DynamicOps<T> ops, final T input) {
            return ops
                    .getNumberValue(input)
                    .map(Number::shortValue);
        }

        @Override
        public <T> T write(final DynamicOps<T> ops, final Short value) {
            return ops.createShort(value);
        }

        @Override
        public String toString() {
            return "Short";
        }
    };
    public static PrimitiveCodec<Integer> INT = new PrimitiveCodec<>() {
        @Override
        public <T> DataResult<Integer> read(final DynamicOps<T> ops, final T input) {
            return ops
                    .getNumberValue(input)
                    .map(Number::intValue);
        }

        @Override
        public <T> T write(final DynamicOps<T> ops, final Integer value) {
            return ops.createInt(value);
        }

        @Override
        public String toString() {
            return "Int";
        }
    };
    public static PrimitiveCodec<Long> LONG = new PrimitiveCodec<>() {
        @Override
        public <T> DataResult<Long> read(final DynamicOps<T> ops, final T input) {
            return ops
                    .getNumberValue(input)
                    .map(Number::longValue);
        }

        @Override
        public <T> T write(final DynamicOps<T> ops, final Long value) {
            return ops.createLong(value);
        }

        @Override
        public String toString() {
            return "Long";
        }
    };
    public static PrimitiveCodec<Float> FLOAT = new PrimitiveCodec<>() {
        @Override
        public <T> DataResult<Float> read(final DynamicOps<T> ops, final T input) {
            return ops
                    .getNumberValue(input)
                    .map(Number::floatValue);
        }

        @Override
        public <T> T write(final DynamicOps<T> ops, final Float value) {
            return ops.createFloat(value);
        }

        @Override
        public String toString() {
            return "Float";
        }
    };
    public static PrimitiveCodec<Double> DOUBLE = new PrimitiveCodec<>() {
        @Override
        public <T> DataResult<Double> read(final DynamicOps<T> ops, final T input) {
            return ops
                    .getNumberValue(input)
                    .map(Number::doubleValue);
        }

        @Override
        public <T> T write(final DynamicOps<T> ops, final Double value) {
            return ops.createDouble(value);
        }

        @Override
        public String toString() {
            return "Double";
        }
    };
    public static PrimitiveCodec<String> STRING = new PrimitiveCodec<>() {
        @Override
        public <T> DataResult<String> read(final DynamicOps<T> ops, final T input) {
            return ops
                    .getStringValue(input);
        }

        @Override
        public <T> T write(final DynamicOps<T> ops, final String value) {
            return ops.createString(value);
        }

        @Override
        public String toString() {
            return "String";
        }
    };
    public static PrimitiveCodec<ByteBuffer> BYTE_BUFFER = new PrimitiveCodec<>() {
        @Override
        public <T> DataResult<ByteBuffer> read(final DynamicOps<T> ops, final T input) {
            return ops
                    .getByteBuffer(input);
        }

        @Override
        public <T> T write(final DynamicOps<T> ops, final ByteBuffer value) {
            return ops.createByteList(value);
        }

        @Override
        public String toString() {
            return "ByteBuffer";
        }
    };
    public static PrimitiveCodec<IntStream> INT_STREAM = new PrimitiveCodec<>() {
        @Override
        public <T> DataResult<IntStream> read(final DynamicOps<T> ops, final T input) {
            return ops
                    .getIntStream(input);
        }

        @Override
        public <T> T write(final DynamicOps<T> ops, final IntStream value) {
            return ops.createIntList(value);
        }

        @Override
        public String toString() {
            return "IntStream";
        }
    };
    public static PrimitiveCodec<LongStream> LONG_STREAM = new PrimitiveCodec<>() {
        @Override
        public <T> DataResult<LongStream> read(final DynamicOps<T> ops, final T input) {
            return ops
                    .getLongStream(input);
        }

        @Override
        public <T> T write(final DynamicOps<T> ops, final LongStream value) {
            return ops.createLongList(value);
        }

        @Override
        public String toString() {
            return "LongStream";
        }
    };
    public static Codec<Dynamic<?>> PASSTHROUGH = new Codec<>() {
        @Override
        public <T> DataResult<Pair<Dynamic<?>, T>> decode(final DynamicOps<T> ops, final T input) {
            return DataResult.success(Pair.of(new Dynamic<>(ops, input), ops.empty()));
        }

        @Override
        public <T> DataResult<T> encode(final Dynamic<?> input, final DynamicOps<T> ops, final T prefix) {
            if (input.getValue() == input.getOps().empty()) {
                // nothing to merge, return rest
                return DataResult.success(prefix, Lifecycle.experimental());
            }

            final T casted = input.convert(ops).getValue();
            if (prefix == ops.empty()) {
                // no need to merge anything, return the old value
                return DataResult.success(casted, Lifecycle.experimental());
            }

            final DataResult<T> toMap = ops.getMap(casted).flatMap(map -> ops.mergeToMap(prefix, map));
            return toMap.result().map(DataResult::success).orElseGet(() -> {
                final DataResult<T> toList = ops.getStream(casted).flatMap(stream -> ops.mergeToList(prefix, stream.collect(Collectors.toList())));
                return toList.result().map(DataResult::success).orElseGet(() ->
                        DataResult.error(() -> "Don't know how to merge " + prefix + " and " + casted, prefix, Lifecycle.experimental())
                );
            });
        }

        @Override
        public String toString() {
            return "passthrough";
        }
    };
    public static MapCodec<Unit> EMPTY = MapCodec.of(Encoder.empty(), Decoder.unit(Unit.INSTANCE));

    private static <N extends Number & Comparable<N>> Function<N, DataResult<N>> checkRange(final N minInclusive, final N maxInclusive) {
        return n -> {
            if (n.compareTo(minInclusive) >= 0 && n.compareTo(maxInclusive) <= 0) {
                return DataResult.success(n);
            }
            return DataResult.error(() -> "Value " + n + " outside of range [" + minInclusive + ":" + maxInclusive + "]", n);
        };
    }

    public static Codec<Integer> intRange(final int minInclusive, final int maxInclusive) {
        final Function<Integer, DataResult<Integer>> checker = checkRange(minInclusive, maxInclusive);
        return Codec.INT.flatXmap(checker, checker);
    }

    public static Codec<Float> floatRange(final float minInclusive, final float maxInclusive) {
        final Function<Float, DataResult<Float>> checker = checkRange(minInclusive, maxInclusive);
        return Codec.FLOAT.flatXmap(checker, checker);
    }

    public static Codec<Double> doubleRange(final double minInclusive, final double maxInclusive) {
        final Function<Double, DataResult<Double>> checker = checkRange(minInclusive, maxInclusive);
        return Codec.DOUBLE.flatXmap(checker, checker);
    }

    public static final Codec<JsonElement> JSON = Codec.PASSTHROUGH
            .xmap(
                    dynamic -> dynamic.convert(JsonOps.INSTANCE).getValue(), jsonElement -> new Dynamic<>(JsonOps.INSTANCE, jsonElement)
            );
    public static final Codec<Component> COMPONENT = JSON.flatXmap(jsonElement -> {
        try {
            //? if >= 1.20.6 {
            /*return DataResult.success(ComponentSerialization.CODEC.parse(JsonOps.INSTANCE, jsonElement).getOrThrow(JsonParseException::new));
            *///?} else {
            return DataResult.success(Component.Serializer.fromJson(jsonElement));
            //?}
        } catch (JsonParseException var2) {
            return DataResult.error(var2::getMessage);
        }
    },component -> {
        try {
            //? if >= 1.20.6 {
            /*return DataResult.success(ComponentSerialization.CODEC.encodeStart(Constants.currentServer().registryAccess().createSerializationContext(JsonOps.INSTANCE), component).getOrThrow(JsonParseException::new));
            *///?} else {
            return DataResult.success(Component.Serializer.toJsonTree(component));
            //?}
        } catch (IllegalArgumentException var2) {
            return DataResult.error(var2::getMessage);
        }
	});
    public static final Codec<Component> FLAT_COMPONENT = Codec.STRING.flatXmap(string -> {
        try {
            return DataResult.success(Component.Serializer.fromJson(string
            //? if >= 1.20.6 {
                /*,Constants.currentServer().registryAccess()
            *///?}
            ));
        } catch (JsonParseException var2) {
            return DataResult.error(var2::getMessage);
        }
    }, component -> {
        try {
            return DataResult.success(Component.Serializer.toJson(component
                    //? if >= 1.20.6 {
                    /*,Constants.currentServer().registryAccess()
                    *///?}
            ));
        } catch (IllegalArgumentException var2) {
            return DataResult.error(var2::getMessage);
        }
    });
    public static final Codec<Vector3f> VECTOR3F = Codec.FLOAT
            .listOf()
            .comapFlatMap(
                    list -> Util.fixedSize(list, 3)
                            .map(listFloat -> new Vector3f(listFloat.getFirst(), listFloat.get(1), listFloat.get(2))),
                    vector3f -> List.of(vector3f.x(), vector3f.y(), vector3f.z())
            );
    public static final Codec<Quaternionf> QUATERNION_FLOAT_COMPONENTS = Codec.FLOAT
            .listOf()
            .comapFlatMap(
                    list -> Util.fixedSize(list, 4)
                            .map(floats -> new Quaternionf(floats.getFirst(), floats.get(1), floats.get(2), floats.get(3))),
                    quaternionf -> List.of(quaternionf.x, quaternionf.y, quaternionf.z, quaternionf.w)
            );
    public static final Codec<AxisAngle4f> AXIS_ANGLE_4F = RecordCodecBuilder.create(
            instance -> instance.group(
                            Codec.FLOAT.fieldOf("angle").forGetter(axisAngle4f -> axisAngle4f.angle),
                            VECTOR3F.fieldOf("axis").forGetter(axisAngle4f -> new Vector3f(axisAngle4f.x, axisAngle4f.y, axisAngle4f.z))
                    )
                    .apply(instance, AxisAngle4f::new)
    );
    public static final Codec<Quaternionf> QUATERNION_FLOAT = Codec.either(QUATERNION_FLOAT_COMPONENTS, AXIS_ANGLE_4F.xmap(Quaternionf::new, AxisAngle4f::new))
            .xmap(either -> either.map(quaternionf -> quaternionf, quaternionf -> quaternionf), Either::left);
    public static Codec<Matrix4f> MATRIX4F = Codec.FLOAT
            .listOf()
            .comapFlatMap(list -> Util.fixedSize(list, 16).map(floatList -> {
                Matrix4f matrix4f = new Matrix4f();

                for (int i = 0; i < floatList.size(); i++) {
                    matrix4f.setRowColumn(i >> 2, i & 3, floatList.get(i));
                }

                return matrix4f.determineProperties();
            }), matrix4f -> {
                FloatList floatList = new FloatArrayList(16);

                for (int i = 0; i < 16; i++) {
                    floatList.add(matrix4f.getRowColumn(i >> 2, i & 3));
                }
                return floatList;
            });
    public static final Codec<Pattern> PATTERN = Codec.STRING.comapFlatMap(string -> {
        try {
            return DataResult.success(Pattern.compile(string));
        } catch (PatternSyntaxException var2) {
            return DataResult.error(() -> "Invalid regex pattern '" + string + "': " + var2.getMessage());
        }
    }, Pattern::pattern);
    public static final Codec<Instant> INSTANT_ISO8601 = instantCodec(DateTimeFormatter.ISO_INSTANT);
    public static final Codec<byte[]> BASE64_STRING = Codec.STRING.comapFlatMap(string -> {
        try {
            return DataResult.success(Base64.getDecoder().decode(string));
        } catch (IllegalArgumentException var2) {
            return DataResult.error(() -> "Malformed base64 string");
        }
    }, bs -> Base64.getEncoder().encodeToString(bs));
    public static final Codec<CodecTypes.TagOrElementLocation> TAG_OR_ELEMENT_ID = Codec.STRING
            .comapFlatMap(
                    string -> string.startsWith("#")
                            ? ResourceLocation.read(string.substring(1))
                            .map( resourceLocation -> new CodecTypes.TagOrElementLocation(resourceLocation, true))
                            : ResourceLocation.read(string).map( resourceLocation -> new CodecTypes.TagOrElementLocation(resourceLocation, false)),
                    CodecTypes.TagOrElementLocation::decoratedId
            );
    private static final Function<Optional<Long>, OptionalLong> TO_OPTIONAL_LONG = optional -> optional.map(OptionalLong::of)
            .orElseGet(OptionalLong::empty);
    private static final Function<OptionalLong, Optional<Long>> FROM_OPTIONAL_LONG = optionalLong -> optionalLong.isPresent()
            ? Optional.of(optionalLong.getAsLong())
            : Optional.empty();
    public static final Codec<BitSet> BIT_SET = Codec.LONG_STREAM
            .xmap(longStream -> BitSet.valueOf(longStream.toArray()), bitSet -> Arrays.stream(bitSet.toLongArray()));
    private static final Codec<Property> PROPERTY = RecordCodecBuilder.create(
            instance -> instance.group(
                            //? if >= 1.20.4 {
                            /*Codec.STRING.fieldOf("name").forGetter(Property::name),
                            Codec.STRING.fieldOf("value").forGetter(Property::value),
                            Codec.STRING.optionalFieldOf("signature").forGetter(property -> Optional.ofNullable(property.signature()))
                            *///?} else {
                            Codec.STRING.fieldOf("name").forGetter(Property::getName),
                            Codec.STRING.fieldOf("value").forGetter(Property::getValue),
                            Codec.STRING.optionalFieldOf("signature").forGetter(property -> Optional.ofNullable(property.getSignature()))
                    //?}
                    )
                    .apply(instance, (string, string2, optional) -> new Property(string, string2, optional.orElse(null)))
    );

    private static final Codec<PropertyMap> PROPERTY_MAP = Codec.either(Codec.unboundedMap(Codec.STRING, Codec.STRING.listOf()), PROPERTY.listOf())
            .xmap(either -> {
                PropertyMap propertyMap = new PropertyMap();
                either.ifLeft(map -> map.forEach((string, list) -> {
                    for (String string2 : list) {
                        propertyMap.put(string, new Property(string, string2));
                    }
                })).ifRight(list -> {
                    for (Property property : list) {
                        //? if >= 1.20.4 {
                        /*propertyMap.put(property.name(), property);
                        *///?} else {
                        propertyMap.put(property.getName(), property);
                        //?}
                    }
                });
                return propertyMap;
            }, propertyMap -> Either.right(propertyMap.values().stream().toList()));
    public static final Codec<GameProfile> GAME_PROFILE = RecordCodecBuilder.create(
            instance -> instance.group(
                            Codec.mapPair(
                                    UUIDUtil.AUTHLIB_CODEC.xmap(Optional::of, optional -> optional.orElse(null))
                                                    .optionalFieldOf("id", Optional.empty()),
                                            Codec.STRING.xmap(Optional::of, optional -> optional.orElse(null))
                                                    .optionalFieldOf("name", Optional.empty())
                                    )
                                    .flatXmap(CodecTypes::mapIdNameToGameProfile, CodecTypes::mapGameProfileToIdName)
                                    .forGetter(Function.identity()),
                            PROPERTY_MAP.optionalFieldOf("properties", new PropertyMap()).forGetter(GameProfile::getProperties)
                    )
                    .apply(instance,(gameProfile, propertyMap) -> {
                        propertyMap.forEach((string, property) -> gameProfile.getProperties().put(string, property));
                        return gameProfile;
                    })
    );
    public static final Codec<UUID> UUID_CODEC = Codec.INT_STREAM
            .comapFlatMap(
                    intStream -> Util.fixedSize(intStream, 4).map(UUIDUtil::uuidFromIntArray), uUID -> Arrays.stream(uuidToIntArray(uUID))
            );
    public static final Codec<Set<UUID>> UUID_CODEC_SET = Codec.list(UUID_CODEC).xmap(Sets::newHashSet, Lists::newArrayList);
    public static final Codec<UUID> STRING_UUID_CODEC = Codec.STRING.comapFlatMap(string -> {
        try {
            return DataResult.success(UUID.fromString(string), Lifecycle.stable());
        } catch (IllegalArgumentException var2) {
            return DataResult.error(() -> "Invalid UUID " + string + ": " + var2.getMessage());
        }
    }, UUID::toString);
    public static Codec<UUID> AUTHLIB_UUID_CODEC = Codec.either(UUID_CODEC, Codec.STRING.comapFlatMap(string -> {
        try {
            return DataResult.success(UndashedUuid.fromStringLenient(string), Lifecycle.stable());
        } catch (IllegalArgumentException var2) {
            return DataResult.error(() -> "Invalid UUID " + string + ": " + var2.getMessage());
        }
    }, UndashedUuid::toString)).xmap(either -> either.map(uuid -> uuid, uuid -> uuid), Either::right);
    public static Codec<UUID> LENIENT_UUID_CODEC = Codec.either(UUID_CODEC, STRING_UUID_CODEC)
            .xmap(either -> either.map(uUID -> uUID, uUID -> uUID), Either::left);


    public static <F, S> Codec<Either<F, S>> xor(Codec<F> first, Codec<S> second) {
        return new CodecTypes.XorCodec<>(first, second);
    }

    public static int[] uuidToIntArray(UUID uuid) {
        long l = uuid.getMostSignificantBits();
        long m = uuid.getLeastSignificantBits();
        return leastMostToIntArray(l, m);
    }

    private static int[] leastMostToIntArray(long most, long least) {
        return new int[]{(int)(most >> 32), (int)most, (int)(least >> 32), (int)least};
    }

    /**
     * @param <R> 区间的对象类型
     * @param <P> 端点类型
     * */
    public static <P, R> Codec<R> intervalCodec(
            Codec<P> valueCodec,
            String lKey,
            String rKey,
            BiFunction<P, P, DataResult<R>> rangeGenerator,
            Function<R, P> lValueGetter,
            Function<R, P> rValueGetter
    ) {
        Codec<R> listType = Codec.list(valueCodec).comapFlatMap(list -> Util.fixedSize(list, 2)
                .flatMap(pList -> {
                    P object = pList.get(0);
                    P object2 = pList.get(1);
                    return rangeGenerator.apply(object, object2);
                }
                ), r -> ImmutableList.of(lValueGetter.apply(r), rValueGetter.apply(r)));
        Codec<R> objectType = RecordCodecBuilder.create(instance -> instance.group(
                valueCodec.fieldOf(lKey).forGetter(o -> lValueGetter.apply((R) o)),
                        valueCodec.fieldOf(rKey).forGetter(o -> rValueGetter.apply((R) o)))
                        .apply(instance, (p, p2) -> rangeGenerator.apply(p, p2)
                                //? if >= 1.20.6 {
                                /*.getOrThrow()
                                *///?} else {
                                .get().orThrow()
                                //?}
                                )
                ).comapFlatMap( o -> {
                    DataResult<R> apply;
                    try {
                        apply = rangeGenerator.apply((P) o, (P) o);
                    } catch (ClassCastException e) {
                        apply = DataResult.success((R) o);
                    }
                    return apply;
                },
                object -> rangeGenerator.apply(lValueGetter.apply(object),rValueGetter.apply(object)));
        Codec<R> general = new CodecTypes.EitherCodec<>(listType, objectType)
                .xmap(either -> either.map(r -> r, object -> object), Either::left);
        return Codec.either(valueCodec, general).comapFlatMap(
                        either -> either.map(object -> rangeGenerator.apply(object, object), DataResult::success),
                        r -> {
                            P object2 = lValueGetter.apply(r);
                            P object3 = rValueGetter.apply(r);
                            return Objects.equals(object2, object3) ? Either.left(object2) : Either.right(r);
                        }
                );
    }

    public static <A> Codec.ResultFunction<A> orElsePartial(A a) {
        return new Codec.ResultFunction<>() {
            @Override
            public <T> DataResult<Pair<A, T>> apply(DynamicOps<T> dynamicOps, T t, DataResult<Pair<A, T>> dataResult) {
                MutableObject<String> mutableObject = new MutableObject<>();
                Optional<Pair<A, T>> optional = dataResult.resultOrPartial(mutableObject::setValue);
                return optional.isPresent() ? dataResult :
                        DataResult.error(() -> "(" + mutableObject.getValue() + " -> using default)", Pair.of(a, t));
            }

            @Override
            public <T> DataResult<T> coApply(DynamicOps<T> dynamicOps, A object, DataResult<T> dataResult) {
                return dataResult;
            }

            @Override
            public String toString() {
                return "OrElsePartial[" + a + "]";
            }
        };
    }

    public static <E> Codec<E> idResolverCodec(ToIntFunction<E> toIntFunction, IntFunction<E> intFunction, int i) {
        return Codec.INT
                .flatXmap(
                        integer -> Optional.ofNullable(intFunction.apply(integer))
                                .map(DataResult::success)
                                .orElseGet(() -> DataResult.error(() -> "Unknown element id: " + integer)),
                        object -> {
                            int j = toIntFunction.applyAsInt(object);
                            return j == i ? DataResult.error(() -> "Element with unknown id: " + object) : DataResult.success(j);
                        }
                );
    }

    public static <E> Codec<E> stringResolverCodec(Function<E, String> function, Function<String, E> function2) {
        return Codec.STRING
                .flatXmap(
                        string -> Optional.ofNullable(function2.apply(string))
                                .map(DataResult::success)
                                .orElseGet(() -> DataResult.error(() -> "Unknown element name:" + string)),
                        object -> Optional.ofNullable(function.apply(object))
                                .map(DataResult::success)
                                .orElseGet(() -> DataResult.error(() -> "Element with unknown name: " + object))
                );
    }

    public static <E> Codec<E> orCompressed(Codec<E> first, Codec<E> second) {
        return new Codec<>() {
            @Override
            public <T> DataResult<T> encode(E object, DynamicOps<T> dynamicOps, T object2) {
                return dynamicOps.compressMaps() ? second.encode(object, dynamicOps, object2) : first.encode(object, dynamicOps, object2);
            }

            @Override
            public <T> DataResult<Pair<E, T>> decode(DynamicOps<T> dynamicOps, T object) {
                return dynamicOps.compressMaps() ? second.decode(dynamicOps, object) : first.decode(dynamicOps, object);
            }

            @Override
            public String toString() {
                return first + " orCompressed " + second;
            }
        };
    }

    public static <E> Codec<E> overrideLifecycle(Codec<E> codec, Function<E, Lifecycle> function, Function<E, Lifecycle> function2) {
        return codec.mapResult(
                new Codec.ResultFunction<>() {
                    @Override
                    public <T> DataResult<Pair<E, T>> apply(DynamicOps<T> dynamicOps, T object, DataResult<Pair<E, T>> dataResult) {
                        return dataResult.result()
                                .map(pair -> dataResult.setLifecycle(function.apply(pair.getFirst())))
                                .orElse(dataResult);
                    }

                    @Override
                    public <T> DataResult<T> coApply(DynamicOps<T> dynamicOps, E object, DataResult<T> dataResult) {
                        return dataResult.setLifecycle(function2.apply(object));
                    }

                    @Override
                    public String toString() {
                        return "WithLifecycle[" + function + " " + function2 + "]";
                    }
                }
        );
    }

    public static <T> Codec<T> validate(Codec<T> codec, Function<T, DataResult<T>> validator) {
        return codec.flatXmap(validator, validator);
    }

    public static <T> MapCodec<T> validate(MapCodec<T> codec, Function<T, DataResult<T>> validator) {
        return codec.flatXmap(validator, validator);
    }

    private static Codec<Integer> intRangeWithMessage(int min, int max, Function<Integer, String> errorMessage) {
        return validate(
                Codec.INT,
                integer -> integer.compareTo(min) >= 0 && integer.compareTo(max) <= 0
                        ? DataResult.success(integer)
                        : DataResult.error(() -> errorMessage.apply(integer))
        );
    }

    public static Codec<Integer> intRangeWithErrorMsg(int min, int max) {
        return intRangeWithMessage(min, max, integer -> "Value must be within range [" + min + ";" + max + "]: " + integer);
    }

    private static Codec<Float> floatRangeMinExclusiveWithMessage(float min, float max, Function<Float, String> errorMessage) {
        return validate(
                Codec.FLOAT,
                aFloat -> aFloat.compareTo(min) > 0 && aFloat.compareTo(max) <= 0
                        ? DataResult.success(aFloat)
                        : DataResult.error(() -> errorMessage.apply(aFloat))
        );
    }

    public static <T> Codec<List<T>> nonEmptyList(Codec<List<T>> codec) {
        return validate(
                codec, list -> list.isEmpty() ? DataResult.error(() -> "List must have contents") : DataResult.success(list)
        );
    }

    public static <T> Codec<HolderSet<T>> nonEmptyHolderSet(Codec<HolderSet<T>> codec) {
        return validate(
                codec,
                holderSet -> holderSet.unwrap().right().filter(List::isEmpty).isPresent()
                        ? DataResult.error(() -> "List must have contents")
                        : DataResult.success(holderSet)
        );
    }

    public static <A> Codec<A> lazyInitializedCodec(Supplier<Codec<A>> delegate) {
        return new CodecTypes.LazyInitializedCodec<>(delegate);
    }

    public static <E> MapCodec<E> retrieveContext(Function<DynamicOps<?>, DataResult<E>> function) {
        class ContextRetrievalCodec extends MapCodec<E> {
            @Override
            public <T> RecordBuilder<T> encode(E object, DynamicOps<T> dynamicOps, RecordBuilder<T> recordBuilder) {
                return recordBuilder;
            }

            @Override
            public <T> DataResult<E> decode(DynamicOps<T> dynamicOps, MapLike<T> mapLike) {
                return function.apply(dynamicOps);
            }

            @Override
            public String toString() {
                return "ContextRetrievalCodec[" + function + "]";
            }

            @Override
            public <T> Stream<T> keys(DynamicOps<T> dynamicOps) {
                return Stream.empty();
            }
        }

        return new ContextRetrievalCodec();
    }

    public static <E, L extends Collection<E>, T> Function<L, DataResult<L>> ensureHomogenous(Function<E, T> function) {
        return collection -> {
            Iterator<E> iterator = collection.iterator();
            if (iterator.hasNext()) {
                T object = function.apply(iterator.next());

                while (iterator.hasNext()) {
                    E object2 = iterator.next();
                    T object3 = function.apply(object2);
                    if (object3 != object) {
                        return DataResult.error(() -> "Mixed type list: element " + object2 + " had type " + object3 + ", but list is of type " + object);
                    }
                }
            }

            return DataResult.success(collection, Lifecycle.stable());
        };
    }

    public static <A> Codec<A> catchDecoderException(Codec<A> codec) {
        return Codec.of(codec, new Decoder<>() {
            @Override
            public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> dynamicOps, T object) {
                try {
                    return codec.decode(dynamicOps, object);
                } catch (Exception var4) {
                    return DataResult.error(() -> "Caught exception decoding " + object + ": " + var4.getMessage());
                }
            }
        });
    }

    public static Codec<Instant> instantCodec(DateTimeFormatter dateTimeFormatter) {
        return Codec.STRING.comapFlatMap(string -> {
            try {
                return DataResult.success(Instant.from(dateTimeFormatter.parse(string)));
            } catch (Exception var3) {
                return DataResult.error(var3::getMessage);
            }
        }, dateTimeFormatter::format);
    }

    public static MapCodec<OptionalLong> asOptionalLong(MapCodec<Optional<Long>> codec) {
        return codec.xmap(TO_OPTIONAL_LONG, FROM_OPTIONAL_LONG);
    }

    private static DataResult<GameProfile> mapIdNameToGameProfile(Pair<Optional<UUID>, Optional<String>> idName) {
        try {
            return DataResult.success(new GameProfile(idName.getFirst().orElse(null), idName.getSecond().orElse(null)));
        } catch (Throwable var2) {
            return DataResult.error(var2::getMessage);
        }
    }

    private static DataResult<Pair<Optional<UUID>, Optional<String>>> mapGameProfileToIdName(GameProfile gameProfile) {
        return DataResult.success(Pair.of(Optional.ofNullable(gameProfile.getId()), Optional.ofNullable(gameProfile.getName())));
    }

    public static Codec<String> sizeLimitedString(int minSize, int maxSize) {
        return validate(
                Codec.STRING,
                string -> {
                    int k = string.length();
                    if (k < minSize) {
                        return DataResult.error(() -> "String \"" + string + "\" is too short: " + k + ", expected range [" + minSize + "-" + maxSize + "]");
                    } else {
                        return k > maxSize
                                ? DataResult.error(() -> "String \"" + string + "\" is too long: " + k + ", expected range [" + minSize + "-" + maxSize + "]")
                                : DataResult.success(string);
                    }
                }
        );
    }

    record EitherCodec<F, S>(Codec<F> first, Codec<S> second) implements Codec<Either<F, S>> {

        @Override
            public <T> DataResult<Pair<Either<F, S>, T>> decode(DynamicOps<T> dynamicOps, T object) {
                DataResult<Pair<Either<F, S>, T>> dataResult = this.first.decode(dynamicOps, object).map(pair -> pair.mapFirst(Either::left));
                if (dataResult.error().isEmpty()) {
                    return dataResult;
                } else {
                    DataResult<Pair<Either<F, S>, T>> dataResult2 = this.second.decode(dynamicOps, object).map(pair -> pair.mapFirst(Either::right));
                    return dataResult2.error().isEmpty() ? dataResult2 : dataResult.apply2((pair, pair2) -> pair2, dataResult2);
                }
            }

            @Override
            public <T> DataResult<T> encode(Either<F, S> input, DynamicOps<T> ops, T prefix) {
                return input.map(
                        object2 -> this.first.encode(object2, ops, prefix), object2 -> this.second.encode(object2, ops, prefix)
                );
            }
        }

    record LazyInitializedCodec<A>(Supplier<Codec<A>> delegate) implements Codec<A> {
        LazyInitializedCodec(Supplier<Codec<A>> delegate) {
            this.delegate = Suppliers.memoize(delegate::get);
        }

        @Override
        public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> dynamicOps, T object) {
            return (this.delegate.get()).decode(dynamicOps, object);
        }

        @Override
        public <T> DataResult<T> encode(A object, DynamicOps<T> dynamicOps, T object2) {
            return (this.delegate.get()).encode(object, dynamicOps, object2);
        }
    }

    public record TagOrElementLocation(ResourceLocation id, boolean tag) {
        @Override
        public @NotNull String toString() {
            return this.decoratedId();
        }

        private String decoratedId() {
            return this.tag ? "#" + this.id : this.id.toString();
        }
    }

    record XorCodec<F, S>(Codec<F> first, Codec<S> second) implements Codec<Either<F, S>> {

        @Override
            public <T> DataResult<Pair<Either<F, S>, T>> decode(DynamicOps<T> dynamicOps, T object) {
                DataResult<Pair<Either<F, S>, T>> dataResult = this.first.decode(dynamicOps, object).map(pair -> pair.mapFirst(Either::left));
                DataResult<Pair<Either<F, S>, T>> dataResult2 = this.second.decode(dynamicOps, object).map(pair -> pair.mapFirst(Either::right));
                Optional<Pair<Either<F, S>, T>> optional = dataResult.result();
                Optional<Pair<Either<F, S>, T>> optional2 = dataResult2.result();
                if (optional.isPresent() && optional2.isPresent()) {
                    return DataResult.error(
                            () -> "Both alternatives read successfully, can not pick the correct one; first: " + optional.get() + " second: " + optional2.get(),
                            optional.get()
                    );
                } else {
                    return optional.isPresent() ? dataResult : dataResult2;
                }
            }

            @Override
            public <T> DataResult<T> encode(Either<F, S> input, DynamicOps<T> ops, T prefix) {
                return input.map(
                        object2 -> this.first.encode(object2, ops, prefix), object2 -> this.second.encode(object2, ops, prefix)
                );
            }
        }

    public static class UndashedUuid {
        public static UUID fromString(final String string) {
            if (string.indexOf('-') != -1) {
                throw new IllegalArgumentException("Invalid undashed UUID string: " + string);
            }
            return fromStringLenient(string);
        }

        public static UUID fromStringLenient(final String string) {
            return UUID.fromString(string.replaceFirst("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5"));
        }

        public static String toString(final UUID uuid) {
            return uuid.toString().replace("-", "");
        }
    }
}
