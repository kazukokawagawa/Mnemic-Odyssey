package org.faketale.mnemic_odyssey.item;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class MemoryCompendium extends Item {
    public MemoryCompendium() {
        super(new Item.Properties().stacksTo(1).fireResistant());
    }

    @Override
    @NotNull
    public InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand){
        ItemStack itemStack = player.getItemInHand(hand);

        if (level.isClientSide){
            //打开GUI
            org.faketale.mnemic_odyssey.client.CompendiumClient.open();
        }

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide);
    }
}
