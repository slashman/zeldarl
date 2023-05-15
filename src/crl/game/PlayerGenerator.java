
package crl.game;

import java.util.Hashtable;

import sz.util.Util;
import zrl.player.Player;
import crl.item.Item;
import crl.item.ItemFactory;
import crl.ui.AppearanceFactory;

public abstract class PlayerGenerator {
	public static PlayerGenerator thus;
	public abstract Player generatePlayer();
	
	private Hashtable SPECIAL_PLAYERS = new Hashtable();
	private void initSpecialPlayers(){
		SPECIAL_PLAYERS.clear();
}
	public Player createSpecialPlayer(String playerID){
		initSpecialPlayers();
		return (Player) SPECIAL_PLAYERS.get(playerID);
	}
	
	public Player getPlayer(String name){
		Player player = new Player();
		if (name.trim().equals("")){
			player.setName(Util.randomElementOf(NAMES));
		} else {
			player.setName(name);
		}
		
		player.setBaseSightRange(8);
		
		player.setWalkCost(50);
		player.setAttackCost(50);
		player.setCastCost(50);
		player.setCarryMax(20);
		
		player.setHeartsMax(12 * 2);
		player.setHearts(12 * 2);
		
		player.setMagicMax(9);
		player.setMagic(9);
		
		ItemFactory itf = ItemFactory.getItemFactory();
		AppearanceFactory apf = AppearanceFactory.getAppearanceFactory();
		if (player.getSex()==Player.MALE)
			player.setAppearance(apf.getAppearance("AVATAR"));
		else
			player.setAppearance(apf.getAppearance("AVATAR_F"));
		
		player.setWeapon(itf.createItem("IRON_SWORD"));
		player.setArmor(itf.createItem("GREEN_TUNIC"));
		player.setArrows(5);
		player.setSecondaryWeapon(itf.createItem("BUCKLER"));

		return player;

	}
	
	protected String [] NAMES = new String [] {"Link", "Slash", "Adam", "Bjorn", "Anubis", "Adral", "Zelda", "Valentina", "Adriana", "Gaby", "Gabriela"};


}
