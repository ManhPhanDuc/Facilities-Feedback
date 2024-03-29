/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import googleuser.GoogleUserDAO;
import googleuser.GoogleUserDTO;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import utils.GoogleUtils;

/**
 *
 * @author Duy
 */
@WebServlet(name = "LoginController", urlPatterns = {"/login"})
public class LoginController extends HttpServlet {

    private static final String MANAGER_PAGE = "manager.jsp";
    private static final String EMPLOYEE_PAGE = "employee.jsp";
    private static final String USER_PAGE = "send-feedback.jsp";
    private static final String BLOCKED_USER_PAGE = "blocked.jsp";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try {
            String id_token = request.getParameter("id_token");
            GoogleUserDTO user = GoogleUtils.getUserInfo(id_token);

            if (user.getHd() != null && user.getHd().equals("fpt.edu.vn")) {
                String url = "";
                GoogleUserDAO userDAO = new GoogleUserDAO();
                String roleID = "";

                roleID = userDAO.checkLogin(user);

                if ("MG".equals(roleID)) {
                    url = MANAGER_PAGE;
                } else if ("EP".equals(roleID)) {
                    url = EMPLOYEE_PAGE;
                } else if ("US".equals(roleID)) {
                    if (user.getStatusID() == 1) {
                        url = USER_PAGE;
                    } else if (user.getStatusID() == 0) {
                        url = BLOCKED_USER_PAGE;
                    }
                } else if (roleID.isEmpty()) { // USER KHÔNG TỒN TẠI TRONG DB -> ADD VÀO DB VỚI ROLE = US
                    user.setRoleID("US");
                    user.setStatusID(1);
                    userDAO.addNewUser(user);
                    url = USER_PAGE;
                }

                HttpSession session = request.getSession();
                session.setAttribute("LOGGED_IN_USER", user);
                response.sendRedirect(url);
            } else { // NOT FPT.EDU.VN EMAIL
                request.setAttribute("ERROR", "email");
                request.getRequestDispatcher("login-error.jsp").forward(request, response);
            }
        } catch (Exception e) {
            log("Error at LoginController");
        }

    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}