package com.enderio.core.common.transform;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;

@SuppressWarnings("unused")
@MCVersion("1.7.10")
@IFMLLoadingPlugin.SortingIndex(Integer.MAX_VALUE)
// we want deobf no matter what
public class EnderCorePlugin implements IFMLLoadingPlugin {

    public static boolean runtimeDeobfEnabled = false;
    public static final Logger logger = LogManager.getLogger("EnderCore");

    @Override
    public String[] getASMTransformerClass() {
        return new String[] { "com.enderio.core.common.transform.EnderCoreTransformer" };
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {
        runtimeDeobfEnabled = (Boolean) data.get("runtimeDeobfuscationEnabled");
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
