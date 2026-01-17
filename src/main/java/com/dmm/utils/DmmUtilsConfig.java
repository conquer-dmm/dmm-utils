package com.dmm.utils;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import java.awt.Color;

@ConfigGroup("dmmutils")
public interface DmmUtilsConfig extends Config
{
  @ConfigItem(
      keyName = "showOverlay",
      name = "Show Timer Overlay",
      description = "Display the overload timer as an infobox",
      position = 0
  )
  default boolean showOverlay()
  {
    return true;
  }

  @ConfigItem(
      keyName = "textColor",
      name = "Timer Color",
      description = "Color of the timer text",
      position = 1
  )
  default Color textColor()
  {
    return Color.GREEN;
  }

  @ConfigItem(
      keyName = "warningTime",
      name = "Warning Time (seconds)",
      description = "Change color when this many seconds remain",
      position = 2
  )
  default int warningTime()
  {
    return 30;
  }

  @ConfigItem(
      keyName = "warningColor",
      name = "Warning Color",
      description = "Color when timer is low",
      position = 3
  )
  default Color warningColor()
  {
    return Color.RED;
  }
}