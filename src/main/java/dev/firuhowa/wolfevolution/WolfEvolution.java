package dev.firuhowa.wolfevolution;

import dev.firuhowa.wolfevolution.network.WhistlePayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.animal.wolf.Wolf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WolfEvolution implements ModInitializer {

	public static final String MOD_ID = "wolfevolution";

	public static final Logger LOGGER =
			LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		LOGGER.info("WolfEvolution is initializing...");

		WolfEvolutionSounds.initialize();

		// 睡眠データの登録
		WolfSleepData.initialize();

		// 口笛パケットを登録
		PayloadTypeRegistry.serverboundPlay().register(
				WhistlePayload.TYPE,
				WhistlePayload.CODEC
		);

		// サーバー側で口笛を受信
		ServerPlayNetworking.registerGlobalReceiver(
				WhistlePayload.TYPE,
				(payload, context) -> {
					WolfWhistleHandler.handle(context.player());
				}
		);

		// AFK・睡眠システム
		ServerTickEvents.END_SERVER_TICK.register(server -> {
			server.getPlayerList().getPlayers().forEach(player -> {
				PlayerAfkTracker.update(player);
				WolfSleepManager.update(player);
			});
		});

		// 狼の読み込み時に睡眠状態を復旧
		ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {

			if (entity instanceof Wolf wolf) {
				WolfSleepData.recoverAfterReload(wolf);
			}
		});

		// 切断時の処理
		ServerPlayConnectionEvents.DISCONNECT.register(
				(handler, server) -> {
					PlayerAfkTracker.remove(handler.player);
					WolfSleepManager.removeOwner(handler.player);
				}
		);

		LOGGER.info("WolfEvolution AFK tracker initialized.");
		LOGGER.info("WolfEvolution wolf sleep manager initialized.");
		LOGGER.info("WolfEvolution whistle system initialized.");
		LOGGER.info("WolfEvolution AI systems are ready.");
		LOGGER.info("WolfEvolution initialized successfully!");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}