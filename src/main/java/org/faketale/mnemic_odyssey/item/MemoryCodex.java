package org.faketale.mnemic_odyssey.item;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.faketale.mnemic_odyssey.client.gui.screen.MemoryCodexScreen;
import org.jetbrains.annotations.NotNull;

public class MemoryCodex extends Item {
    public MemoryCodex() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    @NotNull
    public InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand){
        ItemStack itemStack = player.getItemInHand(hand);

        if (level.isClientSide){
            //打开GUI
            Minecraft.getInstance().setScreen(new MemoryCodexScreen());
        }

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide);
    }
}
