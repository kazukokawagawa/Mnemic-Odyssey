package org.faketale.mnemic_odyssey.client.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.faketale.mnemic_odyssey.MnemicOdyssey;
import org.faketale.mnemic_odyssey.client.key.ModKeyBindings;

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

public class MemoryCodexScreen extends Screen {
    public MemoryCodexScreen() {
        super(Component.literal("Memory Codex"));
    }

    private final ResourceLocation GUI_TEXTURE = ResourceLocation.fromNamespaceAndPath(MnemicOdyssey.MODID, "textures/gui/memory_codex.png");
    //坐标
    protected int globalWidth/* = 512*/;//整体宽高
    protected int globalHeight /*= 300*/;
    protected int leftPos, topPos;//整体坐标
    protected int infoWidth, listWidth;//左侧介绍区，右侧列表区
    protected int tabsWidth, tagsHeight;//最右侧标签宽高
    protected int iconWidth, iconHeight;//图鉴图标的宽高
    protected int globalDistant = 5;

    //状态
    protected int currentTag = -1;//当前选中的标签，-1代表未选中
    protected int currentList = -1;//当前选中的列表

    @Override
    protected void init() {
        super.init();

        //宽高占屏幕的85%
        globalWidth = (int) (this.width * 0.85f);
        globalHeight = (int) (this.height * 0.85f);

        // 计算居中坐标
        leftPos = (this.width - globalWidth) / 2;
        topPos = (this.height - globalHeight) / 2;

        //划分区域
        infoWidth = (int) (globalWidth * 0.5f);//左侧介绍区
        listWidth = (int) (globalWidth * 0.5f);//右侧列表区

        tabsWidth = (int) (globalWidth * 0.05f);//最右侧标签
        tagsHeight = tabsWidth / 5;//与宽的比例为比例为 5:1

        iconWidth = (listWidth / 3);//一行三个
        iconHeight = iconWidth / 5;//比例为 5:1
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // 背景遮罩
        //super.render(guiGraphics, mouseX, mouseY, partialTick);//这个sb方法会绘制背景模糊

        // 主背景面板 (深褐色饥荒风)
        guiGraphics.fill(leftPos, topPos, leftPos + globalWidth, topPos + globalHeight, 0xFF2D2319);
        // 绘制背景纹理
//        guiGraphics.blit(GUI_TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        // 绘制左侧介绍区 (稍微深一点的底色)
        int infoAreaRight = leftPos + infoWidth;
        guiGraphics.fill(leftPos + 5, topPos + 5, infoAreaRight - 5, topPos + globalHeight - 5, 0x55000000);

        // 绘制右侧列表区
        int listAreaRight = infoAreaRight + listWidth;
        int listX = infoAreaRight + 5;
        int listY = topPos + 5;
        guiGraphics.fill(listX, listY, listAreaRight - 5, topPos + globalHeight - 5, 0x33000000);

        //绘制列表区图标
        for(int i = 0; i < 5; i++){
            for(int j = 0; j < 3; j++){
                int iconX = infoAreaRight + 5 + 5 + i * 30;
                int iconY = topPos + 5 + (j * 30);
                guiGraphics.fill(iconX, iconY, iconX + iconWidth, iconY + iconHeight, 0x44000000);
            }
        }

        // 让标签向右突出 15 像素


        // 最右侧标签区域 (画几个示意矩形)
        for(int i = 0; i < 5; i++) {
            int tabY = topPos + 10 + (i * 30);
            guiGraphics.fill(listAreaRight, tabY, leftPos + globalWidth + tabsWidth, tabY + 25, 0xFF4D3E30);
            //guiGraphics.fill(listAreaRight, tabY, leftPos + globalWidth, tabY + 25, 0xFF4D3E30);
        }
//        // 绘制文字（测试）
//        guiGraphics.drawString(this.font, "记忆图鉴", leftPos + 20, topPos + 20, 0xFFFFFF, false);

    }

//    @Override
//    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
//        // 覆盖掉父类中带有 Shader 模糊效果的渲染逻辑
//        guiGraphics.fillGradient(0, 0, this.width, this.height, 0x77000000, 0x77000000);
//    }

    //单机模式打开图鉴时是否暂停游戏
    @Override
    public boolean isPauseScreen() {
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.minecraft.options.keyInventory.matches(keyCode, scanCode) || //按E可以关闭GUI
                ModKeyBindings.OPEN_CODEX_GUI.matches(keyCode, scanCode)//按b可以关闭GUI
        ) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }
}
