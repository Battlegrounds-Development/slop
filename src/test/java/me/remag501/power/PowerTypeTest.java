package me.remag501.power;

import me.remag501.power.ability.Ability;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PowerTypeTest {

	@Test
	void parsesCaseInsensitiveValues() {
		Optional<PowerType> parsed = PowerType.fromInput("sPeEdStEr");
		assertTrue(parsed.isPresent());
		assertEquals(PowerType.SPEEDSTER, parsed.get());
	}

	@Test
	void parsesHyphenatedInput() {
		Optional<PowerType> parsed = PowerType.fromInput("sky-bound");
		assertTrue(parsed.isPresent());
		assertEquals(PowerType.SKYBOUND, parsed.get());
	}

	@Test
	void returnsEmptyForUnknownPower() {
		assertTrue(PowerType.fromInput("laser").isEmpty());
	}

	@Test
	void exposesAbilityMetadata() {
		assertEquals("Dash", PowerType.SPEEDSTER.getAbilities().get(0).getDisplayName());
		assertEquals(8, PowerType.SPEEDSTER.getAbilities().get(0).getCooldownSeconds());
	}

	@Test
	void usesUniqueItemMaterialForEachAbility() {
		Set<org.bukkit.Material> materials = new HashSet<>();
		int abilityCount = 0;

		for (PowerType powerType : PowerType.values()) {
			for (Ability ability : powerType.getAbilities()) {
				abilityCount++;
				materials.add(ability.getItemMaterial());
			}
		}

		assertEquals(abilityCount, materials.size());
	}
}
