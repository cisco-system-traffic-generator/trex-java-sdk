package com.cisco.trex.stateless;

import com.cisco.trex.util.TRexClientUtil;
import com.cisco.trex.util.TRexServerMode;
import org.junit.Test;

public class TRexClientUtilTest {
    @Test
    public void getModeTest() {
        TRexServerMode mode = TRexClientUtil.getMode("trex-host", "4501");
        System.out.println(mode);
    }
}
