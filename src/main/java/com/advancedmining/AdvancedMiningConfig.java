package com.advancedmining;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Units;

@ConfigGroup("advancedmining")
public interface AdvancedMiningConfig extends Config {
	@ConfigItem(
			keyName = "statTimeout",
			name = "Reset Stats",
			description = "Duration the mining indicator and session stats are displayed before being reset",
			position = 1
	)
	@Units(Units.MINUTES)
	default int statTimeout()
	{
		return 5;
	}

	@ConfigItem(
			keyName = "showMiningStats",
			name = "Show Session Stats",
			description = "Configures whether to display mining session stats",
			position = 2
	)
	default boolean showMiningStats()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showMiningState",
			name = "Show Current Mining State",
			description = "Shows current mining state. 'You are currently mining' / 'You are currently NOT mining'",
			position = 3
	)
	default boolean showMiningState()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showOthersMined",
			name = "Show Other Things That Are Mined",
			description = "Shows other things mined during current mining session",
			position = 4
	)
	default boolean showOthersMined()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showOresMined",
			name = "Show Ores Mined",
			description = "Shows ores mined during current mining session",
			position = 5
	)
	default boolean showOresMined()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showAdditionalMined",
			name = "Show Additional Ores Mined",
			description = "Shows additional ores mined during current mining session",
			position = 6
	)
	default boolean showAdditionalMined()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showGemsFound",
			name = "Show Gems Mined/Found",
			description = "Shows gems mined/found during current mining session",
			position = 7
	)
	default boolean showGemsFound()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showEssenceMined",
			name = "Show Essence Mined",
			description = "Shows essence mined during current mining session",
			position = 8
	)
	default boolean showEssenceMined()
	{
		return true;
	}

	@ConfigItem(
			keyName = "showLootIcons",
			name = "Show Icons",
			description = "Display collected ores/gems/others as item images",
			position = 99
	)
	default boolean showLootIcons()
	{
		return false;
	}
}