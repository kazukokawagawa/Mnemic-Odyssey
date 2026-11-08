package org.faketale.mnemic_odyssey.event;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.faketale.mnemic_odyssey.MnemicOdyssey;
import org.faketale.mnemic_odyssey.capability.ModAttachments;
import org.faketale.mnemic_odyssey.network.packet.CraftHistoryPayload;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = MnemicOdyssey.MODID)
public class CraftHistoryEvents {
    @SubscribeEvent
    public static void onCraft(PlayerEvent.ItemCraftedEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)
                || !(event.getInventory() instanceof CraftingContainer inventory)
                || event.getCrafting().isEmpty()) return;

        player.level().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, inventory.asCraftInput(), player.level())
                .ifPresent(recipe -> {
                    ResourceLocation item = BuiltInRegistries.ITEM.getKey(event.getCrafting().getItem());
                    var history = player.getData(ModAttachments.CRAFT_HISTORY);
                    List<ResourceLocation> recipes = new ArrayList<>(history.getOrDefault(item, List.of()));
                    recipes.add(0, recipe.id());
                    if (recipes.size() > 2) recipes.subList(2, recipes.size()).clear();
                    history.put(item, recipes);
                    sync(player);
                });
    }

    public static void sync(ServerPlayer player) {
        List<CraftHistoryPayload.Entry> entries = player.getData(ModAttachments.CRAFT_HISTORY).entrySet().stream()
                .map(entry -> new CraftHistoryPayload.Entry(entry.getKey(), List.copyOf(entry.getValue())))
                .toList();
        PacketDistributor.sendToPlayer(player, new CraftHistoryPayload(entries));
    }
}
