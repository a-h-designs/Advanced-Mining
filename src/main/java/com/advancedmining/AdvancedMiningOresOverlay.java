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

import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

public class AdvancedMiningOresOverlay extends OverlayPanel {
    private final AdvancedMiningPlugin plugin;
    private final AdvancedMiningConfig config;
    private final ItemManager itemManager;

    @Inject
    private AdvancedMiningOresOverlay(final AdvancedMiningPlugin plugin, final AdvancedMiningConfig config, ItemManager itemManager) {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        this.plugin = plugin;
        this.config = config;
        this.itemManager = itemManager;
        addMenuEntry(MenuAction.RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Ores Overlay");
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        MiningSession session = plugin.getSession();

        if (!config.showOresMined()) {
            return null;
        }

        int clayFound = session != null ? session.getClayFound() : 0;
        int copperFound = session != null ? session.getCopperFound() : 0;
        int tinFound = session != null ? session.getTinFound() : 0;
        int ironFound = session != null ? session.getIronFound() : 0;
        int silverFound = session != null ? session.getSilverFound() : 0;
        int coalFound = session != null ? session.getCoalFound() : 0;
        int goldFound = session != null ? session.getGoldFound() : 0;
        int mithrilFound = session != null ? session.getMithrilFound() : 0;
        int adamantiteFound = session != null ? session.getAdamantiteFound() : 0;
        int runiteFound = session != null ? session.getRuniteFound() : 0;
        int amethystFound = session != null ? session.getAmethystFound() : 0;

        if (clayFound == 0 && copperFound == 0 && tinFound == 0 && ironFound == 0 && silverFound == 0 &&
                coalFound == 0 && goldFound == 0 && mithrilFound == 0 && adamantiteFound == 0 && runiteFound == 0 &&
                amethystFound == 0) {
            return null;
        }

        if(config.showLootIcons()) {
            panelComponent.setOrientation(ComponentOrientation.HORIZONTAL);
            if (clayFound > 0) {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.CLAY, clayFound, true)));
            }
            if (copperFound > 0) {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.COPPER_ORE, copperFound, true)));
            }
            if (tinFound > 0) {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.TIN_ORE, tinFound, true)));
            }
            if (ironFound > 0) {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.IRON_ORE, ironFound, true)));
            }
            if (silverFound > 0) {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.SILVER_ORE, silverFound, true)));
            }
            if (coalFound > 0) {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.COAL, coalFound, true)));
            }
            if (goldFound > 0) {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.GOLD_ORE, goldFound, true)));
            }
            if (mithrilFound > 0) {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.MITHRIL_ORE, mithrilFound, true)));
            }
            if (adamantiteFound > 0) {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.ADAMANTITE_ORE, adamantiteFound, true)));
            }
            if (runiteFound > 0) {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.RUNITE_ORE, runiteFound, true)));
            }
            if (amethystFound > 0) {
                panelComponent.getChildren().add(new ImageComponent(itemManager.getImage(ItemID.AMETHYST, amethystFound, true)));
            }
        } else {
            panelComponent.setOrientation(ComponentOrientation.VERTICAL);
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("Ores mined")
                    .color(Color.YELLOW)
                    .build());
            if (clayFound > 0) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Clay:")
                        .right(Integer.toString(clayFound))
                        .build());
            }
            if (copperFound > 0) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Copper:")
                        .right(Integer.toString(copperFound))
                        .build());
            }
            if (tinFound > 0) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Tin:")
                        .right(Integer.toString(tinFound))
                        .build());
            }
            if (ironFound > 0) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Iron:")
                        .right(Integer.toString(ironFound))
                        .build());
            }
            if (silverFound > 0) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Silver:")
                        .right(Integer.toString(silverFound))
                        .build());
            }
            if (coalFound > 0) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Coal:")
                        .right(Integer.toString(coalFound))
                        .build());
            }
            if (goldFound > 0) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Gold:")
                        .right(Integer.toString(goldFound))
                        .build());
            }
            if (mithrilFound > 0) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Mithril:")
                        .right(Integer.toString(mithrilFound))
                        .build());
            }
            if (adamantiteFound > 0) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Adamantite:")
                        .right(Integer.toString(adamantiteFound))
                        .build());
            }
            if (runiteFound > 0) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Runite:")
                        .right(Integer.toString(runiteFound))
                        .build());
            }
            if (amethystFound > 0) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Amethyst:")
                        .right(Integer.toString(amethystFound))
                        .build());
            }
        }
        return super.render(graphics);
    }
}