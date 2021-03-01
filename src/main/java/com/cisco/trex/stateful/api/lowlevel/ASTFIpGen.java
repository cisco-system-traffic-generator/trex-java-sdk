package com.cisco.trex.stateful.api.lowlevel;

import com.google.gson.JsonObject;

/** Java implementation for TRex python sdk ASTFIpGen class */
public class ASTFIpGen {
  private ASTFIpGenDist distClient;
  private ASTFIpGenDist distServer;
  private ASTFIpGenGlobal glob;

  public ASTFIpGen(ASTFIpGenDist distClient, ASTFIpGenDist distServer) {
    this(distClient, distServer, new ASTFIpGenGlobal());
  }

  public ASTFIpGen(ASTFIpGenDist distClient, ASTFIpGenDist distServer, ASTFIpGenGlobal glob) {
    if (distClient.getDirection() != null
        && !distClient.getDirection().equals(ASTFIpGenDist.Direction.CLIENT)) {
      throw new IllegalStateException(
          String.format(
              "dist_client.direction is already dir: %s",
              distClient.getDirection().getDirection()));
    }
    distClient.setDirection(ASTFIpGenDist.Direction.CLIENT);
    distClient.setIpOffset(glob.getIpOffset());

    if (distServer.getDirection() != null
        && !distServer.getDirection().equals(ASTFIpGenDist.Direction.SERVER)) {
      throw new IllegalStateException(
          String.format(
              "dist_server.direction is already dir: %s",
              distServer.getDirection().getDirection()));
    }
    distServer.setDirection(ASTFIpGenDist.Direction.SERVER);
    distServer.setIpOffset(glob.getIpOffset());
    this.distClient = distClient;
    this.distServer = distServer;
    this.glob = glob;
  }

  /**
   * to json format
   *
   * @return JsonObject
   */
  public JsonObject toJson() {
    JsonObject object = new JsonObject();
    object.add("dist_client", distClient.toJson());
    object.add("dist_server", distServer.toJson());
    return object;
  }

  public ASTFIpGenDist getDistClient() {
    return distClient;
  }

  public ASTFIpGenGlobal getGlob() {
    return glob;
  }

  public ASTFIpGenDist getDistServer() {
    return distServer;
  }
}
