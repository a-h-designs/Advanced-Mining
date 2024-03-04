package com.advancedmining;

import com.google.inject.Provides;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import javax.inject.Inject;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.*;

import static net.runelite.api.ObjectID.BARRONITE_ROCKS;
import static net.runelite.api.ObjectID.BARRONITE_ROCKS_41548;
import static net.runelite.api.ObjectID.DEPLETED_VEIN;
import static net.runelite.api.ObjectID.DEPLETED_VEIN_26665;
import static net.runelite.api.ObjectID.DEPLETED_VEIN_26666;
import static net.runelite.api.ObjectID.DEPLETED_VEIN_26667;
import static net.runelite.api.ObjectID.DEPLETED_VEIN_26668;
import static net.runelite.api.ObjectID.EMPTY_WALL;
import static net.runelite.api.ObjectID.GOLD_VEIN;
import static net.runelite.api.ObjectID.GOLD_VEIN_5990;
import static net.runelite.api.ObjectID.GOLD_VEIN_5991;
import static net.runelite.api.ObjectID.ORE_VEIN;
import static net.runelite.api.ObjectID.ORE_VEIN_26662;
import static net.runelite.api.ObjectID.ORE_VEIN_26663;
import static net.runelite.api.ObjectID.ORE_VEIN_26664;
import static net.runelite.api.ObjectID.ROCKS_41549;
import static net.runelite.api.ObjectID.ROCKS_41550;

import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ScriptPreFired;
import net.runelite.api.events.WallObjectSpawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.xptracker.XpTrackerPlugin;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(
		name = "Advanced Mining",
		description = "A more advanced mining plugin to display statistics and ore respawn timers",
		tags = {"mining", "advanced", "gems", "ore", "minerals", "essence", "overlay", "skilling", "timers", "rock"}
)
@PluginDependency(XpTrackerPlugin.class)
public class AdvancedMiningPlugin extends Plugin
{
	public static final Pattern MINING_PATTERN = Pattern.compile(
			"You " +
					"(?:manage to|just|find|mined)" +
					" (?:mined?|quarry|some|found|an) " +
					"(?:some|an?|minerals|extra) " +
					"(?:while you mine|copper|tin|clay|iron|silver|coal|gold|mithril|adamantite|runite|amethyst|sandstone|granite|barronite shards|barronite deposit|Opal|piece of Jade|Red Topaz|Emerald|Sapphire|Ruby|Diamond|block of essence thanks to your completion of the Kourend & Kebos Medium Diary)" +
					"(?:\\.|!)");

	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private AdvancedMiningOverlay overlay;

	@Inject
	private MiningRocksOverlay rocksOverlay;

	@Inject
	private AdvancedMiningConfig config;

	@Getter
	@Nullable
	@Setter(AccessLevel.PACKAGE)
	private MiningSession session;

	@Getter(AccessLevel.PACKAGE)
	private final List<RockRespawn> respawns = new ArrayList<>();
	private boolean recentlyLoggedIn;

	@Getter
	@Nullable
	private Pickaxe pickaxe;

	@Provides
	AdvancedMiningConfig getConfig(ConfigManager configManager)
	{
		return configManager.getConfig(AdvancedMiningConfig.class);
	}

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
		overlayManager.add(rocksOverlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		session = null;
		pickaxe = null;
		overlayManager.remove(overlay);
		overlayManager.remove(rocksOverlay);
		respawns.forEach(respawn -> clearHintArrowAt(respawn.getWorldPoint()));
		respawns.clear();
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		switch (event.getGameState())
		{
			case HOPPING:
				respawns.clear();
				break;
			case LOGGED_IN:
				// After login rocks that are depleted will be changed,
				// so wait for the next game tick before watching for
				// rocks to deplete
				recentlyLoggedIn = true;
				break;
		}
	}

	@Subscribe
	public void onAnimationChanged(final AnimationChanged event)
	{
		Player local = client.getLocalPlayer();

		if (event.getActor() != local)
		{
			return;
		}

		int animId = local.getAnimation();
		if (animId == AnimationID.DENSE_ESSENCE_CHIPPING)
		{
			// Can't use chat messages to start mining session on Dense Essence as they don't have a chat message when mined,
			// so we track the session here instead.
			if (session == null)
			{
				session = new MiningSession();
			}
			session.setLastMined();
		}
		else
		{
			Pickaxe pickaxe = Pickaxe.fromAnimation(animId);
			if (pickaxe != null)
			{
				this.pickaxe = pickaxe;
				session.updateOreFound(ItemID.DENSE_ESSENCE_BLOCK, +1);
			}
		}
	}

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		clearExpiredRespawns();
		recentlyLoggedIn = false;

		if (session == null || session.getLastMined() == null)
		{
			return;
		}

