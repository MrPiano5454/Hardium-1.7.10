package fr.mrpiano.hardium.blocks;

import java.util.List;
import java.util.Random;


import fr.mrpiano.hardium.Hardium;
import fr.mrpiano.hardium.HardiumBlockDef;
import fr.mrpiano.hardium.HardiumFactory;
import fr.mrpiano.hardium.HardiumLifecycle;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import fr.mrpiano.hardium.items.MultiBlockItem;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class WCLadderBlock extends Block implements HardiumLifecycle {

    public static class Factory extends HardiumFactory {
        @Override
        public Block[] buildBlockClasses(HardiumBlockDef def) {
            def.setMetaMask(0x3);
            if (!def.validateMetaValues(new int[] { 0, 1, 2, 3 }, new int[] { 0 })) {
                return null;
            }
            return new Block[] { new WCLadderBlock(def) };
        }
    }
    
    private HardiumBlockDef def;
    
    protected WCLadderBlock(HardiumBlockDef def) {
        super(def.getMaterial());
        this.def = def;
        def.doStandardContructorSettings(this);
    }
    
    /**
     * Returns a bounding box from the pool of bounding boxes (this means this box can change after the pool has been
     * cleared to be reused)
     */
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
        this.setBlockBoundsBasedOnState(par1World, par2, par3, par4);
        return super.getCollisionBoundingBoxFromPool(par1World, par2, par3, par4);
    }

    @SideOnly(Side.CLIENT)

    /**
     * Returns the bounding box of the wired rectangular prism to render.
     */
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World par1World, int par2, int par3, int par4)
    {
        this.setBlockBoundsBasedOnState(par1World, par2, par3, par4);
        return super.getSelectedBoundingBoxFromPool(par1World, par2, par3, par4);
    }

    /**
     * Updates the blocks bounds based on its current state. Args: world, x, y, z
     */
    public void setBlockBoundsBasedOnState(IBlockAccess par1IBlockAccess, int par2, int par3, int par4)
    {
        this.updateLadderBounds(par1IBlockAccess.getBlockMetadata(par2, par3, par4));
    }

    /**
     * Update the ladder block bounds based on the given metadata value.
     */
    public void updateLadderBounds(int meta)
    {
        float f = 0.125F;

        switch(meta >> 2) {
            case 3:
                this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
                break;
            case 2:
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
                break;
            case 1:
                this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                break;
            case 0:
                this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
                break;
        }
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube?  This determines whether or not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone wire, etc to this block.
     */
    public boolean isOpaqueCube()
    {
        return false;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock()
    {
        return false;
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType()
    {
        return Hardium.ladderRenderID;
    }

    /**
     * Checks to see if its valid to put this block at the specified coordinates. Args: world, x, y, z
     */
    @Override
    public boolean canPlaceBlockAt(World par1World, int par2, int par3, int par4)
    {
        return par1World.isSideSolid(par2 - 1, par3, par4, ForgeDirection.EAST ) ||
               par1World.isSideSolid(par2 + 1, par3, par4, ForgeDirection.WEST ) ||
               par1World.isSideSolid(par2, par3, par4 - 1, ForgeDirection.SOUTH) ||
               par1World.isSideSolid(par2, par3, par4 + 1, ForgeDirection.NORTH);
    }

    /**
     * Called when a block is placed using its ItemBlock. Args: World, X, Y, Z, side, hitX, hitY, hitZ, block metadata
     */
    @Override
    public int onBlockPlaced(World par1World, int par2, int par3, int par4, int par5, float par6, float par7, float par8, int meta)
    {
        int j1 = meta & 0x3;

        if ((j1 == 0 || par5 == 2) && par1World.isSideSolid(par2, par3, par4 + 1, ForgeDirection.NORTH))
        {
            j1 |= 0xC;
        }

        if ((j1 == 0 || par5 == 3) && par1World.isSideSolid(par2, par3, par4 - 1, ForgeDirection.SOUTH))
        {
            j1 |= 0x8;
        }

        if ((j1 == 0 || par5 == 4) && par1World.isSideSolid(par2 + 1, par3, par4, ForgeDirection.WEST))
        {
            j1 |= 0x4;
        }

        if ((j1 == 0 || par5 == 5) && par1World.isSideSolid(par2 - 1, par3, par4, ForgeDirection.EAST))
        {
            j1 |= 0x0;
        }

        return j1;
    }

    /**
     * Lets the block know when one of its neighbor changes. Doesn't know which neighbor changed (coordinates passed are
     * their own) Args: x, y, z, neighbor blockID
     */
    @Override
    public void onNeighborBlockChange(World par1World, int par2, int par3, int par4, Block par5)
    {
        int meta = par1World.getBlockMetadata(par2, par3, par4);
        boolean flag = false;
        int sidemeta = (meta >> 2);

        if (sidemeta == 3 && par1World.isSideSolid(par2, par3, par4 + 1, ForgeDirection.NORTH))
        {
            flag = true;
        }

        if (sidemeta == 2 && par1World.isSideSolid(par2, par3, par4 - 1, ForgeDirection.SOUTH))
        {
            flag = true;
        }

        if (sidemeta == 1 && par1World.isSideSolid(par2 + 1, par3, par4, ForgeDirection.WEST))
        {
            flag = true;
        }

        if (sidemeta == 0 && par1World.isSideSolid(par2 - 1, par3, par4, ForgeDirection.EAST))
        {
            flag = true;
        }

        if (!flag)
        {
            this.dropBlockAsItem(par1World, par2, par3, par4, meta & 3, 0);
            par1World.setBlockToAir(par2, par3, par4);
        }

        super.onNeighborBlockChange(par1World, par2, par3, par4, par5);
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    @Override
    public int quantityDropped(Random par1Random)
    {
        return 1;
    }

    @Override
    public boolean isLadder(IBlockAccess world, int x, int y, int z, EntityLivingBase entity)
    {
        return true;
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
        return meta & 3;
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
    @SideOnly(Side.CLIENT)
    @Override
    public boolean shouldSideBeRendered(IBlockAccess access, int x, int y, int z, int side) {
        if ((side == 0) || (side == 1))
            return false;
        ForgeDirection dir = ForgeDirection.values()[side];
        int meta = access.getBlockMetadata(x - dir.offsetX, y, z - dir.offsetZ);
        switch ((meta >> 2) & 0x3) {
            case 0:
            case 1:
                return ((side == 4) || (side == 5));
            default:
                return ((side == 2) || (side == 3));
        }
    }
    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random rnd) {
        def.doRandomDisplayTick(world, x, y, z, rnd);
        super.randomDisplayTick(world, x, y, z, rnd);
    }

}
