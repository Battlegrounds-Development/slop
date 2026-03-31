package me.remag501.power;

import org.junit.jupiter.api.Test;

import java.util.Optional;

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
		assertEquals("Dash", PowerType.SPEEDSTER.getAbilityName());
		assertEquals(8, PowerType.SPEEDSTER.getAbilityCooldownSeconds());
	}
}
