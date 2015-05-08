package me.leothepro555.kingdoms.main;

public enum Messages {
	
	NOPERMERROR, 
	NOTKINGERROR, 
	NOTMODERROR, 
	NOKINGDOMERROR,
    KINGATTEMPTLEAVEERROR, 
    KINGDOMCREATEMESSAGE, 
    NEXUSPLACEMESSAGE,
    INVALIDLANDERROR,
    LANDCLAIMMESSAGE,
    LANDUNCLAIMMESSAGE,
    CANTCLAIMERROROTHERS,
    CANTCLAIMERRORSAFEZONE,
    CANTCLAIMERRORWARZONE, 
    INVADEMESSAGE,
    INVADEINVALIDMESSAGE,
    INVADEUNOCCUPIEDERROR,
    KINGDOMSINFO;
	private static Kingdoms plugin= new Kingdoms();
    private Messages(){
    	
    }
    //lang:
    //    no-perm-error:
    //	  not-king-error:
    //	  not-mod-error:
    //	  no-kingdom-error:
    //	  king-attempt-leave-error:
    //	  kingdom-create-message:
    //	  nexus-place-message:
    //	  invalid-land-error:
    //	  land-claim-error:
    //	  claim-safezone-error:
    //	  claim-warzone-error:
    //	  invalid-invade-message:
    //	  invade-message:
    //	  invade-empty-land-error:
    public String getMessage(){
    	switch(this){
    	case NOPERMERROR: return plugin.getConfig().getString("lang.no-perm-error");
    	case NOTKINGERROR: return plugin.getConfig().getString("lang.not-king-error");
    	case NOTMODERROR: return plugin.getConfig().getString("lang.not-mod-error");
    	case NOKINGDOMERROR: return plugin.getConfig().getString("lang.no-kingdom-error");
    	case KINGATTEMPTLEAVEERROR: return plugin.getConfig().getString("lang.king-attempt-leave-error");
    	case KINGDOMCREATEMESSAGE: return plugin.getConfig().getString("lang.kingdom-create-message");
    	case NEXUSPLACEMESSAGE: return plugin.getConfig().getString("lang.nexus-place-message");
    	case INVALIDLANDERROR: return plugin.getConfig().getString("lang.invalid-land-error");
    	case LANDCLAIMMESSAGE: return plugin.getConfig().getString("lang.land-claim-message");
    	case LANDUNCLAIMMESSAGE: return plugin.getConfig().getString("lang.land-unclaim-message");
    	case CANTCLAIMERROROTHERS: return plugin.getConfig().getString("lang.claim-error");
    	case CANTCLAIMERRORSAFEZONE: return plugin.getConfig().getString("lang.claim-safezone-error");
    	case CANTCLAIMERRORWARZONE: return plugin.getConfig().getString("lang.claim-warzone-error");
    	case INVADEMESSAGE: return plugin.getConfig().getString("lang.invade-message");
    	case INVADEINVALIDMESSAGE: return plugin.getConfig().getString("lang.invalid-invade-message");
    	case INVADEUNOCCUPIEDERROR: return plugin.getConfig().getString("lang.invade-empty-land-error");
    	case KINGDOMSINFO: return plugin.getConfig().getString("lang.kingdoms-info");
		default:
			break;
    	}
		return null;
    }
    
    
    
}


