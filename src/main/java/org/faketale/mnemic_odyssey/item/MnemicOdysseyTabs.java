package org.faketale.mnemic_odyssey.item;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.faketale.mnemic_odyssey.MnemicOdyssey;
import org.faketale.mnemic_odyssey.block.ModBlocks;

public class MnemicOdysseyTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MnemicOdyssey.MODID);

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

    //在这里把物品放进创造物品栏 MnemicOdysseyTab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MnemicOdysseyTab = CREATIVE_MODE_TABS.register("mnemic_odyssey", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.mnemic_odyssey")).withTabsBefore(CreativeModeTabs.COMBAT).icon(() -> ModItems.EXAMPLE_ITEM.get().getDefaultInstance()).displayItems((parameters, output) -> {
        output.accept(ModItems.EXAMPLE_ITEM.get());
    }).build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MnemicOdysseyBlock = CREATIVE_MODE_TABS.register("mnemic_odyssey_blocks", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.mnemic_odyssey.blocks")).icon(() -> ModItems.EXAMPLE_ITEM.get().getDefaultInstance()).displayItems((parameters, output) -> {

    }).build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MnemicOdysseyFunctionalBlock = CREATIVE_MODE_TABS.register("mnemic_odyssey_functional_blocks", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.mnemic_odyssey.functional_blocks")).icon(() -> ModBlocks.EXAMPLE_BLOCK_ITEM.get().getDefaultInstance()).displayItems((parameters, output) -> {
        output.accept(ModBlocks.EXAMPLE_BLOCK_ITEM);
    }).build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MnemicOdysseyIngredient = CREATIVE_MODE_TABS.register("mnemic_odyssey_ingredients", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.mnemic_odyssey.ingredients")).icon(() -> ModItems.MemorySlurry.get().getDefaultInstance()).displayItems((parameters, output) -> {
        output.accept(ModItems.MemorySlurry.get());
    }).build());

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MnemicOdysseyTool = CREATIVE_MODE_TABS.register("mnemic_odyssey_tools", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.mnemic_odyssey.tools")).icon(() -> ModItems.TimeLens.get().getDefaultInstance()).displayItems((parameters, output) -> {
        output.accept(ModItems.MemoryCompendium.get());
        output.accept(ModItems.TimeLens.get());
    }).build());

    //建筑方块
    private static void addIntoBuildingBlocks(BuildCreativeModeTabContentsEvent event){
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {

        }
    }
    //染色方块
    private static void addIntoColoredBlocks(BuildCreativeModeTabContentsEvent event){
        if (event.getTabKey() == CreativeModeTabs.COLORED_BLOCKS) {
        }
    }
    //功能方块
    private static void addIntoFunctionalBlocks(BuildCreativeModeTabContentsEvent event){
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
        }
    }
    //自然方块
    private static void addIntoNaturalBlocks(BuildCreativeModeTabContentsEvent event){
        if (event.getTabKey() == CreativeModeTabs.NATURAL_BLOCKS) {
        }
    }
    //战斗
    private static void addIntoCombat(BuildCreativeModeTabContentsEvent event){
        if (event.getTabKey() == CreativeModeTabs.COMBAT) {
        }
    }
    //食物与饮品
    private static void addIntoFoodAndDrinks(BuildCreativeModeTabContentsEvent event){
        if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {
        }
    }
    //原材料
    private static void addIntoIngredients(BuildCreativeModeTabContentsEvent event){
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
        }
    }
    private static void addIntoInventory(BuildCreativeModeTabContentsEvent event){
        if (event.getTabKey() == CreativeModeTabs.INVENTORY) {
        }
    }
    //红石
    private static void addIntoRedstoneBlocks(BuildCreativeModeTabContentsEvent event){
        if (event.getTabKey() == CreativeModeTabs.REDSTONE_BLOCKS) {
        }
    }
    //生物蛋
    private static void addIntoSpawnEggs(BuildCreativeModeTabContentsEvent event){
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
        }
    }
    //工具与装备
    private static void addIntoToolAndUtilities(BuildCreativeModeTabContentsEvent event){
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
        }
    }
    //OP栏
    private static void addIntoOpBlocks(BuildCreativeModeTabContentsEvent event){
        if (event.getTabKey() == CreativeModeTabs.OP_BLOCKS) {
        }
    }
    //快捷栏
    private static void addIntoHotbar(BuildCreativeModeTabContentsEvent event){
        if (event.getTabKey() == CreativeModeTabs.HOTBAR) {
        }
    }
    //搜索栏
    private static void addIntoSearch(BuildCreativeModeTabContentsEvent event){
        if (event.getTabKey() == CreativeModeTabs.SEARCH) {
        }
    }
}
