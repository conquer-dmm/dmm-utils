package com.dmm.utils;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import java.awt.Color;

@ConfigGroup("dmmutils")
public interface DmmUtilsConfig extends Config
{
  @ConfigSection(
      name = "Overload Timer",
      description = "Settings for the overload timer",
      position = 0
  )
  String overloadSection = "overload";

  @ConfigSection(
      name = "Combat Bracket",
      description = "Settings for hiding players outside combat bracket",
      position = 1
  )
  String combatBracketSection = "combatBracket";

  // ==================== Overload Timer Settings ====================

  @ConfigItem(
      keyName = "showOverlay",
      name = "Show Timer Overlay",
      description = "Display the overload timer as an infobox",
      section = overloadSection,
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
      section = overloadSection,
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
      section = overloadSection,
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
      section = overloadSection,
      position = 3
  )
  default Color warningColor()
  {
    return Color.RED;
  }

  // ==================== Combat Bracket Settings ====================

  @ConfigItem(
      keyName = "hideOutsideBracket",
      name = "Hide Players Outside Bracket",
      description = "Hide players who are not in your combat bracket",
      section = combatBracketSection,
      position = 0
  )
  default boolean hideOutsideBracket()
  {
    return false;
  }
}