package com.advancedmining;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;

import net.runelite.api.*;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.xptracker.XpTrackerService;

import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ComponentOrientation;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

class AdvancedMiningOverlay extends OverlayPanel
{
    private static final String MINING_RESET = "Reset";

    private final Client client;
    private final AdvancedMiningPlugin plugin;
    private final AdvancedMiningConfig config;
    private final ItemManager itemManager;
    private final XpTrackerService xpTrackerService;

    @Inject
    private AdvancedMiningOverlay(final Client client, final AdvancedMiningPlugin plugin, final AdvancedMiningConfig config, ItemManager itemManager, XpTrackerService xpTrackerService)
    {
        super(plugin);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.itemManager = itemManager;
        this.xpTrackerService = xpTrackerService;
        addMenuEntry(MenuAction.RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Advanced Mining overlay");
        addMenuEntry(MenuAction.RUNELITE_OVERLAY, MINING_RESET, "Advanced Mining overlay", e -> plugin.setSession(null));
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        MiningSession session = plugin.getSession();

        int mineralsFound = session.getMineralsFound();
        int geodeFound = session.getGeodeFound();

        int clayFound = session.getClayFound();
        int copperFound = session.getCopperFound();
        int tinFound = session.getTinFound();
        int ironFound = session.getIronFound();
        int silverFound = session.getSilverFound();
        int coalFound = session.getCoalFound();
        int goldFound = session.getGoldFound();
        int boneshardsFound = session.getBoneshardsFound();
        int calcifieddepositFound = session.getCalcifieddepositFound();
        int mithrilFound = session.getMithrilFound();
        int adamantiteFound = session.getAdamantiteFound();
        int runiteFound = session.getRuniteFound();
        int amethystFound = session.getAmethystFound();

        int opalsFound = session.getOpalsFound();
        int jadesFound = session.getJadesFound();
        int topazsFound = session.getTopazsFound();
        int sapphiresFound = session.getSapphiresFound();
        int emeraldsFound = session.getEmeraldsFound();
        int rubiesFound = session.getRubiesFound();
        int diamondsFound = session.getDiamondsFound();

        int runeessFound = session.getRuneessFound();
        int pureessFound = session.getPureessFound();
        int denseessFound = session.getDenseessFound();

        if (mineralsFound == 0 && geodeFound == 0 && clayFound == 0 && copperFound == 0 && tinFound == 0 &&
                ironFound == 0 && silverFound == 0 && coalFound == 0 && goldFound == 0 && boneshardsFound == 0 &&
                calcifieddepositFound == 0 && mithrilFound == 0 && adamantiteFound == 0 && runiteFound == 0 &&
                amethystFound == 0 && opalsFound == 0 && jadesFound == 0 && topazsFound == 0 && sapphiresFound == 0 &&
                emeraldsFound == 0 && rubiesFound == 0 && diamondsFound == 0 && runeessFound == 0 &&
                pureessFound == 0 && denseessFound == 0)
        {
            return null;
        }

        if (session.getLastMined() == null || !config.showMiningStats())
        {
            return null;
        }

        if(!config.showLootIcons())
        {
            panelComponent.setOrientation(ComponentOrientation.VERTICAL);
            setPosition(OverlayPosition.TOP_LEFT);
            Pickaxe pickaxe = plugin.getPickaxe();
            if (pickaxe != null && (pickaxe.matchesMiningAnimation(client.getLocalPlayer()) || client.getLocalPlayer().getAnimation() == AnimationID.DENSE_ESSENCE_CHIPPING))
            {
                panelComponent.getChildren().add(TitleComponent.builder()
                        .text("Mining")
                        .color(Color.GREEN)
                        .build());
            }
            else
            {
                panelComponent.getChildren().add(TitleComponent.builder()
                        .text("NOT mining")
                        .color(Color.RED)
                        .build());
            }

            int actions = xpTrackerService.getActions(Skill.MINING);
            if (actions > 0)
            {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Total mined:")
                        .right(Integer.toString(actions))
                        .build());

                if (actions > 2)
                {
                    panelComponent.getChildren().add(LineComponent.builder()
                            .left("Mined/hr:")
                            .right(Integer.toString(xpTrackerService.getActionsHr(Skill.MINING)))
                            .build());
                }

                panelComponent.getChildren().add(TitleComponent.builder()
                        .text("--------")
                        .build());
            }
            if (mineralsFound > 0)
            {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Minerals:")
                        .right(Integer.toString(mineralsFound))
                        .build());
            }
            if (geodeFound > 0)
            {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Clue Geodes:")
                        .right(Integer.toString(geodeFound))
                        .build());
            }
            if (clayFound > 0 || copperFound > 0 || tinFound > 0 || ironFound > 0 || silverFound > 0 || coalFound > 0 ||
                    goldFound > 0 || boneshardsFound > 0 || calcifieddepositFound > 0 || mithrilFound > 0 ||
                    adamantiteFound > 0 || runiteFound > 0 || amethystFound > 0)
            {
                panelComponent.getChildren().add(TitleComponent.builder()
                        .text("Ores")
                        .color(Color.YELLOW)
                        .build());
            }
            if (clayFound > 0)
            {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Clay:")
                        .right(Integer.toString(clayFound))
                        .build());
            }
            if (copperFound > 0)
            {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Copper:")
                        .right(Integer.toString(copperFound))
                        .build());
            }
            if (tinFound > 0)
            {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Tin:")
                        .right(Integer.toString(tinFound))
                        .build());
            }
            if (ironFound > 0)
            {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Iron:")
                        .right(Integer.toString(ironFound))
                        .build());
            }
            if (silverFound > 0)
            {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Silver:")
                        .right(Integer.toString(silverFound))
                        .build());
            }
            if (coalFound > 0)
            {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Coal:")
                        .right(Integer.toString(coalFound))
                        .build());
            }
            if (goldFound > 0)
            {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Gold:")
                        .right(Integer.toString(goldFound))
                        .build());
            }
            if (boneshardsFound > 0)
            {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Bone Shards:")
                        .right(Integer.toString(boneshardsFound))
                        .build());
            }
            if (calcifieddepositFound > 0)
            {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Calcified Deposit:")
                        .right(Integer.toString(calcifieddepositFound))
                        .build());
            }
            if (mithrilFound > 0)
            {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Mithril:")
                        .right(Integer.toString(mithrilFound))
                        .build());
            }
            if (adamantiteFound > 0)
            {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Adamantite:")
                        .right(Integer.toString(adamantiteFound))
                        .build());
            }
            if (runiteFound > 0)
            {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Runite:")
                        .right(Integer.toString(runiteFound))
                        .build());
            }
            if (amethystFound > 0)
            {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Amethyst:")
                        .right(Integer.toString(amethystFound))
                        .build());
            }
            if (opalsFound > 0 || jadesFound > 0 || topazsFound > 0 ||sapphiresFound > 0 || emeraldsFound > 0 ||
                    rubiesFound > 0 || diamondsFound > 0)
            {
                panelComponent.getChildren().add(TitleComponent.builder()
                        .text("Gems")
                        .color(Color.YELLOW)
                        .build());
            }
            if (opalsFound > 0)
            {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Opals:")
                        .right(Integer.toString(opalsFound))
                        .build());
            }
            if (jadesFound > 0)
            {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Jades:")
                        .right(Integer.toString(jadesFound))
                        .build());
            }
            if (topazsFound > 0)
            {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Red Topaz:")
                        .right(Integer.toString(topazsFound))
                        .build());
            }
            if (sapphiresFound > 0)
            {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Sapphires:")
                        .right(Integer.toString(sapphiresFound))
                        .build());
            }
            if (emeraldsFound > 0)
            {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Emeralds:")
                        .right(Integer.toString(emeraldsFound))
                        .build());
            }
            if (rubiesFound > 0)
            {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Rubies:")
                        .right(Integer.toString(rubiesFound))
                        .build());
            }
            if (diamondsFound > 0)
            {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Diamonds:")
                        .right(Integer.toString(diamondsFound))
                        .build());
            }
            if (runeessFound > 0 || pureessFound > 0 || denseessFound > 0)
            {
                panelComponent.getChildren().add(TitleComponent.builder()
                        .text("Essence")
                        .color(Color.YELLOW)
                        .build());
            }
            if (runeessFound > 0)
            {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Rune:")
                        .right(Integer.toString(runeessFound))
                        .build());
            }
            if (pureessFound > 0)
            {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Pure:")
                        .right(Integer.toString(pureessFound))
                        .build());
            }
            if (denseessFound > 0)
            {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Dense:")
                        .right(Integer.toString(denseessFound))
                        .build());
            }
        }
        else
        {
            panelComponent.setOrientation(ComponentOrientation.HORIZONTAL);
            setPosition(OverlayPosition.TOP_CENTER);
            if (mineralsFound > 0)
            {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.UNIDENTIFIED_MINERALS, mineralsFound, true)));
            }
            if (geodeFound > 0)
            {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.CLUE_GEODE_BEGINNER, geodeFound, true)));
            }
            if (clayFound > 0)
            {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.CLAY, clayFound, true)));
            }
            if (copperFound > 0)
            {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.COPPER_ORE, copperFound, true)));
            }
            if (tinFound > 0)
            {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.TIN_ORE, tinFound, true)));
            }
            if (ironFound > 0)
            {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.IRON_ORE, ironFound, true)));
            }
            if (silverFound > 0)
            {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.SILVER_ORE, silverFound, true)));
            }
            if (coalFound > 0)
            {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.COAL, coalFound, true)));
            }
            if (goldFound > 0)
            {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.GOLD_ORE, goldFound, true)));
            }
            if (boneshardsFound > 0)
            {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.BLESSED_BONE_SHARDS, boneshardsFound, true)));
            }
            if (calcifieddepositFound > 0)
            {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.CALCIFIED_DEPOSIT, calcifieddepositFound, true)));
            }
            if (mithrilFound > 0)
            {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.MITHRIL_ORE, mithrilFound, true)));
            }
            if (adamantiteFound > 0)
            {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.ADAMANTITE_ORE, adamantiteFound, true)));
            }
            if (runiteFound > 0)
            {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.RUNITE_ORE, runiteFound, true)));
            }
            if (amethystFound > 0)
            {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.AMETHYST, amethystFound, true)));
            }
            if (opalsFound > 0)
            {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.UNCUT_OPAL, opalsFound, true)));
            }
            if (jadesFound > 0)
            {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.UNCUT_JADE, jadesFound, true)));
            }
            if (topazsFound > 0)
            {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.UNCUT_RED_TOPAZ, topazsFound, true)));
            }
            if (sapphiresFound > 0)
            {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.UNCUT_SAPPHIRE, sapphiresFound, true)));
            }
            if (emeraldsFound > 0)
            {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.UNCUT_EMERALD, emeraldsFound, true)));
            }
            if (rubiesFound > 0)
            {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.UNCUT_RUBY, rubiesFound, true)));
            }
            if (diamondsFound > 0)
            {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.UNCUT_DIAMOND, diamondsFound, true)));
            }
            if (runeessFound > 0)
            {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.RUNE_ESSENCE, runeessFound, true)));
            }
            if (pureessFound > 0)
            {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.PURE_ESSENCE, pureessFound, true)));
            }
            if (denseessFound > 0)
            {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.DENSE_ESSENCE_BLOCK, denseessFound, true)));
            }
        }
        return super.render(graphics);
    }
}