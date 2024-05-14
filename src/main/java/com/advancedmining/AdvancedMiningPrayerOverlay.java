package com.advancedmining;

import net.runelite.api.MenuAction;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ComponentOrientation;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;
import java.text.DecimalFormat;
import java.time.Duration;
import java.time.Instant;

import static net.runelite.client.ui.overlay.OverlayManager.OPTION_CONFIGURE;

public class AdvancedMiningPrayerOverlay extends OverlayPanel {
    private final AdvancedMiningPlugin plugin;
    private final AdvancedMiningConfig config;

    @Inject
    private AdvancedMiningPrayerOverlay(final AdvancedMiningPlugin plugin, final AdvancedMiningConfig config) {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        this.plugin = plugin;
        this.config = config;
        addMenuEntry(MenuAction.RUNELITE_OVERLAY_CONFIG, OPTION_CONFIGURE, "Ores Overlay");
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        MiningSession session = plugin.getSession();

        if ((session != null ? session.getLastOthersMined() : null) == null || !config.showPrayerXp()) {
            return null;
        }

        panelComponent.setOrientation(ComponentOrientation.VERTICAL);

        Duration statTimeout = Duration.ofMinutes(config.statTimeout());
        Duration sinceLastOthers = Duration.between(session.getLastOthersMined(), Instant.now());

        if (sinceLastOthers.compareTo(statTimeout) >= 0) {
            return null;
        }

        int boneshardsFound = session.getBoneshardsFound();

        if (boneshardsFound == 0) {
            return null;
        }

        double blessedWineXP = Double.parseDouble(String.valueOf(boneshardsFound * 5));
        double blessedSunfireWineXP = Double.parseDouble(String.valueOf(boneshardsFound * 6));
        DecimalFormat formatter = new DecimalFormat("#,###");

        if (config.showPrayerXp()) {
            if (boneshardsFound > 0) {
                panelComponent.getChildren().add(TitleComponent.builder()
                        .text("Potential Prayer XP")
                        .color(Color.CYAN)
                        .build());

                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Blessed Wine:")
                        .right("")
                        .build());
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("")
                        .right(String.format(String.valueOf(formatter.format(blessedWineXP))))
                        .build());

                panelComponent.getChildren().add(LineComponent.builder()
                        .left("Blessed Sunfire Wine:")
                        .right("")
                        .build());
                panelComponent.getChildren().add(LineComponent.builder()
                        .left("")
                        .right(String.format(String.valueOf(formatter.format(blessedSunfireWineXP))))
                        .build());
            }
        }
        return super.render(graphics);
    }
    }