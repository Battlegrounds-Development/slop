package me.remag501.power;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public enum PowerType {
    SPEEDSTER(
            "Speedster",
            "Move very quickly.",
            "Dash",
            "Sneak + right-click with an empty hand to surge forward.",
            8,
            List.of(
                    new EffectSpec("SPEED", 1),
                    new EffectSpec("HASTE", 0)
            )
    ),
    TITAN(
            "Titan",
            "Hit harder and take less damage.",
            "Shockwave",
            "Sneak + right-click with an empty hand to knock nearby enemies away.",
            14,
            List.of(
                    new EffectSpec("STRENGTH", 0),
                    new EffectSpec("RESISTANCE", 0)
            )
    ),
    SKYBOUND(
            "Skybound",
            "Jump higher and fall safely.",
            "Sky Launch",
            "Sneak + right-click with an empty hand to launch upward.",
            10,
            List.of(
                    new EffectSpec("JUMP_BOOST", 1),
                    new EffectSpec("SLOW_FALLING", 0)
            )
    );

    private final String displayName;
    private final String description;
    private final String abilityName;
    private final String abilityDescription;
    private final int abilityCooldownSeconds;
    private final List<EffectSpec> effects;

    PowerType(String displayName, String description, String abilityName, String abilityDescription, int abilityCooldownSeconds, List<EffectSpec> effects) {
        this.displayName = displayName;
        this.description = description;
        this.abilityName = abilityName;
        this.abilityDescription = abilityDescription;
        this.abilityCooldownSeconds = abilityCooldownSeconds;
        this.effects = effects;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public String getAbilityName() {
        return abilityName;
    }

    public String getAbilityDescription() {
        return abilityDescription;
    }

    public int getAbilityCooldownSeconds() {
        return abilityCooldownSeconds;
    }

    public List<EffectSpec> getEffects() {
        return effects;
    }

    public String getKey() {
        return name().toLowerCase(Locale.ROOT);
    }

    public static Optional<PowerType> fromInput(String input) {
        if (input == null || input.isBlank()) {
            return Optional.empty();
        }

        String normalized = input.trim().replace('-', '_').toUpperCase(Locale.ROOT);
        String compact = normalized.replace("_", "");
        return Arrays.stream(values())
                .filter(powerType -> powerType.name().equals(normalized) || powerType.name().equals(compact))
                .findFirst();
    }

    public record EffectSpec(String effectType, int amplifier) {
    }
}

