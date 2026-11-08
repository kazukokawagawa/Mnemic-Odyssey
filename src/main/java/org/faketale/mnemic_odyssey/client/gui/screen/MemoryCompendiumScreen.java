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
    private static final int TAB_H = 22, CELL_H = 27;
    private static final ResourceLocation BACKGROUND = ResourceLocation.fromNamespaceAndPath(
            MnemicOdyssey.MODID, "textures/gui/memory_codex/memory_codex.png");
    private static final int TEXTURE_WIDTH = 188, TEXTURE_HEIGHT = 148;

    public MemoryCompendiumScreen() {
        super(Component.translatable("item.mnemic_odyssey.memory_compendium"));
    }

    protected int globalWidth, globalHeight, leftPos, topPos;
    protected int infoX, infoW, listX, listW, cellW, visibleRows;
    private int columns, contentTop, contentBottom, tabsTop, tabsX, tabsWidth;
    private int detailScroll, detailMaxScroll;

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
        globalWidth = Math.max(1, Math.min(700, this.width - 16));
        globalHeight = Math.max(1, Math.min(460, this.height - 16));
        leftPos = (this.width - globalWidth) / 2;
        topPos = (this.height - globalHeight) / 2;
        int split = globalWidth / 2;
        int marginX = Math.max(10, globalWidth * 13 / TEXTURE_WIDTH);
        int marginY = Math.max(5, globalHeight * 7 / TEXTURE_HEIGHT);
        infoX = leftPos + marginX;
        infoW = Math.max(20, split - marginX - 8);
        listX = leftPos + split + 8;
        listW = Math.max(20, globalWidth - split - marginX - 8);
        columns = Math.max(1, Math.min(3, listW / 100));
        cellW = listW / columns;
        tabsX = leftPos + marginX;
        tabsWidth = globalWidth - 2 * marginX;
        tabsTop = topPos + marginY + 20;
        contentTop = tabsTop + TAB_H + 7;
        contentBottom = topPos + globalHeight - marginY;
        visibleRows = Math.max(1, (contentBottom - contentTop) / CELL_H);
        detailScroll = 0;
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
        int maxScroll = Math.max(0, (entries.size() + columns - 1) / columns - visibleRows);
        scroll = Math.min(scroll, maxScroll);
        if (selected != null && !m.containsKey(selected)) selected = null;
    }

    // ------------------------------------------------------------------ 渲染

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        hoverStack = ItemStack.EMPTY;
        g.fill(0, 0, this.width, this.height, 0xB0000000);
        g.blit(BACKGROUND, leftPos, topPos, globalWidth, globalHeight,
                0, 0, TEXTURE_WIDTH, TEXTURE_HEIGHT, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        g.fill(tabsX, tabsTop - 20, tabsX + tabsWidth, tabsTop - 2, 0xD0382D24);
        g.drawString(font, title, tabsX + 5, tabsTop - 15, 0xFFF4DDA4, false);
        String count = Integer.toString(entries.size());
        g.drawString(font, count, tabsX + tabsWidth - 5 - font.width(count), tabsTop - 15, 0xFFB8A17B, false);
        g.fill(infoX - 4, contentTop - 4, infoX + infoW + 4, contentBottom, 0xD0302821);
        g.fill(listX - 4, contentTop - 4, listX + listW + 4, contentBottom, 0xD0302821);

        renderTabs(g, mouseX, mouseY);
        renderList(g, mouseX, mouseY);
        if (selected == null) {
            Component hint = Component.translatable("gui.mnemic_odyssey.compendium.select_hint");
            int hintY = contentTop + 18;
            for (FormattedCharSequence line : font.split(hint, Math.max(1, infoW - 8))) {
                g.drawCenteredString(font, line, infoX + infoW / 2, hintY, 0xFFB9A98D);
                hintY += 10;
            }
            detailMaxScroll = 0;
        } else {
            g.enableScissor(infoX, contentTop, infoX + infoW, contentBottom);
            renderInfo(g, mouseX, mouseY);
            g.disableScissor();
            renderScrollBar(g, infoX + infoW - 3, contentTop, contentBottom, detailScroll, detailMaxScroll);
        }
        if (!hoverStack.isEmpty()) g.renderTooltip(font, hoverStack, mouseX, mouseY);
    }

    private void renderTabs(GuiGraphics g, int mx, int my) {
        CompendiumCategory[] tabs = CompendiumCategory.values();
        int tabWidth = tabsWidth / tabs.length;
        for (int i = 0; i < tabs.length; i++) {
            boolean active = tabs[i] == currentTab;
            int x = tabsX + i * tabWidth;
            boolean hover = mx >= x && mx < x + tabWidth - 3 && my >= tabsTop && my < tabsTop + TAB_H;
            g.fill(x, tabsTop, x + tabWidth - 3, tabsTop + TAB_H, active ? 0xFF8E7047 : hover ? 0xFF65513B : 0xFF493A2D);
            if (active) g.fill(x, tabsTop + TAB_H - 2, x + tabWidth - 3, tabsTop + TAB_H, 0xFFE8C27A);
            Component name = Component.translatable("gui.mnemic_odyssey.compendium.tab." + tabs[i].key);
            String label = font.plainSubstrByWidth(name.getString(), Math.max(1, tabWidth - 8));
            g.drawCenteredString(font, label, x + (tabWidth - 3) / 2, tabsTop + 7, active ? 0xFFFFFFFF : 0xFFD0C1A4);
        }
    }

    private void renderList(GuiGraphics g, int mx, int my) {
        if (entries.isEmpty()) {
            g.drawCenteredString(font, Component.translatable("gui.mnemic_odyssey.compendium.empty"),
                    listX + listW / 2, contentTop + 18, 0xFFB9A98D);
            return;
        }
        g.enableScissor(listX, contentTop, listX + listW, contentBottom);
        for (int row = 0; row < visibleRows; row++) {
            for (int col = 0; col < columns; col++) {
                int idx = (scroll + row) * columns + col;
                if (idx >= entries.size()) break;
                ResourceLocation id = entries.get(idx);
                int x = listX + col * cellW, y = contentTop + row * CELL_H;
                boolean hover = mx >= x && mx < x + cellW - 3 && my >= y && my < y + CELL_H - 3;
                int bg = id.equals(selected) ? 0xFF795F3C : hover ? 0xFF5A4938 : 0xFF3B3128;
                g.fill(x, y, x + cellW - 3, y + CELL_H - 3, bg);
                if (id.equals(selected)) g.fill(x, y, x + 2, y + CELL_H - 3, 0xFFE5BE75);
                g.renderItem(CompendiumLogic.iconFor(currentTab, id), x + 5, y + 4);
                String name = font.plainSubstrByWidth(CompendiumLogic.displayName(currentTab, id).getString(), Math.max(1, cellW - 30));
                g.drawString(font, name, x + 26, currentTab.leveled ? y + 3 : y + 8, 0xFFF8EBD1, false);
                if (currentTab.leveled) {
                    CompendiumProgress p = data().get(currentTab, id);
                    g.drawString(font, "Lv." + p.level, x + 26, y + 13, p.level >= CompendiumLogic.MAX_LEVEL ? 0xFFFFD875 : 0xFFC5AE88, false);
                }
                if (hover) hoverStack = CompendiumLogic.iconFor(currentTab, id);
            }
        }
        g.disableScissor();
        int maxScroll = Math.max(0, (entries.size() + columns - 1) / columns - visibleRows);
        renderScrollBar(g, listX + listW - 2, contentTop, contentBottom, scroll, maxScroll);
    }

    private void renderScrollBar(GuiGraphics g, int x, int top, int bottom, int value, int max) {
        if (max <= 0 || bottom <= top) return;
        int track = bottom - top;
        int thumb = Math.min(track, Math.max(12, track * track / (track + max * 10)));
        int y = top + (track - thumb) * value / max;
        g.fill(x, top, x + 2, bottom, 0xFF473B30);
        g.fill(x, y, x + 2, y + thumb, 0xFFC5A66F);
    }

    // ---- 详情 ----

    private int cy;

    private void line(GuiGraphics g, Component c, int color) {
        for (FormattedCharSequence s : font.split(c, Math.max(1, infoW - 8))) {
            g.drawString(font, s, infoX + 2, cy, color, false);
            cy += 10;
        }
    }

    private void renderInfo(GuiGraphics g, int mx, int my) {
        CompendiumCategory cat = currentTab;
        ResourceLocation id = selected;
        CompendiumProgress pr = data().get(cat, id);
        if (pr == null) return;
        int top = contentTop + 3 - detailScroll;

        // 标题
        ItemStack icon = CompendiumLogic.iconFor(cat, id);
        g.pose().pushPose();
        g.pose().translate(infoX + 4, top, 0);
        g.pose().scale(2f, 2f, 1f);
        g.renderItem(icon, 0, 0);
        g.pose().popPose();
        String name = font.plainSubstrByWidth(CompendiumLogic.displayName(cat, id).getString(), Math.max(1, infoW - 46));
        String registryName = font.plainSubstrByWidth(id.toString(), Math.max(1, infoW - 46));
        g.drawString(font, name, infoX + 42, top + 4, 0xFFE9B0, false);
        g.drawString(font, registryName, infoX + 42, top + 18, 0xFFAA987D, false);
        cy = top + 38;

        if (cat == CompendiumCategory.MOB || cat == CompendiumCategory.BOSS) {
            renderEntity(g, id, infoX, cy, infoX + infoW - 6, cy + 60, mx, my);
            cy += 66;
        }

        if (cat.leveled) {
            renderLevel(g, cat, id, pr);
        }

        switch (cat) {
            case BLOCK -> blockInfo(g, id);
            case MOB, BOSS -> mobInfo(g, id, cat);
            case ITEM -> itemInfo(g, id, mx, my);
        }
        detailMaxScroll = Math.max(0, cy + detailScroll - contentBottom + 5);
        detailScroll = Math.min(detailScroll, detailMaxScroll);
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
            for (int i = 0; i < ings.size(); i++) {
                int x = infoX + 4 + (i % w) * 18, y = cy + (i / w) * 18;
                g.fill(x, y, x + 17, y + 17, 0x66000000);
                ItemStack[] opts = ings.get(i).getItems();
                if (opts.length == 0) continue;
                ItemStack st = opts[(int) ((System.currentTimeMillis() / 1000) % opts.length)];
                g.renderItem(st, x + 1, y + 1);
                if (my >= contentTop && my < contentBottom && mx >= x && mx < x + 17 && my >= y && my < y + 17) hoverStack = st;
            }
            int ax = infoX + 4 + w * 18 + 8;
            g.drawString(font, "\u2192", ax, cy + gridH / 2 - 4, 0xFFFFFF, false);
            ItemStack res = r.getResultItem(Minecraft.getInstance().level.registryAccess());
            int rx = ax + 16, ry = cy + gridH / 2 - 9;
            g.fill(rx, ry, rx + 17, ry + 17, 0x66000000);
            g.renderItem(res, rx + 1, ry + 1);
            g.renderItemDecorations(font, res, rx + 1, ry + 1);
            if (my >= contentTop && my < contentBottom && mx >= rx && mx < rx + 17 && my >= ry && my < ry + 17) hoverStack = res;
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
        detailScroll = 0;
        detailMaxScroll = 0;
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
        if (button != 0) return super.mouseClicked(mx, my, button);
        CompendiumCategory[] tabs = CompendiumCategory.values();
        int tabWidth = tabsWidth / tabs.length;
        for (int i = 0; i < tabs.length; i++) {
            int x = tabsX + i * tabWidth;
            if (mx >= x && mx < x + tabWidth - 3 && my >= tabsTop && my < tabsTop + TAB_H) {
                if (currentTab != tabs[i]) {
                    currentTab = tabs[i];
                    selected = null;
                    detailScroll = 0;
                    detailMaxScroll = 0;
                    scroll = 0;
                    refreshEntries(true);
                }
                return true;
            }
        }
        if (mx < listX || mx >= listX + listW || my < contentTop || my >= contentBottom) {
            return super.mouseClicked(mx, my, button);
        }
        for (int row = 0; row < visibleRows; row++) {
            for (int col = 0; col < columns; col++) {
                int idx = (scroll + row) * columns + col;
                if (idx >= entries.size()) break;
                int x = listX + col * cellW, y = contentTop + row * CELL_H;
                if (mx >= x && mx < x + cellW - 3 && my >= y && my < y + CELL_H - 3) {
                    select(entries.get(idx));
                    return true;
                }
            }
        }
        return super.mouseClicked(mx, my, button);
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double scrollX, double scrollY) {
        if (my >= contentTop && my < contentBottom && mx >= infoX && mx < infoX + infoW) {
            detailScroll = Math.max(0, Math.min(detailMaxScroll, detailScroll - (int) Math.signum(scrollY) * 24));
            return true;
        }
        if (my >= contentTop && my < contentBottom && mx >= listX && mx < listX + listW) {
            int maxScroll = Math.max(0, (entries.size() + columns - 1) / columns - visibleRows);
            scroll = Math.max(0, Math.min(maxScroll, scroll - (int) Math.signum(scrollY)));
            return true;
        }
        return super.mouseScrolled(mx, my, scrollX, scrollY);
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
