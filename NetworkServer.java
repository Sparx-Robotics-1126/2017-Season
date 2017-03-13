import edu.wpi.first.wpilibj.networktables.NetworkTable;

public class NetworkServer{

/*########################################################################*/		
static NetworkTable table2;
static String key = "mode";
static int currentMode = 0;
static int lastPassedMode=1;
/*########################################################################*/			
public static void starter()
{
	NetworkTable.setServerMode();
	table2 = NetworkTable.getTable(key);
	
}
/*########################################################################*/			
public static void main(String[] arrgs)
{
NetworkClient foo = new NetworkClient();
foo.serverRun();
}
/*########################################################################*/			
}
