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
    public Long[] values;

    private Map<String, Long> matchedNameAndValues = new HashMap<>();

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
    public Long getRxGoodPackets() {
        try {
            return matchedNameAndValues.get("rx_good_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx good packets
     */
    public Long getTxGoodPackets() {
        try {
            return matchedNameAndValues.get("tx_good_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx good bytes
     */
    public Long getRxGoodBytes() {
        try {
            return matchedNameAndValues.get("rx_good_bytes");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx good bytes
     */
    public Long getTxGoodBytes() {
        try {
            return matchedNameAndValues.get("tx_good_bytes");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx errors
     */
    public Long getRxErrors() {
        try {
            return matchedNameAndValues.get("rx_errors");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx errors
     */
    public Long getTxErrors() {
        try {
            return matchedNameAndValues.get("tx_errors");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx mbuf allocation errors
     */
    public Long getRxMbufAllocationErrors() {
        try {
            return matchedNameAndValues.get("rx_mbuf_allocation_errors");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx q0 packets
     */
    public Long getRxQ0Packets() {
        try {
            return matchedNameAndValues.get("rx_q0packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx q0 bytes
     */
    public Long getRxQ0Bytes() {
        try {
            return matchedNameAndValues.get("rx_q0bytes");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx q0 errors
     */
    public Long getRxQ0Errors() {
        try {
            return matchedNameAndValues.get("rx_q0errors");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx q1 packets
     */
    public Long getRxQ1Packets() {
        try {
            return matchedNameAndValues.get("rx_q1packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx q1 bytes
     */
    public Long getRxQ1Bytes() {
        try {
            return matchedNameAndValues.get("rx_q1bytes");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx q1 errors
     */
    public Long getRxQ1Errors() {
        try {
            return matchedNameAndValues.get("rx_q1errors");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx q0 packets
     */
    public Long getTxQ0Packets() {
        try {
            return matchedNameAndValues.get("tx_q0packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx q0 bytes
     */
    public Long getTxQ0Bytes() {
        try {
            return matchedNameAndValues.get("tx_q0bytes");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx q1 packets
     */
    public Long getTxQ1Packets() {
        try {
            return matchedNameAndValues.get("tx_q1packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx q1 bytes
     */
    public Long getTxQ1Bytes() {
        try {
            return matchedNameAndValues.get("tx_q1bytes");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx q2 packets
     */
    public Long getTxQ2Packets() {
        try {
            return matchedNameAndValues.get("tx_q2packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx q2 bytes
     */
    public Long getTxQ2Bytes() {
        try {
            return matchedNameAndValues.get("tx_q2bytes");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx q3 packets
     */
    public Long getTxQ3Packets() {
        try {
            return matchedNameAndValues.get("tx_q3packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx q3 bytes
     */
    public Long getTxQ3Bytes() {
        try {
            return matchedNameAndValues.get("tx_q3bytes");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx unicast packets
     */
    public Long getRxUnicastPackets() {
        try {
            return matchedNameAndValues.get("rx_unicast_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx multicast packets
     */
    public Long getRxMulticastPackets() {
        try {
            return matchedNameAndValues.get("rx_multicast_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx broadcast packets
     */
    public Long getRxBroadcastPackets() {
        try {
            return matchedNameAndValues.get("rx_broadcast_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx dropped
     */
    public Long getRxDropped() {
        try {
            return matchedNameAndValues.get("rx_dropped");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx unknown protocol packets
     */
    public Long getRxUnknownProtocolPackets() {
        try {
            return matchedNameAndValues.get("rx_unknown_protocol_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx unicast packets
     */
    public Long getTxUnicastPackets() {
        try {
            return matchedNameAndValues.get("tx_unicast_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx multicast packets
     */
    public Long getTxMulticastPackets() {
        try {
            return matchedNameAndValues.get("tx_multicast_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx broadcast packets
     */
    public Long getTxBroadcastPackets() {
        try {
            return matchedNameAndValues.get("tx_broadcast_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx dropped
     */
    public Long getTxDropped() {
        try {
            return matchedNameAndValues.get("tx_dropped");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx link down dropped
     */
    public Long getTxLinkDownDropped() {
        try {
            return matchedNameAndValues.get("tx_link_down_dropped");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx crc errors
     */
    public Long getRxCrcErrors() {
        try {
            return matchedNameAndValues.get("rx_crc_errors");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx align errors
     */
    public Long getRxAlignErrors() {
        try {
            return matchedNameAndValues.get("rx_align_errors");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx illegal byte errors
     */
    public Long getRxIllegalByteErrors() {
        try {
            return matchedNameAndValues.get("rx_illegal_byte_errors");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx error bytes
     */
    public Long getRxErrorBytes() {
        try {
            return matchedNameAndValues.get("rx_error_bytes");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return mac local errors
     */
    public Long getMacLocalErrors() {
        try {
            return matchedNameAndValues.get("mac_local_errors");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return mac remote errors
     */
    public Long getMacRemoteErrors() {
        try {
            return matchedNameAndValues.get("mac_remote_errors");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx length errors
     */
    public Long getRxLengthErrors() {
        try {
            return matchedNameAndValues.get("rx_length_errors");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx symbol errors
     */
    public Long getRxSymbolErrors() {
        try {
            return matchedNameAndValues.get("rx_symbol_errors");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx missed errors
     */
    public Long getRxMissedErrors() {
        try {
            return matchedNameAndValues.get("rx_missed_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx single collision packets
     */
    public Long getTxSingleCollisionPackets() {
        try {
            return matchedNameAndValues.get("tx_single_collision_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx multiple collision packets
     */
    public Long getTxMultipleCollisionPackets() {
        try {
            return matchedNameAndValues.get("tx_multiple_collision_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx excessive collision packets
     */
    public Long getTxExcessiveCollisionPackets() {
        try {
            return matchedNameAndValues.get("tx_excessive_collision_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx late collisions
     */
    public Long getTxLateCollisions() {
        try {
            return matchedNameAndValues.get("tx_late_collisions");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx total collisions
     */
    public Long getTxTotalCollisions() {
        try {
            return matchedNameAndValues.get("tx_total_collisions");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx deferred packets
     */
    public Long getTxDeferredPackets() {
        try {
            return matchedNameAndValues.get("tx_deferred_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx no carrier sense packets
     */
    public Long getTxNoCarrierSensePackets() {
        try {
            return matchedNameAndValues.get("tx_no_carrier_sense_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx carrier ext errors
     */
    public Long getTxCarrierExtErrors() {
        try {
            return matchedNameAndValues.get("rx_carrier_ext_errors");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx xon packets
     */
    public Long getTxXonPackets() {
        try {
            return matchedNameAndValues.get("tx_xon_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx xon packets
     */
    public Long getRxXonPackets() {
        try {
            return matchedNameAndValues.get("rx_xon_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx xoff packets
     */
    public Long getTxXoffPackets() {
        try {
            return matchedNameAndValues.get("tx_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx xoff packets
     */
    public Long getRxXoffPackets() {
        try {
            return matchedNameAndValues.get("rx_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx size 64 packets
     */
    public Long getRxSize64Packets() {
        try {
            return matchedNameAndValues.get("rx_size_64_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx size 65-127 packets
     */
    public Long getRxSize65to127Packets() {
        try {
            return matchedNameAndValues.get("rx_size_65_to_127_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx size 128-255 packets
     */
    public Long getRxSize128to255Packets() {
        try {
            return matchedNameAndValues.get("rx_size_128_to_255_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx size 256-511 packets
     */
    public Long getRxSize256to511Packets() {
        try {
            return matchedNameAndValues.get("rx_size_256_to_511_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx size 512-1023 packets
     */
    public Long getRxSize512to1023Packets() {
        try {
            return matchedNameAndValues.get("rx_size_512_to_1023_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx size 1024-1522 packets
     */
    public Long getRxSize1024to1522Packets() {
        try {
            return matchedNameAndValues.get("rx_size_1024_to_1522_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx size 1523-max packets
     */
    public Long getRxSize1523toMaxPackets() {
        try {
            return matchedNameAndValues.get("rx_size_1523_to_max_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx undersized errors
     */
    public Long getRxUndersizedErrors() {
        try {
            return matchedNameAndValues.get("rx_undersized_errors");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx oversized errors
     */
    public Long getRxOversizedErrors() {
        try {
            return matchedNameAndValues.get("rx_oversize_errors");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx mac short dropped
     */
    public Long getRxMacShortDropped() {
        try {
            return matchedNameAndValues.get("rx_mac_short_dropped");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx fragmented errors
     */
    public Long getRxFragmentedErrors() {
        try {
            return matchedNameAndValues.get("rx_fragmented_errors");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx jabber errors
     */
    public Long getRxJabberErrors() {
        try {
            return matchedNameAndValues.get("rx_jabber_errors");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx management packets
     */
    public Long getRxManagementPackets() {
        try {
            return matchedNameAndValues.get("rx_management_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx management dropped
     */
    public Long getRxManagementDropped() {
        try {
            return matchedNameAndValues.get("rx_management_dropped");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx management packets
     */
    public Long getTxManagementPackets() {
        try {
            return matchedNameAndValues.get("tx_management_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx total packets
     */
    public Long getRxTotalPackets() {
        try {
            return matchedNameAndValues.get("rx_total_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx total packets
     */
    public Long getTxTotalPackets() {
        try {
            return matchedNameAndValues.get("tx_total_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx total bytes
     */
    public Long getRxTotalbytes() {
        try {
            return matchedNameAndValues.get("rx_total_bytes");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx total bytes
     */
    public Long getTxTotalbytes() {
        try {
            return matchedNameAndValues.get("tx_total_bytes");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx size 64 packets
     */
    public Long getTxSize64Packets() {
        try {
            return matchedNameAndValues.get("tx_size_64_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx size 65-127 packets
     */
    public Long getTxSize65to127Packets() {
        try {
            return matchedNameAndValues.get("tx_size_65_to_127_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx size 128-255 packets
     */
    public Long getTxSize128to255Packets() {
        try {
            return matchedNameAndValues.get("tx_size_128_to_255_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx size 256-511 packets
     */
    public Long getTxSize256to511Packets() {
        try {
            return matchedNameAndValues.get("tx_size_256_to_511_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx size 512-1023 packets
     */
    public Long getTxSize512to1023Packets() {
        try {
            return matchedNameAndValues.get("tx_size_512_to_1023_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx size 1024-1522 packets
     */
    public Long getTxSize1024to1522Packets() {
        try {
            return matchedNameAndValues.get("tx_size_1024_to_1522_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx size 1523-max packets
     */
    public Long getTxSize1523toMaxPackets() {
        try {
            return matchedNameAndValues.get("tx_size_1523_to_max_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx tso packets
     */
    public Long getTxTsoPackets() {
        try {
            return matchedNameAndValues.get("tx_tso_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx tso errors
     */
    public Long getTxTsoErrors() {
        try {
            return matchedNameAndValues.get("tx_tso_errors");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx sent to host packets
     */
    public Long getRxSentToHostPackets() {
        try {
            return matchedNameAndValues.get("rx_sent_to_host_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx sent to host packets
     */
    public Long getTxSentToHostPackets() {
        try {
            return matchedNameAndValues.get("tx_sent_to_host_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx code violation packets
     */
    public Long getRxCodeViolationPackets() {
        try {
            return matchedNameAndValues.get("rx_code_violation_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return interrupt assert count
     */
    public Long getInterruptAssertCount() {
        try {
            return matchedNameAndValues.get("interrupt_assert_count");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx flow director atr match packets
     */
    public Long getRxFlowDirectorAtrMatchPackets() {
        try {
            return matchedNameAndValues.get("rx_flow_director_atr_match_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx flow director sb match packets
     */
    public Long getRxFlowDirectorSbMatchPackets() {
        try {
            return matchedNameAndValues.get("rx_flow_director_sb_match_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx low power idle status
     */
    public Long getTxLowPowerIdleStatus() {
        try {
            return matchedNameAndValues.get("tx_low_power_idle_status");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx low power idle status
     */
    public Long getRxLowPowerIdleStatus() {
        try {
            return matchedNameAndValues.get("rx_low_power_idle_status");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx low power idle count
     */
    public Long getTxLowPowerIdleCount() {
        try {
            return matchedNameAndValues.get("tx_low_power_idle_count");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx low power idle count
     */
    public Long getRxLowPowerIdleCount() {
        try {
            return matchedNameAndValues.get("rx_low_power_idle_count");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx priority0 xon packets
     */
    public Long getRxPriority0XonPackets() {
        try {
            return matchedNameAndValues.get("rx_priority0_xon_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx priority1 xon packets
     */
    public Long getRxPriority1XonPackets() {
        try {
            return matchedNameAndValues.get("rx_priority1_xon_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx priority2 xon packets
     */
    public Long getRxPriority2XonPackets() {
        try {
            return matchedNameAndValues.get("rx_priority2_xon_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx priority3 xon packets
     */
    public Long getRxPriority3XonPackets() {
        try {
            return matchedNameAndValues.get("rx_priority3_xon_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx priority4 xon packets
     */
    public Long getRxPriority4XonPackets() {
        try {
            return matchedNameAndValues.get("rx_priority4_xon_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx priority5 xon packets
     */
    public Long getRxPriority5XonPackets() {
        try {
            return matchedNameAndValues.get("rx_priority5_xon_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx priority6 xon packets
     */
    public Long getRxPriority6XonPackets() {
        try {
            return matchedNameAndValues.get("rx_priority6_xon_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx priority7 xon packets
     */
    public Long getRxPriority7XonPackets() {
        try {
            return matchedNameAndValues.get("rx_priority7_xon_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx priority0 xoff packets
     */
    public Long getRxPriority0XoffPackets() {
        try {
            return matchedNameAndValues.get("rx_priority0_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx priority1 xoff packets
     */
    public Long getRxPriority1XoffPackets() {
        try {
            return matchedNameAndValues.get("rx_priority1_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx priority2 xoff packets
     */
    public Long getRxPriority2XoffPackets() {
        try {
            return matchedNameAndValues.get("rx_priority2_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx priority3 xoff packets
     */
    public Long getRxPriority3XoffPackets() {
        try {
            return matchedNameAndValues.get("rx_priority3_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx priority4 xoff packets
     */
    public Long getRxPriority4XoffPackets() {
        try {
            return matchedNameAndValues.get("rx_priority4_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx priority5 xoff packets
     */
    public Long getRxPriority5XoffPackets() {
        try {
            return matchedNameAndValues.get("rx_priority5_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx priority6 xoff packets
     */
    public Long getRxPriority6XoffPackets() {
        try {
            return matchedNameAndValues.get("rx_priority6_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return rx priority7 xoff packets
     */
    public Long getRxPriority7XoffPackets() {
        try {
            return matchedNameAndValues.get("rx_priority7_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx priority0 xon packets
     */
    public Long getTxPriority0XonPackets() {
        try {
            return matchedNameAndValues.get("tx_priority0_xon_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx priority1 xon packets
     */
    public Long getTxPriority1XonPackets() {
        try {
            return matchedNameAndValues.get("tx_priority1_xon_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx priority2 xon packets
     */
    public Long getTxPriority2XonPackets() {
        try {
            return matchedNameAndValues.get("tx_priority2_xon_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx priority3 xon packets
     */
    public Long getTxPriority3XonPackets() {
        try {
            return matchedNameAndValues.get("tx_priority3_xon_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx priority4 xon packets
     */
    public Long getTxPriority4XonPackets() {
        try {
            return matchedNameAndValues.get("tx_priority4_xon_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx priority5 xon packets
     */
    public Long getTxPriority5XonPackets() {
        try {
            return matchedNameAndValues.get("tx_priority5_xon_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return Tx priority6 xon packets
     */
    public Long getTxPriority6XonPackets() {
        try {
            return matchedNameAndValues.get("tx_priority6_xon_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return Tx priority7 xon packets
     */
    public Long getTxPriority7XonPackets() {
        try {
            return matchedNameAndValues.get("tx_priority7_xon_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return Tx priority0 xoff packets
     */
    public Long getTxPriority0XoffPackets() {
        try {
            return matchedNameAndValues.get("tx_priority0_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx priority1 xoff packets
     */
    public Long getTxPriority1XoffPackets() {
        try {
            return matchedNameAndValues.get("tx_priority1_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx priority2 xoff packets
     */
    public Long getTxPriority2XoffPackets() {
        try {
            return matchedNameAndValues.get("tx_priority2_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx priority3 xoff packets
     */
    public Long getTxPriority3XoffPackets() {
        try {
            return matchedNameAndValues.get("tx_priority3_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx priority4 xoff packets
     */
    public Long getTxPriority4XoffPackets() {
        try {
            return matchedNameAndValues.get("tx_priority4_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx priority5 xoff packets
     */
    public Long getTxPriority5XoffPackets() {
        try {
            return matchedNameAndValues.get("tx_priority5_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx priority6 xoff packets
     */
    public Long getTxPriority6XoffPackets() {
        try {
            return matchedNameAndValues.get("tx_priority6_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx priority7 xoff packets
     */
    public Long getTxPriority7XoffPackets() {
        try {
            return matchedNameAndValues.get("tx_priority7_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx priority0 xon to xoff packets
     */
    public Long getTxPriority0XonToXoffPackets() {
        try {
            return matchedNameAndValues.get("tx_priority0_xon_to_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx priority1 xon to xoff packets
     */
    public Long getTxPriority1XonToXoffPackets() {
        try {
            return matchedNameAndValues.get("tx_priority1_xon_to_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx priority2 xon to xoff packets
     */
    public Long getTxPriority2XonToXoffPackets() {
        try {
            return matchedNameAndValues.get("tx_priority2_xon_to_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx priority3 xon to xoff packets
     */
    public Long getTxPriority3XonToXoffPackets() {
        try {
            return matchedNameAndValues.get("tx_priority3_xon_to_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx priority4 xon to xoff packets
     */
    public Long getTxPriority4XonToXoffPackets() {
        try {
            return matchedNameAndValues.get("tx_priority4_xon_to_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx priority5 xon to xoff packets
     */
    public Long getTxPriority5XonToXoffPackets() {
        try {
            return matchedNameAndValues.get("tx_priority5_xon_to_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx priority6 xon to xoff packets
     */
    public Long getTxPriority6XonToXoffPackets() {
        try {
            return matchedNameAndValues.get("tx_priority6_xon_to_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }

    /**
     * @return tx priority7 xon to xoff packets
     */
    public Long getTxPriority7XonToXoffPackets() {
        try {
            return matchedNameAndValues.get("tx_priority7_xon_to_xoff_packets");
        } catch (NullPointerException e) {
            throw new IllegalStateException(COUNTER_NOT_SUPPORTED_ON_THIS_SYSTEM, e);
        }
    }
}
