package com.dmm.utils;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.Widget;
import net.runelite.client.callback.Hooks;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import java.awt.image.BufferedImage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@PluginDescriptor(
		name = "DMM Utils",
		description = "Utilities for Deadman Mode including overload timer and combat bracket player hiding",
		tags = {"dmm", "deadman", "overload", "timer", "utilities", "pvp"}
)
public class DmmUtilsPlugin extends Plugin
{
	private static final int OVERLOAD_DURATION = 300;
	private static final String DRINK_MESSAGE = "You drink some of your overload potion";
	private static final String EXPIRE_MESSAGE = "The effects of the blighted overload have worn off, and you feel normal again";
	private static final Pattern BRACKET_PATTERN = Pattern.compile("(\\d+)-(\\d+)");
	private static final int SKULL_WIDGET_GROUP = 90;
	private static final int SKULL_WIDGET_CHILD = 49;

	@Inject
	private Client client;

	@Inject
	private DmmUtilsConfig config;

	@Inject
	private InfoBoxManager infoBoxManager;

	@Inject
	private ItemManager itemManager;

	@Inject
	private Hooks hooks;

	private OverloadTimerInfoBox timerInfoBox;

	@Getter
	private int minCombatLevel = -1;

	@Getter
	private int maxCombatLevel = -1;

	private boolean hideOutsideBracket;

	private final Hooks.RenderableDrawListener drawListener = this::shouldDraw;

	@Override
	protected void startUp() throws Exception
	{
		log.info("DMM Utils started!");
		updateConfig();
		hooks.registerRenderableDrawListener(drawListener);
		updateCombatBracket();
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.info("DMM Utils stopped!");
		hooks.unregisterRenderableDrawListener(drawListener);
		removeInfoBox();
		minCombatLevel = -1;
		maxCombatLevel = -1;
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (event.getGroup().equals("dmmutils"))
		{
			updateConfig();
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		updateCombatBracket();
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

	private void updateConfig()
	{
		hideOutsideBracket = config.hideOutsideBracket();
	}

	boolean shouldDraw(Renderable renderable, boolean drawingUI)
	{
		if (renderable instanceof Player)
		{
			Player player = (Player) renderable;
			Player local = client.getLocalPlayer();

			if (player.getName() == null)
			{
				return true;
			}

			if (player == local)
			{
				return true;
			}

			if (!hideOutsideBracket || minCombatLevel <= 0 || maxCombatLevel <= 0)
			{
				return true;
			}

			int combatLevel = player.getCombatLevel();
			return combatLevel >= minCombatLevel && combatLevel <= maxCombatLevel;
		}

		return true;
	}

	private void updateCombatBracket()
	{
		Widget skullWidget = client.getWidget(SKULL_WIDGET_GROUP, SKULL_WIDGET_CHILD);

		if (skullWidget != null)
		{
			String text = skullWidget.getText();
			if (text != null)
			{
				parseCombatBracket(text);
			}
		}
	}

	private void parseCombatBracket(String text)
	{
		Matcher matcher = BRACKET_PATTERN.matcher(text);
		if (matcher.find())
		{
			try
			{
				int newMin = Integer.parseInt(matcher.group(1));
				int newMax = Integer.parseInt(matcher.group(2));

				if (newMin != minCombatLevel || newMax != maxCombatLevel)
				{
					minCombatLevel = newMin;
					maxCombatLevel = newMax;
					log.info("Combat bracket updated: {}-{}", minCombatLevel, maxCombatLevel);
				}
			}
			catch (NumberFormatException e)
			{
				log.error("Failed to parse combat bracket", e);
				minCombatLevel = -1;
				maxCombatLevel = -1;
			}
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