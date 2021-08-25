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

public class Autodeals {

	public static Connection con = null;

	public static void main(String[] args) throws SQLException, IOException {
		// TODO Auto-generated method stub
		connect();
		proxy();

		Statement stmt = con.createStatement();

		// ResultSet rs = stmt.executeQuery("select * from links where Url<>'' and
		// Website='AutoDeals' and Status=0 and Category='Cars'");
		//
		// while (rs.next()) {
		//
		// int Id = rs.getInt(1);
		// String Url = rs.getString(2);

		// getDetailPage(Id,Url);

		// getDetailPage();

		String Url = "https://autodeals.pk/inventory/";

		for (int i = 1; i <= 198; i++) {

			getListing(Url);
		}

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
		Elements ads = document.select("#main");
		for (Element e : ads) {

			String aHref = e.select("#listings-result ").select(" a").attr("href"); // y line hai jo ads ky links crawl kr rhy hai 
// y jisy abi hightlight kea y jsoup use ki hai or web main inspect kr ky dakha hai 
			if (aHref != "") {

				String href = aHref;
				System.out.println("-->" + href);
				Statement stmt = con.createStatement();
				// ResultSet resultSet = stmt.executeQuery("select count(1) from links where
				// Url='" + href + "'");
				// while (resultSet.next()) {
				// int count = resultSet.getInt(1);
				// if (count == 0) {
				// String sql = "insert into links (Url, Website, Category) values(?,?,?);";
				// PreparedStatement preparedStmt = con.prepareStatement(sql);
				// preparedStmt.setString(1, href);
				// preparedStmt.setString(2, "AutoDeals");
				// preparedStmt.setString(3, "Cars");
				// preparedStmt.execute();
				// } else {
				// System.out.println("Link already exists");
				// }
				// }
			}
		}
	}

	public static void getDetailPage() throws IOException, SQLException {
		Document document = Jsoup.connect("https://www.autodeals.pk/listings/new-toyota-land-cruiser-2016/").get();

		ArrayList<String> s_features = new ArrayList<String>();

		// Document document = Jsoup.connect(Url).get();
		Elements ad = document.select("div[class=container]");

		String s_title = ad.select("div[class=row] h1").text();
		System.out.println("Title---------------- " + s_title);

		String s_email = ad.select("#stm_dealer_car_info-2 div[class=address] a").text();
		System.out.println("Email---------------- " + s_email);

		String s_price = ad.select("div[class=price]").text();
		System.out.println("Price---------------- " + s_price);

		String s_bodytype = document.select("[class*=stm-table-main]").select("tr").get(1).select("td").get(1).text()
				.toString();
		System.out.println("Body ---------" + s_bodytype);

		String s_fuel = document.select("[class*=inner-table]").select("td").get(5).text().toString();
		System.out.println("fuelType ---------" + s_fuel);

		String s_owner_name = ad.select("#stm_dealer_car_info-2 div[class^=clearfix ] h3").text();
		System.out.println("Owner Name---------------- " + s_owner_name);

		String Transmission = document.select("[class*=inner-table]").select("td").get(9).text().toString();
		System.out.println("Transmission ---------" + Transmission);

		String s_Engine_Capacity = document.select("[class*=inner-table]").select("td").get(7).text().toString();
		System.out.println("Engine Capacity ------" + s_Engine_Capacity);

		String s_millage = document.select("[class*=inner-table]").select("td").get(4).text().toString();
		System.out.println("Millage/killometer ------" + s_millage);

		String s_color = document.select("[class*=inner-table]").select("td").get(13).text().toString();
		System.out.println("Color ----------" + s_color);

		String s_drive = document.select("[class*=inner-table]").select("td").get(11).text().toString();
		System.out.println("Drive ----------" + s_drive);

		Elements div = document.select(" div[id^=big-image]").select("img");

		Map imgDirectoy = new HashMap();
		int i = 1;
		for (Element e : div) {
			// System.out.println(e.attr("src"));
			imgDirectoy.put("s_pic_" + i++, e.attr("src"));
		}

		String s_pic_1 = (String) imgDirectoy.get("s_pic_1");
		String s_pic_2 = (String) imgDirectoy.get("s_pic_2");
		String s_pic_3 = (String) imgDirectoy.get("s_pic_3");
		String s_pic_4 = (String) imgDirectoy.get("s_pic_4");

		System.out.println(s_pic_1);
		System.out.println(s_pic_2);
		System.out.println(s_pic_3);
		System.out.println(s_pic_4);

		String s_seller_comments = ad.select("div[class=row] p").text();
		System.out.println("Seller Comments---------------- " + s_seller_comments);

		Elements info = document.select("div[class^=lists-inline] ul[class^=list-style-2]  ");
		for (Element e : info.select("li")) {

			String features = e.text();
			// System.out.println("Car Feature----------"+ features);

			s_features.add(features);

		}

		// System.out.println("Car Feature----------"+ s_features);

		StringBuffer sb = new StringBuffer();

		for (String s : s_features) {
			sb.append(s);
			sb.append("");
		}
		String str = sb.toString();
		System.out.println(str);

		String s_cell_number = ad.select(" div[class^=dealer-contact-unit ] div[class^=phone ]").text();
		System.out.println("Cell Number---------------- " + s_cell_number);

		String sql = "insert into listing (s_title, s_fuel,s_millage,"
				+ "s_price,s_Engine_Capacity,s_bodytype,s_color,s_drive, "
				+ "s_category,s_website_name,s_features,s_owner_name,s_cell_number,"
				+ "s_seller_comments,s_pic_1,s_pic_2,s_pic_3,s_pic_4)" + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";

		PreparedStatement preparedStmt = con.prepareStatement(sql);
		preparedStmt.setString(1, s_title);
		preparedStmt.setString(2, s_fuel);
		preparedStmt.setString(3, s_millage);
		preparedStmt.setString(4, s_price);
		preparedStmt.setString(5, s_Engine_Capacity);
		preparedStmt.setString(6, s_bodytype);
		preparedStmt.setString(7, s_color);
		preparedStmt.setString(8, s_drive);
		preparedStmt.setString(9, "Cars");
		preparedStmt.setString(10, "Auto Deals");
		preparedStmt.setString(11, str);
		preparedStmt.setString(12, s_owner_name);
		preparedStmt.setString(13, s_cell_number);
		preparedStmt.setString(14, s_seller_comments);
		preparedStmt.setString(15, s_pic_1);
		preparedStmt.setString(16, s_pic_2);
		preparedStmt.setString(17, s_pic_3);
		preparedStmt.setString(18, s_pic_4);
		preparedStmt.execute();

		// String sqlUpdate = "update links set Status =1 where id=?";
		// PreparedStatement preparedUpdateStmt = con.prepareStatement(sqlUpdate);
		// preparedUpdateStmt.setInt(1, Id);
		// preparedUpdateStmt.executeUpdate();

	}

	public static String proxy() throws IOException, SQLException {

		Statement stmt = con.createStatement();
		ResultSet r = stmt.executeQuery("select * from proxy ORDER BY RAND() LIMIT 1");
		String p = "n/a";
		while (r.next()) {
			p = r.getString("proxy");
		}
		// System.out.println("Proxy: "+ p);
		return p;

	}

	public static void setproxy(String p) throws IOException {
		System.setProperty("http.proxyHost", p);

		// System.setProperty("http.proxyPort", "3128");
	}

}