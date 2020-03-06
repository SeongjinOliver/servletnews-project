package model.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.vo.NewsVO;

public class NewsDAO {
	
	private Connection connectDB() {
		Connection conn = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		try {
			conn = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "jdbctest", "jdbctest");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	private void close(Connection conn, Statement stmt, ResultSet rs) {
		try {
			if(rs != null)
				rs.close();
			if(stmt != null)
				stmt.close();
			if(conn != null)
				conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean insert(NewsVO vo) {
		boolean result = true;
		Connection conn = connectDB();
		try(PreparedStatement pstmt = conn.prepareStatement(
				"insert into news values"
				+ "(news_seq.nextval, ? ,?, ?,sysdate, 1)");){
			pstmt.setString(1, vo.getWriter());
			pstmt.setString(2, vo.getTitle());
			pstmt.setString(3, vo.getContent());
			//pstmt.setString(5, vo.getWritedate());
			pstmt.executeUpdate();
		}catch (SQLException e) {
			result = false;
			e.printStackTrace();
		} finally {
			close(conn, null, null);
		}
		return result;
	}

	public boolean update(NewsVO vo) {
		boolean result = true;
		Connection conn = connectDB();
		try(PreparedStatement pstmt = conn.prepareStatement(
				"update news set writer = ?, " +
				"title = ?, " + "content = ? " + "where id = ?");){
			pstmt.setString(1, vo.getWriter());
			pstmt.setString(2, vo.getTitle());
			pstmt.setString(3, vo.getContent());
			pstmt.setInt(4, vo.getId());
			pstmt.executeUpdate();
		}catch(SQLException e){
			result = false;
			e.printStackTrace();
		} finally {
			close(conn, null, null);
		}
		return result;
	}

	public boolean delete(int id) {
		Connection conn = connectDB();
		boolean result = true;
		try (PreparedStatement pstmt = conn.prepareStatement(
					"delete from news where id = ?");){
					pstmt.setInt(1, id);
					pstmt.executeUpdate();
		}catch(SQLException e){
			result = false;
			e.printStackTrace();
		} finally {
			close(conn, null, null);
		}
		
		return result;
	}

	public List<NewsVO> listAll() {
		List<NewsVO> list = new ArrayList<>();
		Connection conn = connectDB();
		try (Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery
				("select id, writer, title, content, to_char(writedate, "
				+ "'YYYY-mm-dd') \"writedate\", cnt from news");) {
			NewsVO vo;
			while(rs.next()) {
				//System.out.println("success");
				vo = new NewsVO();
				vo.setId(rs.getInt("id"));
				vo.setWriter(rs.getString("writer"));
				vo.setTitle(rs.getString("title"));
				vo.setContent(rs.getString("content"));
				vo.setWritedate(rs.getString("writedate"));
				vo.setCnt(rs.getInt("cnt"));
				list.add(vo);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(conn, null, null);
		}
		return list;
	}

	public NewsVO listOne(int id) {
//		클라이언트로부터 전달된 id 값에 해당하는 글 하나만을 추출하여 NewsVO 객체에 담아 리턴한다.
//		만일 추출되지 않으면 null 을 리턴하게 구현해서 컨트롤러에서 이 부분을 처리하도록 한다
		NewsVO vo = null;
		Connection conn = connectDB();
		try (Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery
					("select id, writer, title, content, to_char(writedate, "
						+ "'YYYY-mm-dd') \"writedate\", cnt from news where id=" + id);) {
			if(rs.next()) {
				vo = new NewsVO();
				vo.setId(rs.getInt("id"));
				vo.setWriter(rs.getString("writer"));
				vo.setTitle(rs.getString("title"));
				vo.setContent(rs.getString("content"));
				vo.setWritedate(rs.getString("writedate"));
				vo.setCnt(rs.getInt("cnt") + 1);
				stmt.executeUpdate("Update news set cnt="+vo.getCnt()+" where id="+id);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			close(conn, null, null);
		}
		return vo;
	}
	public List<NewsVO> listWriter(String writer){
		List<NewsVO> list = new ArrayList<>();
		Connection conn = connectDB();
		try (Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery
				("select id, writer, title, content, to_char(writedate,"
						+ "'YYYY-mm-dd') \"writedate\", cnt from news where writer like '%"+writer+"%'");) {
			NewsVO vo;
			while(rs.next()) {
				vo = new NewsVO();
				vo.setId(rs.getInt("id"));
				vo.setWriter(rs.getString("writer"));
				vo.setTitle(rs.getString("title"));
				vo.setContent(rs.getString("content"));
				vo.setWritedate(rs.getString("writedate"));
				vo.setCnt(rs.getInt("cnt"));
				list.add(vo);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			close(conn, null, null);
		}
		return list;
	}
	public List<NewsVO> search(String key, String searchType){
		List<NewsVO> list = new ArrayList<>();
		Connection conn = connectDB();
		try (Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery
				("select id, writer, title, content, to_char(writedate,"
						+ "'YYYY-mm-dd') \"writedate\", cnt from news where title like '%"+key+"%'");) {
			NewsVO vo;
			while(rs.next()) {
				vo = new NewsVO();
				vo.setId(rs.getInt("id"));
				vo.setWriter(rs.getString("writer"));
				vo.setTitle(rs.getString("title"));
				vo.setContent(rs.getString("content"));
				vo.setWritedate(rs.getString("writedate"));
				vo.setCnt(rs.getInt("cnt"));
				list.add(vo);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			close(conn, null, null);
		}
		return list;
	}
}