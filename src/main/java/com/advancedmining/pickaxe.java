package com.advancedmining;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import lombok.Getter;

import static net.runelite.api.AnimationID.*;
import static net.runelite.api.ItemID.ADAMANT_PICKAXE;
import static net.runelite.api.ItemID.BLACK_PICKAXE;
import static net.runelite.api.ItemID.BRONZE_PICKAXE;
import static net.runelite.api.ItemID.CRYSTAL_PICKAXE;
import static net.runelite.api.ItemID.DRAGON_PICKAXE;
import static net.runelite.api.ItemID.DRAGON_PICKAXE_OR;
import static net.runelite.api.ItemID.DRAGON_PICKAXE_OR_25376;
import static net.runelite.api.ItemID.DRAGON_PICKAXE_12797;
import static net.runelite.api.ItemID.GILDED_PICKAXE;
import static net.runelite.api.ItemID.INFERNAL_PICKAXE;
import static net.runelite.api.ItemID.IRON_PICKAXE;
import static net.runelite.api.ItemID.MITHRIL_PICKAXE;
import static net.runelite.api.ItemID.RUNE_PICKAXE;
import static net.runelite.api.ItemID.STEEL_PICKAXE;
import static net.runelite.api.ItemID._3RD_AGE_PICKAXE;
import static net.runelite.api.ItemID.INFERNAL_PICKAXE_OR;
import net.runelite.api.Player;

@Getter
enum Pickaxe {
    BRONZE(BRONZE_PICKAXE, MINING_BRONZE_PICKAXE, MINING_MOTHERLODE_BRONZE, MINING_CRASHEDSTAR_BRONZE),
    IRON(IRON_PICKAXE, MINING_IRON_PICKAXE, MINING_MOTHERLODE_IRON, MINING_CRASHEDSTAR_IRON),
    STEEL(STEEL_PICKAXE, MINING_STEEL_PICKAXE, MINING_MOTHERLODE_STEEL, MINING_CRASHEDSTAR_STEEL),
    BLACK(BLACK_PICKAXE, MINING_BLACK_PICKAXE, MINING_MOTHERLODE_BLACK, MINING_CRASHEDSTAR_BLACK),
    MITHRIL(MITHRIL_PICKAXE, MINING_MITHRIL_PICKAXE, MINING_MOTHERLODE_MITHRIL, MINING_CRASHEDSTAR_MITHRIL),
    ADAMANT(ADAMANT_PICKAXE, MINING_ADAMANT_PICKAXE, MINING_MOTHERLODE_ADAMANT, MINING_CRASHEDSTAR_ADAMANT),
    RUNE(RUNE_PICKAXE, MINING_RUNE_PICKAXE, MINING_MOTHERLODE_RUNE, MINING_CRASHEDSTAR_RUNE),
    GILDED(GILDED_PICKAXE, MINING_GILDED_PICKAXE, MINING_MOTHERLODE_GILDED, MINING_CRASHEDSTAR_GILDED),
    DRAGON(DRAGON_PICKAXE, MINING_DRAGON_PICKAXE, MINING_MOTHERLODE_DRAGON, MINING_CRASHEDSTAR_DRAGON),
    DRAGON_OR(DRAGON_PICKAXE_OR, MINING_DRAGON_PICKAXE_OR, MINING_MOTHERLODE_DRAGON_OR, MINING_CRASHEDSTAR_DRAGON_OR),
    DRAGON_OR_TRAILBLAZER(DRAGON_PICKAXE_OR_25376, MINING_DRAGON_PICKAXE_OR_TRAILBLAZER, MINING_MOTHERLODE_DRAGON_OR_TRAILBLAZER, MINING_CRASHEDSTAR_DRAGON_OR_TRAILBLAZER),
    DRAGON_UPGRADED(DRAGON_PICKAXE_12797, MINING_DRAGON_PICKAXE_UPGRADED, MINING_MOTHERLODE_DRAGON_UPGRADED, MINING_CRASHEDSTAR_DRAGON_UPGRADED),
    INFERNAL(INFERNAL_PICKAXE, MINING_INFERNAL_PICKAXE, MINING_MOTHERLODE_INFERNAL, MINING_CRASHEDSTAR_INFERNAL),
    THIRDAGE(_3RD_AGE_PICKAXE, MINING_3A_PICKAXE, MINING_MOTHERLODE_3A, MINING_CRASHEDSTAR_3A),
    CRYSTAL(CRYSTAL_PICKAXE, MINING_CRYSTAL_PICKAXE, MINING_MOTHERLODE_CRYSTAL, MINING_CRASHEDSTAR_CRYSTAL),
    TRAILBLAZER(INFERNAL_PICKAXE_OR, MINING_TRAILBLAZER_PICKAXE, MINING_TRAILBLAZER_PICKAXE_2,
            MINING_TRAILBLAZER_PICKAXE_3, MINING_MOTHERLODE_TRAILBLAZER);

    private final int itemId;
    private final int[] animIds;

    private static final Map<Integer, Pickaxe> PICKAXE_ANIM_IDS;

    static {
        ImmutableMap.Builder<Integer, Pickaxe> builder = new ImmutableMap.Builder<>();

        for (Pickaxe pickaxe : values()) {
            for (int animId : pickaxe.animIds) {
                builder.put(animId, pickaxe);
            }
        }
        PICKAXE_ANIM_IDS = builder.build();
    }

    Pickaxe(int itemId, int ... animIds) {
        this.itemId = itemId;
        this.animIds = animIds;
    }

    boolean matchesMiningAnimation(final Player player) {
        return player != null && fromAnimation(player.getAnimation()) == this;
    }

    static Pickaxe fromAnimation(int animId) {
        return PICKAXE_ANIM_IDS.get(animId);
    }
}