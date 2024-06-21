package servlet;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// ログアウト処理用サーブレット
@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
		// セッション情報をクリア
		HttpSession session = request.getSession();
		session.invalidate();
		
		// ログインにフォワード
		request.getRequestDispatcher("/Common/view/login.jsp").forward(request, response);
	}
}
