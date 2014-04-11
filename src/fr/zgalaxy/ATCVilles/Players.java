package fr.zgalaxy.ATCVilles;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Location;

public class Players {
	
	private ATCVilles pl = null;
	
	public Players(ATCVilles pl) {
		this.pl = pl;
		points = new ArrayList<HashMap<String, Location>>();
		points.add(new HashMap<String, Location>());
		points.add(new HashMap<String, Location>());
	}
	
	private ArrayList<HashMap<String, Location>> points;
	
	public void setPoint(int point, String pname, Location loc) {
		HashMap<String, Location> pts = points.get(point);
		if (pts.containsKey(pname)) {
			delPoint(point, pname);
		}
		points.get(point).put(pname, loc);
		
	}
	
	public void delPoint(int point, String pname) {
		points.get(point).remove(pname);
	}
	
	public Location getPoint(int point, String pname) {
		if (!points.get(point).containsKey(pname))
			return null;
		
		return points.get(point).get(pname);
	}
}
