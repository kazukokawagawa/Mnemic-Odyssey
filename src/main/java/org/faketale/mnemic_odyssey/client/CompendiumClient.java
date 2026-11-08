package org.faketale.mnemic_odyssey.client;

import net.minecraft.client.Minecraft;
import org.faketale.mnemic_odyssey.client.gui.screen.MemoryCompendiumScreen;

public class CompendiumClient {
    public static void open() {
        Minecraft.getInstance().setScreen(new MemoryCompendiumScreen());
    }
}
