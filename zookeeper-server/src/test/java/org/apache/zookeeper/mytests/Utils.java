package org.apache.zookeeper.mytests;

import org.apache.zookeeper.common.X509Exception;
import org.apache.zookeeper.server.quorum.Leader;
import org.apache.zookeeper.server.quorum.LearnerHandler;
import org.apache.zookeeper.server.quorum.QuorumPeer;
import org.apache.zookeeper.server.quorum.ZabUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Utils {

    private static final File testData = new File(System.getProperty("test.data.dir", "src/test/resources/data"));

    static Socket[] getSocketPair() throws IOException {
        ServerSocket ss = new ServerSocket(0, 50, InetAddress.getByName("127.0.0.1"));
        InetSocketAddress endPoint = (InetSocketAddress) ss.getLocalSocketAddress();
        Socket s = new Socket(endPoint.getAddress(), endPoint.getPort());
        return new Socket[]{s, ss.accept()};
    }

    public static LearnerHandler createValidLearner() throws IOException, IllegalAccessException, NoSuchFieldException, X509Exception {
        Socket[] pair = getSocketPair();
        Socket leaderSocket = pair[0];
        Socket followerSocket = pair[1];
        File tmpDir = File.createTempFile("test", "dir", testData);
        tmpDir.delete();
        tmpDir.mkdir();
        QuorumPeer qp = ZabUtils.createQuorumPeer(tmpDir);
        Leader leader = ZabUtils.createLeader(tmpDir, qp);

        LearnerHandler lh = new LearnerHandler(leaderSocket, new BufferedInputStream(leaderSocket.getInputStream()), leader);
        lh.start();

        return lh;
    }


    public static LearnerHandler createBadLearner() throws IOException, IllegalAccessException, NoSuchFieldException, X509Exception {
        Socket[] pair = getSocketPair();
        Socket leaderSocket = pair[0];
        Socket followerSocket = pair[1];
        File tmpDir = File.createTempFile("test", "dir", testData);
        tmpDir.delete();
        tmpDir.mkdir();
        QuorumPeer qp = ZabUtils.createQuorumPeer(tmpDir);
        Leader leader = ZabUtils.createLeader(tmpDir, qp);

        LearnerHandler lh = new LearnerHandler(leaderSocket, null, leader);
        lh.start();

        return lh;
    }
}
