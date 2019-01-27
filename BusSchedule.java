//Erik Dekker
//CS320
//1/26/2019
//Bus Schedule Regex Project

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Scanner;
import java.net.*;
import java.util.HashMap;

public class BusSchedule {
    public static void main(String[] args) throws Exception {
        //opens connection to bus schedule website
        URLConnection ct = new URL("https://www.communitytransit.org/busservice/schedules").openConnection();
        ct.setRequestProperty("user-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");
        
        //BufferedReader reads in html to String text
        BufferedReader in = new BufferedReader(new InputStreamReader(ct.getInputStream()));
        String inputLine = "";
        String text = "";
        while ((inputLine = in.readLine()) != null) {
            text += inputLine + "\n";
        }
        in.close();
        
        //HashMap stores routes and links to route pages
        HashMap<String, String> links = new HashMap<String, String>();
        
        //Takes in user input for desired destination
        System.out.print("Please enter a letter that your destinations start with: ");
        Scanner s = new Scanner(System.in);
        String dest = s.nextLine();
        
        //Uses regex to capture destinations and routes according to user input
        //Displays output in console
        Pattern destPattern = Pattern.compile("<h3>("+dest+".*?)</h3>(.*?)<hr id", Pattern.DOTALL);
        Matcher destMatcher = destPattern.matcher(text);
        String destText = "";
        while(destMatcher.find()){
            System.out.println("Destination: " + destMatcher.group(1));
            destText = destMatcher.group(2); //text for second regex matcher
            Pattern routePattern = Pattern.compile("<strong><a href=\"(.*)\".*>(.*)</a></strong>");
            Matcher routeMatcher = routePattern.matcher(destText);
            while(routeMatcher.find()){
                links.put(routeMatcher.group(2), routeMatcher.group(1)); //stores captured links and route numbers in HashMap
                System.out.println("Bus Number " + routeMatcher.group(2));
            }
            System.out.println("+++++++++++++++++++++++++++++++++++");
        }

        //Takes user input for desired route
        System.out.print("Please enter a route ID as a string: ");
        String routeID = s.nextLine();
        
        //retrieves and displays link from HashMap using provided route input
        String link = "https://www.communitytransit.org/busservice";
        link += links.get(routeID);
        System.out.println("\nThe link for your route is: " + link +"\n");

        //Opens new url for route webpage
        URLConnection rt = new URL(link).openConnection();
        rt.setRequestProperty("user-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.11 (KHTML, like Gecko) Chrome/23.0.1271.95 Safari/537.11");

        //Reads in html from route webpage
        in = new BufferedReader(new InputStreamReader(rt.getInputStream()));
        inputLine = "";
        text = "";
        while ((inputLine = in.readLine()) != null) {
            text += inputLine + "\n";
        }
        in.close();
        
        //Displays Destinations and Stops for desired route
        Pattern toPattern = Pattern.compile("<h2>Weekday<small>(.*?)</small></h2>(.*?)</thead>", Pattern.DOTALL);
        Matcher toMatcher = toPattern.matcher(text);
        String stopText = "";
        while(toMatcher.find()){
            System.out.println("Destination: " + toMatcher.group(1));
            stopText = toMatcher.group(2); //text for second regex matcher
            Pattern stopPattern = Pattern.compile("<strong.*?>(.*?)</strong>.*?</span>.*?<p>(.*?)</p>", Pattern.DOTALL);
            Matcher stopMatcher = stopPattern.matcher(stopText);
            while(stopMatcher.find()){
                System.out.println("Stop Number: " + stopMatcher.group(1) + " is " + stopMatcher.group(2).replaceAll("&amp;", "&"));
            }
            System.out.println("+++++++++++++++++++++++++++++++++++");

        }
    }
}
