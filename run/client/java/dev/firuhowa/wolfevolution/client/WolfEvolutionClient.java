
package dev.firuhowa.wolfevolution.client;

import net.fabricmc.api.ClientModInitializer;

import dev.firuhowa.wolfevolution.WolfEvolution;

public class WolfEvolutionClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		WolfEvolution.LOGGER.info(
				"WolfEvolution client systems initialized."
		);
	}
}
