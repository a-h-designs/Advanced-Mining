package com.advancedmining;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.time.Instant;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ProgressPieComponent;

class MiningRocksOverlay extends Overlay {
    static final int DAEYALT_MAX_RESPAWN_TIME = 110; // Game ticks
    private static final int DAEYALT_MIN_RESPAWN_TIME = 91; // Game ticks
    private static final float DAEYALT_RANDOM_PERCENT_THRESHOLD = (float) DAEYALT_MIN_RESPAWN_TIME / DAEYALT_MAX_RESPAWN_TIME;

    static final int LOVAKITE_ORE_MAX_RESPAWN_TIME = 65; // Game ticks
    private static final int LOVAKITE_ORE_MIN_RESPAWN_TIME = 50; // Game ticks
    private static final float LOVAKITE_ORE_RANDOM_PERCENT_THRESHOLD = (float) LOVAKITE_ORE_MIN_RESPAWN_TIME / LOVAKITE_ORE_MAX_RESPAWN_TIME;

    private static final Color DARK_GREEN = new Color(0, 100, 0);
    private static final int MOTHERLODE_UPPER_FLOOR_HEIGHT = -500;

    private final Client client;
    private final AdvancedMiningPlugin plugin;

    @Inject
    private MiningRocksOverlay(Client client, AdvancedMiningPlugin plugin) {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        this.plugin = plugin;
        this.client = client;
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        List<RockRespawn> respawns = plugin.getRespawns();
        if (respawns.isEmpty()) {
            return null;
        }

        Instant now = Instant.now();
        for (RockRespawn rockRespawn : respawns) {
            LocalPoint loc = LocalPoint.fromWorld(client, rockRespawn.getWorldPoint());
            if (loc == null) {
                continue;
            }

            float percent = (now.toEpochMilli() - rockRespawn.getStartTime().toEpochMilli()) / (float) rockRespawn.getRespawnTime();
            Point point = Perspective.localToCanvas(client, loc, client.getPlane(), rockRespawn.getZOffset());
            if (point == null || percent > 1.0f) {
                continue;
            }

            Rock rock = rockRespawn.getRock();

            // Only draw timer for veins on the same level in motherlode mine
            LocalPoint localLocation = client.getLocalPlayer().getLocalLocation();
            if (rock == Rock.ORE_VEIN && isUpstairsMotherlode(localLocation) != isUpstairsMotherlode(loc)) {
                continue;
            }

            Color pieFillColor = Color.YELLOW;
            Color pieBorderColor = Color.ORANGE;

            // Recolour pie during the portion of the timer where they may respawn
            if ((rock == Rock.DAEYALT_ESSENCE && percent > DAEYALT_RANDOM_PERCENT_THRESHOLD)
                    || (rock == Rock.LOVAKITE && percent > LOVAKITE_ORE_RANDOM_PERCENT_THRESHOLD)) {
                pieFillColor = Color.GREEN;
                pieBorderColor = DARK_GREEN;
            }

            ProgressPieComponent ppc = new ProgressPieComponent();
            ppc.setBorderColor(pieBorderColor);
            ppc.setFill(pieFillColor);
            ppc.setPosition(point);
            ppc.setProgress(percent);
            ppc.render(graphics);
        }
        return null;
    }

    /**
     * Checks if the given point is "upstairs" in the mlm.
     * The upper floor is actually on z=0.
     *
     * This method assumes that the given point is already in the mlm
     * and is not meaningful when outside the mlm.
     *
     * @param localPoint the LocalPoint to be tested
     * @return true if localPoint is at same height as mlm upper floor
     */
    private boolean isUpstairsMotherlode(LocalPoint localPoint) {
        return Perspective.getTileHeight(client, localPoint, 0) < MOTHERLODE_UPPER_FLOOR_HEIGHT;
    }
}