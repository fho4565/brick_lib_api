package com.arc_studio.brick_lib_api.core.data.saved_data;

import com.arc_studio.brick_lib_api.core.data.ResourceID;
import com.mojang.serialization.Codec;

public record SavedDataCodec<T>(ResourceID resourceID, Codec<T> codec) {
}
