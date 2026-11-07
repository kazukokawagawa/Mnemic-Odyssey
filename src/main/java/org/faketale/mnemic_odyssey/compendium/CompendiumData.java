package org.faketale.mnemic_odyssey.compendium;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/** 玩家身上的图鉴进度。进度属于玩家而不是图鉴物品，所以丢书重做后进度原样回归。 */
public class CompendiumData {
    public static final Codec<CompendiumData> CODEC = Codec.unboundedMap(Codec.STRING,
            Codec.unboundedMap(ResourceLocation.CODEC, CompendiumProgress.CODEC))
            .xmap(CompendiumData::fromMap, CompendiumData::toMap);

    private final Map<CompendiumCategory, Map<ResourceLocation, CompendiumProgress>> map = new EnumMap<>(CompendiumCategory.class);

    public Map<ResourceLocation, CompendiumProgress> of(CompendiumCategory cat) {
        return map.computeIfAbsent(cat, c -> new HashMap<>());
    }

    public CompendiumProgress get(CompendiumCategory cat, ResourceLocation id) {
        return of(cat).get(id);
    }

    public int totalLevels() {
        int sum = 0;
        for (CompendiumCategory c : CompendiumCategory.values()) {
            if (!c.leveled) continue;
            for (CompendiumProgress p : of(c).values()) sum += p.level;
        }
        return sum;
    }

    public void clear() {
        map.clear();
    }

    private static CompendiumData fromMap(Map<String, Map<ResourceLocation, CompendiumProgress>> in) {
        CompendiumData d = new CompendiumData();
        for (CompendiumCategory c : CompendiumCategory.values()) {
            Map<ResourceLocation, CompendiumProgress> m = in.get(c.key);
            if (m != null) d.of(c).putAll(m);
        }
        return d;
    }

    private static Map<String, Map<ResourceLocation, CompendiumProgress>> toMap(CompendiumData d) {
        Map<String, Map<ResourceLocation, CompendiumProgress>> out = new HashMap<>();
        for (CompendiumCategory c : CompendiumCategory.values()) out.put(c.key, new HashMap<>(d.of(c)));
        return out;
    }
}
