package com.badgerson.fable.trees;

import com.badgerson.fable.Fable;
import com.badgerson.fable.trees.config.AdvancedTrunkConfig;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacer.TreeNode;
import net.minecraft.world.gen.trunk.TrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;
import org.joml.Vector3f;

public class AdvancedTrunkPlacer extends TrunkPlacer {

  private AdvancedTrunkConfig config;

  public static final MapCodec<AdvancedTrunkPlacer> CODEC =
      RecordCodecBuilder.mapCodec(
          instance ->
              instance
                  .group(
                      AdvancedTrunkConfig.CODEC
                          .fieldOf("config")
                          .forGetter(
                              (placer) -> {
                                return placer.config;
                              }))
                  .apply(instance, AdvancedTrunkPlacer::new));

  public AdvancedTrunkPlacer(AdvancedTrunkConfig config) {
    super((int) config.trunk().length().min(), 0, 0);
    this.config = config;
  }

  @Override
  protected TrunkPlacerType<?> getType() {
    return Fable.TRUNK_PLACER;
  }

  @Override
  public List<TreeNode> generate(
      TestableWorld world,
      BiConsumer<BlockPos, BlockState> replacer,
      Random random,
      int height,
      BlockPos startPos,
      TreeFeatureConfig config) {
    setToDirt(world, replacer, random, startPos.down(), config);

    // How 2 do new generation, very much like incendiumboss:
    // breath-first (one recursion level at a time)
    // use list to collect content from each recursion level
    // loop trough each branch, place in world, also foliage
    // collect subbranches in that same list and clear list somehow i guess or swap lists

    Vector3f initialPosition = Vec3d.of(startPos.down()).add(0.5, 0.5, 0.5).toVector3f();
    Vector3f initialDirection = new Vector3f(0.0f, 1.0f, 0.0f);

    List<FoliagePlacer.TreeNode> treeNodes = new ArrayList<FoliagePlacer.TreeNode>();

    List<BranchProducer> nextLayer = new ArrayList<>();
    nextLayer.add(
        new BranchProducer(
            this.config.trunk(), this.config.bending(), initialPosition, initialDirection));

    while (nextLayer.size() > 0) {
      // Layer swap
      List<BranchProducer> thisLayer = nextLayer;
      nextLayer = new ArrayList<>();

      // Build these
      for (BranchProducer producer : thisLayer) {
        ImmutableList<BranchProduct> products = producer.produceMany(random);
        for (BranchProduct product : products) {
          for (BranchProduct.TrunkBlock block : product.trunkBlocks()) {
            this.getAndSetState(world, replacer, random, block.pos(), config);
          }
          for (BranchProduct.FoliageNode foliage : product.foliageNodes()) {
            // TODO: how 2 detext giantTrunk (trunk thickness)?
            treeNodes.add(new FoliagePlacer.TreeNode(foliage.pos(), foliage.radius(), false));
          }
          for (BranchProducer subBranch : product.subBranches()) {
            nextLayer.add(subBranch);
          }
        }
      }
    }

    return treeNodes;
  }
}
