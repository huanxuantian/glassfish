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
/*
 * MyProtectedServlet.java
 *
 * Created on April 24, 2005, 2:14 AM
 */

import java.io.*;
import java.net.*;

import javax.servlet.*;
import javax.servlet.http.*;

/**
 *
 * @author lm115986
 * @version
 */
public class MyProtectedServlet extends HttpServlet {
    
    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        
        System.out.println("MyProtectedServlet.processRequest " + request.getRequestURI() + " " + request.getQueryString());
        
        String myUrl = request.getRequestURI();
        if(myUrl.indexOf("login") >= 0) {
            login(request, response);
            return;
        } else if(myUrl.indexOf("redirect") >= 0) {
            redirect(request, response);
            return;
        }
        
        if(request.getRemoteUser() == null) {
            String callUrl = request.getRequestURI();
            String query = request.getQueryString();
            if(query != null) {
                callUrl = callUrl + "?" + query;
            }
            String nextEncUrl = java.net.URLEncoder.encode(callUrl);
            String redirectUrl = request.getContextPath() + "/application/redirect?nextencurl=" + nextEncUrl;
            response.sendRedirect(redirectUrl);
        } else {
            response.setContentType("text/html");
            PrintWriter out = response.getWriter();
            
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet MyProtectedServlet</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet MyProtectedServlet at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
            
            out.close();
        }
    }
    
    private void redirect(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
  
        response.sendRedirect(request.getParameter("nextencurl"));
    }
    
     protected void login(HttpServletRequest request, HttpServletResponse response)
     throws ServletException, IOException {

        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }   
    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /** Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }
    
    /** Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "Short description";
    }
    // </editor-fold>
}
