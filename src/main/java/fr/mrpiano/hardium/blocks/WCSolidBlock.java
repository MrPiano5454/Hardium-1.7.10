package fr.mrpiano.hardium.blocks;

import java.util.List;
import java.util.Random;

import fr.mrpiano.hardium.HardiumBlockDef.BoundingBox;
import fr.mrpiano.hardium.HardiumBlockDef;
import fr.mrpiano.hardium.HardiumFactory;
import fr.mrpiano.hardium.HardiumLifecycle;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import fr.mrpiano.hardium.items.MultiBlockItem;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class WCSolidBlock extends Block implements HardiumLifecycle {

    public static class Factory extends HardiumFactory {
        @Override
        public Block[] buildBlockClasses(HardiumBlockDef def) {
            if (!def.validateMetaValues(null, null)) {
                return null;
            }
            return new Block[] { new WCSolidBlock(def) };
        }
    }
    
    private HardiumBlockDef def;
    private boolean isSolidOpaque = true;
    
    protected WCSolidBlock(HardiumBlockDef def) {
        super(def.getMaterial());
        this.isSolidOpaque = !def.nonOpaque;
        if (this.isSolidOpaque && (def.lightOpacity < 0)) {
            def.lightOpacity = 255;
        }
        this.def = def;
        def.doStandardContructorSettings(this);
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
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return def.doStandardIconGet(side, meta);
    }
    
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itm, CreativeTabs tab, List list) {
        def.getStandardSubBlocks(this, Item.getIdFromItem(itm), tab, list);
    }
    @Override
    public int damageDropped(int meta) {
        return meta;
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
    public boolean isOpaqueCube() {
        return isSolidOpaque;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random rnd) {
        def.doRandomDisplayTick(world, x, y, z, rnd);
        super.randomDisplayTick(world, x, y, z, rnd);
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB mask, List list, Entity entity)
    {
        if (def.hasCollisionBoxes() == false) {
            super.addCollisionBoxesToList(world, x, y, z, mask, list, entity);
            return;
        }
        int meta = world.getBlockMetadata(x,  y,  z);
        List<HardiumBlockDef.BoundingBox> cl = def.getCollisionBoxList(meta);
        if (cl == null) {
            super.addCollisionBoxesToList(world, x, y, z, mask, list, entity);
            return;
        }
        for (HardiumBlockDef.BoundingBox c : cl) {
            this.setBlockBounds(c.xMin, c.yMin, c.zMin, c.xMax, c.yMax, c.zMax);
            super.addCollisionBoxesToList(world, x, y, z, mask, list, entity);
        }
        def.setBlockBoundsBasedOnState(this, world, x, y, z);
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 start, Vec3 end) {
        if (def.hasCollisionBoxes() == false) {
            return super.collisionRayTrace(world, x, y, z, start, end);
        }
        int meta = world.getBlockMetadata(x,  y,  z);
        List<BoundingBox> cl = def.getCollisionBoxList(meta); 
        if (cl == null) {
            return super.collisionRayTrace(world, x, y, z, start, end);
        }
        MovingObjectPosition bestpos = null;
        double bestdist = 0.0;
        for (BoundingBox c : cl) {
            this.setBlockBounds(c.xMin, c.yMin, c.zMin, c.xMax, c.yMax, c.zMax);
            MovingObjectPosition pos = super.collisionRayTrace(world, x, y, z, start, end);
            if (pos != null) {
                if (bestpos == null) {
                    bestpos = pos;
                    bestdist = bestpos.hitVec.squareDistanceTo(end);
                }
                else {
                    double dist = pos.hitVec.squareDistanceTo(end);
                    if (dist > bestdist) {
                        bestpos = pos;
                        bestdist = dist;
                    }
                }
            }
        }
        def.setBlockBoundsBasedOnState(this, world, x, y, z);
        return bestpos;
    }
}
