/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2009 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common Development
 * and Distribution License("CDDL") (collectively, the "License").  You
 * may not use this file except in compliance with the License. You can obtain
 * a copy of the License at https://glassfish.dev.java.net/public/CDDL+GPL.html
 * or glassfish/bootstrap/legal/LICENSE.txt.  See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * When distributing the software, include this License Header Notice in each
 * file and include the License file at glassfish/bootstrap/legal/LICENSE.txt.
 * Sun designates this particular file as subject to the "Classpath" exception
 * as provided by Sun in the GPL Version 2 section of the License file that
 * accompanied this code.  If applicable, add the following below the License
 * Header, with the fields enclosed by brackets [] replaced by your own
 * identifying information: "Portions Copyrighted [year]
 * [name of copyright owner]"
 *
 * Contributor(s):
 *
 * If you wish your version of this file to be governed by only the CDDL or
 * only the GPL Version 2, indicate your decision by adding "[Contributor]
 * elects to include this software in this distribution under the [CDDL or GPL
 * Version 2] license."  If you don't indicate a single choice of license, a
 * recipient has the option to distribute your version of this file under
 * either the CDDL, the GPL Version 2 or to extend the choice of license to
 * its licensees as provided above.  However, if you add GPL Version 2 code
 * and therefore, elected the GPL Version 2 license, then the option applies
 * only if the new code is made subject to such option by the copyright
 * holder.
 */
import java.io.*;
import java.net.*;
import com.sun.ejte.ccl.reporter.*;

/*
 * Unit test for new <dispatcher> subelement of <cache-mapping>.
 *
 * According to the cache config in sun-web.xml, the response generated by
 * the "/dispatchTo" servlet is cached only if that servlet is the target of
 * a RequestDispatcher "include" or "forward" operation.
 */
public class WebTest {

    private static SimpleReporterAdapter stat
        = new SimpleReporterAdapter("appserv-tests");

    private static final String TEST_NAME = "cache-filter-mapping-dispatcher";

    public static void main(String[] args) {

        stat.addDescription("Unit test for new <dispatcher> subelement "
                            + "of <cache-mapping>");

        String host = args[0];
        String port = args[1];
        String contextRoot = args[2];
        String hostPortRoot = host  + ":" + port + contextRoot;

        boolean success = false;

        /*
         * Access /dispatchTo directly, expect "RESPONSE-0" in response
         * body.
         * Counter in /dispatchTo gets incremented.
         */
        success = doTest("http://" + hostPortRoot + "/dispatchTo", 
                         "RESPONSE-0");
        
        if (success) {
            /*
             * Access /dispatchTo directly, expect "RESPONSE-1" in response
             * body.
             * Counter in /dispatchTo gets incremented.
             */
             success = doTest("http://" + hostPortRoot + "/dispatchTo",
                              "RESPONSE-1");
        }

        if (success) {
            /*
             * Access /dispatchTo via RequestDispatcher.forward() from
             * /dispatchFrom, expect response ("RESPONSE-2") in response
             * body.
             * Counter in /dispatchTo gets incremented.
             */
            success = doTest("http://" + hostPortRoot + "/dispatchFrom?action=forward",
                             "RESPONSE-2");
        }

        if (success) {
            /*
             * Access /dispatchTo via RequestDispatcher.forward() from
             * /dispatchFrom, expect cached response ("RESPONSE-2") in response
             * body.
             * Counter in /dispatchTo does not get incremented.
             */
            success = doTest("http://" + hostPortRoot + "/dispatchFrom?action=forward",
                             "RESPONSE-2");
        }

        if (success) {
            /*
             * Access /dispatchTo directly, expect "RESPONSE-3" in response
             * body.
             * Counter in /dispatchTo gets incremented.
             */
            success = doTest("http://" + hostPortRoot + "/dispatchTo",
                             "RESPONSE-3");
        }

        if (success) {
            /*
             * Access /dispatchTo via RequestDispatcher.include() from
             * /dispatchFrom, expect response ("RESPONSE-4") in response
             * body..
             * Counter in /dispatchTo gets incremented.
             */
            success = doTest("http://" + hostPortRoot + "/dispatchFrom?action=include",
                             "RESPONSE-4");
        }

        if (success) {
            /*
             * Access /dispatchTo via RequestDispatcher.include() from
             * /dispatchFrom, expect cached response ("RESPONSE-4") in response
             * body.
             * Counter in /dispatchTo does not get incremented.
             */
            success = doTest("http://" + hostPortRoot + "/dispatchFrom?action=include",
                             "RESPONSE-4");
        }        

        if (success) {
            /*
             * Access /dispatchTo directly, expect "RESPONSE-5" in response
             * body.
             * Counter in /dispatchTo gets incremented.
             */
            success = doTest("http://" + hostPortRoot + "/dispatchTo",
                             "RESPONSE-5");
        }

        if (success) {
            stat.addStatus(TEST_NAME, stat.PASS);
        } else {
            stat.addStatus(TEST_NAME, stat.FAIL);
        }

        stat.printSummary(TEST_NAME);
    }

    /*
     * Returns true in case of success, false otherwise.
     */
    private static boolean doTest(String urlString, String expected) {

        try {
            URL url = new URL(urlString);
            System.out.println("Connecting to: " + url.toString());
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) { 
                System.out.println("Wrong response code. Expected: 200"
                                   + ", received: " + responseCode);
                return false;
            }

            InputStream is = conn.getInputStream();
            BufferedReader input = new BufferedReader(new InputStreamReader(is));
            String line = input.readLine();
            System.out.println("Response: " + line);
            if (!expected.equals(line)) {
                System.out.println("Wrong response. Expected: " + expected
                                   + ", received: " + line);
                return false;
            }

        } catch (Exception ex) {
            System.out.println(TEST_NAME + " test failed.");
            ex.printStackTrace();
            return false;
        }

        return true;

    }

}
