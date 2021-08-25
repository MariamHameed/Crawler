package FYP.Project;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class OlxBike implements Runnable {

	public static Connection con = null;

	public static void main(String[] args) throws SQLException, IOException {
		// TODO Auto-generated method stub
		connect();
		proxy();

		OlxBike ex = new OlxBike();
		Thread t1 = new Thread(ex);
		Thread t3 = new Thread(ex);
		Thread t2 = new Thread(ex);
		// t1.start();

		// t2.start();

		// t3.start();
		// Thread t4= new Thread(ex);
		// t4.start();
		// Thread t5= new Thread(ex);
		// t2.start();
		// Thread t9= new Thread(ex);
		// t9.start();
		// Thread t6= new Thread(ex);
		// t6.start();
		// Thread t7= new Thread(ex);
		// t7.start();
		// Thread t8= new Thread(ex);
		// t8.start();
		//

		Statement stmt = con.createStatement();
		ResultSet rs = stmt
				.executeQuery("select * from links where  Url<>'' and Website='Olx' and Status=0 and Category='Bikes'");

		while (rs.next()) {

			int Id = rs.getInt(1);
			String Url = rs.getString(2);

			getDetailPage(Url, Id);

		}

		String Url = "https://www.olx.com.pk/motorcycles_c81";
		for (int i = 1; i <= 52553; i++) {

		 //getListing(Url);

		}

	}

	public void run() {
		int lid = 0;
		try {

			Statement stmt = con.createStatement();

			ResultSet r = stmt
					.executeQuery("select * from links where Status=0 And Website='Olx' and Category='Bikes' ");

			if (r.next()) {
				System.out.println("Ads links are exsit that need to  crawl");

				String sql = "insert into thread (temp) values(?);";
				PreparedStatement preparedStmt = con.prepareStatement(sql);
				preparedStmt.setString(1, "abc");
				preparedStmt.execute();
				ResultSet rs = preparedStmt.getGeneratedKeys();

				if (rs.next()) {
					lid = rs.getInt(1);
					System.out.println("inserted id is  " + lid);

				}
				task2(lid);

			} else {
				System.out.println("ads are already crawl");
			}

		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}

	}

	public static void task2(int lid) throws IOException, SQLException {

		String sql = "select * from links where  Url<>'' and Website='Olx' and Status=0 and Category='Bikes' and Id=?   ;";

		PreparedStatement prepared = con.prepareStatement(sql);
		prepared.setInt(1, lid);
		ResultSet r = prepared.executeQuery();

		while (r.next()) {

			int id = r.getInt(1);
			String Url = r.getString(2);
			System.out.println(" Last inserted id " + id);

			getDetailPage(Url, id);
		}

	}

	public static String proxy() throws IOException, SQLException {

		Statement stmt = con.createStatement();
		ResultSet r = stmt.executeQuery("select * from proxy ORDER BY RAND() LIMIT 1");
		String p = "n/a";
		while (r.next()) {
			p = r.getString("proxy");
		}
		System.out.println("Proxy: " + p);
		return p;

	}

	public static void setproxy(String p) throws IOException {
		System.setProperty("http.proxyHost", p);

		// System.setProperty("http.proxyPort", "3128");
	}

	public static void connect() throws SQLException {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://localhost:3306/crawler", "root", "");
			System.out.println(con);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void getListing(String URL) throws IOException, SQLException {
		Document document = Jsoup.connect(URL).get();
		Elements ads = document.select("ul[class^=rl3f9]").select("li");
		for (Element e : ads) {

			String aHref = e.select(" a").attr("href");
			if (aHref != "") {

				String href = "https://www.olx.com.pk" + aHref;
				System.out.println("-->" + href);
				Statement stmt = con.createStatement();
				ResultSet resultSet = stmt.executeQuery("select count(1) from links where Url='" + href + "'");
				while (resultSet.next()) {
					int count = resultSet.getInt(1);
					if (count == 0) {
						String sql = "insert into links (Url, Website, Category) values(?,?,?);";
						PreparedStatement preparedStmt = con.prepareStatement(sql);
						preparedStmt.setString(1, href);
						preparedStmt.setString(2, "Olx");
						preparedStmt.setString(3, "Bikes");
						preparedStmt.execute();
					} else {
						System.out.println("Link already exists");
					}
				}
			}
		}
	}

	public static void getDetailPage(String Url, int Id) throws IOException, SQLException {
		// public static void getDetailPage() throws IOException, SQLException {
		Document document = Jsoup.connect(Url).get();
		boolean isWhitespace;
		// String Url="https://www.olx.com.pk/item/honda-cg-125-iid-1017714277";

		// Document ad = Jsoup.connect(Url).get();
		Elements ad = document.select("div[id=container]");

		String s_title = ad.select("div[class=rui-2vHTl] h1").text();
		System.out.println("Title---------------- " + s_title);

		String s_location = ad.select("div[class=_2kqti] span[class=_2FRXm]").text();
		System.out.println("Location------------- " + s_location);

		String s_price = ad.select("div[class=rui-2vHTl] ._2xKfz").text();
		 s_price= s_price.substring(2);
		 s_price=s_price.replaceAll(",","");
		System.out.println("Price------------- " +s_price);

		String s_make = ad.select("span[class=_2vNpt]").first().text();
		System.out.println("Make------------- " + s_make);

		String s_Condition = ad.select("span[class=_2vNpt]").last().text();
			
		System.out.println("Condition------------- " + s_Condition);

		String s_year = ad.select(
				"#container > main > div > div > div > div.rui-2SwH7.rui-m4D6f.rui-yyNoO.rui-3CPXI.rui-3E1c2.rui-1JF_2 > section.CBG3S > div > div > div.cb14b > div > div:nth-child(2) > div > span._2vNpt")
				.text();
		System.out.println("Year------------- " + s_year);
		isWhitespace = s_year.matches("^\\s*$");
		if (isWhitespace == true)

		{
			s_year = "N/A";

			System.out.println("year---------------- " + s_year);

		}
		isWhitespace = s_Condition.matches("^\\s*$");
		if (isWhitespace == true)

		{
			s_Condition = "N/A";

			System.out.println("condition---------------- " + s_Condition);

		}

		String s_seller_comments = ad.select(" div[class=rui-2ns2W] p").text();
		System.out.println("Description------------- " + s_seller_comments);

		String s_date = ad.select(
				" #container > main > div > div > div > div.rui-2SwH7.rui-m4D6f.rui-1nZcN.rui-3CPXI.rui-3E1c2.rui-1JF_2 > div.rui-2ns2W._2r-Wm > div > section > div > div._2DGqt > span")
				.text();

		System.out.println("Date------------- " + s_date);

		isWhitespace = s_date.matches("^\\s*$");
		if (isWhitespace == true)

		{
			s_date = "N/A";

			System.out.println("date---------------- " + s_date);

		}

		String s_owner_name = ad.select(
				"#container > main > div > div > div > div.rui-2SwH7.rui-m4D6f.rui-1nZcN.rui-3CPXI.rui-3E1c2.rui-1JF_2 > div.rui-2ns2W.YpyR- > div > div > div._1oSdP > div > a > div")
				.text();
		System.out.println("Owner Name---------------- " + s_owner_name);
		
		isWhitespace = s_owner_name.matches("^\\s*$");
		if (isWhitespace == true)

		{
			s_owner_name = "N/A";

			System.out.println("date---------------- " + s_owner_name);

		}

		String s_cell_number = "N/A";
		System.out.println("Cell Number---------------- " + s_cell_number);

		String s_pic_1 = "https://wsa2.pakwheels.com/assets/default-display-image-bike-50e52ceba2ca90278fc427a6267d2b33.png";
		System.out.println("-------" + s_pic_1);

		System.out.println("------------------------------- END AD ----------------------------");

		String sql = "insert into listing (s_title, s_location, s_seller_comments,s_Condition,"
				+ "s_price,s_make, s_year,s_category,s_website_name,s_detail_link,s_pic_1,s_owner_name,s_date)"
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?);";

		PreparedStatement preparedStmt = con.prepareStatement(sql);
		preparedStmt.setString(1, s_title);
		preparedStmt.setString(2, s_location);
		preparedStmt.setString(3, s_seller_comments);
		preparedStmt.setString(4, s_Condition);
		preparedStmt.setString(5, s_price);
		preparedStmt.setString(6, s_make);
		preparedStmt.setString(7, s_year);
		preparedStmt.setString(8, "Bikes");
		preparedStmt.setString(9, "OLX");
		preparedStmt.setString(10, Url);
		preparedStmt.setString(11, s_pic_1);
		preparedStmt.setString(12, s_owner_name);
		preparedStmt.setString(13, s_date);
		preparedStmt.execute();

		String sqlUpdate = "update links set Status =1 where id=?";
		PreparedStatement preparedUpdateStmt = con.prepareStatement(sqlUpdate);
		preparedUpdateStmt.setInt(1, Id);
		preparedUpdateStmt.executeUpdate();

	}

}
