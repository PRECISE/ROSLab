$includes$


$pub_ports$
$sub_port_values$


void sigint_callback_handler(int signum) 
{
	printf("received SIGINT\n"); 

	//basic sigint handler
	//add your custom code here!
}

$sub_port_callbacks$


int main(int argc, char **argv) { 

	ros::init(argc, argv, "controller_test", ros::init_options::NoSigintHandler); 
	signal(SIGINT, sigint_callback_handler); 
	ros::NodeHandle node; 

	
	$pub_connections$ 

	$sub_callback_setup$

	//set the ros update interval
	//in the future lets make this a 
	//configurable setting from ROSLab
	ros::Rate r(50.0); 

// ---------------------------------------------------------------- WHILE LOOP	
	while(ros::ok()) { //ros::ok()
		ros::spinOnce(); 
		r.sleep(); 
	}
//return 0; 
return EXIT_SUCCESS;
}