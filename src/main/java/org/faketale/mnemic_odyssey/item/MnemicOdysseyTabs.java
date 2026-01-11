package org.faketale.mnemic_odyssey.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.faketale.mnemic_odyssey.MnemicOdyssey;
import org.faketale.mnemic_odyssey.block.MnemicOdysseyBlocks;

public class MnemicOdysseyTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MnemicOdyssey.MODID);

    // Add the example block item to the building blocks tab
    public static void addCreative(BuildCreativeModeTabContentsEvent event) {
        addIntoBuildingBlocks(event);//在建筑方块栏中添加物品
        addIntoColoredBlocks(event);
        addIntoFunctionalBlocks(event);
        addIntoNaturalBlocks(event);
        addIntoCombat(event);//在战斗栏中添加物品
        addIntoHotbar(event);
        addIntoFoodAndDrinks(event);
        addIntoIngredients(event);
        addIntoInventory(event);
        addIntoRedstoneBlocks(event);
        addIntoSpawnEggs(event);
        addIntoToolAndUtilities(event);
        addIntoOpBlocks(event);
        addIntoSearch(event);
    }

    //在这里把物品放进创造物品栏
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MnemicOdysseyTab = CREATIVE_MODE_TABS.register("mnemic_odyssey", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.mnemic_odyssey")).withTabsBefore(CreativeModeTabs.COMBAT).icon(() -> MnemicOdysseyItems.EXAMPLE_ITEM.get().getDefaultInstance()).displayItems((parameters, output) -> {
        output.accept(MnemicOdysseyItems.EXAMPLE_ITEM.get());
    }).build());

    private static void addIntoBuildingBlocks(BuildCreativeModeTabContentsEvent event){
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(MnemicOdysseyBlocks.EXAMPLE_BLOCK_ITEM);
        }
    }
    private static void addIntoColoredBlocks(BuildCreativeModeTabContentsEvent event){
        if (event.getTabKey() == CreativeModeTabs.COLORED_BLOCKS) {
        }
    }
    private static void addIntoFunctionalBlocks(BuildCreativeModeTabContentsEvent event){
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
        }
    }
    private static void addIntoNaturalBlocks(BuildCreativeModeTabContentsEvent event){
        if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
        }
    }
    private static void addIntoCombat(BuildCreativeModeTabContentsEvent event){
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
        }
    }
    private static void addIntoHotbar(BuildCreativeModeTabContentsEvent event){
        if (event.getTabKey() == CreativeModeTabs.HOTBAR) {
        }
    }
    private static void addIntoFoodAndDrinks(BuildCreativeModeTabContentsEvent event){
        if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
        }
    }
    private static void addIntoIngredients(BuildCreativeModeTabContentsEvent event){
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
        }
    }
    private static void addIntoInventory(BuildCreativeModeTabContentsEvent event){
        if (event.getTabKey() == CreativeModeTabs.INVENTORY) {
        }
    }
    private static void addIntoRedstoneBlocks(BuildCreativeModeTabContentsEvent event){
        if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
        }
    }
    private static void addIntoSpawnEggs(BuildCreativeModeTabContentsEvent event){
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
        }
    }
    private static void addIntoToolAndUtilities(BuildCreativeModeTabContentsEvent event){
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
        }
    }
    private static void addIntoOpBlocks(BuildCreativeModeTabContentsEvent event){
        if (event.getTabKey() == CreativeModeTabs.OP_BLOCKS) {
        }
    }
    private static void addIntoSearch(BuildCreativeModeTabContentsEvent event){
        if (event.getTabKey() == CreativeModeTabs.SEARCH) {
        }
    }
}
