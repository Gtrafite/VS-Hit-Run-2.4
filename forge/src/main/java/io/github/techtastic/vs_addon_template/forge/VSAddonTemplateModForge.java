package io.github.techtastic.vs_addon_template.forge;

import io.github.techtastic.vs_addon_template.VSAddonTemplateMod;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static io.github.techtastic.vs_addon_template.VSAddonTemplateMod.init;
import static io.github.techtastic.vs_addon_template.VSAddonTemplateMod.initClient;

@Mod(VSAddonTemplateMod.MOD_ID)
public class VSAddonTemplateModForge {
    public VSAddonTemplateModForge() {
        IEventBus MOD_BUS = FMLJavaModLoadingContext.get().getModEventBus();
        MOD_BUS.addListener(this::clientSetup);
        init();
    }

    private void clientSetup(FMLClientSetupEvent event) {
        initClient();
    }
}
