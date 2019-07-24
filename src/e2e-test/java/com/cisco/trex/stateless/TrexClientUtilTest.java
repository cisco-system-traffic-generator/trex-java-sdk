package com.cisco.trex.stateless;

import com.cisco.trex.util.TrexClientUtil;
import com.cisco.trex.util.TrexServerMode;
import org.junit.Test;

public class TrexClientUtilTest {
    @Test
    public void getModeTest() throws Exception {
        TrexServerMode mode = TrexClientUtil.getMode("10.76.176.8");

        System.out.println(mode);
    }
}
