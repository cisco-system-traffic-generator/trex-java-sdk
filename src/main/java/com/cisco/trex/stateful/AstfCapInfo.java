package com.cisco.trex.stateful;

/**
 * Astf Cap Info,for pcap file usage
 */
public class AstfCapInfo {
    private String filePath;//pcap file name. Filesystem directory location is relative to the profile file in case it is not start with
    private float cps;//new connection per second rate
    private AstfAssociation assoc;//rule for server association in default take the destination port from pcap file
    private AstfIpGen astfIpGen;//tuple generator for this template
    private int port;//Override destination port, by default is taken from pcap
    private float l7Percent;//L7 stream bandwidth percent
    private AstfGlobalInfoPerTemplate serverGlobInfo;//server global param
    private AstfGlobalInfoPerTemplate clientGlobInfo;//client global param
    private int limit;//Limit the number of flows

    private AstfCapInfo(AstfCapInfoBuilder builder) {
        filePath = builder.filePath;
        cps = builder.cps;
        assoc = builder.assoc;
        astfIpGen = builder.astfIpGen;
        port = builder.port;
        l7Percent = builder.l7Percent;
        serverGlobInfo = builder.serverGlobInfo;
        clientGlobInfo = builder.clientGlobInfo;
        limit = builder.limit;
        paramCheck();
    }

    /**
     * new AstfCapInfo builder
     *
     * @return new builder
     */
    public static AstfCapInfoBuilder newBuilder() {
        return new AstfCapInfoBuilder();
    }

    private void paramCheck() {
        if (l7Percent > 0) {
            if (cps > 0) {
                throw new IllegalStateException(String.format("bad param combination,l7Percent %s ,cps %s ", l7Percent, cps));
            }
            l7Percent = l7Percent;
            cps = cps;
        } else {
            if (cps > 0) {
                cps = cps;
            } else {
                cps = 1;
            }
        }
        if (assoc == null) {
            if (port > 0) {
                assoc = new AstfAssociation(new AstfAssociationRule(port));
            } else {
                assoc = null;
            }
        }
    }

    /**
     * getFilePath
     *
     * @return filePath
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * getCps
     *
     * @return cps
     */
    public float getCps() {
        return cps;
    }

    /**
     * getAssoc
     *
     * @return assoc
     */
    public AstfAssociation getAssoc() {
        return assoc;
    }

    /**
     * getAstfIpGen
     *
     * @return astfIpGen
     */
    public AstfIpGen getAstfIpGen() {
        return astfIpGen;
    }

    /**
     * getPort
     *
     * @return port
     */
    public int getPort() {
        return port;
    }

    /**
     * getL7Percent
     *
     * @return l7Percent
     */
    public float getL7Percent() {
        return l7Percent;
    }

    /**
     * getServerGlobInfo
     *
     * @return serverGlobInfo
     */
    public AstfGlobalInfoPerTemplate getServerGlobInfo() {
        return serverGlobInfo;
    }

    /**
     * getClientGlobInfo
     *
     * @return clientGlobInfo
     */
    public AstfGlobalInfoPerTemplate getClientGlobInfo() {
        return clientGlobInfo;
    }

    /**
     * getLimit
     *
     * @return limit
     */
    public int getLimit() {
        return limit;
    }

    /**
     * AstfCapInfo builder
     */
    public static final class AstfCapInfoBuilder {
        private String filePath;
        private float cps;
        private AstfAssociation assoc;
        private AstfIpGen astfIpGen;
        private int port;
        private float l7Percent;
        private AstfGlobalInfoPerTemplate serverGlobInfo;
        private AstfGlobalInfoPerTemplate clientGlobInfo;
        private int limit;

        public AstfCapInfoBuilder filePath(String val) {
            filePath = val;
            return this;
        }

        public AstfCapInfoBuilder cps(float val) {
            cps = val;
            return this;
        }

        public AstfCapInfoBuilder assoc(AstfAssociation val) {
            assoc = val;
            return this;
        }

        public AstfCapInfoBuilder astfIpGen(AstfIpGen val) {
            astfIpGen = val;
            return this;
        }

        public AstfCapInfoBuilder port(int val) {
            port = val;
            return this;
        }

        public AstfCapInfoBuilder l7Percent(float val) {
            l7Percent = val;
            return this;
        }

        public AstfCapInfoBuilder serverGlobInfo(AstfGlobalInfoPerTemplate val) {
            serverGlobInfo = val;
            return this;
        }

        public AstfCapInfoBuilder clientGlobInfo(AstfGlobalInfoPerTemplate val) {
            clientGlobInfo = val;
            return this;
        }

        public AstfCapInfoBuilder limit(int val) {
            limit = val;
            return this;
        }

        public AstfCapInfo build() {
            return new AstfCapInfo(this);
        }
    }
}
