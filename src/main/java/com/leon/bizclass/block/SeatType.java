package com.leon.bizclass.block;

import net.minecraft.util.StringRepresentable;

/**
 * The six suite positions that make up one 1-2-1 business row:
 *
 *   [WINDOW_LEFT] [AISLE_LEFT]   [POD_LEFT|POD_RIGHT]   [AISLE_RIGHT] [WINDOW_RIGHT]
 *        single seats alternate        shared double pod        single seats alternate
 *
 * WINDOW_LEFT / AISLE_LEFT doors open toward the LEFT aisle.
 * AISLE_RIGHT / WINDOW_RIGHT doors open toward the RIGHT aisle.
 * POD_LEFT / POD_RIGHT are the two seats sharing one pod door in the centre.
 */
public enum SeatType implements StringRepresentable {
    WINDOW_LEFT("window_left", false, true),
    AISLE_LEFT("aisle_left", false, false),
    POD_LEFT("pod_left", true, false),
    POD_RIGHT("pod_right", true, false),
    AISLE_RIGHT("aisle_right", false, false),
    WINDOW_RIGHT("window_right", false, true);

    private final String name;
    private final boolean pod;
    private final boolean window;

    SeatType(String name, boolean pod, boolean window) {
        this.name = name;
        this.pod = pod;
        this.window = window;
    }

    public boolean isPod() {
        return pod;
    }

    public boolean isWindow() {
        return window;
    }

    /** True for the two "left" single suites whose doors face the left aisle. */
    public boolean isLeftSingle() {
        return this == WINDOW_LEFT || this == AISLE_LEFT;
    }

    /** True for the two "right" single suites whose doors face the right aisle. */
    public boolean isRightSingle() {
        return this == WINDOW_RIGHT || this == AISLE_RIGHT;
    }

    /** Returns the alternating partner type on the same side (window<->aisle). */
    public SeatType alternateSingle() {
        return switch (this) {
            case WINDOW_LEFT -> AISLE_LEFT;
            case AISLE_LEFT -> WINDOW_LEFT;
            case WINDOW_RIGHT -> AISLE_RIGHT;
            case AISLE_RIGHT -> WINDOW_RIGHT;
            default -> this;
        };
    }

    public SeatType podPartner() {
        return this == POD_LEFT ? POD_RIGHT : POD_LEFT;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
