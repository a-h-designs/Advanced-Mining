package com.advancedmining;

import java.time.Instant;

import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.ItemID;

class MiningSession
{
    @Getter
    private Instant lastMined;

    @Getter(AccessLevel.PACKAGE)
    private int diamondsFound;

    @Getter(AccessLevel.PACKAGE)
    private int rubiesFound;

    @Getter(AccessLevel.PACKAGE)
    private int emeraldsFound;

    @Getter(AccessLevel.PACKAGE)
    private int sapphiresFound;

    @Getter(AccessLevel.PACKAGE)
    private int mineralsFound;

    @Getter(AccessLevel.PACKAGE)
    private int ironFound;

    @Getter(AccessLevel.PACKAGE)
    private int coalFound;

    @Getter(AccessLevel.PACKAGE)
    private int goldFound;

    @Getter(AccessLevel.PACKAGE)
    private int mithrilFound;

    @Getter(AccessLevel.PACKAGE)
    private int adamantiteFound;

    @Getter(AccessLevel.PACKAGE)
    private int runiteFound;

    void updateOreFound(int item, int count)
    {
        switch (item)
        {
            case ItemID.UNIDENTIFIED_MINERALS:
                mineralsFound += count;
                break;
            case ItemID.IRON_ORE:
                ironFound += count;
                break;
            case ItemID.COAL:
                coalFound += count;
                break;
            case ItemID.GOLD_ORE:
                goldFound += count;
                break;
            case ItemID.MITHRIL_ORE:
                mithrilFound += count;
                break;
            case ItemID.ADAMANTITE_ORE:
                adamantiteFound += count;
                break;
            case ItemID.RUNITE_ORE:
                runiteFound += count;
                break;
            default:
                //log.debug("Invalid ore specified. The ore count will not be updated.");
        }
    }

    void setLastMined()
    {
        lastMined = Instant.now();
    }
}