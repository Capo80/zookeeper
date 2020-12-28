package org.apache.zookeeper.mytests;


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
public class ZooKeeperMainParseCommandTest {

    //arguments
    private boolean expResult;
    private String cmdstring;

    public ZooKeeperMainParseCommandTest(boolean expResult, String cmdstring) {
        this.expResult = expResult;
        this.cmdstring = cmdstring;
    }

    @Parameterized.Parameters
    public static Collection<?> getTestParameters() throws NoSuchFieldException, X509Exception, IllegalAccessException, IOException {
        //function signature
        //boolean parseCommand(String cmdstring)

        return Arrays.asList(new Object[][]{

                //invalid configuration - empty arguments or command
                {false, null},
                {false, ""},

                //valid configurations? - somehow an empty argument is still valid
                {true, "'' arguments"},
                {true, "\"\" arguments"},
                {true, "cmd ''"},
                {true, "cmd \"\""},

                //valid configurations
                {true, "cmd arg1 arg2"},
                {true, "cmd 'arg1' arg2"},
                {true, "cmd arg1 \"arg2\""},
                {true, "'cmd' arg1 arg2"},
                {true, "\"cmd\" arg1 arg2"},

        });
    }

    @Test
    public void parseCommandTest(){

        ZooKeeperMain.MyCommandOptions optClass = new ZooKeeperMain.MyCommandOptions();

        boolean actResult;

        try {
            actResult = optClass.parseCommand(cmdstring);
        } catch (NullPointerException e) {
            Assert.assertFalse(expResult);
            return;
        }
        Assert.assertEquals(expResult, actResult);

    }


}
