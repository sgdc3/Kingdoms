package me.leothepro555.kingdoms.main;

public class RpmManager{
	
	public Kingdoms plugin;
	
	public RpmManager(Kingdoms plugin){
		this.plugin = plugin;
	}
	
	public int addMight(String kingdom, int addamt){
		plugin.kingdoms.set(kingdom + ".might", plugin.kingdoms.getInt(kingdom + ".might") + addamt);
		plugin.saveKingdoms();
		return plugin.kingdoms.getInt(kingdom + ".might");
	}
	
	public int minusMight(String kingdom, int minusamt){
		if(plugin.kingdoms.getInt(kingdom + ".might") - minusamt >= 0){
			plugin.kingdoms.set(kingdom + ".might", plugin.kingdoms.getInt(kingdom + ".might") - minusamt);
			plugin.saveKingdoms();
		return minusamt;
		}else{
			int amt = plugin.kingdoms.getInt(kingdom + ".might");
			plugin.kingdoms.set(kingdom + ".might", 0);
			plugin.saveKingdoms();
			return amt;
		}
	}
	
	

	public int minusRP(String kingdom, int minusamt){
		if(plugin.kingdoms.getInt(kingdom + ".resourcepoints") - minusamt >= 0){
			plugin.kingdoms.set(kingdom + ".resourcepoints", plugin.kingdoms.getInt(kingdom + ".resourcepoints") - minusamt);
			plugin.saveKingdoms();
		return minusamt;
		}else{
			int amt = plugin.kingdoms.getInt(kingdom + ".resourcepoints");
			plugin.kingdoms.set(kingdom + ".resourcepoints", 0);
			plugin.saveKingdoms();
			return amt;
		}
	}
	
	public int addRP(String kingdom, int addamt){
		plugin.kingdoms.set(kingdom + ".resourcepoints", plugin.kingdoms.getInt(kingdom + ".resourcepoints") + addamt);
		plugin.saveKingdoms();
		return plugin.kingdoms.getInt(kingdom + ".resourcepoints");
	}
	
	public boolean hasAmtRp(String kingdom, int amt){
		
		if(plugin.kingdoms.getInt(kingdom + ".resourcepoints") >= amt){
			return true;
		}else{
		
		return false;
		}
	}
	
	public int getRp(String kingdom){
		
		return plugin.kingdoms.getInt(kingdom + ".resourcepoints");

	}

}
