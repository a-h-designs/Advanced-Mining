package com.advancedmining;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.coords.WorldPoint;

@AllArgsConstructor
@Getter
class RockRespawn
{
    private final Rock rock;
    private final WorldPoint worldPoint;
    private final Instant startTime;
    private final int respawnTime;
    private final int zOffset;

    boolean isExpired()
    {
        return Instant.now().isAfter(startTime.plusMillis(respawnTime));
    }
}