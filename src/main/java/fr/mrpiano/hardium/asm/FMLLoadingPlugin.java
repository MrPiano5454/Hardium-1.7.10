package fr.mrpiano.hardium.asm;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin.MCVersion;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

@MCVersion(value = "1.7.10")
@TransformerExclusions({ "com.westeroscraft.westerosblocks.asm" })
public class FMLLoadingPlugin  implements cpw.mods.fml.relauncher.IFMLLoadingPlugin {
    @Override
    public String[] getASMTransformerClass() {
        return new String[]{ ClassTransformer.class.getName()};
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
        
    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

}
