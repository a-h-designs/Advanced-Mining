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

public class AdvancedMiningGemsOverlay extends OverlayPanel {
    private final AdvancedMiningPlugin plugin;
    private final AdvancedMiningConfig config;
    private final ItemManager itemManager;

    @Inject
    private AdvancedMiningGemsOverlay(final AdvancedMiningPlugin plugin, final AdvancedMiningConfig config, ItemManager itemManager) {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        this.plugin = plugin;
        this.config = config;
        this.itemManager = itemManager;
        addMenuEntry(MenuAction.RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Gems Overlay");
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        MiningSession session = plugin.getSession();

        if (session.getLastGemFound() == null || !config.showGemsFound()) {
            return null;
        }

        Duration statTimeout = Duration.ofMinutes(config.statTimeout());
        Duration sinceLastGem = Duration.between(session.getLastGemFound(), Instant.now());

        if (sinceLastGem.compareTo(statTimeout) >= 0)
        {
            return null;
        }

        int opalsFound = session.getOpalsFound();
        int jadesFound = session.getJadesFound();
        int topazsFound = session.getTopazsFound();
        int sapphiresFound = session.getSapphiresFound();
        int emeraldsFound = session.getEmeraldsFound();
        int rubiesFound = session.getRubiesFound();
        int diamondsFound = session.getDiamondsFound();

        if (opalsFound == 0 && jadesFound == 0 && topazsFound == 0 &&
                sapphiresFound == 0 && emeraldsFound == 0 && rubiesFound == 0 && diamondsFound == 0) {
            return null;
        }

        if(config.showLootIcons()) {
            panelComponent.setOrientation(ComponentOrientation.HORIZONTAL);
            if (opalsFound > 0) {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.UNCUT_OPAL, opalsFound, true)));
            }
            if (jadesFound > 0) {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.UNCUT_JADE, jadesFound, true)));
            }
            if (topazsFound > 0) {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.UNCUT_RED_TOPAZ, topazsFound, true)));
            }
            if (sapphiresFound > 0) {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.UNCUT_SAPPHIRE, sapphiresFound, true)));
            }
            if (emeraldsFound > 0) {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.UNCUT_EMERALD, emeraldsFound, true)));
            }
            if (rubiesFound > 0) {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.UNCUT_RUBY, rubiesFound, true)));
            }
            if (diamondsFound > 0) {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.UNCUT_DIAMOND, diamondsFound, true)));
            }
        } else {
            panelComponent.setOrientation(ComponentOrientation.VERTICAL);
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Gems found")
                    .color(Color.YELLOW)
                    .build());
            if (opalsFound > 0) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Opals:")
                        .right(Integer.toString(opalsFound))
                        .build());
            }
            if (jadesFound > 0) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Jades:")
                        .right(Integer.toString(jadesFound))
                        .build());
            }
            if (topazsFound > 0) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Red Topaz:")
                        .right(Integer.toString(topazsFound))
                        .build());
            }
            if (sapphiresFound > 0) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Sapphires:")
                        .right(Integer.toString(sapphiresFound))
                        .build());
            }
            if (emeraldsFound > 0) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Emeralds:")
                        .right(Integer.toString(emeraldsFound))
                        .build());
            }
            if (rubiesFound > 0) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Rubies:")
                        .right(Integer.toString(rubiesFound))
                        .build());
            }
            if (diamondsFound > 0) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Diamonds:")
                        .right(Integer.toString(diamondsFound))
                        .build());
            }
        }
        return super.render(graphics);
    }
}