		if (pickaxe != null && pickaxe.matchesMiningAnimation(client.getLocalPlayer()))
		{
			session.setLastMined();
			return;
		}

		Duration statTimeout = Duration.ofMinutes(config.statTimeout());
		Duration sinceMined = Duration.between(session.getLastMined(), Instant.now());

		if (sinceMined.compareTo(statTimeout) >= 0)
		{
			resetSession();
		}
	}

	/**
	 * Clears expired respawns and removes the hint arrow from expired Daeyalt essence rocks.
	 */
	private void clearExpiredRespawns()
	{
		respawns.removeIf(rockRespawn ->
		{
			final boolean expired = rockRespawn.isExpired();

			if (expired && rockRespawn.getRock() == Rock.DAEYALT_ESSENCE)
			{
				clearHintArrowAt(rockRespawn.getWorldPoint());
			}

			return expired;
		});
	}

	public void resetSession()
	{
		session = null;
		pickaxe = null;
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event)
	{
		if (client.getGameState() != GameState.LOGGED_IN || recentlyLoggedIn)
		{
			return;
		}

		final GameObject object = event.getGameObject();
		final int region = client.getLocalPlayer().getWorldLocation().getRegionID();

		Rock rock = Rock.getRock(object.getId());
		if (rock != null)
		{
			final WorldPoint point = object.getWorldLocation();

			if (rock == Rock.DAEYALT_ESSENCE)
			{
				respawns.removeIf(rockRespawn -> rockRespawn.getWorldPoint().equals(point));
				clearHintArrowAt(point);
			}
			else
			{
				RockRespawn rockRespawn = new RockRespawn(rock, point, Instant.now(), (int) rock.getRespawnTime(region).toMillis(), rock.getZOffset());
				respawns.add(rockRespawn);
			}
		}
	}

	private void clearHintArrowAt(WorldPoint worldPoint)
	{
		if (client.getHintArrowType() == HintArrowType.COORDINATE && client.getHintArrowPoint().equals(worldPoint))
		{
			client.clearHintArrow();
		}
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event)
	{
		if (client.getGameState() != GameState.LOGGED_IN || recentlyLoggedIn)
		{
			return;
		}

		GameObject object = event.getGameObject();
		Rock rock = Rock.getRock(object.getId());

		// Inverse timer to track daeyalt essence active duration
		if (rock == Rock.DAEYALT_ESSENCE)
		{
			final int region = client.getLocalPlayer().getWorldLocation().getRegionID();
			RockRespawn rockRespawn = new RockRespawn(rock, object.getWorldLocation(), Instant.now(), (int) rock.getRespawnTime(region).toMillis(), rock.getZOffset());
			respawns.add(rockRespawn);
			client.setHintArrow(object.getWorldLocation());
		}
		// If the Lovakite ore respawns before the timer is up, remove it
		else if (rock == Rock.LOVAKITE)
		{
			final WorldPoint point = object.getWorldLocation();
			respawns.removeIf(rockRespawn -> rockRespawn.getWorldPoint().equals(point));
		}
	}

	@Subscribe
	public void onWallObjectSpawned(WallObjectSpawned event)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		final WallObject object = event.getWallObject();
		final int region = client.getLocalPlayer().getWorldLocation().getRegionID();

		switch (object.getId())
		{
			case EMPTY_WALL:
			{
				Rock rock = Rock.AMETHYST;
				RockRespawn rockRespawn = new RockRespawn(rock, object.getWorldLocation(), Instant.now(), (int) rock.getRespawnTime(region).toMillis(), rock.getZOffset());
				respawns.add(rockRespawn);
				break;
			}
			case ROCKS_41549: // Depleted barronite vein
			case ROCKS_41550: // Depleted barronite vein
			{
				Rock rock = Rock.BARRONITE;
				RockRespawn rockRespawn = new RockRespawn(rock, object.getWorldLocation(), Instant.now(), (int) rock.getRespawnTime(region).toMillis(), rock.getZOffset());
				respawns.add(rockRespawn);
				break;
			}
			case DEPLETED_VEIN: // Depleted gold vein
			{
				Rock rock = Rock.MINERAL_VEIN;
				RockRespawn rockRespawn = new RockRespawn(rock, object.getWorldLocation(), Instant.now(), (int) rock.getRespawnTime(region).toMillis(), rock.getZOffset());
				respawns.add(rockRespawn);
				break;
			}
			case ORE_VEIN: // Motherlode vein
			case ORE_VEIN_26662: // Motherlode vein
			case ORE_VEIN_26663: // Motherlode vein
			case ORE_VEIN_26664: // Motherlode vein
			case BARRONITE_ROCKS: // Barronite vein
			case BARRONITE_ROCKS_41548: // Barronite vein
			case GOLD_VEIN: // Arzinian gold vein
			case GOLD_VEIN_5990: // Gold vein
			case GOLD_VEIN_5991: // Gold vein
			{
				// If the vein respawns before the timer is up, remove it
				final WorldPoint point = object.getWorldLocation();
				respawns.removeIf(rockRespawn -> rockRespawn.getWorldPoint().equals(point));
				break;
			}
		}
	}

	@Subscribe
	public void onChatMessage(ChatMessage event)
	{
		String chatMessage = event.getMessage();


		if (event.getType() == ChatMessageType.SPAM || event.getType() == ChatMessageType.GAMEMESSAGE)
		{
			if (MINING_PATTERN.matcher(event.getMessage()).matches())
			{
				if (session == null)
				{
					session = new MiningSession();
				}

				session.setLastMined();

				switch (chatMessage)
				{
				case "You find some minerals while you mine.":
					session.updateOreFound(ItemID.UNIDENTIFIED_MINERALS, +1);
					break;

				case "You manage to mine some clay.":
					session.updateOreFound(ItemID.CLAY, +1);
					break;
				case "You manage to mine some copper.":
					session.updateOreFound(ItemID.COPPER_ORE, +1);
					break;
				case "You manage to mine some tin.":
					session.updateOreFound(ItemID.TIN_ORE, +1);
					break;
				case "You manage to mine some iron.":
					session.updateOreFound(ItemID.IRON_ORE, +1);
					break;
				case "You manage to mine some silver.":
					session.updateOreFound(ItemID.SILVER_ORE, +1);
					break;
				case "You manage to mine some coal.":
					session.updateOreFound(ItemID.COAL, +1);
					break;
				case "You manage to mine some gold.":
					session.updateOreFound(ItemID.GOLD_ORE, +1);
					break;
				case "You manage to mine some mithril.":
					session.updateOreFound(ItemID.MITHRIL_ORE, +1);
					break;
				case "You manage to mine some adamantite.":
					session.updateOreFound(ItemID.ADAMANTITE_ORE, +1);
					break;
				case "You manage to mine some runite.":
					session.updateOreFound(ItemID.RUNITE_ORE, +1);
					break;
				case "You manage to mine some amethyst.":
					session.updateOreFound(ItemID.AMETHYST, +1);
					break;

				case "You just mined an Opal!":
					session.updateOreFound(ItemID.UNCUT_OPAL, +1);
					break;
				case "You just mined a piece of Jade!":
					session.updateOreFound(ItemID.UNCUT_JADE, +1);
					break;
				case "You just mined a Red Topaz!":
					session.updateOreFound(ItemID.UNCUT_RED_TOPAZ, +1);
					break;
				case "You just found a Sapphire!":
				case "You just mined a Sapphire!":
					session.updateOreFound(ItemID.UNCUT_SAPPHIRE, +1);
					break;
				case "You just found an Emerald!":
				case "You just mined an Emerald!":
					session.updateOreFound(ItemID.UNCUT_EMERALD, +1);
					break;
				case "You just found a Ruby!":
				case "You just mined a Ruby!":
					session.updateOreFound(ItemID.UNCUT_RUBY, +1);
					break;
				case "You just found a Diamond!":
				case "You just mined a Diamond!":
					session.updateOreFound(ItemID.UNCUT_DIAMOND, +1);
					break;
				case "You mined an extra block of essence thanks to your completion of the Kourend & Kebos Medium Diary.":
					session.updateOreFound(ItemID.DENSE_ESSENCE_BLOCK, +1);
					break;
				}
			}
		}
	}

	@Subscribe
	public void onScriptPreFired(ScriptPreFired scriptPreFired)
	{
		if (scriptPreFired.getScriptId() == ScriptID.ADD_OVERLAYTIMER_LOC)
		{
			var args = scriptPreFired.getScriptEvent().getArguments();
			int locCoord = (int) args[1];
			int locId = (int) args[2];
			int ticks = (int) args[5];

			switch (locId)
			{
				case DEPLETED_VEIN_26665: // Depleted motherlode vein
				case DEPLETED_VEIN_26666: // Depleted motherlode vein
				case DEPLETED_VEIN_26667: // Depleted motherlode vein
				case DEPLETED_VEIN_26668: // Depleted motherlode vein
				{
					WorldPoint worldPoint = new WorldPoint((locCoord >>> 14) & 0x3FFF, locCoord & 0x3FFF, (locCoord >>> 28) & 0x3);
					Rock rock = Rock.ORE_VEIN;
					RockRespawn rockRespawn = new RockRespawn(rock, worldPoint, Instant.now(), ticks * Constants.GAME_TICK_LENGTH, rock.getZOffset());
					respawns.add(rockRespawn);
					break;
				}
			}
		}
	}
}