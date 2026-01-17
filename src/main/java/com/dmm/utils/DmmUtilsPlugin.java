package com.dmm.utils;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import java.awt.image.BufferedImage;

@Slf4j
@PluginDescriptor(
		name = "DMM Utils",
		description = "Utilities for Deadman Mode including overload timer",
		tags = {"dmm", "deadman", "overload", "timer", "utilities"}
)
public class DmmUtilsPlugin extends Plugin
{
	private static final int OVERLOAD_DURATION = 300;
	private static final String DRINK_MESSAGE = "You drink some of your overload potion";
	private static final String EXPIRE_MESSAGE = "The effects of the blighted overload have worn off, and you feel normal again";

	@Inject
	private Client client;

	@Inject
	private DmmUtilsConfig config;

	@Inject
	private InfoBoxManager infoBoxManager;

	@Inject
	private ItemManager itemManager;

	private OverloadTimerInfoBox timerInfoBox;

	@Override
	protected void startUp() throws Exception
	{
		log.info("DMM Utils started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("DMM Utils stopped!");
		removeInfoBox();
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		if (event.getType() != ChatMessageType.GAMEMESSAGE &&
				event.getType() != ChatMessageType.SPAM)
		{
			return;
		}

		String message = event.getMessage();

		if (message.contains(DRINK_MESSAGE))
		{
			removeInfoBox();

			BufferedImage overloadImage = itemManager.getImage(ItemID.OVERLOAD_4);

			timerInfoBox = new OverloadTimerInfoBox(
					OVERLOAD_DURATION * 1000L,
					overloadImage,
					this,
					config
			);

			infoBoxManager.addInfoBox(timerInfoBox);
			log.debug("Overload timer started");
		}
		else if (message.contains(EXPIRE_MESSAGE))
		{
			removeInfoBox();
			log.debug("Overload timer expired");
		}
	}

	private void removeInfoBox()
	{
		if (timerInfoBox != null)
		{
			infoBoxManager.removeInfoBox(timerInfoBox);
			timerInfoBox = null;
		}
	}

	@Provides
	DmmUtilsConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(DmmUtilsConfig.class);
	}
}