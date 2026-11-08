package org.faketale.mnemic_odyssey.client.gui.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.FormattedCharSequence;
import org.faketale.mnemic_odyssey.MnemicOdyssey;
import org.faketale.mnemic_odyssey.capability.ModAttachments;
import org.faketale.mnemic_odyssey.compendium.*;

import java.util.*;

/*
 * GUI示意图
 * —————————————————————————————————————————————
 * |                     | [icon][icon][icon]  |---┐ --->[Tags]
 * |                     | [icon][icon][icon]  |---┘
 * |                     | [icon][icon][icon]  |---┐
 * |                     |                     |---┘
 * |        [Info]       |        [List]       |
 * |                     |                     |
 * |                     |                     |
 * |                     |                     |
 * |                     |                     |
 * |                     |                     |
 * |                     |                     |
 * —————————————————————————————————————————————
 * */
/*
 * 左：详情区；右：已解锁条目列表（3 列，可滚动）；最右侧：四个门类标签
 */
public class MemoryCompendiumScreen extends Screen {
    private static final int TAB_W = 46, TAB_H = 26, CELL_H = 24, COLS = 3;

    public MemoryCompendiumScreen() {
        super(Component.translatable("item.mnemic_odyssey.memory_compendium"));
    }

    protected int globalWidth, globalHeight, leftPos, topPos;
    protected int infoX, infoW, listX, listW, cellW, visibleRows;

    private CompendiumCategory currentTab = CompendiumCategory.BLOCK;
    private ResourceLocation selected = null;
    private int scroll = 0;
    private List<ResourceLocation> entries = new ArrayList<>();
    private int cachedSize = -1;
    private final Map<ResourceLocation, Entity> entityCache = new HashMap<>();
    private List<RecipeHolder<?>> recipeCache = List.of();
    private ItemStack hoverStack = ItemStack.EMPTY;

    private CompendiumData data() {
        return Minecraft.getInstance().player.getData(ModAttachments.COMPENDIUM);
    }

    @Override
    protected void init() {
        super.init();
        globalWidth = Math.min((int) (this.width * 0.85f), this.width - 2 * (TAB_W + 4));
        globalHeight = (int) (this.height * 0.85f);
        leftPos = (this.width - globalWidth) / 2;
        topPos = (this.height - globalHeight) / 2;
        infoX = leftPos + 8;
        infoW = globalWidth / 2 - 16;
        listX = leftPos + globalWidth / 2 + 5;
        listW = globalWidth / 2 - 10;
        cellW = listW / COLS;
        visibleRows = Math.max(1, (globalHeight - 10) / CELL_H);
        refreshEntries(true);
    }

    @Override
    public void tick() {
        refreshEntries(false);
    }

    private void refreshEntries(boolean force) {
        Map<ResourceLocation, CompendiumProgress> m = data().of(currentTab);
        if (!force && m.size() == cachedSize) return;
        cachedSize = m.size();
        entries = new ArrayList<>(m.keySet());
        entries.sort(Comparator.comparing(id -> CompendiumLogic.displayName(currentTab, id).getString()));
        int maxScroll = Math.max(0, (entries.size() + COLS - 1) / COLS - visibleRows);
        scroll = Math.min(scroll, maxScroll);
        if (selected != null && !m.containsKey(selected)) selected = null;
    }

    // ------------------------------------------------------------------ 渲染

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        hoverStack = ItemStack.EMPTY;
        g.fill(0, 0, this.width, this.height, 0x88000000);
        g.fill(leftPos, topPos, leftPos + globalWidth, topPos + globalHeight, 0xFF2D2319);
        g.fill(leftPos + 5, topPos + 5, leftPos + globalWidth / 2 - 3, topPos + globalHeight - 5, 0x55000000);
        g.fill(listX - 2, topPos + 5, leftPos + globalWidth - 5, topPos + globalHeight - 5, 0x33000000);

