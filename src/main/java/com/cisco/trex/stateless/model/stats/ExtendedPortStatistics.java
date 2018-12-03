package com.cisco.trex.stateless.model.stats;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * extended statistics for trex port
 */
public class ExtendedPortStatistics {

    private static final String COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM = "Counter not supported on this system";

    /**
     * Json Property
     */
    @JsonProperty("xstats_values")
    public int[] values;

    private Map<String, Integer> matchedNameAndValues = new HashMap<>();

    /**
     * @param names
     * @return this
     */
    public ExtendedPortStatistics setValueNames(XstatsNames names) {
        int i = 0;
        String[] allNames = names.getAllNames();
        if (values.length != allNames.length) {
            throw new IllegalStateException("Statistic names and values does not match");
        }
        for (String name : allNames) {
            matchedNameAndValues.put(name, values[i++]);
        }
        return this;
    }

    /**
     * @return all values as string
     */
    public String getAllValues() {
        return Arrays.toString(values).replaceAll("\\[|\\]", "");
    }

    /**
     * @return rx good packets
     */
    public int getRxGoodPackets() {
        try {
            return matchedNameAndValues.get("rx_good_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx good packets
     */
    public int getTxGoodPackets() {
        try {
            return matchedNameAndValues.get("tx_good_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx good bytes
     */
    public int getRxGoodBytes() {
        try {
            return matchedNameAndValues.get("rx_good_bytes");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx good bytes
     */
    public int getTxGoodBytes() {
        try {
            return matchedNameAndValues.get("tx_good_bytes");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx errors
     */
    public int getRxErrors() {
        try {
            return matchedNameAndValues.get("rx_errors");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx errors
     */
    public int getTxErrors() {
        try {
            return matchedNameAndValues.get("tx_errors");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx mbuf allocation errors
     */
    public int getRxMbufAllocationErrors() {
        try {
            return matchedNameAndValues.get("rx_mbuf_allocation_errors");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx q0 packets
     */
    public int getRxQ0Packets() {
        try {
            return matchedNameAndValues.get("rx_q0packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx q0 bytes
     */
    public int getRxQ0Bytes() {
        try {
            return matchedNameAndValues.get("rx_q0bytes");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx q0 errors
     */
    public int getRxQ0Errors() {
        try {
            return matchedNameAndValues.get("rx_q0errors");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx q1 packets
     */
    public int getRxQ1Packets() {
        try {
            return matchedNameAndValues.get("rx_q1packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx q1 bytes
     */
    public int getRxQ1Bytes() {
        try {
            return matchedNameAndValues.get("rx_q1bytes");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx q1 errors
     */
    public int getRxQ1Errors() {
        try {
            return matchedNameAndValues.get("rx_q1errors");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx q0 packets
     */
    public int getTxQ0Packets() {
        try {
            return matchedNameAndValues.get("tx_q0packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx q0 bytes
     */
    public int getTxQ0Bytes() {
        try {
            return matchedNameAndValues.get("tx_q0bytes");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx q1 packets
     */
    public int getTxQ1Packets() {
        try {
            return matchedNameAndValues.get("tx_q1packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx q1 bytes
     */
    public int getTxQ1Bytes() {
        try {
            return matchedNameAndValues.get("tx_q1bytes");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx q2 packets
     */
    public int getTxQ2Packets() {
        try {
            return matchedNameAndValues.get("tx_q2packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx q2 bytes
     */
    public int getTxQ2Bytes() {
        try {
            return matchedNameAndValues.get("tx_q2bytes");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx q3 packets
     */
    public int getTxQ3Packets() {
        try {
            return matchedNameAndValues.get("tx_q3packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx q3 bytes
     */
    public int getTxQ3Bytes() {
        try {
            return matchedNameAndValues.get("tx_q3bytes");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx unicast packets
     */
    public int getRxUnicastPackets() {
        try {
            return matchedNameAndValues.get("rx_unicast_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx multicast packets
     */
    public int getRxMulticastPackets() {
        try {
            return matchedNameAndValues.get("rx_multicast_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx broadcast packets
     */
    public int getRxBroadcastPackets() {
        try {
            return matchedNameAndValues.get("rx_broadcast_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx dropped
     */
    public int getRxDropped() {
        try {
            return matchedNameAndValues.get("rx_dropped");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx unknown protocol packets
     */
    public int getRxUnknownProtocolPackets() {
        try {
            return matchedNameAndValues.get("rx_unknown_protocol_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx unicast packets
     */
    public int getTxUnicastPackets() {
        try {
            return matchedNameAndValues.get("tx_unicast_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx multicast packets
     */
    public int getTxMulticastPackets() {
        try {
            return matchedNameAndValues.get("tx_multicast_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx broadcast packets
     */
    public int getTxBroadcastPackets() {
        try {
            return matchedNameAndValues.get("tx_broadcast_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx dropped
     */
    public int getTxDropped() {
        try {
            return matchedNameAndValues.get("tx_dropped");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx link down dropped
     */
    public int getTxLinkDownDropped() {
        try {
            return matchedNameAndValues.get("tx_link_down_dropped");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx crc errors
     */
    public int getRxCrcErrors() {
        try {
            return matchedNameAndValues.get("rx_crc_errors");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx align errors
     */
    public int getRxAlignErrors() {
        try {
            return matchedNameAndValues.get("rx_align_errors");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx illegal byte errors
     */
    public int getRxIllegalByteErrors() {
        try {
            return matchedNameAndValues.get("rx_illegal_byte_errors");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx error bytes
     */
    public int getRxErrorBytes() {
        try {
            return matchedNameAndValues.get("rx_error_bytes");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return mac local errors
     */
    public int getMacLocalErrors() {
        try {
            return matchedNameAndValues.get("mac_local_errors");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return mac remote errors
     */
    public int getMacRemoteErrors() {
        try {
            return matchedNameAndValues.get("mac_remote_errors");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx length errors
     */
    public int getRxLengthErrors() {
        try {
            return matchedNameAndValues.get("rx_length_errors");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx symbol errors
     */
    public int getRxSymbolErrors() {
        try {
            return matchedNameAndValues.get("rx_symbol_errors");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx missed errors
     */
    public int getRxMissedErrors() {
        try {
            return matchedNameAndValues.get("rx_missed_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx single collision packets
     */
    public int getTxSingleCollisionPackets() {
        try {
            return matchedNameAndValues.get("tx_single_collision_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx multiple collision packets
     */
    public int getTxMultipleCollisionPackets() {
        try {
            return matchedNameAndValues.get("tx_multiple_collision_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx excessive collision packets
     */
    public int getTxExcessiveCollisionPackets() {
        try {
            return matchedNameAndValues.get("tx_excessive_collision_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx late collisions
     */
    public int getTxLateCollisions() {
        try {
            return matchedNameAndValues.get("tx_late_collisions");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx total collisions
     */
    public int getTxTotalCollisions() {
        try {
            return matchedNameAndValues.get("tx_total_collisions");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx deferred packets
     */
    public int getTxDeferredPackets() {
        try {
            return matchedNameAndValues.get("tx_deferred_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx no carrier sense packets
     */
    public int getTxNoCarrierSensePackets() {
        try {
            return matchedNameAndValues.get("tx_no_carrier_sense_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx carrier ext errors
     */
    public int getTxCarrierExtErrors() {
        try {
            return matchedNameAndValues.get("rx_carrier_ext_errors");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx xon packets
     */
    public int getTxXonPackets() {
        try {
            return matchedNameAndValues.get("tx_xon_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx xon packets
     */
    public int getRxXonPackets() {
        try {
            return matchedNameAndValues.get("rx_xon_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx xoff packets
     */
    public int getTxXoffPackets() {
        try {
            return matchedNameAndValues.get("tx_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx xoff packets
     */
    public int getRxXoffPackets() {
        try {
            return matchedNameAndValues.get("rx_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx size 64 packets
     */
    public int getRxSize64Packets() {
        try {
            return matchedNameAndValues.get("rx_size_64_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx size 65-127 packets
     */
    public int getRxSize65to127Packets() {
        try {
            return matchedNameAndValues.get("rx_size_65_to_127_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx size 128-255 packets
     */
    public int getRxSize128to255Packets() {
        try {
            return matchedNameAndValues.get("rx_size_128_to_255_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx size 256-511 packets
     */
    public int getRxSize256to511Packets() {
        try {
            return matchedNameAndValues.get("rx_size_256_to_511_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx size 512-1023 packets
     */
    public int getRxSize512to1023Packets() {
        try {
            return matchedNameAndValues.get("rx_size_512_to_1023_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx size 1024-1522 packets
     */
    public int getRxSize1024to1522Packets() {
        try {
            return matchedNameAndValues.get("rx_size_1024_to_1522_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx size 1523-max packets
     */
    public int getRxSize1523toMaxPackets() {
        try {
            return matchedNameAndValues.get("rx_size_1523_to_max_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx undersized errors
     */
    public int getRxUndersizedErrors() {
        try {
            return matchedNameAndValues.get("rx_undersized_errors");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx oversized errors
     */
    public int getRxOversizedErrors() {
        try {
            return matchedNameAndValues.get("rx_oversize_errors");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx mac short dropped
     */
    public int getRxMacShortDropped() {
        try {
            return matchedNameAndValues.get("rx_mac_short_dropped");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx fragmented errors
     */
    public int getRxFragmentedErrors() {
        try {
            return matchedNameAndValues.get("rx_fragmented_errors");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx jabber errors
     */
    public int getRxJabberErrors() {
        try {
            return matchedNameAndValues.get("rx_jabber_errors");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx management packets
     */
    public int getRxManagementPackets() {
        try {
            return matchedNameAndValues.get("rx_management_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx management dropped
     */
    public int getRxManagementDropped() {
        try {
            return matchedNameAndValues.get("rx_management_dropped");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx management packets
     */
    public int getTxManagementPackets() {
        try {
            return matchedNameAndValues.get("tx_management_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx total packets
     */
    public int getRxTotalPackets() {
        try {
            return matchedNameAndValues.get("rx_total_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx total packets
     */
    public int getTxTotalPackets() {
        try {
            return matchedNameAndValues.get("tx_total_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx total bytes
     */
    public int getRxTotalbytes() {
        try {
            return matchedNameAndValues.get("rx_total_bytes");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx total bytes
     */
    public int getTxTotalbytes() {
        try {
            return matchedNameAndValues.get("tx_total_bytes");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx size 64 packets
     */
    public int getTxSize64Packets() {
        try {
            return matchedNameAndValues.get("tx_size_64_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx size 65-127 packets
     */
    public int getTxSize65to127Packets() {
        try {
            return matchedNameAndValues.get("tx_size_65_to_127_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx size 128-255 packets
     */
    public int getTxSize128to255Packets() {
        try {
            return matchedNameAndValues.get("tx_size_128_to_255_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx size 256-511 packets
     */
    public int getTxSize256to511Packets() {
        try {
            return matchedNameAndValues.get("tx_size_256_to_511_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx size 512-1023 packets
     */
    public int getTxSize512to1023Packets() {
        try {
            return matchedNameAndValues.get("tx_size_512_to_1023_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx size 1024-1522 packets
     */
    public int getTxSize1024to1522Packets() {
        try {
            return matchedNameAndValues.get("tx_size_1024_to_1522_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx size 1523-max packets
     */
    public int getTxSize1523toMaxPackets() {
        try {
            return matchedNameAndValues.get("tx_size_1523_to_max_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx tso packets
     */
    public int getTxTsoPackets() {
        try {
            return matchedNameAndValues.get("tx_tso_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx tso errors
     */
    public int getTxTsoErrors() {
        try {
            return matchedNameAndValues.get("tx_tso_errors");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx sent to host packets
     */
    public int getRxSentToHostPackets() {
        try {
            return matchedNameAndValues.get("rx_sent_to_host_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx sent to host packets
     */
    public int getTxSentToHostPackets() {
        try {
            return matchedNameAndValues.get("tx_sent_to_host_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx code violation packets
     */
    public int getRxCodeViolationPackets() {
        try {
            return matchedNameAndValues.get("rx_code_violation_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return interrupt assert count
     */
    public int getInterruptAssertCount() {
        try {
            return matchedNameAndValues.get("interrupt_assert_count");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx flow director atr match packets
     */
    public int getRxFlowDirectorAtrMatchPackets() {
        try {
            return matchedNameAndValues.get("rx_flow_director_atr_match_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx flow director sb match packets
     */
    public int getRxFlowDirectorSbMatchPackets() {
        try {
            return matchedNameAndValues.get("rx_flow_director_sb_match_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx low power idle status
     */
    public int getTxLowPowerIdleStatus() {
        try {
            return matchedNameAndValues.get("tx_low_power_idle_status");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx low power idle status
     */
    public int getRxLowPowerIdleStatus() {
        try {
            return matchedNameAndValues.get("rx_low_power_idle_status");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx low power idle count
     */
    public int getTxLowPowerIdleCount() {
        try {
            return matchedNameAndValues.get("tx_low_power_idle_count");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx low power idle count
     */
    public int getRxLowPowerIdleCount() {
        try {
            return matchedNameAndValues.get("rx_low_power_idle_count");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx priority0 xon packets
     */
    public int getRxPriority0XonPackets() {
        try {
            return matchedNameAndValues.get("rx_priority0_xon_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx priority1 xon packets
     */
    public int getRxPriority1XonPackets() {
        try {
            return matchedNameAndValues.get("rx_priority1_xon_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx priority2 xon packets
     */
    public int getRxPriority2XonPackets() {
        try {
            return matchedNameAndValues.get("rx_priority2_xon_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx priority3 xon packets
     */
    public int getRxPriority3XonPackets() {
        try {
            return matchedNameAndValues.get("rx_priority3_xon_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx priority4 xon packets
     */
    public int getRxPriority4XonPackets() {
        try {
            return matchedNameAndValues.get("rx_priority4_xon_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx priority5 xon packets
     */
    public int getRxPriority5XonPackets() {
        try {
            return matchedNameAndValues.get("rx_priority5_xon_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx priority6 xon packets
     */
    public int getRxPriority6XonPackets() {
        try {
            return matchedNameAndValues.get("rx_priority6_xon_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx priority7 xon packets
     */
    public int getRxPriority7XonPackets() {
        try {
            return matchedNameAndValues.get("rx_priority7_xon_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx priority0 xoff packets
     */
    public int getRxPriority0XoffPackets() {
        try {
            return matchedNameAndValues.get("rx_priority0_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx priority1 xoff packets
     */
    public int getRxPriority1XoffPackets() {
        try {
            return matchedNameAndValues.get("rx_priority1_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx priority2 xoff packets
     */
    public int getRxPriority2XoffPackets() {
        try {
            return matchedNameAndValues.get("rx_priority2_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx priority3 xoff packets
     */
    public int getRxPriority3XoffPackets() {
        try {
            return matchedNameAndValues.get("rx_priority3_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx priority4 xoff packets
     */
    public int getRxPriority4XoffPackets() {
        try {
            return matchedNameAndValues.get("rx_priority4_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx priority5 xoff packets
     */
    public int getRxPriority5XoffPackets() {
        try {
            return matchedNameAndValues.get("rx_priority5_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx priority6 xoff packets
     */
    public int getRxPriority6XoffPackets() {
        try {
            return matchedNameAndValues.get("rx_priority6_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx priority7 xoff packets
     */
    public int getRxPriority7XoffPackets() {
        try {
            return matchedNameAndValues.get("rx_priority7_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx priority0 xon packets
     */
    public int getTxPriority0XonPackets() {
        try {
            return matchedNameAndValues.get("tx_priority0_xon_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx priority1 xon packets
     */
    public int getTxPriority1XonPackets() {
        try {
            return matchedNameAndValues.get("tx_priority1_xon_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx priority2 xon packets
     */
    public int getTxPriority2XonPackets() {
        try {
            return matchedNameAndValues.get("tx_priority2_xon_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx priority3 xon packets
     */
    public int getTxPriority3XonPackets() {
        try {
            return matchedNameAndValues.get("tx_priority3_xon_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx priority4 xon packets
     */
    public int getTxPriority4XonPackets() {
        try {
            return matchedNameAndValues.get("tx_priority4_xon_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx priority5 xon packets
     */
    public int getTxPriority5XonPackets() {
        try {
            return matchedNameAndValues.get("tx_priority5_xon_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return Tx priority6 xon packets
     */
    public int getTxPriority6XonPackets() {
        try {
            return matchedNameAndValues.get("tx_priority6_xon_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return Tx priority7 xon packets
     */
    public int getTxPriority7XonPackets() {
        try {
            return matchedNameAndValues.get("tx_priority7_xon_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return Tx priority0 xoff packets
     */
    public int getTxPriority0XoffPackets() {
        try {
            return matchedNameAndValues.get("tx_priority0_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx priority1 xoff packets
     */
    public int getTxPriority1XoffPackets() {
        try {
            return matchedNameAndValues.get("tx_priority1_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx priority2 xoff packets
     */
    public int getTxPriority2XoffPackets() {
        try {
            return matchedNameAndValues.get("tx_priority2_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx priority3 xoff packets
     */
    public int getTxPriority3XoffPackets() {
        try {
            return matchedNameAndValues.get("tx_priority3_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx priority4 xoff packets
     */
    public int getTxPriority4XoffPackets() {
        try {
            return matchedNameAndValues.get("tx_priority4_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx priority5 xoff packets
     */
    public int getTxPriority5XoffPackets() {
        try {
            return matchedNameAndValues.get("tx_priority5_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx priority6 xoff packets
     */
    public int getTxPriority6XoffPackets() {
        try {
            return matchedNameAndValues.get("tx_priority6_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx priority7 xoff packets
     */
    public int getTxPriority7XoffPackets() {
        try {
            return matchedNameAndValues.get("tx_priority7_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx priority0 xon to xoff packets
     */
    public int getTxPriority0XonToXoffPackets() {
        try {
            return matchedNameAndValues.get("tx_priority0_xon_to_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx priority1 xon to xoff packets
     */
    public int getTxPriority1XonToXoffPackets() {
        try {
            return matchedNameAndValues.get("tx_priority1_xon_to_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx priority2 xon to xoff packets
     */
    public int getTxPriority2XonToXoffPackets() {
        try {
            return matchedNameAndValues.get("tx_priority2_xon_to_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx priority3 xon to xoff packets
     */
    public int getTxPriority3XonToXoffPackets() {
        try {
            return matchedNameAndValues.get("tx_priority3_xon_to_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx priority4 xon to xoff packets
     */
    public int getTxPriority4XonToXoffPackets() {
        try {
            return matchedNameAndValues.get("tx_priority4_xon_to_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx priority5 xon to xoff packets
     */
    public int getTxPriority5XonToXoffPackets() {
        try {
            return matchedNameAndValues.get("tx_priority5_xon_to_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx priority6 xon to xoff packets
     */
    public int getTxPriority6XonToXoffPackets() {
        try {
            return matchedNameAndValues.get("tx_priority6_xon_to_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx priority7 xon to xoff packets
     */
    public int getTxPriority7XonToXoffPackets() {
        try {
            return matchedNameAndValues.get("tx_priority7_xon_to_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }
}
