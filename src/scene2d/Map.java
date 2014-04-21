package scene2d;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapLayers;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSets;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer.Cell;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.utils.Array;

/** The Map Class
 * <p>
 * The Map is a Group which automatically loads all the tiles and arranges them accordingly it is
 * highly recommended that you override the loadLayer method and customize the map
 * </p>
 * @author pyros2097 */
public class Map extends Group{
	protected int tileSize;
	
	/* List of MapLayers */
	protected MapLayers mlayers;
	protected MapObjects mobjects;
	
	public static int NoOfColumns;
	public static int NoOfRows;
	protected float mapWidth;
	protected float mapHeight;
	
	public static TiledMapTileSets tileSets;
	private Array<MapActor[][]> tiles = new Array<MapActor[][]>();
	
	public Map(){
		
	}
	
	public Map(int levelNo, int tileSize){
		setPosition(0, 0);
		setOrigin(0, 0);
		TiledMap map = Asset.map(levelNo);
		this.tileSize = tileSize;
		mlayers  = map.getLayers();
		tileSets = map.getTileSets();
	}
	
	public void loadLayer(int layerNo){
		Scene.log("Tiles Layer no: "+layerNo);
		TiledMapTileLayer layer = (TiledMapTileLayer)mlayers.get(layerNo);
		NoOfColumns = layer.getWidth();
		Scene.log("MapColumns: "+NoOfColumns);
		NoOfRows = layer.getHeight();
		Scene.log("MapRows: "+NoOfRows);
		tiles .add(new MapActor[NoOfRows][NoOfColumns]);
		for(int i=0; i<NoOfRows; i++)
			for(int j=0; j<NoOfColumns; j++){
				Cell c = layer.getCell(j, i);
				if(c != null){
					tiles.get(layerNo)[i][j] = new MapActor(c.getTile().getTextureRegion(),
							i, j, c.getTile().getId(), tileSize);
					addActor(tiles.get(layerNo)[i][j]);
				}
				else{
					tiles.get(layerNo)[i][j] = new MapActor((TextureRegion)null,i, j, 0, tileSize);
					addActor(tiles.get(layerNo)[i][j]);
				}
		}
		mapWidth = tileSize * NoOfColumns;
		mapHeight = tileSize * NoOfRows;
		//Stage.mapOffsetX = mapWidth - Stage.camOffsetX;
		//Stage.mapOffsetY = mapHeight - Stage.camOffsetYTop;
	}
	
	public void loadObjects(int no){
		Scene.log("Objects Layer no: "+no);
		MapLayer layer1 = mlayers.get(no);
		mobjects = layer1.getObjects();
	}
	
	public float getWidth(){
		return mapWidth;
	}
	
	public float getHeight(){
		return mapHeight;
	}
	
	public int getNoOfColumns(){
		return NoOfColumns;
	}
	
	public int getNoOfRows(){
		return NoOfRows;
	}
	
	public MapObjects getMapObjects(){
		return mobjects;
	}
	
	public MapLayers getMapLayers(){
		return mlayers;
	}
}

class MapLayerg extends Group {
	
	public MapLayerg(int index){
		this.setZIndex(index);
	}
}

class RPGMap {
	private static RPGMap instance;
	MapLayerg mapLayer1;
	MapLayerg mapLayer2;
	MapLayerg mapLayer3;
	MapLayerg mapLayer4;
	MapLayerg mapLayer5;
	MapLayerg mapLayer6;
	MapLayerg mapLayer7;
	
	private RPGMap(){
		mapLayer1 = new MapLayerg(1);
		mapLayer2 = new MapLayerg(2);
		mapLayer3 = new MapLayerg(3);
		mapLayer4 = new MapLayerg(4);
		mapLayer5 = new MapLayerg(5);
		mapLayer6 = new MapLayerg(6);
		mapLayer7 = new MapLayerg(7);
	}
	
	public static RPGMap getInstance(){
		if(instance == null)
			instance = new RPGMap();
		return instance;
	}
}