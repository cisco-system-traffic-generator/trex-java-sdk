package com.cisco.trex.stateful.api.lowlevel;

import com.google.gson.JsonArray;
import java.util.ArrayList;
import java.util.List;

/** Java implementation for TRex python sdk ASTFIPGenDistCache class */
public class ASTFIPGenDistCache {
  List<ASTFIpGenDist.Inner> innerList = new ArrayList<>();

  public ASTFIPGenDistCache() {}

  public void clearCache() {
    innerList.clear();
  }

  public JsonArray toJson() {
    JsonArray jsonArray = new JsonArray();
    for (ASTFIpGenDist.Inner inner : innerList) {
      jsonArray.add(inner.toJson());
    }
    return jsonArray;
  }

  public void addInner(ASTFIpGen ipGen) {
    ASTFIpGenDist ipDestClient = ipGen.getDistClient();
    ASTFIpGenDist ipDestServer = ipGen.getDistServer();
    addInner(ipDestClient, true);
    addInner(ipDestServer, false);
  }

  private void addInner(ASTFIpGenDist ipGenDist, boolean isClient) {
    ASTFIpGenDist.Inner newInner = ipGenDist.getInner();
    ASTFIpGenDist.Inner overlapInner = null;
    for (int i = 0; i < innerList.size(); i++) {
      ASTFIpGenDist.Inner inner = innerList.get(i);
      if (newInner.equals(inner)) {
        ipGenDist.setIndex(i);
        ipGenDist.setInner(inner);
        return;
      } else if (isClient && inner.isOverLaps(newInner)) {
        overlapInner = inner;
      }
    }
    if (overlapInner != null) {
      throw new IllegalStateException(
          String.format(
              "Bad Ip range! [%s , %s] overlaps with [%s , %s]",
              newInner.getIpStart(),
              newInner.getIpEnd(),
              overlapInner.getIpStart(),
              overlapInner.getIpEnd()));
    }

    innerList.add(newInner);
    ipGenDist.setIndex(innerList.size() - 1);
  }
}
