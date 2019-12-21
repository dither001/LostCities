package mcjty.lostcities.dimensions.world.driver;

import net.minecraft.block.BlockState;
import net.minecraft.block.FourWayBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.WorldGenRegion;

import java.util.Objects;

public class PrimerDriver {

    private WorldGenRegion region;
    private IChunk primer;
    private final BlockPos.MutableBlockPos current = new BlockPos.MutableBlockPos();
    private final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

    public void setPrimer(WorldGenRegion region, IChunk primer) {
        this.region = region;
        this.primer = primer;
    }

    public IChunk getPrimer() {
        return primer;
    }

    public PrimerDriver current(int x, int y, int z) {
        current.setPos(x, y, z);
        return this;
    }

    public PrimerDriver current(IIndex index) {
        Index i = (Index) index;
        current.setPos(i.x, i.y, i.z);
        return this;
    }

    public IIndex getCurrent() {
        return new Index(current.getX(), current.getY(), current.getZ());
    }

    public void incY() {
        current.setY(current.getY()+1);
    }

    public void incY(int amount) {
        current.setY(current.getY()+amount);
    }

    public void decY() {
        current.setY(current.getY()-1);
    }

    public void incX() {
        current.setX(current.getX()+1);
    }

    public void incZ() {
        current.setZ(current.getZ()+1);
    }

    public int getX() {
        return current.getX();
    }

    public int getY() {
        return current.getY();
    }

    public int getZ() {
        return current.getZ();
    }

    public void setBlockRange(int x, int y, int z, int y2, BlockState state) {
        pos.setPos(x, y, z);
        while (y < y2) {
            if (primer.getBlockState(pos) != state) {
                primer.setBlockState(pos, state, false);
            }
            y++;
            pos.setY(y);
        }
    }

    public void setBlockRangeSafe(int x, int y, int z, int y2, BlockState state) {
        pos.setPos(x, y, z);
        while (y < y2) {
            if (primer.getBlockState(pos) != state) {
                primer.setBlockState(pos, state, false);
            }
            y++;
            pos.setY(y);
        }
    }

    private BlockState correct(BlockState state) {
        if (state.getBlock() instanceof FourWayBlock) {
            int cx = current.getX() + primer.getPos().x * 16;
            int cy = current.getY();
            int cz = current.getZ() + primer.getPos().z * 16;

            BlockState west = region.getBlockState(pos.setPos(cx - 1, cy, cz));
            if (west.getBlock() instanceof FourWayBlock) {
                state = state.with(FourWayBlock.WEST, true);
                region.setBlockState(pos, west.getBlock().updatePostPlacement(west, Direction.EAST, state, region, pos, current), 0);
            }

            BlockState east = region.getBlockState(pos.setPos(cx + 1, cy, cz));
            if (east.getBlock() instanceof FourWayBlock) {
                state = state.with(FourWayBlock.EAST, true);
                region.setBlockState(pos, east.getBlock().updatePostPlacement(east, Direction.WEST, state, region, pos, current), 0);
            }

            BlockState north = region.getBlockState(pos.setPos(cx, cy, cz - 1));
            if (north.getBlock() instanceof FourWayBlock) {
                state = state.with(FourWayBlock.NORTH, true);
                region.setBlockState(pos, north.getBlock().updatePostPlacement(north, Direction.SOUTH, state, region, pos, current), 0);
            }

            BlockState south = region.getBlockState(pos.setPos(cx, cy, cz + 1));
            if (south.getBlock() instanceof FourWayBlock) {
                state = state.with(FourWayBlock.SOUTH, true);
                region.setBlockState(pos, south.getBlock().updatePostPlacement(south, Direction.NORTH, state, region, pos, current), 0);
            }
        }
        return state;
    }

    public PrimerDriver block(BlockState c) {
        primer.setBlockState(current, correct(c), false);
        return this;
    }

    public PrimerDriver add(BlockState state) {
        primer.setBlockState(current, correct(state), false);
        incY();
        return this;
    }

    public BlockState getBlock() {
        return primer.getBlockState(current);
    }

    public BlockState getBlockDown() {
        return primer.getBlockState(pos.setPos(current.getX(), current.getY()-1, current.getZ()));
    }

    public BlockState getBlockEast() {
        return primer.getBlockState(pos.setPos(current.getX()+1, current.getY(), current.getZ()));
    }

    public BlockState getBlockWest() {
        return primer.getBlockState(pos.setPos(current.getX()-1, current.getY(), current.getZ()));
    }

    public BlockState getBlockSouth() {
        return primer.getBlockState(pos.setPos(current.getX(), current.getY(), current.getZ()+1));
    }

    public BlockState getBlockNorth() {
        return primer.getBlockState(pos.setPos(current.getX(), current.getY(), current.getZ()-1));
    }


    public BlockState getBlock(int x, int y, int z) {
        return primer.getBlockState(pos.setPos(x, y, z));
    }

    public IIndex getIndex(int x, int y, int z) {
        return new Index(x, y, z);
    }


    private class Index implements IIndex {
        private final int x;
        private final int y;
        private final int z;

        Index(int x, int y, int z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Index index = (Index) o;
            return x == index.x &&
                    y == index.y &&
                    z == index.z;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, z);
        }
    }

    public PrimerDriver copy() {
        PrimerDriver driver = new PrimerDriver();
        driver.current.setPos(current);
        driver.primer = primer;
        return driver;
    }
}