package servlet;

import java.io.IOException;

import bean.User;
import bean.UserData;
import bean.UserInfo;
import dao.UserDAO;
import dao.UserInfoDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import util.SetError;

// ログイン処理用サーブレット
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
	// エラーメッセージ用変数
	String error = "";
	
	// エラー画面表示後の遷移パス用変数
	String erroredPass = "";
	
	// エラー画面表示後の遷移パスメッセージ用変数
	String passMessage = "";
	
	// 入力内容が間違っている時のメッセージ
	String message = "";
	
	// SetErrorクラスのオブジェクト生成
	SetError setError = new SetError();
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException{
		
		// パス情報
		String pass = "/User/view/userMenu.jsp";
		
		try {
			// ログインIDとパスワードをパラメータで取得
			String userid = request.getParameter("userid");
			String password  =request.getParameter("password");
			
			// UserDAOのインスタンスを生成
			UserDAO userDAO = new UserDAO();
			
			// ユーザー情報が存在するかデータベースにアクセス
			User user = userDAO.selectByUser(userid, password);
			
			// ユーザー情報がない場合
			if (user.getUserid() == null || user.getPassword() == null) {
				request.setAttribute("message", "入力データが間違っています!");
				pass = "/Common/view/login.jsp";
				return;
			}
			
			// ユーザー情報の中の権限が管理者だった場合
			if (user.getAuthority().equals("0")) {
				pass = "/Admin/view/adminMenu.jsp";
				
				// セッションオブジェクトの生成
				HttpSession session = request.getSession();
				
				// セッションへのデータの登録
				session.setAttribute("user", user);
				
				// クッキー保存（userid,password）
				// ログインID用
				Cookie userCookie = new Cookie("userid", user.getUserid());
				userCookie.setMaxAge(60 * 60 * 24 * 5);
				response.addCookie(userCookie);
				
				// パスワード用
				Cookie passwordCookie = new Cookie("password", user.getPassword());
				passwordCookie.setMaxAge(60 * 60 * 24 * 5);
				
				response.addCookie(passwordCookie);
			
				return;
			}
			
			// 会員だった場合
			// UserInfoDAOのインスタンス生成
			UserInfoDAO userInfoDAO = new UserInfoDAO();
			
			// 会員の詳細情報を取得する
			UserInfo userInfo = userInfoDAO.selectByDetailUser(user.getUsernum());
			
			// 会員のデータをまとめる
			UserData userData = new UserData();
			
			userData.setUsernum(user.getUsernum());
			userData.setUserid(user.getUserid());
			userData.setPassword(user.getPassword());
			userData.setAuthority(user.getAuthority());
			userData.setName(userInfo.getName());
			userData.setEmail(userInfo.getEmail());
			userData.setEmail(userInfo.getArea());
			
			// セッションオブジェクトの生成
			HttpSession session = request.getSession();
			
			// セッションへのデータの登録
			session.setAttribute("userData", userData);
			
			// クッキー保存（userid,password）
			// ログインID用
			Cookie userCookie = new Cookie("userid", user.getUserid());
			userCookie.setMaxAge(60 * 60 * 24 * 5);
			response.addCookie(userCookie);
			
			// パスワード用
			Cookie passwordCookie = new Cookie("password", user.getPassword());
			passwordCookie.setMaxAge(60 * 60 * 24 * 5);
			
			response.addCookie(passwordCookie);
		} catch (IllegalStateException e) {
			error = "DB接続エラーの為、ログインは出来ません。";
			pass = "/Common/view/error.jsp";
			erroredPass = "/logout";
			passMessage = "[ログイン画面へ]";
			// エラーのリクエストスコープを登録するためのメソッド
			setError.setErrorRequestScoop(request, response, passMessage, erroredPass, error);
		} catch (Exception e) {
			error = "予期せぬエラーが発生しました。 <br>" + e;
			pass = "/Common/view/error.jsp";
			erroredPass = "/logout";
			passMessage = "[ログイン画面へ]";
			// エラーのリクエストスコープを登録するためのメソッド
			setError.setErrorRequestScoop(request, response, passMessage, erroredPass, error);
		} finally {
			// フォワード
			request.getRequestDispatcher(pass).forward(request, response);
		}
		
		
	}
} // class: LoginServlet
