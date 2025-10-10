package io.github.techtastic.vs_addon_template.fabric;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import org.valkyrienskies.mod.fabric.common.ValkyrienSkiesModFabric;

import static io.github.techtastic.vs_addon_template.VSAddonTemplateMod.init;
import static io.github.techtastic.vs_addon_template.VSAddonTemplateMod.initClient;

public class VSAddonTemplateModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // force VS2 to load before eureka
        new ValkyrienSkiesModFabric().onInitialize();
        init();
    }

    @Environment(EnvType.CLIENT)
    public static class Client implements ClientModInitializer {
        @Override
        public void onInitializeClient() {
            initClient();
        }
    }
}
