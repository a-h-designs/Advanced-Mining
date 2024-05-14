package com.advancedmining;

import java.time.Instant;

import ch.qos.logback.classic.Logger;
import lombok.AccessLevel;
import lombok.Getter;
import net.runelite.api.ItemID;
import net.runelite.api.SpriteID;

class MiningSession {
    @Getter
    private Instant lastMined;

    @Getter(AccessLevel.PACKAGE)
    private Instant lastOthersMined;
    @Getter(AccessLevel.PACKAGE)
    private Instant lastGemFound;
    @Getter(AccessLevel.PACKAGE)
    private Instant lastAdditionalMined;

    @Getter(AccessLevel.PACKAGE)
    private int mineralsFound;
    @Getter(AccessLevel.PACKAGE)
    private int stardustFound;
    @Getter(AccessLevel.PACKAGE)
    private int volcanicashFound;
    @Getter(AccessLevel.PACKAGE)
    private int boneshardsFound;
    @Getter(AccessLevel.PACKAGE)
    private int calcifieddepositFound;
    @Getter(AccessLevel.PACKAGE)
    private int barroniteshardsFound;
    @Getter(AccessLevel.PACKAGE)
    private int barronitedepositFound;
    @Getter(AccessLevel.PACKAGE)
    private int geodeFound;

    @Getter(AccessLevel.PACKAGE)
    private int clayFound;
    @Getter(AccessLevel.PACKAGE)
    private int copperFound;
    @Getter(AccessLevel.PACKAGE)
    private int tinFound;
    @Getter(AccessLevel.PACKAGE)
    private int limestoneFound;
    @Getter(AccessLevel.PACKAGE)
    private int bluriteFound;
    @Getter(AccessLevel.PACKAGE)
    private int ironFound;
    @Getter(AccessLevel.PACKAGE)
    private int silverFound;
    @Getter(AccessLevel.PACKAGE)
    private int coalFound;
    @Getter(AccessLevel.PACKAGE)
    private int sandstoneFound;
    @Getter(AccessLevel.PACKAGE)
    private int goldFound;
    @Getter(AccessLevel.PACKAGE)
    private int mithrilFound;
    @Getter(AccessLevel.PACKAGE)
    private int adamantiteFound;
    @Getter(AccessLevel.PACKAGE)
    private int runiteFound;
    @Getter(AccessLevel.PACKAGE)
    private int amethystFound;

    @Getter(AccessLevel.PACKAGE)
    private int platebodyFound;
    @Getter(AccessLevel.PACKAGE)
    private int capeFound;
    @Getter(AccessLevel.PACKAGE)
    private int achievmentFound;

    @Getter(AccessLevel.PACKAGE)
    private int opalsFound;
    @Getter(AccessLevel.PACKAGE)
    private int jadesFound;
    @Getter(AccessLevel.PACKAGE)
    private int topazsFound;
    @Getter(AccessLevel.PACKAGE)
    private int sapphiresFound;
    @Getter(AccessLevel.PACKAGE)
    private int emeraldsFound;
    @Getter(AccessLevel.PACKAGE)
    private int rubiesFound;
    @Getter(AccessLevel.PACKAGE)
    private int diamondsFound;

    @Getter(AccessLevel.PACKAGE)
    private int runeessFound;
    @Getter(AccessLevel.PACKAGE)
    private int pureessFound;
    @Getter(AccessLevel.PACKAGE)
    private int denseessFound;

    @Getter(AccessLevel.PACKAGE)
    private int smashing;

    private Logger log;

    void updateOthersMined (int itemID, int count) {
        lastOthersMined = Instant.now();
        switch (itemID) {
            case ItemID.UNIDENTIFIED_MINERALS:
                mineralsFound += count;
                break;
            case ItemID.STARDUST:
                stardustFound += count;
                break;
            case ItemID.VOLCANIC_ASH:
                volcanicashFound += count;
                break;
            case ItemID.BARRONITE_SHARDS:
                barroniteshardsFound += count;
                break;
            case ItemID.BARRONITE_DEPOSIT:
                barronitedepositFound += count;
                break;
            case ItemID.BLESSED_BONE_SHARDS:
                boneshardsFound += count;
                break;
            case ItemID.CALCIFIED_DEPOSIT:
                calcifieddepositFound += count;
                break;
            case ItemID.CLUE_GEODE_BEGINNER:
                geodeFound += count;
                break;
            case ItemID.DRAGON_PICKAXE_12797:
                smashing += count;
                break;
            default:
                log.debug("Invalid others specified. The others count will not be updated.");
        }
    }

    void updateOreFound(int item, int count) {
        switch (item) {
            case ItemID.CLAY:
                clayFound += count;
                break;
            case ItemID.COPPER_ORE:
                copperFound += count;
                break;
            case ItemID.TIN_ORE:
                tinFound += count;
                break;
            case ItemID.LIMESTONE:
                limestoneFound += count;
                break;
            case ItemID.BLURITE_ORE:
                bluriteFound += count;
                break;
            case ItemID.IRON_ORE:
                ironFound += count;
                break;
            case ItemID.SILVER_ORE:
                silverFound += count;
                break;
            case ItemID.COAL:
                coalFound += count;
                break;
            case ItemID.SANDSTONE_1KG:
            case ItemID.SANDSTONE_2KG:
            case ItemID.SANDSTONE_5KG:
            case ItemID.SANDSTONE_10KG:
                sandstoneFound += count;
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
            case ItemID.AMETHYST:
                amethystFound += count;
                break;

            case ItemID.RUNE_ESSENCE:
                runeessFound += count;
                break;
            case ItemID.PURE_ESSENCE:
                pureessFound += count;
                break;
            case ItemID.DENSE_ESSENCE_BLOCK:
                denseessFound += count;
                break;
            default:
                log.debug("Invalid ore specified. The ore count will not be updated.");
        }
    }

    void updateAdditionalMined (int itemID) {
        lastAdditionalMined = Instant.now();
        switch (itemID) {
            case ItemID.VARROCK_ARMOUR:
                platebodyFound++;
                break;
            case ItemID.MINING_CAPE:
                capeFound++;
                break;
            case SpriteID.QUESTS_PAGE_ICON_GREEN_ACHIEVEMENT_DIARIES:
                achievmentFound++;
                break;
            default:
                log.debug("Invalid item specified. The ore count will not be updated.");
        }
    }

    void incrementGemFound(int gemID) {
        lastGemFound = Instant.now();
        switch (gemID) {
            case ItemID.UNCUT_OPAL:
                opalsFound++;
                break;
            case ItemID.UNCUT_JADE:
                jadesFound++;
                break;
            case ItemID.UNCUT_RED_TOPAZ:
                topazsFound++;
                break;
            case ItemID.UNCUT_SAPPHIRE:
                sapphiresFound++;
                break;
            case ItemID.UNCUT_EMERALD:
                emeraldsFound++;
                break;
            case ItemID.UNCUT_RUBY:
                rubiesFound++;
                break;
            case ItemID.UNCUT_DIAMOND:
                diamondsFound++;
                break;
            default:
                log.debug("Invalid gem type specified. The gem count will not be incremented.");
        }
    }

    void incrementpublicChat(int itemID) {
        switch(itemID) {
            case ItemID.DRAGON_PICKAXE_12797:
                smashing++;
                break;
            case ItemID._3RD_AGE_AXE:
                diamondsFound++;
                break;
            default:
                log.debug("Invalid item type Specified. The count will not be incremented.");
        }
    }

    void setLastMined() {
        lastMined = Instant.now();
    }
}