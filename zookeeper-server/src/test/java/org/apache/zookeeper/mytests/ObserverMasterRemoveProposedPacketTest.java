package org.apache.zookeeper.mytests;


import org.apache.zookeeper.common.X509Exception;
import org.apache.zookeeper.server.quorum.LearnerHandler;
import org.apache.zookeeper.server.quorum.ObserverMaster;
import org.apache.zookeeper.server.quorum.QuorumPacket;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

@RunWith(value = Parameterized.class)
public class ObserverMasterRemoveProposedPacketTest {

    private static final int LAST_PKT_ID = 50;
    private static final int FIRST_PKT_ID = 25;

    //packet to remove
    private static QuorumPacket remQp;

    //Observer class to test
    private ObserverMaster obsM;

    //arguments
    private long zxid;
    private boolean fillQueue;
    private boolean expResult;

    public ObserverMasterRemoveProposedPacketTest(boolean expResult, long zxid, boolean fillQueue) {
        //the arguments of the constructor are not used in the method we are testing
        //we can leave them as null
        obsM = new ObserverMaster(null, null, 80);

        this.zxid = zxid;
        this.fillQueue = fillQueue;
        this.expResult = expResult;
    }

    @Before
    public void setUpQueue() {
        //add some dummy packets in the queue if necessary
        if (fillQueue) {
            remQp = new QuorumPacket(0, FIRST_PKT_ID, "removed".getBytes(), null);
            obsM.proposalReceived(remQp);
            for (int i = FIRST_PKT_ID+1; i < LAST_PKT_ID; i++) {
                QuorumPacket qp = new QuorumPacket(0, i, "dummy".getBytes(), null);
                obsM.proposalReceived(qp);
            }
        }

    }


    @Parameterized.Parameters
    public static Collection<?> getTestParameters() throws NoSuchFieldException, X509Exception, IllegalAccessException, IOException {
        //function signature
        //QuorumPacket removeProposedPacket(long zxid)

        return Arrays.asList(new Object[][]{

                //invalid configurations
                {false, FIRST_PKT_ID-1, true},
                {false, FIRST_PKT_ID, false},
                {false, FIRST_PKT_ID+1, true},

                //valid configuration
                {true, FIRST_PKT_ID, true}
        });
    }

    @Test
    public void startForwardingTest() {

        QuorumPacket result;

        try {
            result = obsM.removeProposedPacket(zxid);
        } catch (RuntimeException e) {
            Assert.assertEquals(e.getMessage(), "Unexpected proposal packet on commit ack, expected zxid 0x"+(FIRST_PKT_ID+1)+" got zxid 0x" + FIRST_PKT_ID);
            return;
        }
        Assert.assertTrue((!expResult && result == null) || (expResult && Arrays.equals(result.getData(), remQp.getData())));

    }



}
