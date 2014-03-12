/**
 * Copyright 2012, Kansas State University. 
 */

package mdcf.device.mock.$type_name$;

import mdcf.channelservice.common.IMdcfMessageListener;
import mdcf.channelservice.common.IMdcfReceiver;
import mdcf.channelservice.common.IMdcfSender;
import mdcf.channelservice.common.MdcfMessage;
import mdcf.channelservice.common.ReceiverFactory;
import mdcf.channelservice.common.SenderFactory;
import mdcf.channelservice.common.security.IReceiveSecurityProvider;
import mdcf.channelservice.common.security.ISendSecurityProvider;
import mdcf.channelservice.common.security.RecvSecFactory;
import mdcf.channelservice.common.security.SendSecFactory;
import mdcf.core.ctypes.device.DeviceComponent;
import mdcf.core.messagetypes.devicemgmt.PubChannelAssignmentMsg;
import mdcf.core.messagetypes.devicemgmt.SubChannelAssignmentMsg;

$if(user_code_free)$
/**
 * AUTO-GENERATED CLASS.
 * NO USER SERVICEABLE PARTS INSIDE.
 * DO NOT EDIT.
 * */
public abstract class $type_name$$type_name_suffix$ extends DeviceComponent {
$else$
public final class $type_name$ extends DeviceComponent {
$endif$
	
	public static final String DEVICE_TYPE_NAME = "$type_name$";

	/*
	 * AUTO-GENERATED CODE OF THE DATAINTERFACES
	 */
	$sender_declarations$
	$receiver_declarations$
	
$if(user_code_free)$
	protected $type_name$$type_name_suffix$(boolean useCommunicationSecurityFeature) {
		this(DEVICE_TYPE_NAME+System.currentTimeMillis(), DEVICE_TYPE_NAME, useCommunicationSecurityFeature);
	}

	protected $type_name$$type_name_suffix$(String guid, String name, boolean useCommunicationSecurityFeature) {
		super(guid, name);
		setUseCommunicationSecurityFeature(useCommunicationSecurityFeature);
	}
$else$
	/*
	 * USER DEFINED FIELDS
	 * */
	
	/*
	 * AUTO-GENERATED CONSTRUCTOR
	 * */
	public $type_name$() {
		this(false);
	}
	
	public $type_name$(boolean useCommunicationSecurityFeature) {
		super("$uuid$", DEVICE_TYPE_NAME);
		setUseCommunicationSecurityFeature(useCommunicationSecurityFeature);
	}
$endif$
	
	/*
	 * AUTO-GENERATED DEVICE DRIVER'S ENTRY FUNCTION
	 * */
	// FIXME: this should be in a separate loader class.
	public static void main(String[] args) {
		$type_name$ dev = new $type_name$();
		dev.connectToNetworkController();
	}

$if(user_code_free)$
	/** Perform device initialization. */
	public abstract void init();
$else$
	/*
	 * AUTO-GENERATED CODE FOR INIT
	 * */
	public void init() {		

	}
$endif$
	
$if(user_code_free)$
	public abstract void start_publish();
$else$
	/*
	 * AUTO-GENERATED CODE FOR START PUBLISHING
	 * USER NEEDS TO FILL IN THE METHOD FOR START PUBLISHING
	 * */
	public void start_publish() {
		
	}
$endif$
	
$if(user_code_free)$
	public abstract void stop_publish();
$else$
	/*
	 * AUTO-GENERATED CODE FOR STOP PUBLISHING
	 * USER NEEDS TO FILL IN THE METHOD FOR STOP PUBLISHING
	 * */
	public void stop_publish() {
		
	}
$endif$


	/*
	 * AUTO-GENERATED CODE FOR PUBLISHER CHANNEL ASSIGNMENT
	 * */
	@Override
	public void processPublisherChannelAssignment(PubChannelAssignmentMsg pubAssign) {
		$pub_assign_lines$
	}

	/*
	 * AUTO-GENERATED CODE FOR SUBSCRIBER CHANNEL ASSIGNMENT
	 * */
	@Override
	public void processSubscriberChannelAssignment(SubChannelAssignmentMsg subAssign) {
		$sub_assign_lines$
	}

	$listener_defns$

	$sender_method_defns$
}