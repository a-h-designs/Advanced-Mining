package com.advancedmining;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;

import net.runelite.api.*;
import net.runelite.client.plugins.xptracker.XpTrackerService;

import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ComponentOrientation;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

class AdvancedMiningOverlay extends OverlayPanel {
    private static final String MINING_RESET = "Reset";

    private final Client client;
    private final AdvancedMiningPlugin plugin;
    private final AdvancedMiningConfig config;
    private final XpTrackerService xpTrackerService;

    @Inject
    private AdvancedMiningOverlay(final Client client, final AdvancedMiningPlugin plugin, final AdvancedMiningConfig config, XpTrackerService xpTrackerService) {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.xpTrackerService = xpTrackerService;
        addMenuEntry(MenuAction.RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Advanced Mining overlay");
        addMenuEntry(MenuAction.RUNELITE_OVERLAY, MINING_RESET, "Advanced Mining overlay", e -> plugin.setSession(null));
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        MiningSession session = plugin.getSession();
        
        if ((session != null ? session.getLastMined() : null) == null || !config.showMiningStats()) {
            return null;
        }

        panelComponent.setOrientation(ComponentOrientation.VERTICAL);
        Pickaxe pickaxe = plugin.getPickaxe();
        if (config.showMiningState()) {
            if (pickaxe != null && (pickaxe.matchesMiningAnimation(client.getLocalPlayer()) ||
                    client.getLocalPlayer().getAnimation() == AnimationID.DENSE_ESSENCE_CHIPPING)) {
                panelComponent.getChildren().add(TitleComponent.builder()
                        .text("Mining")
                        .color(Color.GREEN)
                        .build());
            } else {
                panelComponent.getChildren().add(TitleComponent.builder()
                        .text("NOT mining")
                        .color(Color.RED)
                        .build());
            }
        }

        int actions = xpTrackerService.getActions(Skill.MINING);
        if (actions > 0) {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Total mined:")
                    .right(Integer.toString(actions))
                    .build());

            if (actions > 2) {
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Mined/hr:")
                        .right(Integer.toString(xpTrackerService.getActionsHr(Skill.MINING)))
                        .build());
            }
        }
        return super.render(graphics);
    }
}