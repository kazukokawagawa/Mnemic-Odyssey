package org.faketale.mnemic_odyssey.data;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

//enum MemoryCodexCategory {
//
//}

public record MemoryCodexEntry(
        //ResourceLocation icon,        // 图标项的 ID
        //Component title,              // 标题
        Component descriptionLv1,        // 描述
        Component descriptionLv5,        // 描述
        Component descriptionLv10,        // 描述
//        ResourceLocation entityId,    // 渲染用的生物 ID (可选)
        String category               // 分类标签：如 "monsters", "blocks"
) {}
