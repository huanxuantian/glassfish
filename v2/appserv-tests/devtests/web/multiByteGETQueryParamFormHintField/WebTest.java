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
import java.lang.*;
import java.io.*;
import java.net.*;

import com.sun.ejte.ccl.reporter.*;

/**
 * Unit test for 6339608 ("Differences in the behaviour of 8.1 PE ur1
 * and 8.1 EE ur2 using IE (HTTP GET Query with I18N)"):
 *
 * Make sure that form-hint-field specified in sun-web.xml is applied
 * to request's query params. This is verified by the fact that the target
 * servlet does not call ServletRequest.setCharacterEncoding().
 */
public class WebTest {

    private static SimpleReporterAdapter stat
        = new SimpleReporterAdapter("appserv-tests");

    private static final String TEST_NAME =
        "multi-byte-get-query-param-form-hint-field";
    private static final String EXPECTED_RESPONSE = "true";

    private String host;
    private String port;
    private String contextRoot;
    private Socket sock = null;

    public WebTest(String[] args) {
        host = args[0];
        port = args[1];
        contextRoot = args[2];
    }
    
    public static void main(String[] args) {
        stat.addDescription("Unit test for 6339608");
        WebTest webTest = new WebTest(args);
        try {
            webTest.doTest();
            stat.addStatus(TEST_NAME, stat.PASS);
        } catch (Exception ex) {
            stat.addStatus(TEST_NAME, stat.FAIL);
            ex.printStackTrace();
        } finally {
            try {
                if (webTest.sock != null) {
                    webTest.sock.close();
                }
            } catch (IOException ioe) {
                // ignore
            }
        }
        stat.printSummary(TEST_NAME);
    }

    public void doTest() throws Exception {
     
        sock = new Socket(host, Integer.parseInt(port));
    
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
            sock.getOutputStream(), "Shift_JIS"));
        bw.write("GET " + contextRoot
                 + "/TestServlet?"
                 + "japaneseName=\u3068\u4eba\u6587"
                 + "&requestCharset=Shift_JIS"
                 + " HTTP/1.0\r\n");
        bw.write("\r\n");
        bw.flush();

        // Read response
        InputStream is = null;
        BufferedReader input = null;
        String line = null;
        String lastLine = null;
        try {
            is = sock.getInputStream();
            input = new BufferedReader(new InputStreamReader(is));
            while ((line = input.readLine()) != null) {
                lastLine = line;
            }
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ioe) {
                // ignore
            }
            try {
                if (input != null) {
                    input.close();
                }
            } catch (IOException ioe) {
                // ignore
            }
        }

        if (!EXPECTED_RESPONSE.equals(lastLine)) {
            throw new Exception("Wrong response body. Expected: " +
                EXPECTED_RESPONSE + ", received: " + lastLine);
        }
    }
}
