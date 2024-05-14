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

import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

public class AdvancedMiningEssenceOverlay extends OverlayPanel {
    private final AdvancedMiningPlugin plugin;
    private final AdvancedMiningConfig config;
    private final ItemManager itemManager;

    @Inject
    private AdvancedMiningEssenceOverlay(final AdvancedMiningPlugin plugin, final AdvancedMiningConfig config, ItemManager itemManager) {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        this.plugin = plugin;
        this.config = config;
        this.itemManager = itemManager;
        addMenuEntry(MenuAction.RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Essence Overlay");
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        MiningSession session = plugin.getSession();

        if (!config.showEssenceMined()) {
            return null;
        }

        int runeessFound = session != null ? session.getRuneessFound() : 0;
        int pureessFound = session != null ? session.getPureessFound() : 0;
        int denseessFound = session != null ? session.getDenseessFound() : 0;

        if (runeessFound == 0 && pureessFound == 0 && denseessFound == 0) {
            return null;
        }

        if(config.showLootIcons()) {
            panelComponent.setOrientation(ComponentOrientation.HORIZONTAL);
            if (runeessFound > 0) {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.RUNE_ESSENCE, runeessFound, true)));
            }
            if (pureessFound > 0) {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.PURE_ESSENCE, pureessFound, true)));
            }
            if (denseessFound > 0) {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.DENSE_ESSENCE_BLOCK, denseessFound, true)));
            }
        } else {
            panelComponent.setOrientation(ComponentOrientation.VERTICAL);
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Essence")
                    .color(Color.YELLOW)
                    .build());

            if (runeessFound > 0) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Rune:")
                        .right(Integer.toString(runeessFound))
                        .build());
            }
            if (pureessFound > 0) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Pure:")
                        .right(Integer.toString(pureessFound))
                        .build());
            }
            if (denseessFound > 0) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Dense:")
                        .right(Integer.toString(denseessFound))
                        .build());
            }
        }
        return super.render(graphics);
    }
}