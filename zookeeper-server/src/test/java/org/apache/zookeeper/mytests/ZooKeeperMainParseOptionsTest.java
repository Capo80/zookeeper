package org.apache.zookeeper.mytests;


import org.apache.zookeeper.MyCommandOptionsExt;
import org.apache.zookeeper.ZooKeeperMain;
import org.apache.zookeeper.common.X509Exception;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

@RunWith(value = Parameterized.class)
public class ZooKeeperMainParseOptionsTest {

    //arguments
    private boolean expResult;
    private String[] args;

    public ZooKeeperMainParseOptionsTest(boolean expResult, String[] args) {
        this.expResult = expResult;
        this.args = args;
    }

    @Parameterized.Parameters
    public static Collection<?> getTestParameters() throws NoSuchFieldException, X509Exception, IllegalAccessException, IOException {
        //function signature
        //boolean parseOptions(String[] args)

        return Arrays.asList(new Object[][]{

                //invalid configuration - empty options
                {false, new String[1]},

                //invalid configurations - missing arguments
                {false, new String[]{"-server"}},
                {false, new String[]{"-timeout"}},
                {false, new String[]{"-client-configuration"}},

                //valid configurations
                {true, new String[]{"-r"}},
                {true, new String[]{"-r", "cmd"}}, //correct - the argument can be after the option
                {true, new String[]{"-server", "argument"}},
                {true, new String[]{"-timeout", "argument"}},
                {true, new String[]{"-client-configuration", "argument"}},
                {true, new String[]{"cmd", "cdaArg", "-client-configuration", "argument"}},


        });
    }

    @Test
    public void parseOptionsTest(){

        MyCommandOptionsExt optClass = new MyCommandOptionsExt();

        boolean actResult;

        try {
            actResult = optClass.parseOptions(args);
        } catch (NullPointerException e) {
            Assert.assertFalse(expResult);
            return;
        }
        Assert.assertEquals(expResult, actResult);

    }
}
