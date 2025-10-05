package com.arc_studio.brick_lib_api.entrypoints;

import com.arc_studio.brick_lib_api.BrickLibAPI;

/**
 * 这是模组公共端的入口点，由每个模组加载器特定端调用。
 */
public class CommonEP {
    public static void init(){
        BrickLibAPI.init();
    }
}
