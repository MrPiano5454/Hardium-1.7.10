package fr.mrpiano.hardium.blocks;


import java.util.Random;

import fr.mrpiano.hardium.HardiumBlockDef;
import fr.mrpiano.hardium.HardiumFactory;
import fr.mrpiano.hardium.HardiumLifecycle;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCake;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import fr.mrpiano.hardium.items.MultiBlockItem;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class WCCakeBlock extends BlockCake implements HardiumLifecycle {

    public static class Factory extends HardiumFactory {
        @Override
        public Block[] buildBlockClasses(HardiumBlockDef def) {
            // Validate meta : we require meta 0, and only allow it
            def.setMetaMask(0x0);
            if (!def.validateMetaValues(new int[] { 0 }, new int[] { 0 })) {
                return null;
            }

            return new Block[] { new WCCakeBlock(def) };
        }
    }
    
    private HardiumBlockDef def;
    
    protected WCCakeBlock(HardiumBlockDef def) {
        super();
        this.def = def;
        this.setCreativeTab(def.getCreativeTab());
        this.setBlockName(def.blockName);
        if (def.lightOpacity < 0) {    // Workaround for f*cked up MC lighting hacks
            this.setLightOpacity(0);
        }
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
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister)
    {
        def.doStandardRegisterIcons(iconRegister);
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
    @Override
    @SideOnly(Side.CLIENT)
    public Item getItem(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_)
    {
        return Item.getItemFromBlock(this);
    }
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        int idx;
        if (side < 2) { // Top or bottom
            idx = side;
        }
        else if ((meta > 0) && (side == 4)) {
            idx = 3;    // Inside
        }
        else {
            idx = 2;    // Side
        }
        return def.doStandardIconGet(idx, meta);
    }
    @SideOnly(Side.CLIENT)
    public int getRenderBlockPass()
    {
        return (def.alphaRender?1:0);
    }
    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random rnd) {
        def.doRandomDisplayTick(world, x, y, z, rnd);
        super.randomDisplayTick(world, x, y, z, rnd);
    }

}
