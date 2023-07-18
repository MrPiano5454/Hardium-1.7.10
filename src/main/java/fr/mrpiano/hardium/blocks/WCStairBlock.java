package fr.mrpiano.hardium.blocks;


import java.util.Random;

import fr.mrpiano.hardium.Hardium;
import fr.mrpiano.hardium.HardiumBlockDef;
import fr.mrpiano.hardium.HardiumFactory;
import fr.mrpiano.hardium.HardiumLifecycle;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStairs;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import fr.mrpiano.hardium.items.MultiBlockItem;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class WCStairBlock extends BlockStairs implements HardiumLifecycle {

    public static class Factory extends HardiumFactory {
        @Override
        public Block[] buildBlockClasses(HardiumBlockDef def) {
            if ((def.modelBlockName == null) || (def.modelBlockMeta < 0)) {
                Hardium.log.severe("Type 'stair' requires modelBlockName and modelBlockMeta settings");
                return null;
            }
            // Try to find model block
            Block blk = Hardium.findBlockByName(def.modelBlockName);
            if (blk == null) {
                Hardium.log.severe(String.format("modelBlockName '%s' not found for block '%s'", def.modelBlockName, def.blockName));
                return null;
            }
            // Validate meta : we require meta 0, and only allow it
            def.setMetaMask(0x0);
            if (!def.validateMetaValues(new int[] { 0 }, new int[] { 0 })) {
                return null;
            }

            return new Block[] { new WCStairBlock(def, blk) };
        }
    }
    
    private HardiumBlockDef def;
    private final Block ourModelBlock;
    
    protected WCStairBlock(HardiumBlockDef def, Block blk) {
        super(blk, def.modelBlockMeta);
        this.def = def;
        this.ourModelBlock = blk;
        if (def.lightOpacity == HardiumBlockDef.DEF_INT) {
            def.lightOpacity = 255;
        }
        this.setCreativeTab(def.getCreativeTab());
        this.setBlockName(def.blockName);
        useNeighborBrightness = true;
    }

    public boolean initializeBlockDefinition() {
        def.doStandardInitializeActions(this);

        return true;
    }

    public boolean registerBlockDefinition() {
        def.doStandardRegisterActions(this, MultiBlockItem.class);
        
        return true;
    }
    @Override
    public HardiumBlockDef getWBDefinition() {
        return def;
    }
    @Override
    public int getFireSpreadSpeed(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        return def.getFireSpreadSpeed(world, x, y, z, face);
    }
    @Override
    public int getFlammability(IBlockAccess world, int x, int y, int z, ForgeDirection face) {
        return def.getFlammability(world, x, y, z, face);
    }
    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        return def.getLightValue(world, x, y, z);
    }
    @Override
    public int getLightOpacity(IBlockAccess world, int x, int y, int z) {
        return def.getLightOpacity(this, world, x, y, z);
    }
    @SideOnly(Side.CLIENT)
    @Override
    public int getBlockColor() {
        return def.getBlockColor();
    }
    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderColor(int meta)
    {
        return def.getRenderColor(meta);
    }
    @SideOnly(Side.CLIENT)
    @Override
    public int colorMultiplier(IBlockAccess access, int x, int y, int z)
    {
        return def.colorMultiplier(access, x, y, z);
    }

    @SideOnly(Side.CLIENT)
    public int getRenderBlockPass()
    {
        return (def.alphaRender?1:0);
    }
    @Override
    public int getRenderType() {
        return Hardium.stairRenderID;    // Use custom to make inventory render correctly
    }
    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random rnd) {
        def.doRandomDisplayTick(world, x, y, z, rnd);
        super.randomDisplayTick(world, x, y, z, rnd);
    }
}
