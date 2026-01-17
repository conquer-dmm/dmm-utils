package com.dmm.utils;

import com.dmm.utils.DmmUtilsPlugin;
import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class DmmUtilsPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(DmmUtilsPlugin.class);
		RuneLite.main(args);
	}
}