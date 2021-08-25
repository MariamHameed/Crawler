package FYP.Project;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class OlxCar implements Runnable {

	public static Connection con = null; // connection type  con variable

	public static void main(String[] args) throws SQLException, IOException {
		// TODO Auto-generated method stub
		connect(); // function call
		proxy(); // function call

		OlxCar ex = new OlxCar();
		Thread t1 = new Thread(ex);
		// t1.start();
		Thread t2 = new Thread(ex);
		// t2.start();
		Thread t3 = new Thread(ex);
		// t3.start();
		 Thread t4= new Thread(ex);
		// t4.start();
		 Thread t5= new Thread(ex);
		// t5.start();
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
				.executeQuery("select * from links where  Url<>'' and Website='Olx' and Status=0 and Category='Cars'");

		while (rs.next()) {

			int Id = rs.getInt(1);
			String Url = rs.getString(2);

			 getDetailPage(Url,Id);

		}

		String Url = "https://www.olx.com.pk/cars_c84";
		for (int i = 1; i <= 52553; i++) {

		//getListing(Url);
		}

	}

	public void run() {
		int lid = 0;
		try {

			Statement stmt = con.createStatement();

			ResultSet r = stmt
					.executeQuery("select * from links where Status=0 And Website='Olx' and Category='Cars' ");

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

		String sql = "select * from links where  Url<>'' and Website='Olx' and Status=0 and Category='Cars' and Id=?   ;";

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
		// Document document = Jsoup.connect(URL)..proxy(p, 1080).get();
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
						preparedStmt.setString(3, "Cars");
						preparedStmt.execute();
					} else {
						System.out.println("Link already exists");
					}
				}
			}
		}
	}

	public static void getDetailPage(String Url, int id) throws IOException, SQLException {
		Document document = Jsoup.connect(Url).get();
		// String
		// Url="https://www.olx.com.pk/item/suzuki-alto-rs-turbo-paddel-shifter-2016-register-2017-iid-1013830017";

		// Document document = Jsoup.connect(Url).get();
		Elements ad = document.select("div[id=container]");
		String s_Registered = null;
		boolean isWhitespace;

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

		Elements elements = document.select("span[class=_2vNpt]");

		String s_Condition = elements.get(5).text();
		System.out.println("Condition------------- " + s_Condition);

		String s_millage = elements.get(3).text();
		System.out.println("KMS Driven------------- " + s_millage);

		String s_year = elements.get(2).text();
		System.out.println("Year------------- " + s_year);

		String s_model = elements.get(1).text();
		System.out.println("Model------------- " + s_model);

		String s_fuel = elements.get(4).text();
		System.out.println("Fuel------------- " + s_fuel);
		if (s_fuel == null) {
			s_fuel = "N/A";
		}

		s_Registered = elements.get(6).text();
		System.out.println("Registered ---------" + s_Registered);

		if (s_Registered == null) {
			s_Registered = "N/A";
		}

		String s_seller_comments = ad.select(" div[class=rui-2ns2W] p").text();
		System.out.println("Description------------- " + s_seller_comments);

		if (s_seller_comments == null) {
			s_seller_comments = "N/A";
		}

		String s_date = ad.select(" div[class=_2DGqt] span").text();
		System.out.println("Date------------- " + s_date);

		if (s_date == null) {
			s_date = "N/A";
		}

		String s_owner_name = ad.select(
				" #container > main > div > div > div > div.rui-2SwH7.rui-m4D6f.rui-1nZcN.rui-3CPXI.rui-3E1c2.rui-1JF_2 > div.rui-2ns2W.YpyR- > div > div > div._1oSdP > div > a > div")
				.text();
		System.out.println("Owner Name---------------- " + s_owner_name);

		if (s_owner_name == null) {
			s_owner_name = "N/A";
		}

		String s_cell_number = "N/A";
		System.out.println("Cell Number---------------- " + s_cell_number);

		Elements div = document.select("#container .slick-list ").select(" figure").select(" img");
		Map imgDirectoy = new HashMap();
		int i = 1;
		for (Element e : div) {
			System.out.println(e.attr("src"));
			imgDirectoy.put("s_pic_" + i++, e.attr("src"));
		}

		String s_pic_1 = (String) imgDirectoy.get("s_pic_1");
		System.out.println("-------" + s_pic_1);

		System.out.println("------------------------------- END AD ----------------------------");

		String sql = "insert into listing (s_title, s_location,s_Registered, s_fuel, s_seller_comments,s_model,s_Condition,s_millage,"
				+ "s_price,s_make, s_year,s_category,s_website_name,s_detail_link,s_pic_1,s_owner_name,s_date)"
				+ "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";

		PreparedStatement preparedStmt = con.prepareStatement(sql);
		preparedStmt.setString(1, s_title);
		preparedStmt.setString(2, s_location);
		preparedStmt.setString(3, s_Registered);
		preparedStmt.setString(4, s_fuel);
		preparedStmt.setString(5, s_seller_comments);
		preparedStmt.setString(6, s_model);
		preparedStmt.setString(7, s_Condition);
		preparedStmt.setString(8, s_millage);
		preparedStmt.setString(9, s_price);
		preparedStmt.setString(10, s_make);
		preparedStmt.setString(11, s_year);
		preparedStmt.setString(12, "Cars");
		preparedStmt.setString(13, "OLX");
		preparedStmt.setString(14, Url);
		preparedStmt.setString(15, s_pic_1);
		preparedStmt.setString(16, s_owner_name);
		preparedStmt.setString(17, s_date);
		preparedStmt.execute();

		String sqlUpdate = "update links set Status =1 where id=?";
		PreparedStatement preparedUpdateStmt = con.prepareStatement(sqlUpdate);
		preparedUpdateStmt.setInt(1, id);
		preparedUpdateStmt.executeUpdate();

	}

}
