package org.apache.zookeeper.mytests;


import org.apache.zookeeper.server.quorum.LearnerHandler;
import org.apache.zookeeper.server.quorum.ObserverMaster;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(value = Parameterized.class)
public class ObserverMasterStartForwordingTest {

    //Observer class to test
    private ObserverMaster obsM;

    //arguments
    private LearnerHandler lh;
    private long lastSeenZxid;




}
