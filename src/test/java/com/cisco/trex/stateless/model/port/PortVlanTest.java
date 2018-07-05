package com.cisco.trex.stateless.model.port;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

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