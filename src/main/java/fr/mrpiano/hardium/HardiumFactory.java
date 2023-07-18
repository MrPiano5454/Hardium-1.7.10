package fr.mrpiano.hardium;

import net.minecraft.block.Block;

// Abstract factory class : each custom block type needs to have one
public abstract class HardiumFactory {
    /* Build instance of given block type and given blknum within factory (replace for factories needing more than one block def)
     *
     * @param def - definition loaded for block
     * @returns block based on definition
     */
    public abstract Block[] buildBlockClasses(HardiumBlockDef def);
    /**
     * Get number of blocks defined for factory
     */
    public int getBlockClassCount() { return 1; }
}

