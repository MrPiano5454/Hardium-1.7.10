package fr.mrpiano.hardium.blocks;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import fr.mrpiano.hardium.Hardium;
import fr.mrpiano.hardium.HardiumBlockDef;
import fr.mrpiano.hardium.HardiumFactory;
import fr.mrpiano.hardium.HardiumLifecycle;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
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

public class WCCuboidBlock extends Block implements HardiumLifecycle {

    public static class Factory extends HardiumFactory {
        @Override
        public Block[] buildBlockClasses(HardiumBlockDef def) {
            if (!def.validateMetaValues(null, null)) {
                return null;
            }
            return new Block[] { new WCCuboidBlock(def) };
        }
    }
    
    protected HardiumBlockDef def;
    protected HardiumBlockDef.Cuboid currentCuboid = null; // Current rendering cuboid
    protected int cuboidIndex = -1;
    protected IIcon sideIcons[][] = new IIcon[16][];
    protected HardiumBlockDef.CuboidRotation[] metaRotations = null;
    
    protected WCCuboidBlock(HardiumBlockDef def) {
        super(def.getMaterial());
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
        boolean tmpset = false;
        if (cuboidIndex < 0) {
            List<HardiumBlockDef.Cuboid> clist = def.getCuboidList(meta);
            if ((clist != null) && (clist.size() > 0)) {    
                currentCuboid = clist.get(0);
            }
            if (currentCuboid == null) {
                return null;
            }
            cuboidIndex = 0;
            tmpset = true;
        }
        IIcon ico = getIconInternal(side, meta);

        if (tmpset) {
            currentCuboid = null;
            cuboidIndex = -1;
        }
        return ico;
    }
    @SideOnly(Side.CLIENT)
    protected IIcon getIconInternal(int side, int meta) {
        if ((side == 2) || (side == 5)) { // North or East
            if (this.sideIcons[meta] == null) {
                List<HardiumBlockDef.Cuboid> lst = def.getCuboidList(meta);
                if (lst != null) {
                    this.sideIcons[meta] = new IIcon[lst.size() * 6];
                }
            }
            if (this.sideIcons[meta][6*cuboidIndex+side] != null) { // North needs shift
                return this.sideIcons[meta][6*cuboidIndex+side];
            }
        }
        int[] sidemap = null;
        if (this.currentCuboid != null) {
            sidemap = this.currentCuboid.sideTextures;
        }
        int nside = side;
        if (sidemap != null) {
            if (nside >= sidemap.length) {
                nside = sidemap.length - 1;
            }
            nside = sidemap[nside];
        }
        IIcon ico = def.doStandardIconGet(nside, meta);
        if (side == 2) { // North
            float shft = (1.0F - currentCuboid.xMax) - currentCuboid.xMin;
            if (shft != 0.0F) {
                ico = new ShiftedIcon(ico, shft);
            }
            this.sideIcons[meta][6*cuboidIndex + side] = ico;
        }
        else if (side == 5) { // East
            float shft = (1.0F - currentCuboid.zMax) - currentCuboid.zMin;
            if (shft != 0.0F) {
                ico = new ShiftedIcon(ico, shft);
            }
            this.sideIcons[meta][6*cuboidIndex + side] = ico;
        }
        return ico;
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
    public boolean isOpaqueCube() {
        return false;
    }
    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }
    @SideOnly(Side.CLIENT)
    @Override
    public boolean shouldSideBeRendered(IBlockAccess access, int x, int y, int z, int side)
    {
        if (currentCuboid != null) {
            switch (side) {
                case 0: // Bottom
                    if (currentCuboid.yMin > 0.0F)
                        return true;
                    break;
                case 1: // Top
                    if (currentCuboid.yMax < 1.0F)
                        return true;
                    break;
                case 2: // Zmin
                    if (currentCuboid.zMin > 0.0F)
                        return true;
                    break;
                case 3: // Zmax
                    if (currentCuboid.zMax < 1.0F)
                        return true;
                    break;
                case 4: // Xmin
                    if (currentCuboid.xMin > 0.0F)
                        return true;
                    break;
                case 5: // Xmax
                    if (currentCuboid.xMax < 1.0F)
                        return true;
                    break;
            }
        }
        return !access.getBlock(x, y, z).isOpaqueCube();
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
    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return Hardium.cuboidRenderID;
    }
    
    /**
     * Set active cuboid during render
     */
    public void setActiveRenderCuboid(HardiumBlockDef.Cuboid c, RenderBlocks renderer, int meta, int index) {
        if (c != null) {
            this.currentCuboid = c;
        }
        else {
            this.currentCuboid = null;
        }
        cuboidIndex = index;
    }
    /**
     *  Get cuboid list at given meta
     *  @param meta
     */
    public List<HardiumBlockDef.Cuboid> getCuboidList(int meta) {
        List<HardiumBlockDef.Cuboid> rslt = def.getCuboidList(meta);
        if (rslt == null) {
            rslt = Collections.emptyList();
        }
        return rslt;
    }
    
    public void setBoundingBoxFromCuboidList(int meta) {
        List<HardiumBlockDef.Cuboid> cl = getCuboidList(meta);
        float xmin = 100.0F, ymin = 100.0F, zmin = 100.0F;
        float xmax = -100.0F, ymax = -100.0F, zmax = -100.0F;
        for(HardiumBlockDef.Cuboid c : cl) {
            if (c.xMin < xmin) xmin = c.xMin;
            if (c.yMin < ymin) ymin = c.yMin;
            if (c.zMin < zmin) zmin = c.zMin;
            if (c.xMax > xmax) xmax = c.xMax;
            if (c.yMax > ymax) ymax = c.yMax;
            if (c.zMax > zmax) zmax = c.zMax;
        }
        def.setBoundingBox(meta, xmin, ymin, zmin, xmax, ymax, zmax);
    }
    @SideOnly(Side.CLIENT)
    public int getRenderBlockPass()
    {
        return (def.alphaRender?1:0);
    }
    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random rnd) {
        def.doRandomDisplayTick(world, x, y, z, rnd, metaRotations);
        super.randomDisplayTick(world, x, y, z, rnd);
    }

}
