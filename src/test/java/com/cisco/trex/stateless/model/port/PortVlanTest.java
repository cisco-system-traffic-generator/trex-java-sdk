package com.cisco.trex.stateless.model.port;

import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.cisco.trex.model.port.PortVlan;
import org.junit.Test;

public class PortVlanTest {
  @Test
  public void getTags() {
    PortVlan vlanGet = new PortVlan();
    List<Integer> tagsList = IntStream.range(0, 10).boxed().collect(Collectors.toList());
    vlanGet.tags = tagsList;
    for (Integer i : tagsList) {
      assertTrue(vlanGet.getTags().contains(i));
    }
  }

  @Test
  public void setTags() {
    PortVlan vlanSet = new PortVlan();
    List<Integer> tagsList = IntStream.range(0, 10).boxed().collect(Collectors.toList());
    vlanSet.setTags(tagsList);
    for (Integer i : tagsList) {
      assertTrue(vlanSet.tags.contains(i));
    }
  }
}
