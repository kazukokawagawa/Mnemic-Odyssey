package org.faketale.mnemic_odyssey.item;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.faketale.mnemic_odyssey.MnemicOdyssey;

public class MnemicOdysseyItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MnemicOdyssey.MODID);

    // Creates a new food item with the id "mnemic_odyssey:example_id", nutrition 1 and saturation 2
    public static final DeferredItem<Item> EXAMPLE_ITEM = ITEMS.registerSimpleItem("example_item", new Item.Properties().food(new FoodProperties.Builder().alwaysEdible().nutrition(1).saturationModifier(2f).build()));
}
