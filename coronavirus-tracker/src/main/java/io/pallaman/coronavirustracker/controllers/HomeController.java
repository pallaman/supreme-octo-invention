package io.pallaman.coronavirustracker.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import io.pallaman.coronavirustracker.models.LocationStats;
import io.pallaman.coronavirustracker.services.CoronaVirusDataService;

@Controller
public class HomeController {

	@Autowired
	CoronaVirusDataService coronaVirusDataService;
	
	@GetMapping("/")
	public String home(Model model)
	{
		List<LocationStats> allStats=coronaVirusDataService.getAllstats();
		int totalReportedCases=allStats.stream().mapToInt(stat->stat.getLatestTotalCases()).sum();
		int totalNewCases=allStats.stream().mapToInt(stat->stat.getDiffFromPrevDay()).sum();
		//System.out.println("Total cases: "+totalReportedCases); where does it get printed?
		model.addAttribute("locationStats",allStats);
		model.addAttribute("totalReportedCases", totalReportedCases);
		model.addAttribute("totalNewCases", totalNewCases);
		return "home";
	}
}
