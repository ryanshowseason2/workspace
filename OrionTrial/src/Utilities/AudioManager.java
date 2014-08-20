package Utilities;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class AudioManager
{
	static Map m_soundInstances = new HashMap();
	
	public AudioManager()
	{
		
	}
	
	public static void CullInstanceList()
	{
		//Any instances older than 5 seconds can be forgotten
		long current = System.currentTimeMillis();
		
		Iterator<Map.Entry<Long, Integer>> entries = m_soundInstances.entrySet().iterator();
		while (entries.hasNext()) 
		{
		  Map.Entry<Long, Integer> entry = entries.next();
		  long key = entry.getKey();
		  int value = entry.getValue();
		  if( (current - key) > 5000 )
		  {
			  m_soundInstances.remove( key );
		  }		  
		}
	}
	
	public static void disposeAll()
	{
		
	}

}
