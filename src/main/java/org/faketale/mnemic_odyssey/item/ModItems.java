package org.faketale.mnemic_odyssey.item;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.faketale.mnemic_odyssey.MnemicOdyssey;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MnemicOdyssey.MODID);

    public static final DeferredItem<Item> EXAMPLE_ITEM = ITEMS.registerSimpleItem("example_item", new Item.Properties().food(new FoodProperties.Builder().alwaysEdible().nutrition(1).saturationModifier(2f).build()));

    //工具与装备
    public static final DeferredItem<Item> TimeLens = ITEMS.register("time_lens", TimeLens::new);//时间透镜
    public static final DeferredItem<Item> MemoryCompendium = ITEMS.register("memory_compendium", MemoryCompendium::new);//记忆图鉴

    //原材料
    public static final DeferredItem<Item> MemorySlurry = ITEMS.registerSimpleItem("memory_slurry", new Item.Properties());//记忆原浆
}
