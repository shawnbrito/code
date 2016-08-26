RelocateShards.java
---------
A standa-alone java application that can relocate Elastic-Search shards.
Set the following two variable and compile-run.

	String ipAddress = "192.168.1.31";   // The Elastic Search IP (any server)
	String nodeName = "Master";    // The node.name (as found in the elasticsearch.yml)
