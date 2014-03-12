ros::Subscriber $port_name$_subscriber;
$port_name$_subscriber = node.subscribe<$port_type$>("$port_topic$", 1, $port_name$Callback);