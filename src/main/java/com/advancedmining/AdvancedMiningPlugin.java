package com.advancedmining;

import com.google.common.collect.ImmutableSet;
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

import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.*;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.xptracker.XpTrackerPlugin;
import net.runelite.client.ui.overlay.OverlayManager;

import static net.runelite.api.ObjectID.*;

@PluginDescriptor(
		name = "Advanced Mining",
		description = "A more advanced mining plugin to display statistics and ore respawn timers",
		tags = {"mining", "advanced", "gems", "ore", "minerals", "essence", "overlay", "skilling", "timers", "rock", "clue", "geode", "calcified", "deposit", "bone", "shard", "star", "stardust"},
		conflicts = "Mining"
)
@PluginDependency(XpTrackerPlugin.class)
public class AdvancedMiningPlugin extends Plugin {
	private static final int ARCEUUS_REGION = 6972;
	private static final ImmutableSet<Integer> MLM_REGION = ImmutableSet.of(14679, 14680, 14681, 14935, 14936, 14937, 15191, 15192, 15193);
	public static final Pattern KOUREND_PATTERN = Pattern.compile(
			"You mined an extra block of essence thanks to your completion of the Kourend & Kebos Medium Diary."
	);
	public static final Pattern MINING_PATTERN = Pattern.compile(
			"(?:Your?|The) " +
					"(?:manage to|just|find|mined|Varrock platebody|cape|swing)" +
					" (?:mined?|quarry|some|found|an?|chip off|mine|enabled you|allows you|your) " +
					"(?:some|an?|minerals|extra|clue|to mine|pick)[\\w ]+(?:\\.|!)");
					/*"(?:some|an?|minerals|extra|clue|to mine|pick) " +
					"(?:while you mine|copper|tin|clay|iron|silver|coal|gold|bone shards|calcified deposit|mithril|adamantite|runite|amethyst|sandstone|granite|barronite shards|barronite deposit|Opal|piece of Jade|Red Topaz|Emerald|Sapphire|Ruby|Diamond|block of essence thanks to your completion of the Kourend & Kebos Medium Diary|geode|an additional ore|at the star)" +
					"(?:\\.|!)");*/

	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private AdvancedMiningOverlay overlay;

	@Inject
	private AdvancedMiningOthersOverlay advancedMiningOthersOverlay;

	@Inject
	private AdvancedMiningOresOverlay advancedMiningOresOverlay;

	@Inject
	private AdvancedMiningAdditionalOverlay advancedMiningAdditionalOverlay;

	@Inject
	private AdvancedMiningGemsOverlay advancedMiningGemsOverlay;

	@Inject
	private AdvancedMiningEssenceOverlay advancedMiningEssenceOverlay;

	@Inject
	private AdvancedMiningPrayerOverlay advancedMiningPrayerOverlay;

	@Inject
	private MiningRocksOverlay rocksOverlay;

	@Inject
	private AdvancedMiningConfig config;

	private int previousAmount;
	private int newInventoryAmount;
    private int newAmount;

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
	AdvancedMiningConfig getConfig(ConfigManager configManager) {
		return configManager.getConfig(AdvancedMiningConfig.class);
	}

	@Override
	protected void startUp() {
		overlayManager.add(overlay);
		overlayManager.add(advancedMiningOthersOverlay);
		overlayManager.add(advancedMiningOresOverlay);
		overlayManager.add(advancedMiningAdditionalOverlay);
		overlayManager.add(advancedMiningGemsOverlay);
		overlayManager.add(advancedMiningEssenceOverlay);
		overlayManager.add(advancedMiningPrayerOverlay);
		overlayManager.add(rocksOverlay);
	}

	@Override
	protected void shutDown() throws Exception {
		session = null;
		pickaxe = null;
		overlayManager.remove(overlay);
		overlayManager.remove(advancedMiningOthersOverlay);
		overlayManager.remove(advancedMiningOresOverlay);
		overlayManager.remove(advancedMiningAdditionalOverlay);
		overlayManager.remove(advancedMiningGemsOverlay);
		overlayManager.remove(advancedMiningEssenceOverlay);
		overlayManager.remove(advancedMiningPrayerOverlay);
		overlayManager.remove(rocksOverlay);
		respawns.forEach(respawn -> clearHintArrowAt(respawn.getWorldPoint()));
		respawns.clear();
	}

