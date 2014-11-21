$includes$

$subscriber_callbacks$

int main(int argc, char **argv) {
	ros::init(argc, argv, "$node_name$");
	ros::NodeHandle node; 

	$publishers$ 
	$subscribers$

	// Set the ROS update interval
	ros::Rate r($rate$); 

	while(ros::ok()) {
		ros::spinOnce();
        
        // TODO: Add your custom code here!

        // Publish commands
        $publish_commands$

		r.sleep(); 
	}

    return 0;
}