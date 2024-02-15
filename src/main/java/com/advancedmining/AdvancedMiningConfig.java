package com.advancedmining;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Units;

@ConfigGroup("advancedmining")
public interface AdvancedMiningConfig extends Config
{
	@ConfigItem(
			keyName = "statTimeout",
			name = "Reset stats",
			description = "Duration the mining indicator and session stats are displayed before being reset"
	)
	@Units(Units.MINUTES)
	default int statTimeout()
	{
		return 5;
	}

	@ConfigItem(
			keyName = "showMiningStats",
			name = "Show session stats",
			description = "Configures whether to display mining session stats"
	)
	default boolean showMiningStats()
	{
		return true;
	}
}