package sz.csi.textcomponents;

import java.util.*;

import sz.csi.ConsoleSystemInterface;
import sz.util.Debug;

public class ListBox extends TextComponent {
	
	public Vector itemsLista; 
	
	public ListBox(ConsoleSystemInterface si){
		super(si);
		itemsLista = new Vector(10);
	}

	public void draw() {
		clearBox();
		int length = (itemsLista.size() < super.inHeight ? itemsLista.size() : super.inHeight);
		for (int i = 0; i < length; i++){
			ListItem item = (ListItem) itemsLista.elementAt(i);
			//Debug.say("Item app"+item);
			si.print(inPosition.x, inPosition.y+i, item.getIndex(), item.getIndexColor());
			if (item.getRow().length() > inWidth)
				si.print(inPosition.x+2, inPosition.y + i, item.getRow().substring(0, inWidth), foreColor);
			else
				si.print(inPosition.x+2, inPosition.y + i, item.getRow(), foreColor);
		}
	}
	
	public void clear(){
		itemsLista.removeAllElements();
	}
	
	public void setElements(Vector elements){
		clear();
		addElements(elements);
	}
	
	public void addElements(Vector elements){
		itemsLista.addAll(elements);
	}	
	
	public void addElement(ListItem element){
		itemsLista.add(element);
	}
}

