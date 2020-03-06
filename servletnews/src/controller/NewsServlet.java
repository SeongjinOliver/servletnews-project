package controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import model.dao.NewsDAO;
import model.vo.NewsVO;


@WebServlet("/news")
public class NewsServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	//	뉴스의 전체 리스트 출력
	//	제목 선택 시 : 해당 뉴스 id 로 해당 뉴스 내용 출력
	//	삭제 버튼 클릭시 : 뉴스 삭제
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int newsId = 0;
		String action = "";
		String keyword = "", searchType = "";
		if(request.getParameter("newsId") != null)
			newsId = Integer.parseInt(request.getParameter("newsId"));
		if(request.getParameter("action") != null)
			 action = request.getParameter("action");
		if(request.getParameter("keyword") != null){
			keyword = request.getParameter("keyword");
		}
		if(request.getParameter("searchType") != null){
			searchType = request.getParameter("searchType");
		}
		NewsDAO dao = new NewsDAO();
		
		if(action != null) {
			if(action.equals("search")) {
				System.out.println("action = search");
				List<NewsVO> list = null;
				if(searchType != null) {
					System.out.println("searchType null x");
					if(searchType.equals("title")) {
						list = dao.search(keyword, searchType);
						if(keyword != null) {
							if (list != null && list.size() == 0) {
								request.setAttribute("msg", keyword + "(이)가 포함된 글이 없습니다.");
								request.setAttribute("list", dao.listAll());
							} else {
								request.setAttribute("list", dao.search(keyword, searchType));
							}
						}
					}
					else if(searchType.equals("writer")) {
						list = dao.listWriter(keyword);
						if(keyword != null) {
							if (list != null && list.size() == 0) {
								request.setAttribute("msg", keyword + "(이)가 포함된 글이 없습니다.");
								request.setAttribute("list", dao.listAll());
							} else {
								request.setAttribute("list", dao.listWriter(keyword));
							}
						}
					}
				}
				
			}
			else {
				if(action.equals("delete")) {
					if(newsId != 0) {
						System.out.println(newsId);
						boolean result = dao.delete(newsId);
						if (result) {
							request.setAttribute("msg", "글이 성공적으로 삭제되었습니다.");
						} else {
							request.setAttribute("msg", "글이 삭제되지 않았습니다.");
						}
					}
				}
				else if(action.equals("read")) {
					if(newsId != 0) {
						request.setAttribute("one", dao.listOne(newsId));
					}
				}
				request.setAttribute("list", dao.listAll());
			}
		}
		else request.setAttribute("list", dao.listAll());
		
		
		request.getRequestDispatcher("/news.jsp").forward(request, response);
	}

	//	POST 방식 요청 시 기능
	//	뉴스 작성
	//	뉴스 수정
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int newsId = 0;
		String action = null;
		String writer = null;
		String title = null;
		String content = null;
		NewsDAO dao = new NewsDAO();
		NewsVO vo = new NewsVO();
		
		if(request.getParameter("action") != null)
			 action = request.getParameter("action");
		
		if(action.equals("insert")) {
			writer = request.getParameter("writer");
			title = request.getParameter("title");
			content = request.getParameter("content");
			//vo.setId(newsId);
			vo.setWriter(writer);
			vo.setTitle(title);
			vo.setContent(content);
			boolean result = dao.insert(vo);
			if (result) {			
				request.setAttribute("msg", writer + "님의 글이 성공적으로 입력되었습니다.");			
			} else {
				request.setAttribute("msg", writer + "님의 글이 입력되지 않았습니다.");
			}
		}else if(action.equals("update")){
			newsId = Integer.parseInt(request.getParameter("newsId"));
			writer = request.getParameter("writer");
			title = request.getParameter("title");
			content = request.getParameter("content");
			vo.setId(newsId);
			vo.setWriter(writer);
			vo.setTitle(title);
			vo.setContent(content);
			boolean result = dao.update(vo);
			if (result) {			
				request.setAttribute("msg", writer + "님의 글이 성공적으로 수정되었습니다.");			
			} else {
				request.setAttribute("msg", writer + "님의 글이 수정되지 않았습니다.");
			}
		}
	
		request.setAttribute("list", dao.listAll());			
		request.getRequestDispatcher("/news.jsp").forward(request, response);
	}

}
