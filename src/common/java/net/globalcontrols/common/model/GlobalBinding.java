package net.globalcontrols.common.model;

import java.util.List;

public record GlobalBinding(
    String translationKey,
    String modId,
    List<Integer> heldKeys
) {}