        renderTabs(g, mouseX, mouseY);
        renderList(g, mouseX, mouseY);
        if (selected == null) {
            Component hint = Component.translatable("gui.mnemic_odyssey.compendium.select_hint");
            g.drawCenteredString(font, hint, infoX + infoW / 2, topPos + globalHeight / 2, 0xAAAAAA);
        } else {
            renderInfo(g, mouseX, mouseY);
        }
        if (!hoverStack.isEmpty()) g.renderTooltip(font, hoverStack, mouseX, mouseY);
    }

    private void renderTabs(GuiGraphics g, int mx, int my) {
        CompendiumCategory[] tabs = CompendiumCategory.values();
        for (int i = 0; i < tabs.length; i++) {
            boolean active = tabs[i] == currentTab;
            int x = leftPos + globalWidth - (active ? 0 : 3);
            int y = topPos + 10 + i * (TAB_H + 4);
            g.fill(x, y, x + TAB_W, y + TAB_H, active ? 0xFF7A5F43 : 0xFF4D3E30);
            Component name = Component.translatable("gui.mnemic_odyssey.compendium.tab." + tabs[i].key);
            g.drawCenteredString(font, name, x + TAB_W / 2, y + (TAB_H - 8) / 2, active ? 0xFFFFFF : 0xC8B89A);
        }
    }

    private void renderList(GuiGraphics g, int mx, int my) {
        if (entries.isEmpty()) {
            g.drawCenteredString(font, Component.translatable("gui.mnemic_odyssey.compendium.empty"),
                    listX + listW / 2, topPos + globalHeight / 2, 0x888888);
            return;
        }
        for (int row = 0; row < visibleRows; row++) {
            for (int col = 0; col < COLS; col++) {
                int idx = (scroll + row) * COLS + col;
                if (idx >= entries.size()) return;
                ResourceLocation id = entries.get(idx);
                int x = listX + col * cellW, y = topPos + 5 + row * CELL_H;
                boolean hover = mx >= x && mx < x + cellW - 2 && my >= y && my < y + CELL_H - 2;
                int bg = id.equals(selected) ? 0xAA9C7A3C : hover ? 0x66FFFFFF : 0x44000000;
                g.fill(x, y, x + cellW - 2, y + CELL_H - 2, bg);
                g.renderItem(CompendiumLogic.iconFor(currentTab, id), x + 2, y + 2);
                String name = font.plainSubstrByWidth(CompendiumLogic.displayName(currentTab, id).getString(), cellW - 26);
                g.drawString(font, name, x + 22, currentTab.leveled ? y + 3 : y + 7, 0xFFFFFF, false);
                if (currentTab.leveled) {
                    CompendiumProgress p = data().get(currentTab, id);
                    g.drawString(font, "Lv." + p.level, x + 22, y + 12, p.level >= CompendiumLogic.MAX_LEVEL ? 0xFFD700 : 0xE0C070, false);
                }
            }
        }
    }

    // ---- 详情 ----

    private int cy;

    private void line(GuiGraphics g, Component c, int color) {
        for (FormattedCharSequence s : font.split(c, infoW - 4)) {
            if (cy + 9 > topPos + globalHeight - 8) return;
            g.drawString(font, s, infoX + 2, cy, color, false);
            cy += 10;
        }
    }

    private void renderInfo(GuiGraphics g, int mx, int my) {
        CompendiumCategory cat = currentTab;
        ResourceLocation id = selected;
        CompendiumProgress pr = data().get(cat, id);
        if (pr == null) return;
        int top = topPos + 10;

        // 标题
        ItemStack icon = CompendiumLogic.iconFor(cat, id);
        g.pose().pushPose();
        g.pose().translate(infoX + 4, top, 0);
        g.pose().scale(2f, 2f, 1f);
        g.renderItem(icon, 0, 0);
        g.pose().popPose();
        g.drawString(font, CompendiumLogic.displayName(cat, id), infoX + 42, top + 4, 0xFFE9B0, false);
        g.drawString(font, id.toString(), infoX + 42, top + 18, 0x77705F, false);
        cy = top + 38;

        if (cat == CompendiumCategory.MOB || cat == CompendiumCategory.BOSS) {
            renderEntity(g, id, infoX, cy, infoX + infoW, cy + 80, mx, my);
            cy += 84;
        }

        if (cat.leveled) {
            renderLevel(g, cat, id, pr);
        }

        switch (cat) {
            case BLOCK -> blockInfo(g, id);
            case MOB, BOSS -> mobInfo(g, id, cat);
            case ITEM -> itemInfo(g, id, mx, my);
        }
    }

    private void renderLevel(GuiGraphics g, CompendiumCategory cat, ResourceLocation id, CompendiumProgress pr) {
        boolean max = pr.level >= CompendiumLogic.MAX_LEVEL;
        line(g, Component.translatable("gui.mnemic_odyssey.compendium.level", pr.level, CompendiumLogic.MAX_LEVEL), 0xFFD700);
        int bx = infoX + 2, bw = infoW - 4;
        g.fill(bx, cy, bx + bw, cy + 6, 0xFF111111);
        int need = CompendiumLogic.xpNeeded(pr.level);
        int fill = max ? bw : (int) (bw * (pr.xp / (float) need));
        g.fill(bx, cy, bx + fill, cy + 6, max ? 0xFFFFC400 : 0xFF6FBF3F);
        cy += 9;
        line(g, max ? Component.translatable("gui.mnemic_odyssey.compendium.xp_max")
                : Component.translatable("gui.mnemic_odyssey.compendium.xp", pr.xp, need), 0xAAAAAA);
        cy += 3;

        boolean block = cat == CompendiumCategory.BLOCK;
        String k = block ? "block" : "mob";
        int pct1 = Math.round((block ? CompendiumLogic.MINING_SPEED_BONUS : CompendiumLogic.DAMAGE_BONUS) * 100);
        int pct2 = Math.round((block ? CompendiumLogic.DOUBLE_DROP_CHANCE : CompendiumLogic.ARMOR_PIERCE_CHANCE) * 100);
        bonus(g, pr.level >= 5, Component.translatable("gui.mnemic_odyssey.compendium.bonus5." + k, pct1), 5);
        bonus(g, pr.level >= 10, Component.translatable("gui.mnemic_odyssey.compendium.bonus10." + k, pct2), 10);
        Component b15 = block
                ? Component.translatable("gui.mnemic_odyssey.compendium.bonus15.block", Component.translatable(BlockPerk.of(BuiltInRegistries.BLOCK.get(id)).langKey()))
                : Component.translatable("gui.mnemic_odyssey.compendium.bonus15.mob");
        bonus(g, max, b15, 15);
        if (max) line(g, Component.translatable("gui.mnemic_odyssey.compendium.max_note"), 0x9FD0FF);
        cy += 4;
    }

    private void bonus(GuiGraphics g, boolean got, Component text, int lv) {
        line(g, Component.literal((got ? "\u2714 " : "\u2716 ") + "Lv." + lv + " ").append(text), got ? 0x7FE07F : 0x777777);
    }

    private void blockInfo(GuiGraphics g, ResourceLocation id) {
        Block b = BuiltInRegistries.BLOCK.get(id);
        BlockState s = b.defaultBlockState();
        float hard = s.getDestroySpeed(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
        line(g, Component.translatable("gui.mnemic_odyssey.compendium.hardness",
                hard < 0 ? I18n.get("gui.mnemic_odyssey.compendium.unbreakable") : String.format("%.1f", hard)), 0xFFFFFF);
        line(g, Component.translatable("gui.mnemic_odyssey.compendium.light", s.getLightEmission()), 0xFFFFFF);
        line(g, Component.translatable("gui.mnemic_odyssey.compendium.blast", String.format("%.1f", b.getExplosionResistance())), 0xFFFFFF);

        List<String> props = new ArrayList<>();
        if (s.is(BlockTags.MINEABLE_WITH_PICKAXE)) props.add(I18n.get("gui.mnemic_odyssey.compendium.tool.pickaxe"));
        if (s.is(BlockTags.MINEABLE_WITH_AXE)) props.add(I18n.get("gui.mnemic_odyssey.compendium.tool.axe"));
        if (s.is(BlockTags.MINEABLE_WITH_SHOVEL)) props.add(I18n.get("gui.mnemic_odyssey.compendium.tool.shovel"));
        if (s.is(BlockTags.MINEABLE_WITH_HOE)) props.add(I18n.get("gui.mnemic_odyssey.compendium.tool.hoe"));
        if (!props.isEmpty()) line(g, Component.translatable("gui.mnemic_odyssey.compendium.tool", String.join("/", props)), 0xFFFFFF);
        if (s.requiresCorrectToolForDrops()) line(g, Component.translatable("gui.mnemic_odyssey.compendium.needs_tool"), 0xFFB070);
        if (b instanceof FallingBlock) line(g, Component.translatable("gui.mnemic_odyssey.compendium.gravity"), 0xFFFFFF);
        if (b instanceof LiquidBlock) line(g, Component.translatable("gui.mnemic_odyssey.compendium.liquid"), 0xFFFFFF);
        if (b instanceof EntityBlock) line(g, Component.translatable("gui.mnemic_odyssey.compendium.block_entity"), 0xFFFFFF);
        special(g, "compendium.mnemic_odyssey.special.block." + id.getPath());
    }

    private void mobInfo(GuiGraphics g, ResourceLocation id, CompendiumCategory cat) {
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(id);
        @SuppressWarnings("unchecked")
        EntityType<? extends LivingEntity> lt = (EntityType<? extends LivingEntity>) type;
        if (DefaultAttributes.hasSupplier(lt)) {
            var sup = DefaultAttributes.getSupplier(lt);
            if (sup.hasAttribute(Attributes.MAX_HEALTH)) {
                double hp = sup.getBaseValue(Attributes.MAX_HEALTH);
                line(g, Component.translatable("gui.mnemic_odyssey.compendium.health", String.format("%.0f", hp), String.format("%.1f", hp / 2)), 0xFF7070);
            }
            if (sup.hasAttribute(Attributes.ATTACK_DAMAGE)) {
                line(g, Component.translatable("gui.mnemic_odyssey.compendium.attack", String.format("%.1f", sup.getBaseValue(Attributes.ATTACK_DAMAGE))), 0xFFFFFF);
            }
        }
        line(g, Component.translatable("gui.mnemic_odyssey.compendium.mobtype." + type.getCategory().getName().toLowerCase(Locale.ROOT)), 0xFFFFFF);
        line(g, Component.translatable("gui.mnemic_odyssey.compendium.size", String.format("%.1f", type.getWidth()), String.format("%.1f", type.getHeight())), 0xFFFFFF);
        if (type.is(EntityTypeTags.UNDEAD)) line(g, Component.translatable("gui.mnemic_odyssey.compendium.undead"), 0xFFFFFF);
        if (type.is(EntityTypeTags.ARTHROPOD)) line(g, Component.translatable("gui.mnemic_odyssey.compendium.arthropod"), 0xFFFFFF);
        if (type.is(EntityTypeTags.AQUATIC)) line(g, Component.translatable("gui.mnemic_odyssey.compendium.aquatic"), 0xFFFFFF);
        if (type.fireImmune()) line(g, Component.translatable("gui.mnemic_odyssey.compendium.fire_immune"), 0xFFFFFF);
        special(g, "compendium.mnemic_odyssey.special.mob." + id.getPath());
    }

    private void special(GuiGraphics g, String key) {
        if (I18n.exists(key)) {
            cy += 2;
            line(g, Component.translatable("gui.mnemic_odyssey.compendium.special").withStyle(ChatFormatting.AQUA), 0x66CCFF);
            line(g, Component.translatable(key), 0xDDDDDD);
        }
    }

    private void itemInfo(GuiGraphics g, ResourceLocation id, int mx, int my) {
        line(g, Component.translatable("gui.mnemic_odyssey.compendium.faction", CompendiumLogic.faction(id)), 0xFFFFFF);
        cy += 4;
        line(g, Component.translatable("gui.mnemic_odyssey.compendium.recipes"), 0xFFD700);
        if (recipeCache.isEmpty()) {
            line(g, Component.translatable("gui.mnemic_odyssey.compendium.no_recipe"), 0x888888);
            return;
        }
        for (RecipeHolder<?> h : recipeCache) {
            Recipe<?> r = h.value();
            List<Ingredient> ings = r.getIngredients();
            int w = r instanceof ShapedRecipe sr ? sr.getWidth() : Math.min(3, Math.max(1, ings.size()));
            int rows = (int) Math.ceil(ings.size() / (double) w);
            if (r instanceof ShapedRecipe sr) rows = sr.getHeight();
            int gridH = Math.max(rows, 1) * 18;
            if (cy + gridH > topPos + globalHeight - 8) return;
            for (int i = 0; i < ings.size(); i++) {
                int x = infoX + 4 + (i % w) * 18, y = cy + (i / w) * 18;
                g.fill(x, y, x + 17, y + 17, 0x66000000);
                ItemStack[] opts = ings.get(i).getItems();
                if (opts.length == 0) continue;
                ItemStack st = opts[(int) ((System.currentTimeMillis() / 1000) % opts.length)];
                g.renderItem(st, x + 1, y + 1);
                if (mx >= x && mx < x + 17 && my >= y && my < y + 17) hoverStack = st;
            }
            int ax = infoX + 4 + w * 18 + 8;
            g.drawString(font, "\u2192", ax, cy + gridH / 2 - 4, 0xFFFFFF, false);
            ItemStack res = r.getResultItem(Minecraft.getInstance().level.registryAccess());
            int rx = ax + 16, ry = cy + gridH / 2 - 9;
            g.fill(rx, ry, rx + 17, ry + 17, 0x66000000);
            g.renderItem(res, rx + 1, ry + 1);
            g.renderItemDecorations(font, res, rx + 1, ry + 1);
            if (mx >= rx && mx < rx + 17 && my >= ry && my < ry + 17) hoverStack = res;
            cy += gridH + 8;
        }
    }

    private void renderEntity(GuiGraphics g, ResourceLocation id, int x1, int y1, int x2, int y2, int mx, int my) {
        g.fill(x1, y1, x2, y2, 0x44000000);
        try {
            Entity e = entityCache.computeIfAbsent(id, i -> BuiltInRegistries.ENTITY_TYPE.get(i).create(Minecraft.getInstance().level));
            if (e instanceof LivingEntity le) {
                float size = Math.max(Math.max(le.getBbHeight(), le.getBbWidth()), 0.3f);
                int scale = (int) Math.max(4, Math.min(60, (y2 - y1) * 0.8f / size));
                InventoryScreen.renderEntityInInventoryFollowsMouse(g, x1, y1, x2, y2, scale, 0.0625f, mx, my, le);
            }
        } catch (Exception ignored) {
        }
    }

    // ------------------------------------------------------------------ 交互

    private void select(ResourceLocation id) {
        selected = id;
        recipeCache = List.of();
        if (currentTab == CompendiumCategory.ITEM && Minecraft.getInstance().level != null) {
            List<RecipeHolder<?>> found = new ArrayList<>();
            for (ResourceLocation recipeId : Minecraft.getInstance().player.getData(ModAttachments.CRAFT_HISTORY).getOrDefault(id, List.of())) {
                Minecraft.getInstance().level.getRecipeManager().byKey(recipeId).ifPresent(found::add);
            }
            recipeCache = found;
        }
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        CompendiumCategory[] tabs = CompendiumCategory.values();
        for (int i = 0; i < tabs.length; i++) {
            int x = leftPos + globalWidth - (tabs[i] == currentTab ? 0 : 3);
            int y = topPos + 10 + i * (TAB_H + 4);
            if (mx >= x && mx < x + TAB_W && my >= y && my < y + TAB_H) {
                if (currentTab != tabs[i]) {
                    currentTab = tabs[i];
                    selected = null;
                    scroll = 0;
                    refreshEntries(true);
                }
                return true;
            }
        }
        for (int row = 0; row < visibleRows; row++) {
            for (int col = 0; col < COLS; col++) {
                int idx = (scroll + row) * COLS + col;
                if (idx >= entries.size()) break;
                int x = listX + col * cellW, y = topPos + 5 + row * CELL_H;
                if (mx >= x && mx < x + cellW - 2 && my >= y && my < y + CELL_H - 2) {
                    select(entries.get(idx));
                    return true;
                }
            }
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double scrollX, double scrollY) {
        int maxScroll = Math.max(0, (entries.size() + COLS - 1) / COLS - visibleRows);
        scroll = Math.max(0, Math.min(maxScroll, scroll - (int) Math.signum(scrollY)));
        return true;
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.minecraft.options.keyInventory.matches(keyCode, scanCode) ||
                keyCode == org.faketale.mnemic_odyssey.config.ModConfig.OpenCompendiumKey.get()) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
