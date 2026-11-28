
package dev.firuhowa.wolfevolution.client;

import dev.firuhowa.wolfevolution.WolfEvolution;
import net.fabricmc.api.ClientModInitializer;

public class WolfEvolutionClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {

		WolfEvolution.LOGGER.info(
				"WolfEvolution client systems initialized."
		);

		// 口笛キーを登録
		WhistleKeyBindings.initialize();

		WolfEvolution.LOGGER.info(
				"WolfEvolution whistle key registered."
		);
	}
}
