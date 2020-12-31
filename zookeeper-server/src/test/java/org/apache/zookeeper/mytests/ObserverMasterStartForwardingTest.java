package org.apache.zookeeper.mytests;


import org.apache.zookeeper.common.X509Exception;
import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.quorum.LearnerHandler;
import org.apache.zookeeper.server.quorum.ObserverMaster;
import org.apache.zookeeper.server.quorum.QuorumPacket;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.apache.zookeeper.server.quorum.ZabUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@RunWith(value = Parameterized.class)
public class ObserverMasterStartForwardingTest {

    private static final int LAST_PKT_ID = 50;
    private static final int FIRST_PKT_ID = 25;

    //Observer class to test
    private ObserverMaster obsM;

    //arguments
    private LearnerHandler lh;
    private long lastSeenZxid;
    private long expResult;
    private boolean queuePkt;

    public ObserverMasterStartForwardingTest(long expResult, LearnerHandler lh, long lastSeenZxid, boolean queuePkt) {
        //the arguments of the constructor are not used in the method we are testing
        //we can leave them as null
        obsM = new ObserverMaster(null, null, 80);

        this.expResult = expResult;
        this.lh = lh;
        this.lastSeenZxid = lastSeenZxid;
        this.queuePkt = queuePkt;

    }

    @Before
    public void setUpQueue() {
        //add some dummy packets in the queue if necessary
        if (queuePkt) {
            for (int i = FIRST_PKT_ID; i < LAST_PKT_ID; i++) {
                QuorumPacket qp = new QuorumPacket(0, i, "dummy".getBytes(), null);
                obsM.cacheCommittedPacket(qp);
            }
        }

    }

    @Parameterized.Parameters
    public static Collection<?> getTestParameters() throws NoSuchFieldException, X509Exception, IllegalAccessException, IOException {
        //function signature
        //long startForwarding(LearnerHandler learnerHandler, long lastSeenZxid)

        LearnerHandler validLh = Utils.createValidLearner();

        return Arrays.asList(new Object[][]{

                //invalid configuration - learner is null or invalid
                {-2, null, 30, true},

                //invalid configuration - learner is too far behind
                {-1, validLh, 10, true},

                //method does nothing with empty queue, test only one configuration
                {0, validLh, 30, false},

                //valid configurations
                {0, validLh, 30, true},
                {0, validLh, FIRST_PKT_ID-1, true},
                {0, validLh, FIRST_PKT_ID+10, true},
        });
    }

    @Test
    public void startForwardingTest() {

        long actualResult;

        try {
            actualResult = obsM.startForwarding(lh, lastSeenZxid);
        } catch (NullPointerException e) {
            Assert.assertNull(lh);
            return;
        }

        Assert.assertEquals(actualResult, expResult);

    }

}
