package com.advancedmining;

import net.runelite.api.ItemID;
import net.runelite.api.MenuAction;
import net.runelite.api.SpriteID;
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

public class AdvancedMiningAdditionalOverlay extends OverlayPanel {
    private final AdvancedMiningPlugin plugin;
    private final AdvancedMiningConfig config;
    private final ItemManager itemManager;

    @Inject
    private AdvancedMiningAdditionalOverlay(final AdvancedMiningPlugin plugin, final AdvancedMiningConfig config, ItemManager itemManager) {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        this.plugin = plugin;
        this.config = config;
        this.itemManager = itemManager;
        addMenuEntry(MenuAction.RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Additional Overlay");
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        MiningSession session = plugin.getSession();

        if (session.getLastAdditionalMined() == null || !config.showAdditionalMined()) {
            return null;
        }

        Duration statTimeout = Duration.ofMinutes(config.statTimeout());
        Duration sinceLastAdditional = Duration.between(session.getLastAdditionalMined(), Instant.now());

        if (sinceLastAdditional.compareTo(statTimeout) >= 0)
        {
            return null;
        }

        int platebodyFound = session.getPlatebodyFound();
        int capeFound = session.getCapeFound();
        int achievmentFound = session.getAchievmentFound();

        if (platebodyFound == 0 && capeFound == 0 && achievmentFound == 0) {
            return null;
        }

        if(config.showLootIcons()) {
            panelComponent.setOrientation(ComponentOrientation.HORIZONTAL);
            if (platebodyFound > 0) {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.VARROCK_ARMOUR, platebodyFound, true)));
            }
            if (capeFound > 0) {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.MINING_CAPE, capeFound, true)));
            }
            if (achievmentFound > 0) {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(SpriteID.QUESTS_PAGE_ICON_GREEN_ACHIEVEMENT_DIARIES, achievmentFound, true)));
            }
        } else {
            panelComponent.setOrientation(ComponentOrientation.VERTICAL);
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Additional Ore")
                    .color(Color.YELLOW)
                    .build());

            if (platebodyFound > 0) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Platebody:")
                        .right(Integer.toString(platebodyFound))
                        .build());
            }
            if (capeFound > 0) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Cape:")
                        .right(Integer.toString(capeFound))
                        .build());
            }
            if (achievmentFound > 0) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Diaries:")
                        .right(Integer.toString(achievmentFound))
                        .build());
            }
        }
        return super.render(graphics);
    }
}