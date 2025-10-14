package io.github.kawaiicakes.vs_hitnrun.forge;

import io.github.kawaiicakes.vs_hitnrun.VSHitNRun;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static io.github.kawaiicakes.vs_hitnrun.VSHitNRun.init;
import static io.github.kawaiicakes.vs_hitnrun.VSHitNRun.initClient;

@Mod(VSHitNRun.MOD_ID)
public class VSHitNRunForge {
    public VSHitNRunForge() {
        IEventBus MOD_BUS = FMLJavaModLoadingContext.get().getModEventBus();
        MOD_BUS.addListener(this::clientSetup);
        init();
    }

    private void clientSetup(FMLClientSetupEvent event) {
        initClient();
    }
}
