package org.faketale.mnemic_odyssey.compendium;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import org.faketale.mnemic_odyssey.MnemicOdyssey;

public class ModTags {
    /** 本 mod 的头目（在 data/mnemic_odyssey/tags/entity_type/bosses.json 中登记） */
    public static final TagKey<EntityType<?>> BOSSES = TagKey.create(Registries.ENTITY_TYPE,
            ResourceLocation.fromNamespaceAndPath(MnemicOdyssey.MODID, "bosses"));
}
