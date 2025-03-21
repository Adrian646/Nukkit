package cn.nukkit.block;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityMinecartAbstract;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBlock;
import cn.nukkit.level.Level;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.SimpleAxisAlignedBB;

/**
 * Created on 2015/11/22 by CreeperFace.
 * Contributed by: larryTheCoder on 2017/7/8.
 * <p>
 * Nukkit Project,
 * Minecart and Riding Project,
 * Package cn.nukkit.block in project Nukkit.
 */
public class BlockRailDetector extends BlockRail {

    public BlockRailDetector() {
        this(0);
        canBePowered = true;
    }

    public BlockRailDetector(int meta) {
        super(meta);
    }

    @Override
    public int getId() {
        return DETECTOR_RAIL;
    }

    @Override
    public String getName() {
        return "Detector Rail";
    }

    @Override
    public boolean isPowerSource() {
        return true;
    }

    @Override
    public int getWeakPower(BlockFace side) {
        return isActive() ? 15 : 0;
    }

    @Override
    public int getStrongPower(BlockFace side) {
        return !isActive() ? 0 : (side == BlockFace.UP ? 15 : 0);
    }

    @Override
    public int onUpdate(int type) {
        if (type == Level.BLOCK_UPDATE_SCHEDULED) {
            updateState();
            return type;
        }
        return super.onUpdate(type);
    }

    @Override
    public boolean hasEntityCollision() {
        return true;
    }

    @Override
    public void onEntityCollide(Entity entity) {
        updateState();
    }

    protected void updateState() {
        boolean wasPowered = isActive();
        boolean isPowered = false;
        boolean changed = false;

        for (Entity entity : level.getCollidingEntities(new SimpleAxisAlignedBB(
                getFloorX() + 0.125D,
                getFloorY(),
                getFloorZ() + 0.125D,
                getFloorX() + 0.875D,
                getFloorY() + 0.750D,
                getFloorZ() + 0.875D))) {
            if (entity instanceof EntityMinecartAbstract) {
                isPowered = true;
                break;
            }
        }

        if (isPowered && !wasPowered) {
            setActive(true);
            level.scheduleUpdate(this, this, 0);
            level.scheduleUpdate(this, this.getSideVec(BlockFace.DOWN), 0);
            changed = true;
        }

        if (!isPowered && wasPowered) {
            setActive(false);
            level.scheduleUpdate(this, this, 0);
            level.scheduleUpdate(this, this.getSideVec(BlockFace.DOWN), 0);
            changed = true;
        }

        if (changed) {
            level.updateComparatorOutputLevel(this);
        }
    }

    @Override
    public Item toItem() {
        return new ItemBlock(Block.get(this.getId(), 0), 0);
    }

    @Override
    public Item[] getDrops(Item item) {
        return new Item[]{
                toItem()
        };
    }
}
