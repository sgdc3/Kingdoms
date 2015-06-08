package me.leothepro555.kingdoms.main;


import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class AsciiCompass
{
	
	public AsciiCompass(){
		
	}
	public enum Point
	{
		N('W'),
		NE('/'),
		E('S'),
		SE('\\'),
		S('E'),
		SW('/'),
		W('N'),
		NW('\\');
		
		public final char asciiChar;
		
		private Point(final char asciiChar)
		{
			this.asciiChar = asciiChar;
		}
		
		@Override
		public String toString()
		{
			return String.valueOf(this.asciiChar);
		}
		
		public String toString(boolean isActive, ChatColor colorActive, String colorDefault)
		{
			return (isActive ? colorActive : colorDefault)+String.valueOf(this.asciiChar);
		}
	}
	
	public static AsciiCompass.Point getCompassPointForDirection(double inDegrees)
	{
		double degrees = (inDegrees - 180) % 360 ;
		if (degrees < 0)
			degrees += 360;
		
		if (0 <= degrees && degrees < 22.5)
			return AsciiCompass.Point.S;
		else if (22.5 <= degrees && degrees < 67.5)
			return AsciiCompass.Point.SE;
		else if (67.5 <= degrees && degrees < 112.5)
			return AsciiCompass.Point.E;
		else if (112.5 <= degrees && degrees < 157.5)
			return AsciiCompass.Point.NE;
		else if (157.5 <= degrees && degrees < 202.5)
			return AsciiCompass.Point.N;
		else if (202.5 <= degrees && degrees < 247.5)
			return AsciiCompass.Point.SW;
		else if (247.5 <= degrees && degrees < 292.5)
			return AsciiCompass.Point.W;
		else if (292.5 <= degrees && degrees < 337.5)
			return AsciiCompass.Point.NW;
		else if (337.5 <= degrees && degrees < 360.0)
			return AsciiCompass.Point.S;
		else
			return null;
	}
	
	public static Point getCardinalDirection(Player player) {
		double rotation = (player.getLocation().getYaw() - 90) % 360;
		if (rotation < 0) {
		rotation += 360.0;
		}
		if (0 <= rotation && rotation < 22.5) {
		return Point.N;
		} else if (22.5 <= rotation && rotation < 67.5) {
		return Point.NE;
		} else if (67.5 <= rotation && rotation < 112.5) {
		return Point.E;
		} else if (112.5 <= rotation && rotation < 157.5) {
		return Point.SE;
		} else if (157.5 <= rotation && rotation < 202.5) {
		return Point.S;
		} else if (202.5 <= rotation && rotation < 247.5) {
		return Point.SW;
		} else if (247.5 <= rotation && rotation < 292.5) {
		return Point.W;
		} else if (292.5 <= rotation && rotation < 337.5) {
		return Point.NW;
		} else if (337.5 <= rotation && rotation < 360.0) {
		return Point.N;
		} else {
		return null;
		}
		}
	
	public static ArrayList<String> getAsciiCompass(Point point, ChatColor colorActive, String colorDefault)
	{
		ArrayList<String> ret = new ArrayList<String>();
		String row;
		
		row = "";
		row += Point.NW.toString(Point.NW == point, colorActive, colorDefault);
		row += Point.N.toString(Point.N == point, colorActive, colorDefault);
		row += Point.NE.toString(Point.NE == point, colorActive, colorDefault);
		ret.add(row);
		
		row = "";
		row += Point.W.toString(Point.W == point, colorActive, colorDefault);
		row += colorDefault+"+";
		row += Point.E.toString(Point.E == point, colorActive, colorDefault);
		ret.add(row);
		
		row = "";
		row += Point.SW.toString(Point.SW == point, colorActive, colorDefault);
		row += Point.S.toString(Point.S == point, colorActive, colorDefault);
		row += Point.SE.toString(Point.SE == point, colorActive, colorDefault);
		ret.add(row);

		return ret;
	}
	
	public static ArrayList<String> getAsciiCompass(double inDegrees, ChatColor colorActive, String colorDefault)
	{
		return getAsciiCompass(getCompassPointForDirection(inDegrees), colorActive, colorDefault);
	}
}