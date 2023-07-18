package fr.mrpiano.hardium.blocks;

import fr.mrpiano.hardium.HardiumBlockDef;
import fr.mrpiano.hardium.HardiumFactory;
import net.minecraft.block.Block;


public class WCCropBlock extends WCPlantBlock {
    public static class Factory extends HardiumFactory {
        @Override
        public Block[] buildBlockClasses(HardiumBlockDef def) {
            if (!def.validateMetaValues(null, null)) {
                return null;
            }
            return new Block[] { new WCCropBlock(def) };
        }
    }

    protected WCCropBlock(HardiumBlockDef def) {
        super(def);
    }

    @Override
    public int getRenderType() {
        return 6;   // Just switch to crop renderer
    }

}
