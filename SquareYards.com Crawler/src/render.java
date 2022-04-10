import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;		
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
public class render {

	//Fix ALT IMAGE TEXT --- FIX EXCEL TEMPLATE PAGE -- WORK ON SPLITTING FLOOR IMAGES

	public static void main(String[] args) throws IOException, InterruptedException {
		//getProductUrl();
		scrapeProductUrl();
	}

	public static void getProductUrl(String urlLink) throws IOException, InterruptedException {

		int totalPages = 6;
		BufferedWriter writer = new BufferedWriter(new FileWriter("SquareYearsURLs123.txt"));

		for (int i = 1; i <= totalPages; i++) {		
			String dataToScrape = "https://www.squareyards.com/search?developerId=104&page=" +  i;
			//System.out.println(dataToScrape);
			Thread.sleep(1000);

			LinkedList<String> housesURL = new LinkedList<String>();


			URL url = new URL (dataToScrape);
			URL url2 = new URL (dataToScrape);

			HttpURLConnection connection = (HttpURLConnection) url2.openConnection();
			connection.setRequestMethod("GET");
			System.setProperty("http.agent", "");
			connection.connect();

			int code = connection.getResponseCode();
			if (code == 429) {
				System.out.println("Got here");

			}

			else {

				Document doc = Jsoup.connect(url.toString()).userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36").get();
				String docForRegex = doc.toString();
				//System.out.println(docForRegex);


				Pattern linkPattern = Pattern.compile("(?<=togclickurl=\")(.*)(?=\"><\\/span>)");
				Matcher itemURLMatcher = linkPattern.matcher(docForRegex);
				while(itemURLMatcher.find()){
					housesURL.add(itemURLMatcher.group() + "-");
				}



				for (int j = 0; j < housesURL.size(); j++ ) {
					writer.write(housesURL.get(j) + "\n");
					System.out.println(housesURL.get(j));
				}



			}


		} 
		writer.close();
	} 

