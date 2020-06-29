package io.pallaman.coronavirustracker.services;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import io.pallaman.coronavirustracker.models.LocationStats;

@Service
public class CoronaVirusDataService {
	
	private static String VIRUS_DATA_URL="https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
	
	private List<LocationStats> allstats=new ArrayList<>();
	
	public List<LocationStats> getAllstats() {
		return allstats;
	}

	//postconstruct- to run the method fetchVirusData when the service is called
	@PostConstruct 
	@Scheduled(cron="* * 1 * * *") //scheduled to run this method every second coz data is going to change and we want updated data
	//cron=*-> second, minute, hour, day, month, year
	public void fetchVirusData() throws IOException, InterruptedException
	{
		List<LocationStats> newstats=new ArrayList<>(); //making a new list to avoid concurrency issues as while fetching new data, we do want ppl to see prev stats till new ones are ready..
		
		HttpClient client=HttpClient.newHttpClient();  //make a new client
		
		HttpRequest request= HttpRequest.newBuilder().uri(URI.create(VIRUS_DATA_URL)).build(); //create a request with the url 
		
		HttpResponse<String> httpResponse= client.send(request, HttpResponse.BodyHandlers.ofString()); //client sends the request and response is received
		//System.out.println(httpResponse.body());
		StringReader csvBodyReader = new StringReader(httpResponse.body()); //response is a csv format data, so parse it with csv reader java library
		Iterable<CSVRecord> records = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader); //parse and break into records
		for (CSVRecord record : records) {   //get field values per record
		    LocationStats locationStat=new LocationStats();
		    locationStat.setCountry(record.get("Country/Region"));
		    locationStat.setState(record.get("Province/State"));
		    locationStat.setLatestTotalCases(Integer.parseInt(record.get(record.size()-1)));
		    locationStat.setDiffFromPrevDay(Integer.parseInt(record.get(record.size()-1))-(Integer.parseInt(record.get(record.size()-2))));
		    //System.out.println(locationStat);
		    newstats.add(locationStat);
	 
		    //System.out.println(record.get("Province/State"););
		   
		}
		
		this.allstats=newstats;
		
		
	}

}
