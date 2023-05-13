package crl.ui.consoleUI;

import sz.csi.ConsoleSystemInterface;
import zrl.player.Player;
import crl.game.PlayerGenerator;

public class CharPlayerGenerator extends PlayerGenerator{
	public CharPlayerGenerator(ConsoleSystemInterface si){
		this.si = si;
	}
	private ConsoleSystemInterface si;
	
	public Player generatePlayer(){
		si.cls();
		si.print(2,3, "Hero name:", ConsoleSystemInterface.WHITE);
		si.refresh();
		si.locateCaret(3 +"Hero name:".length(), 3);
		String name = si.input(10);
    	return getPlayer(name);
	}
}