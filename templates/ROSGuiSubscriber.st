        self.$port_name$_subscriber = rospy.Subscriber("$port_topic$", $port_type$, self.$port_name$_subscriber_callback)
self.pubsubs.append(self.$port_name$_subscriber)