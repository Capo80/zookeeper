package org.apache.zookeeper.mytests;


import org.apache.zookeeper.data.Id;
import org.apache.zookeeper.server.quorum.Learner;
import org.apache.zookeeper.server.quorum.LearnerHandler;
import org.apache.zookeeper.server.quorum.ObserverMaster;
import org.apache.zookeeper.server.quorum.QuorumPacket;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@RunWith(value = Parameterized.class)
public class ObserverMasterCacheCommittedPacketTest {

    private static int pktSizeLimit;

    //class to test
    private ObserverMaster obsM;

    //arguments
    private boolean expResult;
    private QuorumPacket qp;



    public ObserverMasterCacheCommittedPacketTest(boolean expResult, QuorumPacket qp) {
        //the arguments of the contructor are not used in the method we are testing
        //we can leave them as null
        obsM = new ObserverMaster(null, null, 80);

        this.expResult = expResult;
        this.qp = qp;

    }

    @Before
    public void setUpQueue() {
        //add some dummy packets in the queue to check if they are removed correctly
        pktSizeLimit = Integer.getInteger("zookeeper.observerMaster.sizeLimit", 32 * 1024 * 1024);

        QuorumPacket qp = new QuorumPacket(0, 1234, "dummy".getBytes(), null);

        //fill half the queue
        long pktNumber = (pktSizeLimit/2)/LearnerHandler.packetSize(qp);
        for (int i = 0; i < pktNumber; i++) {
            obsM.cacheCommittedPacket(qp);
        }

    }

    @Parameterized.Parameters
    public static Collection<?> getTestParameters() {
        //function signature
        //void cacheCommittedPacket(final QuorumPacket pkt)

        Id dummyId = new Id("dummy", "dummy");
        List<Id> dummyAuthInfo = new ArrayList<Id>();
        dummyAuthInfo.add(dummyId);

        QuorumPacket undersizedPkt = new QuorumPacket(0, 1234, "dummy".getBytes(), dummyAuthInfo);
        QuorumPacket almostOverSizedPkt = new QuorumPacket(0, 1234, new byte[(pktSizeLimit/2)-((int)(pktSizeLimit*0.15))], dummyAuthInfo);
        QuorumPacket overSizedPkt = new QuorumPacket(0, 1234, new byte[pktSizeLimit/2 + ((int)(pktSizeLimit*0.15))], dummyAuthInfo);
        QuorumPacket reallyOverSizedPkt = new QuorumPacket(0, 1234, new byte[pktSizeLimit*2], dummyAuthInfo);

        return Arrays.asList(new Object[][]{

                //null packets are not allowed
                {false, null},

                //valid configurations
                {true, undersizedPkt},
                {true, almostOverSizedPkt},
                {true, overSizedPkt},
                {false, reallyOverSizedPkt},
        });

    }

    @Test
    public void cacheCommittedPacketTest() {

        System.out.println("prima: " + qp);
        //call the test method
        try {
            obsM.cacheCommittedPacket(qp);
        } catch (NullPointerException e) {
            Assert.assertNull(qp);
            return;
        }

        System.out.println("dopo: " + qp);
        //get the committed packet queue
        ConcurrentLinkedQueue<QuorumPacket> queue = obsM.getCommittedPkts();

        //check that our packet has been added
        Assert.assertTrue(!expResult || queue.contains(qp));

        //check that queue has not exceeded maximum size
        int queueSize = 0;
        QuorumPacket pkt;
        while (true) {
            pkt = queue.poll();
            if (pkt == null) {
                break;
            }
            queueSize += LearnerHandler.packetSize(pkt);
        }

        Assert.assertTrue(queueSize < pktSizeLimit);



    }

}
