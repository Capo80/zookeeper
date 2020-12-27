package org.apache.zookeeper.mytests;


import org.apache.zookeeper.common.X509Exception;
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


        });
    }
}
