#include <cstdio>
#include <cstdlib>
#include <cmath>

#include <ros/ros.h>
#include <std_msgs/String.h>
#include <geometry_msgs/Twist.h>
#include "encoder_msg.h"
#include <sensor_msgs/Joy.h>

#define METERSPERTICK 0.002216568

int fl_tick_global; 
int fr_tick_global; 
int rl_tick_global; 
int rr_tick_global; 

float wyaw_global; 
int stop_counter; 
float initial_yaw = 0.0;

void encoderCallback(const custom_msgs::encoder_msg::ConstPtr& enc_message) {
	//printf("encoder received\n"); 

	fl_tick_global = enc_message->fl_tick; 
	fr_tick_global = enc_message->fr_tick; 
	rl_tick_global = enc_message->rl_tick; 
	rr_tick_global = enc_message->rr_tick; 
}

void joyCallback(const sensor_msgs::JoyConstPtr joy_msg)
{
    
}

float subtract_initial_yaw(float current, float initial) {
	float result;
	result = current - initial;
	if (result < -3.14) {
		result = result + 2 * 3.14;
	}
	
	else if (result > 3.14) {
		result = result - 2 * 3.14;
	}
	return result;
}

//---------------------------------------------MAIN----------------------------------------------

int main(int argc, char **argv) { 

	ros::init(argc, argv, "encoder_vel_node"); 

	ros::NodeHandle nh_; 
	
	ros::Publisher cmd_vel_pub = nh_.advertise<geometry_msgs::Twist>("/cmd_vel", 1); 
	ros::Subscriber joy_sub = nh_.subscribe("joy", 1, joyCallback);
	ros::Subscriber encoder_sub = nh_.subscribe("/encoder", 1, encoderCallback); 

	ros::Time start_time = ros::Time::now(); 

	ros::Time last_update = start_time; 

	fl_tick_global = 0; 
	fr_tick_global = 0; 
	rl_tick_global = 0; 
	rr_tick_global = 0; 

	stop_counter = 0; 


	ros::Rate r(50.0); 

	int counter = 0; 

	double x_pos = 0.0; 
	double y_pos = 0.0; 
	double theta_pos = 0.0; 

	double x_vel = 0.0; 
	double y_vel = 0.0; 
	double theta_vel = 0.0; 

	while(nh_.ok()) { 
		ros::spinOnce(); 
	
		counter++; 

		//calculate odometry based on encoders and IMU values
		ros::Time current_time = ros::Time::now(); 
		double step_time = (current_time - last_update).toSec(); 

		double rc = rr_tick_global * METERSPERTICK; 
		double lc = rl_tick_global * METERSPERTICK; 
/*
		if(rr_tick_global == 0 && rl_tick_global == 0) { 
			stop_counter++; 
		} else { 
			stop_counter = 0; 
		}

		double vdt = (rc + lc) / 2; 
		double wdt = 0.0; 

		
		if(stop_counter < 40) { 
			//wdt = wyaw_global * 0.025; 
			wdt = wyaw_global * 0.025 * 1.0800; //1.0884; 
		} else { 
			wdt = 0.0; 
			vdt = 0.0; 
		}
		

		//update values
		double dx = 0.0; 
		double dy = 0.0; 
		double dtheta = 0.0; 



		
		theta_pos = subtract_initial_yaw(yaw_global, initial_yaw); 
		dx = vdt * cos(theta_pos); 
		dy = vdt * sin(theta_pos); 
		x_pos += dx; 
		y_pos += dy; 

		x_vel = dx / step_time; 
		y_vel = dy / step_time; 
		theta_vel = dtheta / step_time; 
*/
        
        geometry_msgs::Twist cmd_vel;
        cmd_vel.linear.x = 0;
        cmd_vel.linear.y = 0;
        cmd_vel.angular.z = 0;
        
        // publish cmd velocity
        cmd_vel_pub.publish(cmd_vel);


		//print statement so we can debug and know what's going on
		ROS_INFO("Encoder Count:");
		std::cout<<fl_tick_global<<", "<<fr_tick_global<<", "<<rl_tick_global<<", "<<rr_tick_global<<std::endl;
		ROS_INFO("Command Velocity: ");
		std::cout<<"v: "<<cmd_vel.linear.x<<", "<<"w: "<<cmd_vel.angular.z<<std::endl;


		last_update = current_time; 

		//cycle at "rate" hertz
		r.sleep(); 
	}
	return 0; 
}