	public static void scrapeProductUrl() throws IOException, InterruptedException {

		File file = new File("SquareYearsURLs123.txt"); 
		String ans = "";
		int maxImageAmount = 15;

		BufferedReader br = new BufferedReader(new FileReader(file)); 

		String st; 
		while ((st = br.readLine()) != null) {
			ans = ans + st + "\n";
		} 

		String [] final1 = ans.split("\n");
		int counter = 1;

		for (int i = 0; i < final1.length; i++) {

			String dataToScrape = final1[i];
			//System.out.println(dataToScrape);
			Thread.sleep(1000);


			LinkedList<String> posterLink = new LinkedList<String>();

			URL url2 = new URL (dataToScrape);

			Document doc = Jsoup.connect(url2.toString()).userAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.110 Safari/537.36").ignoreHttpErrors(true).get();

			String docForRegex = doc.toString();
			//System.out.println(docForRegex);

			LinkedList projectLL = new LinkedList();
			Pattern linkPattern = Pattern.compile("(?<=\"name\":\")(.*)(?=\",\"d)");
			Matcher projectMatcher = linkPattern.matcher(docForRegex);
			while(projectMatcher.find()){
				projectLL.add(projectMatcher.group());
			}


			int imagesLLSizeOriginal = 0;

			LinkedList imagesLL = new LinkedList();
			Pattern linkPattern2 = Pattern.compile("(?<=<img itemprop=\"image\" class=\"img-responsive\" src=\")(.*)(?=\\\" alt=\\\")");
			Matcher imagesMatcher = linkPattern2.matcher(docForRegex);
			while(imagesMatcher.find()){
				imagesLL.add(imagesMatcher.group() + "|");
			}

			//imagesLLSizeOriginal = imagesLL.size();
			//System.out.println(imagesLLSizeOriginal);

			int imageAmountMax = 15;

			if (imagesLL.size() < imageAmountMax) {
				int difference = imageAmountMax-imagesLL.size();
				for (int b = 0; b < difference; b++) {
					imagesLL.add(" - |");
				}
			}


			LinkedList imagesAltTextLL = new LinkedList();
			for (int a = 0; a < imagesLL.size(); a++) {

				String patternForAlt = "(?<=" + imagesLL.get(a).toString().replace("/","\\/").replace("?","\\?").replace(".","\\.").replace("|", "") + "\\\" alt=\\\")(.*)(?=\\\")";		
				//System.out.println(patternForAlt);
				Pattern linkPattern32 = Pattern.compile(patternForAlt);
				Matcher imagesAltTextMatcher = linkPattern32.matcher(docForRegex);

				while(imagesAltTextMatcher.find()){
					imagesAltTextLL.add(imagesAltTextMatcher.group() + "|");
				}
			}


			int temp1 = imagesAltTextLL.size();
			int temp2 = imagesLL.size();
			int temp3 = temp2 - temp1;
			for (int c = 0; c <temp3;c++) {
				imagesAltTextLL.add(" - |");
			}



			LinkedList localityLL = new LinkedList();
			Pattern linkPattern3 = Pattern.compile("(?<=\"addressLocality\":\")(.*)(?=\",\"address)");
			Matcher localityMatcher = linkPattern3.matcher(docForRegex);
			while(localityMatcher.find()){
				localityLL.add(localityMatcher.group());
			}

			LinkedList cityLL = new LinkedList();
			Pattern linkPattern4 = Pattern.compile("(?<=\"addressRegion\":\")(.*)(?=\"},\"address)");
			Matcher cityMatcher = linkPattern4.matcher(docForRegex);
			while(cityMatcher.find()){
				cityLL.add(cityMatcher.group());
			}

			LinkedList priceTextLL = new LinkedList();
			Pattern linkPattern5 = Pattern.compile("(?<=          <div class=\"priceBox\">\n" + 
					"             ₹ )(.*)(?=\n)");
			Matcher priceTextMatcher = linkPattern5.matcher(docForRegex);
			while(priceTextMatcher.find()){
				priceTextLL.add(priceTextMatcher.group());
			}

			LinkedList priceLowLL = new LinkedList();
			Pattern linkPattern6 = Pattern.compile("(?<=<span itemprop=\"minPrice\">)(.*)(?=<\\/span>)");
			Matcher priceLowMatcher = linkPattern6.matcher(docForRegex);
			while(priceLowMatcher.find()){
				priceLowLL.add(priceLowMatcher.group());
			}

			LinkedList priceHighLL = new LinkedList();
			Pattern linkPattern7 = Pattern.compile("(?<=<span itemprop=\"maxPrice\">)(.*)(?=<\\/span>)");
			Matcher priceHighMatcher = linkPattern7.matcher(docForRegex);
			while(priceHighMatcher.find()){
				priceHighLL.add(priceHighMatcher.group());
			}

			LinkedList stageLL = new LinkedList();
			Pattern linkPattern8 = Pattern.compile("(?<=<div class=\"statusBox\">\n" + 
					"        Status: \n" + 
					"        <span>)(.*)(?=<\\/span>)");
			Matcher stageMatcher = linkPattern8.matcher(docForRegex);
			while(stageMatcher.find()){
				stageLL.add(stageMatcher.group());
			}

			LinkedList summaryLL = new LinkedList();
			Pattern linkPattern9 = Pattern.compile("(?<=<div class=\"highlightBox\"> \n" + 
					"        <ul> \n" + 
					"         <li>)([\\S\\s]*)(?=<\\/li> \n" + 
					"        <\\/ul> \n" + 
					"       <\\/div> \n" + 
					"      <\\/div> \n" + 
					"     <\\/div> \n" + 
					"    <\\/div> \n" + 
					"   <\\/div> )");
			Matcher summaryMatcher = linkPattern9.matcher(docForRegex);
			while(summaryMatcher.find()){
				summaryLL.add(summaryMatcher.group());
			}

			LinkedList reraLL = new LinkedList();
			Pattern linkPattern10 = Pattern.compile("(?<=project; <br>1\\. )(.*)(?=<br><\\/span> <\\/li>)");
			Matcher reraMatcher = linkPattern10.matcher(docForRegex);
			while(reraMatcher.find()){
				reraLL.add(reraMatcher.group());
			}

			LinkedList projectInfoLL = new LinkedList();
			Pattern linkPattern11 = Pattern.compile("(?<=<div itemprop=\"description\"> \n" + 
					"        <p>)(.*)(?=<\\/p>)");
			Matcher projectInfoMatcher = linkPattern11.matcher(docForRegex);
			while(projectInfoMatcher.find()){
				projectInfoLL.add(projectInfoMatcher.group());
			}

			LinkedList floorTypeLL = new LinkedList();
			Pattern linkPattern12 = Pattern.compile("(?<=<\\/tr> \n" + 
					"          <tr> \n" + 
					"           <td>)(.*)(?=<\\/td>)");
			Matcher floorTypeMatcher = linkPattern12.matcher(docForRegex);
			while(floorTypeMatcher.find()){
				floorTypeLL.add(floorTypeMatcher.group());
			}

			LinkedList floorNameLL = new LinkedList();
			Pattern linkPattern13 = Pattern.compile("(?<=<small itemprop=\"name\">)(.*)(?=<\\/small> <\\/a> <\\/p> )");
			Matcher floorNameMatcher = linkPattern13.matcher(docForRegex);
			while(floorNameMatcher.find()){
				floorNameLL.add(floorNameMatcher.group());
			}

			LinkedList floorPageURLLL = new LinkedList();
			Pattern linkPattern14 = Pattern.compile("(?<=<a itemprop=\"url\" href=\")(.*)(?=\"> <small itemprop=\"name\">)");
			Matcher floorPageURLMatcher = linkPattern14.matcher(docForRegex);
			while(floorPageURLMatcher.find()){
				floorPageURLLL.add(floorPageURLMatcher.group());
			}

			int floorPlanAmount = 7;

			if (floorPageURLLL.size() < floorPlanAmount ) {
				int difference = floorPlanAmount -floorPageURLLL.size();
				for (int b = 0; b < difference; b++) {
					floorPageURLLL.add(" --- ");
				}
			}

			LinkedList floorImageURLLL = new LinkedList();
			Pattern linkPattern15 = Pattern.compile("(?<=<img itemprop=\"image\" class=\"img-responsive lazy\" data-src=\")(.*)(?=\" alt=\")");
			Matcher floorImageURLMatcher = linkPattern15.matcher(docForRegex);
			while(floorImageURLMatcher.find()){
				floorImageURLLL.add(floorImageURLMatcher.group());
			}

			while (floorImageURLLL.size() < floorPlanAmount ) {
				int difference = floorPlanAmount -floorImageURLLL.size();
				for (int b = 0; b < difference; b++) {
					floorImageURLLL.add(" --- ");
				}
			}


			LinkedList floorText1LL = new LinkedList();
			Pattern linkPattern16 = Pattern.compile("(?<=<ul> \n" + 
					"            <li>)(.*)(?=<\\/li> \n" + 
					"            <li>)");
			Matcher floorText1Matcher = linkPattern16.matcher(docForRegex);
			while(floorText1Matcher.find()){
				floorText1LL.add(floorText1Matcher.group());
			}

			if (floorText1LL.size() < floorPlanAmount ) {
				int difference = floorPlanAmount -floorText1LL.size();
				for (int b = 0; b < difference; b++) {
					floorText1LL.add(" - ");
				}
			}

			LinkedList floorSquareFeetLL = new LinkedList();
			Pattern linkPattern17 = Pattern.compile("(?<=<li>Saleable Area : <strong>)(.*)(?=<\\/strong>)");
			Matcher floorSquareFeetMatcher = linkPattern17.matcher(docForRegex);
			while(floorSquareFeetMatcher.find()){
				floorSquareFeetLL.add(floorSquareFeetMatcher.group());
			}

			if (floorSquareFeetLL.size() < floorPlanAmount ) {
				int difference = floorPlanAmount -floorSquareFeetLL.size();
				for (int b = 0; b < difference; b++) {
					floorSquareFeetLL.add(" - ");
				}
			}

			LinkedList priceCurrencyLL = new LinkedList();
			Pattern linkPattern18 = Pattern.compile("(?<=<small itemprop=\"priceCurrency\">)(.*)(?=<\\/small> <small)");
			Matcher priceCurrencyMatcher = linkPattern18.matcher(docForRegex);
			while(priceCurrencyMatcher.find()){
				priceCurrencyLL.add(priceCurrencyMatcher.group());
			}

			LinkedList actualPriceLL = new LinkedList();
			Pattern linkPattern19 = Pattern.compile("(?<=<small itemprop=\"price\">)(.*)(?=<\\/small>)");
			Matcher actualPriceMatcher = linkPattern19.matcher(docForRegex);
			while(actualPriceMatcher.find()){
				actualPriceLL.add(actualPriceMatcher.group());
			}

			LinkedList addressLL = new LinkedList();
			Pattern linkPattern20 = Pattern.compile("(?<=<\\/h2> \n" + 
					"           <span>)(.*)(?=<\\/span>)");
			Matcher addressMatcher = linkPattern20.matcher(docForRegex);
			while(addressMatcher.find()){
				addressLL.add(addressMatcher.group());
			}

			LinkedList aHeadingLL = new LinkedList();
			Pattern linkPattern21 = Pattern.compile("(?<=<\\/figure> <span>)(.*)(?=<\\/span> <\\/li>)");
			Matcher aHeadingMatcher = linkPattern21.matcher(docForRegex);
			while(aHeadingMatcher.find()){
				aHeadingLL.add(aHeadingMatcher.group());
			}

			LinkedList aIconLL = new LinkedList();
			Pattern linkPattern22 = Pattern.compile("(?<=        <div class=\"amenitiesDetails\"> \n" + 
					"         <ul> \n" + 
					"          <li> \n" + 
					"           <figure> \n" + 
					"            <img class=\"img-responsive lazy\" data-src=\")(.*)(?=\"> \n" + 
					"           <\\/figure> <span>)");
			Matcher aIconMatcher = linkPattern22.matcher(docForRegex);
			while(aIconMatcher.find()){
				aIconLL.add(aIconMatcher.group());
			}

			LinkedList road1LL = new LinkedList();
			Pattern linkPattern23 = Pattern.compile("(?<=<span itemprop=\"name\" id=\"Project_Connecting_Roads)(.*)(?=<\\/span><\\/a>)");
			Matcher road1Matcher = linkPattern23.matcher(docForRegex);
			while(road1Matcher.find()){
				road1LL.add(road1Matcher.group());
			}

			LinkedList aboutDevLL = new LinkedList();
			Pattern linkPattern24 = Pattern.compile("(?<=<a itemprop=\"url\" href=\"\\/)(.*)(?=\"> <span)");
			Matcher aboutDevMatcher = linkPattern24.matcher(docForRegex);
			while(aboutDevMatcher.find()){
				aboutDevLL.add(aboutDevMatcher.group());
			}

			LinkedList devLogoLL = new LinkedList();
			Pattern linkPattern25 = Pattern.compile("(?<=https:\\/\\/static.squareyards.com\\/resources\\/images\\/developerlogo\\/)(.*)(?= alt=\")");
			Matcher devLogoMatcher = linkPattern25.matcher(docForRegex);
			while(devLogoMatcher.find()){
				devLogoLL.add(devLogoMatcher.group());
			}

			LinkedList builderProjectsLL = new LinkedList();
			Pattern linkPattern26 = Pattern.compile("(?<=Projects<\\/p> \n" + 
					"        <h5>)(.*)(?=<\\/h5>)");
			Matcher builderProjectsMatcher = linkPattern26.matcher(docForRegex);
			while(builderProjectsMatcher.find()){
				builderProjectsLL.add(builderProjectsMatcher.group());
			}

			LinkedList builderExperienceLL = new LinkedList();
			Pattern linkPattern27 = Pattern.compile("(?<=Experience<\\/p> \n" + 
					"        <h5>)(.*)(?=<\\/h5>)");
			Matcher builderExperienceMatcher = linkPattern27.matcher(docForRegex);
			while(builderExperienceMatcher.find()){
				builderExperienceLL.add(builderExperienceMatcher.group());
			}


			LinkedList builderDescriptionLL = new LinkedList();
			Pattern linkPattern28 = Pattern.compile("(?<=<div itemprop=\"description\"> \n" + 
					"        <p>)([\\s\\S]*)(?=<\\/div> \n" + 
					"       <div class=\"readMoreBox\")");
			Matcher builderDescriptionMatcher = linkPattern28.matcher(docForRegex);
			while(builderDescriptionMatcher.find()){
				builderDescriptionLL.add(builderDescriptionMatcher.group());
			}

			LinkedList projectDescriptionLL = new LinkedList();
			Pattern linkPattern29 = Pattern.compile("(?<=<div id=\"dev_projectdesc\" itemprop=\"description\"> \n" + 
					"        <p>)(.*)(?=<\\/p>)");
			Matcher projectDescriptionMatcher = linkPattern29.matcher(docForRegex);
			while(projectDescriptionMatcher.find()){
				projectDescriptionLL.add(projectDescriptionMatcher.group());
			}



			System.out.print("101 |");

			System.out.print(dataToScrape + "|");

			for (int a = 0; a < projectLL.size(); a++) {
				System.out.print(projectLL.get(a).toString() + "---");
			}

			System.out.print("|");

			System.out.print("IMAGES |");


			for (int a = 0; a < imagesLL.size(); a++) {
				System.out.print(imagesLL.get(a).toString());
				System.out.print(imagesAltTextLL.get(a).toString());
			}

			System.out.print(" |");

			System.out.print("PRODUCT DETAILS |");

			for (int a = 0; a < localityLL.size(); a++) {
				System.out.print(localityLL.get(a).toString() + "---");
			}

			System.out.print(" |");

			for (int a = 0; a < cityLL.size(); a++) {
				System.out.print(cityLL.get(a).toString() + "---");
			}

			System.out.print(" |");

			for (int a = 0; a < priceTextLL.size(); a++) {
				System.out.print(priceTextLL.get(a).toString() + "---");
			}

			System.out.print(" |");


			if (priceLowLL.size() == 0) {
				System.out.print(" - ");
			}
			else {
				for (int a = 0; a < priceLowLL.size(); a++) {
					System.out.print(priceLowLL.get(a).toString() + "---");
				}	
			}

			System.out.print(" |");

			if (priceHighLL.size() == 0) {
				System.out.print(" - ");
			}
			else {
				for (int a = 0; a < priceHighLL.size(); a++) {
					System.out.print(priceHighLL.get(a).toString() + "---");
				}
			}

			System.out.print(" |");

			for (int a = 0; a < stageLL.size(); a++) {
				System.out.print(stageLL.get(a).toString() + "---");
			}

			System.out.print(" |");

			for (int a = 0; a < summaryLL.size(); a++) {
				System.out.print(summaryLL.get(a).toString().replace("\n", " - ").replace("</li>", "").replace("<li>", "") + "---");
			}

			System.out.print(" |");

			if (reraLL.size() == 0) {
				System.out.print(" - ");
			}
			else {
				for (int a = 0; a < reraLL.size(); a++) {
					System.out.print(reraLL.get(a).toString() + "---");
				}
			}

			System.out.print(" |");

			if (projectInfoLL.size() == 0) {
				System.out.print(" - ");
			}
			else {
				for (int a = 0; a < projectInfoLL.size(); a++) {
					System.out.print(projectInfoLL.get(a).toString() + "---");
				}
			}

			System.out.print(" |");

			System.out.print("FLOOR PLANS |");

			if (floorTypeLL.size() == 0) {
				System.out.print(" - ");
			}
			else {
				for (int a = 0; a < 1; a++) {
					System.out.print(floorTypeLL.get(a).toString() + "---");
				}
			}

			System.out.print(" |");

			if (floorNameLL.size() == 0) {
				System.out.print(" - ");
			}
			else {
				for (int a = 0; a < 1; a++) {
					System.out.print(floorNameLL.get(a).toString() + "---");
				}
			}

			System.out.print(" |");

			if (floorPageURLLL.size() == 0) {
				System.out.print(" - ");
			}
			else if (floorPageURLLL.size() > 7) {
				for (int a = 0; a < 7; a++) {
					if (floorPageURLLL.get(a).toString().equals(" --- ")) {
						System.out.print("--- | --- | --- | --- |");

					}
					else {
						System.out.print(floorPageURLLL.get(a).toString() + "|");
						System.out.print(floorImageURLLL.get(a).toString() + "|");
						System.out.print(floorSquareFeetLL.get(a).toString() + "|");
						System.out.print(floorText1LL.get(a).toString() + "|");
					}

				}
			}
			else {
				for (int a = 0; a < floorPageURLLL.size(); a++) {
					if (floorPageURLLL.get(a).toString().equals(" --- ")) {
						System.out.print("--- | --- | --- | --- |");

					}
					else {
						System.out.print(floorPageURLLL.get(a).toString() + "|");
						System.out.print(floorImageURLLL.get(a).toString() + "|");
						System.out.print(floorSquareFeetLL.get(a).toString() + "|");
						System.out.print(floorText1LL.get(a).toString() + "|");
					}
				}
			}

			//System.out.print(" |");

			/*if (floorImageURLLL.size() == 0) {
				System.out.print(" - ");
			}
			else {
				for (int a = 0; a < floorImageURLLL.size(); a++) {
					System.out.print(floorImageURLLL.get(a).toString() + "---");
				}
			} 

			System.out.print(" |");


			if (floorText1LL.size() == 0) {
				System.out.print(" - ");
			}
			else {
				for (int a = 0; a < 1; a++) {
					System.out.print(floorText1LL.get(a).toString() + "---");
				}
			} 

			System.out.print(" |");

			/*if (floorSquareFeetLL.size() == 0) {
				System.out.print(" - ");
			}
			else {
				for (int a = 0; a < floorSquareFeetLL.size(); a++) {
					System.out.print(floorSquareFeetLL.get(a).toString() + "---");
				}
			} */

			//System.out.print(" |");

			if (priceCurrencyLL.size() == 0) {
				System.out.print(" - ");
			}
			else {
				for (int a = 0; a < 1; a++) {
					System.out.print(priceCurrencyLL.get(a).toString() + "---");
				}
			}

			System.out.print(" |");

			if (actualPriceLL.size() == 0) {
				System.out.print(" - ");
			}
			else {
				for (int a = 0; a < actualPriceLL.size(); a++) {
					System.out.print(actualPriceLL.get(a).toString() + "---");
				}
			}

			System.out.print(" |");

			if (addressLL.size() == 0) {
				System.out.print(" - ");
			}
			else {
				for (int a = 0; a < addressLL.size(); a++) {
					System.out.print(addressLL.get(a).toString() + "---");
				}
			}

			System.out.print(" |");

			System.out.print("AMENITIES |");

			if (aHeadingLL.size() == 0) {
				System.out.print(" - ");
			}
			else {
				for (int a = 0; a < 1; a++) {
					System.out.print(aHeadingLL.get(a).toString() + "---");
				}
			}

			System.out.print(" |");

			if (aIconLL.size() == 0) {
				System.out.print(" - ");
			}
			else {
				for (int a = 0; a < 1; a++) {
					System.out.print(aIconLL.get(a).toString() + "---");
				}
			}

			System.out.print(" |");

			System.out.print("CONNECTING ROADS |");

			if (road1LL.size() == 0) {
				System.out.print(" - ");
			}
			else {
				for (int a = 0; a < road1LL.size(); a++) {
					System.out.print(road1LL.get(a).toString() + "---");
				}
			}

			System.out.print(" |");

			if (aboutDevLL.size() == 0) {
				System.out.print(" - ");
			}
			else {
				for (int a = 0; a < aboutDevLL.size(); a++) {
					System.out.print("https://www.squareyards.com/" + aboutDevLL.get(a).toString() + "---");
				}
			}

			System.out.print(" |");

			if (devLogoLL.size() == 0) {
				System.out.print(" - ");
			}
			else {
				for (int a = 0; a < devLogoLL.size(); a++) {
					System.out.print("https://static.squareyards.com/resources/images/developerlogo/" + devLogoLL.get(a).toString() + "---");
				}
			}

			System.out.print(" |");

			if (builderProjectsLL.size() == 0) {
				System.out.print(" - ");
			}
			else {
				for (int a = 0; a < builderProjectsLL.size(); a++) {
					System.out.print(builderProjectsLL.get(a).toString() + "---");
				}
			}

			System.out.print(" |");

			if (builderExperienceLL.size() == 0) {
				System.out.print(" - ");
			}
			else {
				for (int a = 0; a < builderExperienceLL.size(); a++) {
					System.out.print(builderExperienceLL.get(a).toString() + "---");
				}
			}

			System.out.print(" |");

			if (builderDescriptionLL.size() == 0) {
				System.out.print(" - ");
			}
			else {
				for (int a = 0; a < builderDescriptionLL.size(); a++) {
					System.out.print(builderDescriptionLL.get(a).toString().replace("\n", " - ").replace("</li>", "").replace("<li>", "") + "|");
				}
			}

			if (projectDescriptionLL.size() == 0) {
				System.out.print(" - ");
			}
			else {
				for (int a = 0; a < projectDescriptionLL.size(); a++) {
					System.out.print(projectDescriptionLL.get(a).toString().replace("\n", " - ").replace("</li>", "").replace("<li>", "") + "---");
				}
			} 


			System.out.println("");
		}
	}
}
