package com.arc_studio.brick_lib_api.update_checker;

import com.google.gson.annotations.SerializedName;

public record ModrinthModHashes(

	@SerializedName("sha1")
	String sha1,

	@SerializedName("sha512")
	String sha512
) {
}