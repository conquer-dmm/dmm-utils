package com.dmm.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import net.runelite.client.ui.overlay.infobox.Timer;

public class OverloadTimerInfoBox extends Timer
{
    private final DmmUtilsConfig config;

    public OverloadTimerInfoBox(long duration, BufferedImage image, DmmUtilsPlugin plugin, DmmUtilsConfig config)
    {
        super(duration, ChronoUnit.MILLIS, image, plugin);
        this.config = config;
    }

    @Override
    public Color getTextColor()
    {
        long remaining = getEndTime().until(Instant.now(), ChronoUnit.SECONDS);
        if (Math.abs(remaining) <= config.warningTime())
        {
            return config.warningColor();
        }
        return config.textColor();
    }

    @Override
    public String getText()
    {
        long seconds = Math.abs(getEndTime().until(Instant.now(), ChronoUnit.SECONDS));
        int minutes = (int) (seconds / 60);
        int secs = (int) (seconds % 60);
        return String.format("%d:%02d", minutes, secs);
    }

    @Override
    public String getTooltip()
    {
        return "Overload Timer";
    }
}