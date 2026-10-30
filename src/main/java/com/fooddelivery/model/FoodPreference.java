package com.fooddelivery.model;

/** User-level filter. BOTH is the default. */
public enum FoodPreference {
    VEG, NON_VEG, BOTH;

    public boolean allows(FoodType type) {
        return this == BOTH || (this == VEG && type == FoodType.VEG)
                || (this == NON_VEG && type == FoodType.NON_VEG);
    }
}