	private boolean isInArceuusRegion() {
		if (client.getLocalPlayer() != null) {
			return client.getLocalPlayer().getWorldLocation().getRegionID() == ARCEUUS_REGION;
		}
		return false;
	}

	private boolean isInMLMRegion() {
		GameState gameState = client.getGameState();
		if (gameState != GameState.LOGGED_IN
				&& gameState != GameState.LOADING)
		{
			return false;
		}

		int[] currentMapRegions = client.getMapRegions();

		// Verify that all regions exist in MLM_REGION
		for (int region : currentMapRegions)
		{
			if (!MLM_REGION.contains(region))
			{
				return false;
			}
		}

		return true;
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event) {
		switch (event.getGameState()) {
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
	public void onAnimationChanged(final AnimationChanged event) {
		Player local = client.getLocalPlayer();

		if (event.getActor() != local) {
			return;
		}

		int animId = local.getAnimation();
		if (animId == AnimationID.DENSE_ESSENCE_CHIPPING) {
			// Can't use chat messages to start mining session on Dense Essence as they don't have a chat message when mined,
			// so we track the session here instead.
			if (session == null) {
				session = new MiningSession();
			}
			session.setLastMined();
		} else {
			Pickaxe pickaxe = Pickaxe.fromAnimation(animId);
			if (pickaxe != null) {
				this.pickaxe = pickaxe;
				//Big thanks to Gamma1991 & Antimated for giving me the suggestions on how to get this to work better.
				if (isInArceuusRegion()) {
					session.updateOreFound(ItemID.DENSE_ESSENCE_BLOCK, +1);
				}
			}
		}
	}

	@Subscribe
	public void onGameTick(GameTick gameTick) {
		if (recentlyLoggedIn) {
			final ItemContainer itemContainer = client.getItemContainer(InventoryID.INVENTORY);
			if (itemContainer != null) {
				final Item[] items = itemContainer.getItems();
				for (Item i : items) {
					if (i.getId() == ItemID.BLESSED_BONE_SHARDS) {
						previousAmount = i.getQuantity();
						recentlyLoggedIn = false;
					}
					if (i.getId() == ItemID.STARDUST) {
						previousAmount = i.getQuantity();
						recentlyLoggedIn = false;
					}
					if (i.getId() == ItemID.BARRONITE_SHARDS) {
						previousAmount = i.getQuantity();
						recentlyLoggedIn = false;
					}
				}
			}
		}
		clearExpiredRespawns();
		recentlyLoggedIn = false;

		if (session == null || session.getLastMined() == null) {
			return;
		}

		if (pickaxe != null && pickaxe.matchesMiningAnimation(client.getLocalPlayer())) {
			session.setLastMined();
			return;
		}

		Duration statTimeout = Duration.ofMinutes(config.statTimeout());
		Duration sinceMined = Duration.between(session.getLastMined(), Instant.now());

		if (sinceMined.compareTo(statTimeout) >= 0) {
			resetSession();
		}
	}

	/**
	 * Clears expired respawns and removes the hint arrow from expired Daeyalt essence rocks.
	 */
	private void clearExpiredRespawns() {
		respawns.removeIf(rockRespawn -> {
			final boolean expired = rockRespawn.isExpired();

			if (expired && rockRespawn.getRock() == Rock.DAEYALT_ESSENCE) {
				clearHintArrowAt(rockRespawn.getWorldPoint());
			}
			return expired;
		});
	}

	public void resetSession() {
		session = null;
		pickaxe = null;
	}

	@Subscribe
	public void onGameObjectDespawned(GameObjectDespawned event) {
		if (client.getGameState() != GameState.LOGGED_IN || recentlyLoggedIn) {
			return;
		}

		final GameObject object = event.getGameObject();
		final int region = client.getLocalPlayer().getWorldLocation().getRegionID();

		Rock rock = Rock.getRock(object.getId());
		if (rock != null) {
			final WorldPoint point = object.getWorldLocation();

			if (rock == Rock.DAEYALT_ESSENCE) {
				respawns.removeIf(rockRespawn -> rockRespawn.getWorldPoint().equals(point));
				clearHintArrowAt(point);
			} else {
				RockRespawn rockRespawn = new RockRespawn(rock, point, Instant.now(), (int) rock.getRespawnTime(region).toMillis(), rock.getZOffset());
				respawns.add(rockRespawn);
			}
		}
	}

	private void clearHintArrowAt(WorldPoint worldPoint) {
		if (client.getHintArrowType() == HintArrowType.COORDINATE && client.getHintArrowPoint().equals(worldPoint)) {
			client.clearHintArrow();
		}
	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned event) {
		if (client.getGameState() != GameState.LOGGED_IN || recentlyLoggedIn) {
			return;
		}

		GameObject object = event.getGameObject();
		Rock rock = Rock.getRock(object.getId());

		// Inverse timer to track daeyalt essence active duration
		if (rock == Rock.DAEYALT_ESSENCE) {
			final int region = client.getLocalPlayer().getWorldLocation().getRegionID();
			RockRespawn rockRespawn = new RockRespawn(rock, object.getWorldLocation(), Instant.now(), (int) rock.getRespawnTime(region).toMillis(), rock.getZOffset());
			respawns.add(rockRespawn);
			client.setHintArrow(object.getWorldLocation());
		}
		// If the Lovakite ore or Calcified Rock respawns before the timer is up, remove it
		else if (rock == Rock.LOVAKITE || rock == Rock.CALCIFIED_ROCK) {
			final WorldPoint point = object.getWorldLocation();
			respawns.removeIf(rockRespawn -> rockRespawn.getWorldPoint().equals(point));
		}
	}

	@Subscribe
	public void onWallObjectSpawned(WallObjectSpawned event) {
		if (client.getGameState() != GameState.LOGGED_IN) {
			return;
		}

		final WallObject object = event.getWallObject();
		final int region = client.getLocalPlayer().getWorldLocation().getRegionID();

		switch (object.getId()) {
			case EMPTY_WALL: {
				Rock rock = Rock.AMETHYST;
				RockRespawn rockRespawn = new RockRespawn(rock, object.getWorldLocation(), Instant.now(), (int) rock.getRespawnTime(region).toMillis(), rock.getZOffset());
				respawns.add(rockRespawn);
				break;
			}
			case ROCKS_41549: // Depleted barronite vein
			case ROCKS_41550: { // Depleted barronite vein
				Rock rock = Rock.BARRONITE;
				RockRespawn rockRespawn = new RockRespawn(rock, object.getWorldLocation(), Instant.now(), (int) rock.getRespawnTime(region).toMillis(), rock.getZOffset());
				respawns.add(rockRespawn);
				break;
			}
			case DEPLETED_VEIN: { // Depleted gold vein
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
			case GOLD_VEIN_5991: { // Gold vein
				// If the vein respawns before the timer is up, remove it
				final WorldPoint point = object.getWorldLocation();
				respawns.removeIf(rockRespawn -> rockRespawn.getWorldPoint().equals(point));
				break;
			}
		}
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event) {
		if (event.getItemContainer() == client.getItemContainer(InventoryID.INVENTORY)) {
			Item[] items = event.getItemContainer().getItems();
			for (Item i : items) {
                if (i.getId() == ItemID.BLESSED_BONE_SHARDS) {
                    newInventoryAmount = i.getQuantity();
                }
				if (i.getId() == ItemID.STARDUST) {
					newInventoryAmount = i.getQuantity();
					checkAmount();
					session.updateOthersMined(ItemID.STARDUST, newAmount);
				}
				if (i.getId() == ItemID.BARRONITE_SHARDS) {
					newInventoryAmount = i.getQuantity();
				}
			}
		}
	}

	private void checkAmount() {
		newAmount = newInventoryAmount - previousAmount;
		previousAmount = newInventoryAmount;
	}

	@Subscribe
	public void onChatMessage(ChatMessage event) {
		String chatMessage = event.getMessage();

        if (event.getType() == ChatMessageType.PUBLICCHAT) {
            if (chatMessage.equals("Smashing!")) {
				session.updateOthersMined(ItemID.DRAGON_PICKAXE_12797, +1);
            }
        }

		if (event.getType() == ChatMessageType.SPAM || event.getType() == ChatMessageType.GAMEMESSAGE) {
			if (KOUREND_PATTERN.matcher(event.getMessage()).matches()) {
				session.updateOreFound(SpriteID.QUESTS_PAGE_ICON_GREEN_ACHIEVEMENT_DIARIES, +1);
			}
			if (MINING_PATTERN.matcher(event.getMessage()).matches()) {
				if (session == null) {
					session = new MiningSession();
				}
				session.setLastMined();
				//int miningSKILL
				if (!isInMLMRegion()) {
					switch (chatMessage) {
						case "You find some minerals while you mine.":
							session.updateOthersMined(ItemID.UNIDENTIFIED_MINERALS, +1);
							break;
						case "You swing your pick at the star.":
							checkAmount();
							break;
						case "You manage to mine some volcanic ash.":
							//if ()

						case "You manage to mine some barronite shards.":
							checkAmount();
							session.updateOthersMined(ItemID.BARRONITE_SHARDS, newAmount);
							break;
						case "You manage to mine a barronite deposit.":
							session.updateOthersMined(ItemID.BARRONITE_DEPOSIT, +1);
							break;
						case "You manage to chip off some bone shards.":
							checkAmount();
							session.updateOthersMined(ItemID.BLESSED_BONE_SHARDS, newAmount);
							break;
						case "You manage to mine a calcified deposit.":
							session.updateOthersMined(ItemID.CALCIFIED_DEPOSIT, +1);
							break;
						case "You find a clue geode!":
							session.updateOthersMined(ItemID.CLUE_GEODE_BEGINNER, +1);
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

						case "The Varrock platebody enabled you to mine an additional ore.":
							session.updateAdditionalMined(ItemID.VARROCK_ARMOUR);
							break;
						case "Your cape allows you to mine an additional ore.":
							session.updateAdditionalMined(ItemID.MINING_CAPET);
							break;

						case "You just mined an Opal!":
							session.incrementGemFound(ItemID.UNCUT_OPAL);
							break;
						case "You just mined a piece of Jade!":
							session.incrementGemFound(ItemID.UNCUT_JADE);
							break;
						case "You just mined a Red Topaz!":
							session.incrementGemFound(ItemID.UNCUT_RED_TOPAZ);
							break;
						case "You just found a Sapphire!":
						case "You just mined a Sapphire!":
							session.incrementGemFound(ItemID.UNCUT_SAPPHIRE);
							break;
						case "You just found an Emerald!":
						case "You just mined an Emerald!":
							session.incrementGemFound(ItemID.UNCUT_EMERALD);
							break;
						case "You just found a Ruby!":
						case "You just mined a Ruby!":
							session.incrementGemFound(ItemID.UNCUT_RUBY);
							break;
						case "You just found a Diamond!":
						case "You just mined a Diamond!":
							session.incrementGemFound(ItemID.UNCUT_DIAMOND);
							break;
					}
				}
			}
		}
	}

	@Subscribe
	public void onScriptPreFired(ScriptPreFired scriptPreFired) {
		if (scriptPreFired.getScriptId() == ScriptID.ADD_OVERLAYTIMER_LOC) {
			var args = scriptPreFired.getScriptEvent().getArguments();
			int locCoord = (int) args[1];
			int locId = (int) args[2];
			int ticks = (int) args[5];

			switch (locId) {
				case DEPLETED_VEIN_26665: // Depleted motherlode vein
				case DEPLETED_VEIN_26666: // Depleted motherlode vein
				case DEPLETED_VEIN_26667: // Depleted motherlode vein
				case DEPLETED_VEIN_26668: { // Depleted motherlode vein
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