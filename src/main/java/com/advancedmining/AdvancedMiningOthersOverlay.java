package com.advancedmining;

import net.runelite.api.ItemID;
import net.runelite.api.MenuAction;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ComponentOrientation;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;
import java.time.Duration;
import java.time.Instant;

import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

public class AdvancedMiningOthersOverlay extends OverlayPanel {
    private final AdvancedMiningPlugin plugin;
    private final AdvancedMiningConfig config;
    private final ItemManager itemManager;

    @Inject
    private AdvancedMiningOthersOverlay(final AdvancedMiningPlugin plugin, final AdvancedMiningConfig config, ItemManager itemManager) {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        this.plugin = plugin;
        this.config = config;
        this.itemManager = itemManager;
        addMenuEntry(MenuAction.RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Others Overlay");
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        MiningSession session = plugin.getSession();

        if (session.getLastOthersMined() == null || !config.showOthersMined()) {
            return null;
        }

        Duration statTimeout = Duration.ofMinutes(config.statTimeout());
        Duration sinceLastOthers = Duration.between(session.getLastOthersMined(), Instant.now());

        if (sinceLastOthers.compareTo(statTimeout) >= 0)
        {
            return null;
        }

        int mineralsFound = session.getMineralsFound();
        int stardustFound = session.getStardustFound();
        int barroniteshardsFound = session.getBarroniteshardsFound();
        int barronitedepositFound = session.getBarronitedepositFound();
        int boneshardsFound = session.getBoneshardsFound();
        int calcifieddepositFound = session.getCalcifieddepositFound();
        int geodeFound = session.getGeodeFound();

        if (mineralsFound == 0 && stardustFound == 0 && barroniteshardsFound == 0 && barronitedepositFound == 0 &&
                boneshardsFound == 0 && calcifieddepositFound == 0 && geodeFound == 0) {
            return null;
        }

        if(config.showLootIcons()) {
            panelComponent.setOrientation(ComponentOrientation.HORIZONTAL);
            if (mineralsFound > 0) {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.UNIDENTIFIED_MINERALS, mineralsFound, true)));
            }
            if (stardustFound > 0) {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.STARDUST, stardustFound, true)));
            }
            if (barroniteshardsFound > 0) {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.BARRONITE_SHARDS, barroniteshardsFound, true)));
            }
            if (barronitedepositFound > 0) {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.BARRONITE_DEPOSIT, barronitedepositFound, true)));
            }
            if (boneshardsFound > 0) {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.BLESSED_BONE_SHARDS, boneshardsFound, true)));
            }
            if (calcifieddepositFound > 0) {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.CALCIFIED_DEPOSIT, calcifieddepositFound, true)));
            }
            if (geodeFound > 0) {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.CLUE_GEODE_BEGINNER, geodeFound, true)));
            }
        } else {
            panelComponent.setOrientation(ComponentOrientation.VERTICAL);
            if (mineralsFound > 0) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Minerals:")
                        .right(Integer.toString(mineralsFound))
                        .build());
            }
            if (stardustFound > 0) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Stardust:")
                        .right(Integer.toString(stardustFound))
                        .build());
            }
            if (barroniteshardsFound > 0) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Barronite Shards:")
                        .right(Integer.toString(barroniteshardsFound))
                        .build());
            }
            if (barronitedepositFound > 0) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Barronite Deposit:")
                        .right(Integer.toString(barronitedepositFound))
                        .build());
            }
            if (boneshardsFound > 0) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Bone Shards:")
                        .right(Integer.toString(boneshardsFound))
                        .build());
            }
            if (calcifieddepositFound > 0) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Calcified Deposit:")
                        .right(Integer.toString(calcifieddepositFound))
                        .build());
            }
            if (geodeFound > 0) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Clue Geodes:")
                        .right(Integer.toString(geodeFound))
                        .build());
            }
        }
        return super.render(graphics);
    }
}