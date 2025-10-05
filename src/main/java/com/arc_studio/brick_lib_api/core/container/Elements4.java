package com.arc_studio.brick_lib_api.core.container;

public record Elements4<E1, E2, E3, E4>(E1 element1, E2 element2, E3 element3, E4 element4) {
    @Override
    public String toString() {
        return "(" + element1 + "," + element2 + "," + element3+","+element4 + ")";
    }
}
