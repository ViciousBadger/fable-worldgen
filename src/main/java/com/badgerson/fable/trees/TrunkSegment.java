package com.badgerson.fable.trees;

import java.util.Iterator;
import net.minecraft.util.math.BlockPos;

public class TrunkSegment implements Iterable<BlockPos> {
  private final BlockPos center;
  private final int thickness;

  public TrunkSegment(BlockPos center, int thickness) {
    this.center = center;
    this.thickness = thickness;
  }

  @Override
  public Iterator<BlockPos> iterator() {
    return new TrunkSegmentIter(center, thickness);
  }

  private class TrunkSegmentIter implements Iterator<BlockPos> {
    private BlockPos[] inner;
    private int i = 0;

    public TrunkSegmentIter(BlockPos center, int thickness) {
      if (thickness == 2) {
        inner = new BlockPos[] {center, center.east(), center.south(), center.east().south()};
      } else if (thickness == 3) {
        inner =
            new BlockPos[] {
              center, center.east(), center.south(), center.north(), center.west(),
            };
      } else if (thickness == 4) {
        inner =
            new BlockPos[] {
              center.north(),
              center.north().east(),
              center.west(),
              center.east(2),
              center.south().west(),
              center.south().east(2),
              center.south(2),
              center.south(2).east(),
            };
      } else {
        inner = new BlockPos[] {center};
      }
    }

    @Override
    public boolean hasNext() {
      return i < inner.length;
    }

    @Override
    public BlockPos next() {
      return inner[i++];
    }
  }
}
