


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
		Stage.log("Tiles Layer no: "+layerNo);
		TiledMapTileLayer layer = (TiledMapTileLayer)mlayers.get(layerNo);
		NoOfColumns = layer.getWidth();
		Stage.log("MapColumns: "+NoOfColumns);
		NoOfRows = layer.getHeight();
		Stage.log("MapRows: "+NoOfRows);
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
		Stage.mapOffsetX = mapWidth - Stage.camOffsetX;
		Stage.mapOffsetY = mapHeight - Stage.camOffsetYTop;
	}
	
	public void loadObjects(int no){
		Stage.log("Objects Layer no: "+no);
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