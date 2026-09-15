package me.remag501.power;

import me.remag501.power.ability.Ability;
import me.remag501.power.ability.BlinkAbility;
import me.remag501.power.ability.BloodFrenzyAbility;
import me.remag501.power.ability.CityBreakerModeAbility;
import me.remag501.power.ability.CometDiveAbility;
import me.remag501.power.ability.DashAbility;
import me.remag501.power.ability.EventHorizonAbility;
import me.remag501.power.ability.FortressAbility;
import me.remag501.power.ability.GlideAbility;
import me.remag501.power.ability.GroundSlamAbility;
import me.remag501.power.ability.HyperDashAbility;
import me.remag501.power.ability.NovaBurstAbility;
import me.remag501.power.ability.PredatorLockAbility;
import me.remag501.power.ability.SeismicClapAbility;
import me.remag501.power.ability.ShockwaveAbility;
import me.remag501.power.ability.SkyLaunchAbility;
import me.remag501.power.ability.SprintAbility;
import me.remag501.power.ability.StarfallAbility;
import me.remag501.power.ability.SupersonicFlightAbility;
import me.remag501.power.ability.UpdraftAbility;
import me.remag501.power.ability.ViltrumiteRushAbility;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public enum PowerType {
    SPEEDSTER(
            "Speedster",
            "Move very quickly.",
            List.of(
                    new DashAbility(),
                    new SprintAbility(),
                    new BlinkAbility()
            ),
            List.of(
                    new EffectSpec("SPEED", 1),
                    new EffectSpec("HASTE", 0)
            )
    ),
    TITAN(
            "Titan",
            "Hit harder and take less damage.",
            List.of(
                    new ShockwaveAbility(),
                    new GroundSlamAbility(),
                    new FortressAbility()
            ),
            List.of(
                    new EffectSpec("STRENGTH", 0),
                    new EffectSpec("RESISTANCE", 0)
            )
    ),
    SKYBOUND(
            "Skybound",
            "Jump higher and fall safely.",
            List.of(
                    new SkyLaunchAbility(),
                    new GlideAbility(),
                    new UpdraftAbility()
            ),
            List.of(
                    new EffectSpec("JUMP_BOOST", 1),
                    new EffectSpec("SLOW_FALLING", 0)
            )
    ),
    COSMIC(
            "Cosmic",
            "Bend gravity, ignite starfields, and dominate movement with pure cosmic force.",
            List.of(
                    new NovaBurstAbility(),
                    new EventHorizonAbility(),
                    new HyperDashAbility(),
                    new CometDiveAbility(),
                    new StarfallAbility()
            ),
            List.of(
                    new EffectSpec("SPEED", 2),
                    new EffectSpec("STRENGTH", 1),
                    new EffectSpec("RESISTANCE", 1),
                    new EffectSpec("JUMP_BOOST", 2),
                    new EffectSpec("FIRE_RESISTANCE", 0)
            )
    ),
    VILTRUMITE(
            "Viltrumite",
            "Overwhelming force, brutal flight control, and elite predator combat power.",
            List.of(
                    new CityBreakerModeAbility(),
                    new ViltrumiteRushAbility(),
                    new SupersonicFlightAbility(),
                    new SeismicClapAbility(),
                    new PredatorLockAbility(),
                    new BloodFrenzyAbility()
            ),
            List.of(
                    new EffectSpec("SPEED", 3),
                    new EffectSpec("STRENGTH", 3),
                    new EffectSpec("RESISTANCE", 2),
                    new EffectSpec("JUMP_BOOST", 2),
                    new EffectSpec("FIRE_RESISTANCE", 0),
                    new EffectSpec("REGENERATION", 1),
                    new EffectSpec("HASTE", 1)
            )
    );

    private final String displayName;
    private final String description;
    private final List<Ability> abilities;
    private final List<EffectSpec> effects;

    PowerType(String displayName, String description, List<Ability> abilities, List<EffectSpec> effects) {
        this.displayName = displayName;
        this.description = description;
        this.abilities = abilities;
        this.effects = effects;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public List<Ability> getAbilities() {
        return abilities;
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
