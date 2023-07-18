package fr.mrpiano.hardium.blocks;

import java.util.ArrayList;
import java.util.List;

import fr.mrpiano.hardium.Hardium;
import fr.mrpiano.hardium.HardiumBlockDef;
import fr.mrpiano.hardium.HardiumFactory;
import fr.mrpiano.hardium.HardiumLifecycle;
import net.minecraft.block.Block;
import net.minecraft.util.IIcon;
import fr.mrpiano.hardium.items.WCCuboidNSEWUDItem;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class WCCuboidNSEWUDBlock extends WCCuboidBlock implements HardiumLifecycle {

    public static class Factory extends HardiumFactory {
        @Override
        public Block[] buildBlockClasses(HardiumBlockDef def) {
            def.setMetaMask(0x1);
            if (!def.validateMetaValues(new int[] { 0, 1 }, new int[] { 0 })) {
                return null;
            }
            return new Block[] { new WCCuboidNSEWUDBlock(def) };
        }
    }
    
    private List<HardiumBlockDef.Cuboid> cuboids_by_meta[];
    
    @SuppressWarnings("unchecked")
    protected WCCuboidNSEWUDBlock(HardiumBlockDef def) {
        super(def);
        // Set rotations for effects
        this.metaRotations = new HardiumBlockDef.CuboidRotation[] { HardiumBlockDef.CuboidRotation.NONE,
                HardiumBlockDef.CuboidRotation.ROTY90, HardiumBlockDef.CuboidRotation.ROTY180, HardiumBlockDef.CuboidRotation.ROTY270 };

        cuboids_by_meta = (List<HardiumBlockDef.Cuboid>[])new List[16];
        for (int i = 0; i < 2; i++) {
            List<HardiumBlockDef.Cuboid> lst = def.getCuboidList(i);
            if (lst == null) continue;
            cuboids_by_meta[i] = lst;
            cuboids_by_meta[i+2] = new ArrayList<HardiumBlockDef.Cuboid>();
            cuboids_by_meta[i+4] = new ArrayList<HardiumBlockDef.Cuboid>();
            cuboids_by_meta[i+6] = new ArrayList<HardiumBlockDef.Cuboid>();
            cuboids_by_meta[i+8] = new ArrayList<HardiumBlockDef.Cuboid>();
            cuboids_by_meta[i+10] = new ArrayList<HardiumBlockDef.Cuboid>();
            for (HardiumBlockDef.Cuboid c : lst) {
                cuboids_by_meta[i+2].add(c.rotateCuboid(HardiumBlockDef.CuboidRotation.ROTY90));
                cuboids_by_meta[i+4].add(c.rotateCuboid(HardiumBlockDef.CuboidRotation.ROTY180));
                cuboids_by_meta[i+6].add(c.rotateCuboid(HardiumBlockDef.CuboidRotation.ROTY270));
                cuboids_by_meta[i+8].add(c.rotateCuboid(HardiumBlockDef.CuboidRotation.ROTZ90));
                cuboids_by_meta[i+10].add(c.rotateCuboid(HardiumBlockDef.CuboidRotation.ROTZ270));
            }
            setBoundingBoxFromCuboidList(i+2);
            setBoundingBoxFromCuboidList(i+4);
            setBoundingBoxFromCuboidList(i+6);
            setBoundingBoxFromCuboidList(i+8);
            setBoundingBoxFromCuboidList(i+10);
        }
    }

    public boolean registerBlockDefinition() {
        def.doStandardRegisterActions(this, WCCuboidNSEWUDItem.class);
        
        return true;
    }
    /**
     * The type of render function that is called for this block
     */
    @Override
    public int getRenderType()
    {
        return Hardium.cuboidNSEWUDRenderID;
    }
    
    @Override
    public int damageDropped(int meta) {
        return meta & 1;
    }
    /**
     *  Get cuboid list at given meta
     *  @param meta
     */
    public List<HardiumBlockDef.Cuboid> getCuboidList(int meta) {
        return cuboids_by_meta[meta];
    }
    
    @SideOnly(Side.CLIENT)
    protected IIcon getIconInternal(int side, int meta) {
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
        return def.doStandardIconGet(nside, meta);
    }

    @SideOnly(Side.CLIENT)
    public int getRenderBlockPass()
    {
        return (def.alphaRender?1:0);
    }
}
