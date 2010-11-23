package crl.levelgen;

import sz.util.*;
import zrl.*;
import zrl.player.*;

import java.util.*;

import crl.level.*;
import crl.monster.*;
import crl.game.*;
import crl.feature.*;

public class GirdLevelGenerator extends LevelGenerator {
	private boolean visitedRooms [][];
	private int roomWidth;
	private int roomHeight;
	private int startIndex;
	private int endIndex;
	
	private String baseWall;
	private String baseFloor;
	private int candles;
	
	public void setCandles(int value){
		candles = value;
	}
	
	/*public Level generateLevel(String param, Dispatcher disp){
		Debug.enterMethod(this, "generateLevel", param);
		String[] parameters = param.split(" ");
		Level ret = generateLevel(new Position(Integer.parseInt(parameters[0]), Integer.parseInt(parameters[1])), Integer.parseInt(parameters[2]), Integer.parseInt(parameters[3]),Integer.parseInt(parameters[4]), Integer.parseInt(parameters[5]));
		Debug.exitMethod(ret);
		return ret;
	}*/

	public void init(String baseWall, String baseFloor){
		this.baseWall = baseWall;
		this.baseFloor = baseFloor;
	}
	
	private void printVisited(){
		/*for (int y = 0; y < visitedRooms[0].length; y++){
			for (int x = 0; x < visitedRooms.length; x++)
				if (visitedRooms[x][y])
					System.out.print(".");
				else
					System.out.print("X");
			Debug.say("");
		}*/
	}

	private void lightCandles(Level l){
		for (int i = 0; i < candles; i++){
			int xrnd = Util.rand(1, getWidth()*roomWidth -1);
			int yrnd = Util.rand(1, getHeight()*roomHeight-1);
			if (l.getMapCell(xrnd, yrnd, 0).isSolid()){
				i--;
				continue;
			}
				
			Feature vFeature = FeatureFactory.getFactory().buildFeature("CANDLE");
			vFeature.setPosition(xrnd,yrnd,0);
			l.addFeature(vFeature);
		}
	}
	public Level generateLevel(Position size, int startIndex, int endIndex, int roomWidth, int roomHeight) throws CRLException{
		Debug.enterMethod(this, "generateLevel", "size"+size+" startindex "+startIndex+" endIndex "+endIndex+" roomWidth "+ roomWidth + "roomHeight"+roomHeight);
		visitedRooms = new boolean[size.x][size.y];
		this.roomHeight = roomHeight;
		this.roomWidth = roomWidth;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		setVisitedRooms();
		//printVisited();
		Level ret = plotLevel();
		lightCandles(ret);
		int keys = placeKeys(ret);
		//Place the magic door
		Cell[][][] cells = ret.getCells();
		Feature door = FeatureFactory.getFactory().buildFeature("MAGIC_DOOR");
		cells[0][cells[0].length-1][roomHeight * endIndex + roomHeight / 2] = MapCellFactory.getMapCellFactory().getMapCell(baseFloor);
		door.setPosition(cells[0].length-1,roomHeight * endIndex + roomHeight / 2,0);
		door.setKeyCost(keys);
		ret.addFeature(door);
		Debug.exitMethod(ret);
		return ret;

	}

	private void setVisitedRooms(){
		Debug.enterMethod(this, "setVisitedRooms");
		visitedRooms[0][startIndex] = true;
		visitedRooms[visitedRooms.length - 1][endIndex] = true;
		Position newPosition = new Position(0,0);
		while (!connected()){
			newPosition.x = Util.rand(0, visitedRooms.length - 1);
			newPosition.y = Util.rand(0, visitedRooms[0].length - 1);
			if ((newPosition.x > 0 && visitedRooms[newPosition.x-1][newPosition.y]) ||
				(newPosition.y > 0 && visitedRooms[newPosition.x][newPosition.y-1]) ||
				(newPosition.x < getWidth() -1 && visitedRooms[newPosition.x+1][newPosition.y]) ||
				(newPosition.y < getHeight() -1 && visitedRooms[newPosition.x][newPosition.y+1]))
				visitedRooms[newPosition.x][newPosition.y] = true;
		}
		Debug.exitMethod();
	}

