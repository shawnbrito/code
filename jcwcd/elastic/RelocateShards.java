/* Simple Standa-Alone Program to repair ElasticSearch 
 * UNASSIGNED Shards problem. This program will read and
 * reassign the unassigned shards back to the Master
 * Author: Shawn Brito (2016-AUG-25)
*/
package jcwcd.elastic;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class RelocateShards 
{
	public RelocateShards()
	{
		//--- External 177.21.32.22  , Internal 192.168.1.3
		String ipAddress = "192.168.1.3";   // The Elastic Search IP (any server)
		String nodeName = "Master";    // The node.name (as found in the elasticsearch.yml)
		
		String getUrlShards = "http://"+ipAddress+":9200/_cat/shards";
		String urlFixShards = "http://"+ipAddress+":9200/_cluster/reroute";
		String inputLine="";
		
		try{
			URL url = new URL(getUrlShards);
			URLConnection urlcon = url.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(urlcon.getInputStream()));
			
			System.out.println("Starting Application");
			
			while ((inputLine = in.readLine()) !=null )
			{
				if(inputLine.indexOf("UNASS")>0)
				{
					String splitArray[] = inputLine.split("\\s+");
					System.out.println(splitArray[0] + "- " + splitArray[1]+ " - "+ splitArray[2]);
					if(splitArray[2].equals("p"))
						{reassignShards (urlFixShards, splitArray[1], splitArray[0], nodeName);}
				}
			}

			in.close();
			System.out.println("App Completed--- Done...");
		}catch(Exception e)
		{
			System.out.println(e.toString()	);
		}
	}
	
	
	public void reassignShards(String request, String shardNumber, String indexName, String nodeName)
	{
		try{
			String postData = "{   \"commands\" : [ {  \n" + 
					"              \"allocate\" : {\n" + 
					"                  \"index\" : \""+indexName+"\", \n" + 
					"                  \"shard\" : "+shardNumber+", \n" + 
					"                  \"node\" : \""+nodeName+"\", \n" + 
					"                  \"allow_primary\" : true\n" + 
					"              }\n" + 
					"            }\n" + 
					"        ]\n" + 
					"    }";


			int readBytes = 0;
			int totalRead = 0;
			int    postDataLength = postData.length();
			URL    url            = new URL( request );
			HttpURLConnection conn= (HttpURLConnection) url.openConnection();           
				conn.setDoOutput( true );
				conn.setInstanceFollowRedirects( false );
				conn.setRequestMethod( "POST" );
				conn.setRequestProperty( "Accept-Encoding", "gzip, deflate");
				conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
				
				conn.setUseCaches( false );
			
			DataOutputStream wr = new DataOutputStream( conn.getOutputStream());
			   wr.write( postData.getBytes() );
			   InputStream in = conn.getInputStream();
				
			    byte [] b = new byte[2048];
				while ( (readBytes = in.read(b)) > -1 ){	totalRead += readBytes;	}
				System.out.println("Bytes Read = "+totalRead);
				
			in.close();
			wr.close();
		}catch(Exception e){
			System.out.println(e.toString());
		}
	}
	
	
	public static void main(String arg[])
	{ 
	  // Launch the application and fix the unassigned shards
		new RelocateShards();
	}
}
