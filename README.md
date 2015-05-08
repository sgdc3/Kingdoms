<b><i><span style="font-size: 26px">The github repository of Kingdoms (Minecraft/Bukkit plugin)</span></i></b><br>

Dev builds: [![Circle CI](https://circleci.com/gh/sgdc3/Kingdoms.svg?style=svg)](https://circleci.com/gh/sgdc3/Kingdoms)

Kingdoms is a bukkit/spigot 1.8 plugin similar to factions, but with some different features. For now, I would accept bug fixing/lag reduction pull requests, along with feature requests that seem promising/essential.
Kingdoms is a beta version currently.
For now, I would accept bug fixing/lag reduction pull requests, along with feature requests that seem promising/essential.  

Plugin's page on spigotmc: http://www.spigotmc.org/resources/kingdoms.6392/

Developer's email: leothepro555@gmail.com

<hr>

<b><i><span style="font-size: 26px">Kingdoms description</span></i></b><br>
<br>
The plugin of empires and champions! Let your players battle for land, might and bonuses!<br>
<br>
Being in a Kingdom now has more perks than ever! Have bonuses while you are in a kingdom! Utilize resource points to upgrade your Kingdom's perks and defenses!<br>
<br>
New way to siege others! There is now infinite claiming of land, but it can be attacked by others! Enemies will need to do /k invade, then they will challenge their opponent Kingdom's champion, no matter if the kingdom members are offline or online! If the champion wins, the invader's items will be lost, and the kingdom gets to keep the land! If the invader wins, the land will fall into their hands!<br>
<br>
There is also the Nexus Block. To place it, do /k nexus, then right click a block to replace it with your kingdom's nexus. But guard it well! The nexus is your central hub. Enemies can steal resource points from it even when the land is claimed. Enemies can't claim the land where your nexus is. The nexus allows your members to convert items to resource points: 1 resource point for each 10 items. Kingdom mods and the kingdom king can use these points to claim more land, invade or upgrade bonuses.<br>
<br>
All new admin commands! Permission: kingdoms.admin<br>
<br>
<span style="font-size: 18px"><b><b>If you are using worldguard, this only works on worldguard 5.9</b></b></span><br>
<br>
<span style="font-size: 22px"><b>Features</b></span><br>
<ul>
<li>Factions-like! Commands designed to suit your players, who are used to playing on Factions!</li>
<li>Safezone Warzone to prevent players from claiming certain chunks!</li>
<li>Supports worldguard! Prevent players from claiming important parts of your regions and stop them from placing nexuses in your regions!</li>
<li>Supports java 6 and above!</li>
<li>Per world disable! Allow this only on worlds you desire!</li>
<li>Kingdoms API: Developers can use this to make custom versions of this plugin! API info can be found <a href="https://github.com/Hex27/Kingdoms" target="_blank" class="externalLink" rel="nofollow">here</a></li>
<li>More invading action than Factions! Players can claim large areas, and others can invade it even if the player is offline, allowing for more interesting experiences, and also an excuse to keep coming back!</li>
<li>Nexus GUI! Showing player resource points, upgrades, champion upgrades and so on!</li>
<li>A /k info! Players only need one sigh telling them to do so, and they can see how to use Kingdoms on their own!</li>
<li>Create kingdom names! Anything that players can copypaste into minecraft can be their kingdom name!</li>
<li>On Github! Click on the link on API info to access it and suggest code snippets or improvements! Suggestions can also be made through comments</li>
</ul><b><span style="font-size: 22px">Commands</span></b><br>
<ul>
<li>/k (Shows all commands)</li>
<li>/k nexus (Allows you to replace a block in your land with your nexus. )</li>
<li>/k info (Shows how Kingdoms works)</li>
<li>/k join [kingdom] (Use to join another Kingdom. Must be invited.)</li>
<li>/k create [kingdom] (Use to create a kingdom with the name.)</li>
<li>/k claim (Use to claim land where you are standing. Costs 5 resource points, and awards 5 might)</li>
<li>/k unclaim (Use to unclaim one of your lands. Refund 5 resource points, but deducts 5 might)</li>
<li>/k invade (Use when standing on land that doesn't belong to your kingdom. Spends 10 resource points to challenge their kingdom's champion. If you win, you gain that land and 5 might)</li>
<li>/k show [kingdom] (Shows censored information on a particular kingdom. Doesn't show their allies and enemies)</li>
<li>/k show (Shows your own information. Shows allies and enemies)</li>
<li>/k king [player] (Passes leadership of your Kingdom to another, turning you into a mod, and that player into a King)</li>
<li>/k mod [player] (Mods a player in your Kingdom)</li>
<li>/k demote [player] (Unmods a mod in your Kingdom)</li>
<li>/k kick [player] (Forcefully remove a player from your kingdom)</li>
<li>/k invite [player] (Invites a player to your kingdom)</li>
<li>/k uninvite [player] (Revokes a player's invite)</li>
<li>/k sethome (Sets kingdom home)</li>
<li>/k home (Goes to kingdom home if it is still valid, not claimed etc)</li>
<li>/k leave (Leaves your current kingdom)</li>
<li>/k ally [kingdom/playername] (Allies another kingdom)</li>
<li>/k enemy [kingdom/playername] (Enemies another kingdom)</li>
<li>/k neutral [kingdom/playername] (Neutralizes all kingdom relations)</li>
<li><b>/k admin (Shows admin commands) Permission: kingdoms.admin</b></li>
<li><b>/k admin toggle (Toggles admin mode, allowing you to destroy, place and attack players anywhere) Permission: kingdoms.admin</b></li>
<li><b>/k admin safezone (Claims a safezone patch) Permission: kingdoms.admin</b></li>
<li><b>/k admin warzone (Claims a warzone patch) Permission: kingdoms.admin</b></li>
<li><b>/k admin unclaim (Forcefully unclaim a non-nexus piece if land) Permission: kingdoms.admin</b></li>
<li><b>/k admin show [Kingdom] (Shows full information on a kingdom) Permission: kingdoms.admin</b></li>
<li><b>/k admin rp [kingdom] [amount] (Adds/subtracts the amount from a kingdom. Negative amount to subtract, positive amount to add.) Permission: kingdoms.admin</b></li>
</ul><b><span style="font-size: 22px">Planned Features</span></b><br>
<ul>
<li>/k unclaimall</li>
<li>/k disband</li>
<li>/k map on</li>
<li>/k map</li>
<li>/k tag</li>
<li>/k chat</li>
<li>Finish Github API wiki</li>
<li>Champion special abilities</li>
<li>More config options</li>
</ul><br>

<hr>

<b><span style="font-size: 22px">The Api</span></b><br><br>
Kingdoms can also be used as an API for developers to make sub-plugins of Kingdoms, for their own custom versions.
This can be done relatively easily by importing the Kingdoms.jar file, importing Kingdoms.class then getting information and setting data with it (Ex. Kingdoms.newKingdom(king.getUniqueId(), "TestKingdom"); )

<hr>

<b><i>I want to hear from you! </i></b>Feel free to comment in the discussions!
