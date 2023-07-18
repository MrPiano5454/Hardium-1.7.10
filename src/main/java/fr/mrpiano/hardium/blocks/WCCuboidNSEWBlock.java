package fr.mrpiano.hardium.blocks;

import java.util.ArrayList;
import java.util.List;

import fr.mrpiano.hardium.HardiumBlockDef;
import fr.mrpiano.hardium.HardiumFactory;
import fr.mrpiano.hardium.HardiumLifecycle;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

import fr.mrpiano.hardium.items.WCCuboidNSEWItem;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class WCCuboidNSEWBlock extends WCCuboidBlock implements HardiumLifecycle {

    public static class Factory extends HardiumFactory {
        @Override
        public Block[] buildBlockClasses(HardiumBlockDef def) {
            def.setMetaMask(0x3);
            if (!def.validateMetaValues(new int[] { 0, 1, 2, 3 }, new int[] { 0 })) {
                return null;
            }
            return new Block[] { new WCCuboidNSEWBlock(def) };
        }
    }
    
    private List<HardiumBlockDef.Cuboid> cuboids_by_meta[];
    
    @SuppressWarnings("unchecked")
    protected WCCuboidNSEWBlock(HardiumBlockDef def) {
        super(def);
        // Set rotations for effects
        this.metaRotations = new HardiumBlockDef.CuboidRotation[] { HardiumBlockDef.CuboidRotation.NONE,
                HardiumBlockDef.CuboidRotation.ROTY90, HardiumBlockDef.CuboidRotation.ROTY180, HardiumBlockDef.CuboidRotation.ROTY270 };

        cuboids_by_meta = (List<HardiumBlockDef.Cuboid>[])new List[16];
        for (int i = 0; i < 4; i++) {
            List<HardiumBlockDef.Cuboid> lst = def.getCuboidList(i);
            if (lst == null) continue;
            cuboids_by_meta[i] = lst;
            cuboids_by_meta[i+4] = new ArrayList<HardiumBlockDef.Cuboid>();
            cuboids_by_meta[i+8] = new ArrayList<HardiumBlockDef.Cuboid>();
            cuboids_by_meta[i+12] = new ArrayList<HardiumBlockDef.Cuboid>();
            for (HardiumBlockDef.Cuboid c : lst) {
                cuboids_by_meta[i+4].add(c.rotateCuboid(HardiumBlockDef.CuboidRotation.ROTY90));
                cuboids_by_meta[i+8].add(c.rotateCuboid(HardiumBlockDef.CuboidRotation.ROTY180));
                cuboids_by_meta[i+12].add(c.rotateCuboid(HardiumBlockDef.CuboidRotation.ROTY270));
            }
            setBoundingBoxFromCuboidList(i+4);
            setBoundingBoxFromCuboidList(i+8);
            setBoundingBoxFromCuboidList(i+12);
        }
    }

    public boolean registerBlockDefinition() {
        def.doStandardRegisterActions(this, WCCuboidNSEWItem.class);
        
        return true;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubBlocks(Item itm, CreativeTabs tab, List list) {
        def.getStandardSubBlocks(this, Item.getIdFromItem(itm), tab, list);
    }

    @Override
    public int damageDropped(int meta) {
        return meta & 3;
    }
    /**
     *  Get cuboid list at given meta
     *  @param meta
     */
    public List<HardiumBlockDef.Cuboid> getCuboidList(int meta) {
        return cuboids_by_meta[meta];
    }
    /**
     * Set active cuboid during render
     */
    @Override
    public void setActiveRenderCuboid(HardiumBlockDef.Cuboid c, RenderBlocks renderer, int meta, int index) {
        super.setActiveRenderCuboid(c, renderer, meta, index);
        int dir = (meta >> 2);
        if (c != null) {
            if (dir == 1) {
                renderer.uvRotateTop = 1;
                renderer.uvRotateBottom = 1;
            }
            else if (dir == 2) {
                renderer.uvRotateTop = 3;
                renderer.uvRotateBottom = 3;
            }
            else if (dir == 3) {
                renderer.uvRotateTop = 2;
                renderer.uvRotateBottom = 2;
            }
            else {
                renderer.uvRotateTop = renderer.uvRotateBottom = 0;
            }
        }
        else {
            renderer.uvRotateTop = renderer.uvRotateBottom = 0;
        }
    }
    @SideOnly(Side.CLIENT)
    public int getRenderBlockPass()
    {
        return (def.alphaRender?1:0);
    }    
}
