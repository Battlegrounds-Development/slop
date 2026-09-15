package me.remag501.power;

import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PowerManagerUnitTest {

	@Test
	void testGetPowerReturnsEmptyByDefault() {
		PowerManager manager = new PowerManager(null);
		UUID uuid = UUID.randomUUID();
		Optional<PowerType> power = manager.getPower(uuid);
		assertTrue(power.isEmpty());
	}
}