	private boolean connected(){
		Debug.enterMethod(this, "connected");
		Stack stack = new Stack();
		Position now = new Position(0, startIndex);
		stack.push(now);
		Position end = new Position(visitedRooms.length -1, endIndex);
		boolean [][] goneThru = new boolean[visitedRooms.length][visitedRooms[0].length];
		for (int x = 0; x < goneThru.length; x++)
			for (int y = 0; y < goneThru[0].length; y++)
				goneThru[x][y]  = visitedRooms[x][y];

		do {
			now = (Position) stack.pop();
			if (now.x > 0 && goneThru[now.x-1][now.y] == true){
				stack.push(new Position(now.x-1, now.y));
				goneThru[now.x-1][now.y] = false;
			}
			if (now.y > 0 && goneThru[now.x][now.y-1] == true){
				stack.push(new Position(now.x, now.y-1));
				goneThru[now.x][now.y-1] = false;
			}
			if (now.x < getWidth() - 1 && goneThru[now.x+1][now.y] == true){
				stack.push(new Position(now.x+1, now.y));
				goneThru[now.x+1][now.y] = false;
			}
			if (now.y < getHeight() - 1 && goneThru[now.x][now.y+1] == true){
				stack.push(new Position(now.x, now.y+1));
				goneThru[now.x][now.y+1] = false;
			}
			if (now.equals(end)){
				Debug.exitMethod("true");
				return true;
			}

		} while (!stack.isEmpty());
		Debug.exitMethod("false");
		return false;
	}

	private Level plotLevel() throws CRLException{
		Debug.enterMethod(this, "plotLevel");
		Cell[][][] cells = new Cell[1][getWidth() * roomWidth][getHeight() * roomHeight];
		//Fill with base
		for (int x = 0; x < cells[0].length; x++)
			for (int y = 0; y < cells[0][0].length; y++)
				cells[0][x][y] = MapCellFactory.getMapCellFactory().getMapCell(baseWall);

		for (int x = 0; x < visitedRooms.length; x++)
			for (int y = 0; y < visitedRooms[0].length; y++)
				if (visitedRooms[x][y])
					carveRoom (cells, x,y);
		Level ret = new Level();
		ret.setCells(cells);
		//make the border
		for (int x = 0; x < cells[0].length; x++){
			cells[0][x][0] = MapCellFactory.getMapCellFactory().getMapCell(baseWall);
			cells[0][x][cells[0][0].length-1] = MapCellFactory.getMapCellFactory().getMapCell(baseWall);
		}
		for (int y = 0; y < cells[0][0].length; y++){
			cells[0][0][y] = MapCellFactory.getMapCellFactory().getMapCell(baseWall);
			cells[0][cells[0].length-1][y] = MapCellFactory.getMapCellFactory().getMapCell(baseWall);
		}
		//Place the entrance
		//ret.setPositions(new Position(0,roomHeight * startIndex + roomHeight / 2,0), new Position(cells[0].length-1,roomHeight * endIndex + roomHeight / 2, 0));
		ret.addExit(new Position(0,roomHeight * startIndex + roomHeight / 2,0), "_BACK");
		ret.addExit(new Position(cells[0].length-1,roomHeight * endIndex + roomHeight / 2, 0), "_NEXT");
		cells[0][0][roomHeight * startIndex + roomHeight / 2] = MapCellFactory.getMapCellFactory().getMapCell(baseFloor);
		
		Debug.exitMethod(ret);
		return ret;
	}

	private void carveRoom(Cell[][][] cells, int px, int py) throws CRLException{
		Debug.enterMethod(this, "carveRoom", "px"+px +" py"+py);
		for (int x = 0; x < roomWidth; x++)
			for (int y = 0; y < roomHeight; y++)
				cells[0][x + px*roomWidth][y+py*roomHeight] = MapCellFactory.getMapCellFactory().getMapCell(baseFloor);
		cells[0][px*roomWidth][py*roomHeight]= MapCellFactory.getMapCellFactory().getMapCell(baseWall);
		cells[0][(px+1)*roomWidth-1][(py+1)*roomHeight-1]= MapCellFactory.getMapCellFactory().getMapCell(baseWall);
		cells[0][px*roomWidth][(py+1)*roomHeight-1]= MapCellFactory.getMapCellFactory().getMapCell(baseWall);
		cells[0][(px+1)*roomWidth-1][py*roomHeight]= MapCellFactory.getMapCellFactory().getMapCell(baseWall);

		Debug.exitMethod();
	}

	private int getWidth(){
		return visitedRooms.length;
	}

	private int getHeight(){
		return visitedRooms[0].length;
	}
}