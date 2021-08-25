package FYP.Project;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PakwheelCar implements Runnable {

	public static Connection con = null;

	public static void main(String[] args) throws SQLException, IOException {
		// TODO Auto-generated method stub
		connect();
		proxy();

		PakwheelCar ex = new PakwheelCar();
		Thread t1 = new Thread(ex);
		// t1.start();
		Thread t2 = new Thread(ex);
		// t2.start();
		Thread t3 = new Thread(ex);
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

		Statement stmt = con.createStatement();

		ResultSet rs = stmt.executeQuery(
				"select * from links where  Url<>'' and Website='Pak Wheel' and Status=0 and Category='Featured Used Cars'");

		while (rs.next()) {

			int Id = rs.getInt(1);
			String Url = rs.getString(2);

			getDetailPage(Url, Id);
		}

		for (int i = 151; i >= 1; i--) {
			// getListing("https://www.pakwheels.com/used-cars/search/-/featured_1/?p="+i);
		}
	}

	public void run() {
		int lid = 0;
		try {

			Statement stmt = con.createStatement();

			ResultSet r = stmt.executeQuery(
					"select * from links where Status=0 And Website='Pak Wheel' and Category='Featured Used Cars' ");

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

		String sql = "select * from links where  Url<>'' and Website='Pak Wheel' and Status=0 and Category='Featured Used Cars' and Id=?   ;";

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
		Elements ads = document.select("div[class=search-page-new] ul[class^=list-unstyled]").select("li");
		for (Element e : ads) {

			String aHref = e.select("div[class=search-title-row] a").attr("href");
			if (aHref != "") {

				String href = "https://www.pakwheels.com" + aHref;
				System.out.println("-->" + href);
				Statement stmt = con.createStatement();
				ResultSet resultSet = stmt.executeQuery("select count(1) from links where Url='" + href + "'");
				while (resultSet.next()) {
					int count = resultSet.getInt(1);
					if (count == 0) {
						String sql = "insert into links (Url, Website, Category) values(?,?,?);";
						PreparedStatement preparedStmt = con.prepareStatement(sql);
						preparedStmt.setString(1, href);
						preparedStmt.setString(2, "Pak Wheel");
						preparedStmt.setString(3, "Featured Used Cars");
						preparedStmt.execute();
					} else {
						System.out.println("Link already exists");
					}
				}
			}
		}
	}

	public static void getDetailPage(String Url, int Id) throws IOException, SQLException {
		// Document document =
		// Jsoup.connect("https://www.pakwheels.com/used-cars/honda-vezel-2016-for-sale-in-islamabad-3913983").get();
		// String
		// Url="https://www.pakwheels.com/used-cars/honda-civic-2018-for-sale-in-karachi-3908552";

		String s_title, s_location, s_color = "N/A";
		String s_price, s_year = "N/A";
		String s_millage = "N/A", s_fuel = "N/A";
		String Transmission, s_Registered = "N/A";
		String s_Chassis_No = "N/A", s_Engine_Capacity = "N/A";
		String s_bodytype = "N/A", s_Assembly = "N/A";
		String feature;
		boolean isWhitespace;
		String s_owner_name;
		String s_date = "N/A";
		String s_condition = "N/A";
		String s_dealer_timing = "N/A", s_dealer_address = "N/A", s_dealer_name = "N/A";
		ArrayList<String> s_features = new ArrayList<String>();

		Document document = Jsoup.connect(Url).get();
		Elements ad = document.select("div[class=container]");

		s_title = ad.select("#scroll_car_info h1").text();
		System.out.println("Title---------------- " + s_title);

		s_location = ad.select(".detail-sub-heading").text();
		System.out.println("Location---------------- " + s_location);

		s_price = ad.select("#scrollToFixed .price-box").text();
		s_price = s_price.substring(3);
		s_price = s_price.replaceAll("\\s", "");

		// function name
		s_price = getprice(s_price);// string pass kryn idr
		System.out.println("Price---------------- " + s_price);
		Element table = ad.select("#scroll_car_info").first();
		for (Element column : table.select("td:eq(0)")) {

			s_year = column.text();
			System.out.println("Year ---------" + s_year);

		}
		for (Element column : table.select("td:eq(1)")) {

			s_millage = column.text();
			System.out.println("Millage ---------" + s_millage);

		}
		for (Element column : table.select("td:eq(2)")) {

			s_fuel = column.text();
			System.out.println("fuelType ---------" + s_fuel);

		}

		for (Element column : table.select("td:eq(3)")) {

			Transmission = column.text();
			System.out.println("Transmission ---------" + Transmission);
		}

		Elements div = document.select("div[id=myCarousel]").select("img");

		Map imgDirectoy = new HashMap();
		int i = 1;
		for (Element e : div) {
			// System.out.println(e.attr("data-original"));
			imgDirectoy.put("s_pic_" + i++, e.attr("data-original"));
		}

		String s_pic_1 = (String) imgDirectoy.get("s_pic_1");
		String s_pic_2 = (String) imgDirectoy.get("s_pic_2");
		String s_pic_3 = (String) imgDirectoy.get("s_pic_3");
		String s_pic_4 = (String) imgDirectoy.get("s_pic_4");

		System.out.println(s_pic_1);
		System.out.println(s_pic_2);
		System.out.println(s_pic_3);
		System.out.println(s_pic_4);

		Elements features = document.select("#scroll_car_detail li");
		for (Element e : features.select("li:eq(1)")) {

			s_Registered = e.text();
			System.out.println("Registered -------" + s_Registered);
		}

		for (Element e : features.select("li:eq(3)")) {

			s_color = e.text();
			System.out.println("Color ----------" + s_color);
		}

		for (Element e : features.select("li:eq(5)")) {

			s_Assembly = e.text();
			System.out.println("Assembly --------" + s_Assembly);
		}
		for (Element e : features.select("li:eq(9)")) {

			s_bodytype = e.text();
			System.out.println("Body Type --------" + s_bodytype);
		}

		for (Element e : features.select("li:eq(11)")) {

			s_date = e.text();
			System.out.println("Date --------" + s_date);
		}

		for (Element e : features.select("li:eq(17)")) {

			s_Chassis_No = e.text();
			System.out.println("Chassis No ------" + s_Chassis_No);
		}

		for (Element e : features.select("li:eq(7)")) {

			s_Engine_Capacity = e.text();
			System.out.println("Engine Capacity ------" + s_Engine_Capacity);
		}

		s_owner_name = ad.select("div[class=well] h5[class=nomargin]").text();
		System.out.println("Owner Name---------------- " + s_owner_name);
		isWhitespace = s_owner_name.matches("^\\s*$");

		if (isWhitespace == true)

		{
			s_owner_name = "N/A";

			System.out.println("Owner Name---------------- " + s_owner_name);

		}

		String s_cell_number;
		String first11 = ad.select("div[class^=well]  span").text();
		System.out.println("Cell Number---------------- " + first11);
		if (first11.charAt(0) == 'A' || first11.charAt(1) == 'A') {
			s_cell_number = first11.substring(15, 28);
		} else {
			s_cell_number = first11.substring(0, 11);
		}
		System.out.println("Cell Number---------------- " + s_cell_number);

		s_dealer_name = ad.select("#scrollToFixed  div[class=seller-info] div[class^=col-md-9] label[itemprop=name]")
				.text();
		System.out.println("Dealer Name---------------- " + s_dealer_name);
		isWhitespace = s_dealer_name.matches("^\\s*$");

		if (isWhitespace == true)

		{
			s_dealer_name = "N/A";

			System.out.println("Dealer Name---------------- " + s_dealer_name);

		}

		s_dealer_address = ad.select("div[class=seller-info]  div.col-md-9 label[itemprop=address]").text();
		System.out.println("Dealer Address---------------- " + s_dealer_address);
		isWhitespace = s_dealer_address.matches("^\\s*$");

		if (isWhitespace == true)

		{
			s_dealer_address = "N/A";

			System.out.println("Dealer Address---------------- " + s_dealer_address);

		}

		s_dealer_timing = ad.select("div[class=seller-info] div:nth-child(3) div.col-md-9 label").text();
		System.out.println("timings---------------- " + s_dealer_timing);
		isWhitespace = s_dealer_timing.matches("^\\s*$");

		if (isWhitespace == true)

		{
			s_dealer_timing = "N/A";

			System.out.println("timings---------------- " + s_dealer_timing);

		}

		s_condition = ad.select("div[id=scroll_carsure_report] div[class^=right]").text();
		System.out.println("Condition---------------- " + s_condition);
		isWhitespace = s_condition.matches("^\\s*$");

		if (isWhitespace == true)

		{
			s_condition = "N/A";

			System.out.println("Condition---------------- " + s_condition);

		}

		String s_seller_comments = ad.select("div[id=scroll_car_info] div[class^=primary-lang]").text();
		System.out.println("Seller Comments---------------- " + s_seller_comments);

		isWhitespace = s_seller_comments.matches("^\\s*$");

		if (isWhitespace == true)

		{
			s_seller_comments = "N/A";

			System.out.println("Seller Comments---------------- " + s_seller_comments);

		}

		Elements info = document.select("div[id=scroll_car_info] ul[class*=list-unstyled car-feature-list nomargin ]");
		for (Element e : info.select("li")) {

			feature = e.text();
			// System.out.println("Car Feature----------"+ feature);

			s_features.add(feature);

		}

		// System.out.println("Car Feature----------"+ s_features);

		StringBuffer sb = new StringBuffer();

		for (String s : s_features) {
			sb.append(s);
			sb.append(",");
		}
		String str = sb.toString();
		System.out.println(str);

		if (str == null) {
			str = "N/A";
		}

		System.out.println("------------------------------- END AD ----------------------------");

		// String sql = "insert into listing (s_title, s_location,s_Registered,
		// s_fuel,s_millage,"
		// + "s_price,s_Engine_Capacity,s_bodytype,s_color, s_Assembly,s_Chassis_No,
		// s_year,"
		// +
		// "s_category,s_website_name,s_features,s_detail_link,s_owner_name,s_cell_number,"
		// +
		// "s_dealer_name,s_dealer_address,s_dealer_timing,s_seller_comments,s_pic_1,s_pic_2,s_pic_3,s_pic_4,s_date,s_Condition)"
		// + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
		//
		// PreparedStatement preparedStmt = con.prepareStatement(sql);
		// preparedStmt.setString(1, s_title);
		// preparedStmt.setString(2, s_location);
		// preparedStmt.setString(3, s_Registered);
		// preparedStmt.setString(4, s_fuel);
		// preparedStmt.setString(5, s_millage);
		// preparedStmt.setString(6, s_price);
		// preparedStmt.setString(7, s_Engine_Capacity);
		// preparedStmt.setString(8, s_bodytype);
		// preparedStmt.setString(9, s_color);
		// preparedStmt.setString(10, s_Assembly);
		// preparedStmt.setString(11, s_Chassis_No);
		// preparedStmt.setString(12, s_year);
		// preparedStmt.setString(13, "Cars");
		// preparedStmt.setString(14, "Pak Wheel");
		// preparedStmt.setString(15, str);
		// preparedStmt.setString(16, Url);
		// preparedStmt.setString(17, s_owner_name);
		// preparedStmt.setString(18, s_cell_number);
		// preparedStmt.setString(19, s_dealer_name);
		// preparedStmt.setString(20, s_dealer_address);
		// preparedStmt.setString(21, s_dealer_timing);
		// preparedStmt.setString(22, s_seller_comments);
		// preparedStmt.setString(23, s_pic_1);
		// preparedStmt.setString(24, s_pic_2);
		// preparedStmt.setString(25, s_pic_3);
		// preparedStmt.setString(26, s_pic_4);
		// preparedStmt.setString(27, s_date);
		// preparedStmt.setString(28, s_condition);
		// preparedStmt.execute();
		//
		// String sqlUpdate = "update links set Status =1 where id=?";
		// PreparedStatement preparedUpdateStmt = con.prepareStatement(sqlUpdate);
		// preparedUpdateStmt.setInt(1, Id);
		// preparedUpdateStmt.executeUpdate();

	}

	public static String getprice(String s_price) throws IOException, SQLException {

		if (s_price.contains(".")) {
			s_price = s_price.replace(".", "");
			s_price = s_price.replaceAll("lacs", "0000");
			s_price = s_price.replaceAll("crore", "00000");

			return s_price;
		} else {
			s_price = s_price.replaceAll("lacs", "00000");
			s_price = s_price.replaceAll("crore", "000000");
			return s_price;
		}
		/*
		 * if(!(s_price.contains("."))) { System.out.println("Price---------------- "
		 * +s_price ); }
		 */

	}

}
