/**
 * Copyright 2012, Kansas State University. 
 */

package mdcf.app.$app_name$;

import mdcf.channelservice.common.IMdcfMessageListener;
import mdcf.channelservice.common.IMdcfReceiver;
import mdcf.channelservice.common.IMdcfSender;
import mdcf.channelservice.common.MdcfMessage;
import mdcf.channelservice.common.ReceiverFactory;
import mdcf.channelservice.common.SenderFactory;
import mdcf.core.app.PredefinedExceptions;
import mdcf.core.ctypes.ServerComponentEvent;
import mdcf.core.ctypes.ServerComponentEventListener;
import mdcf.core.ctypes.apppanel.AppPanelComponent;
import mdcf.core.messagetypes.devicemgmt.PubChannelAssignmentMsg;
import mdcf.core.messagetypes.devicemgmt.SubChannelAssignmentMsg;

$if(user_code_free)$
/**
 * AUTO-GENERATED CLASS.
 * NO USER SERVICEABLE PARTS INSIDE.
 * DO NOT EDIT.
 * */
public abstract class $type_name$$type_name_suffix$ extends AppPanelComponent {
$else$
public class $type_name$ extends AppPanelComponent {
$endif$
	
	/*
	 * AUTO-GENERATED CODE OF THE DATAINTERFACES
	 */
	$sender_declarations$
	$receiver_declarations$
	
$if(user_code_free)$
$else$
	/*
	 * USER DEFINED FIELDS
	 * */

$endif$
	/*
	 * AUTO-GENERATED CONSTRUCTOR
	 * */
	public $type_name$$type_name_suffix$(String GUID) {
		super(GUID, "$type_name$");
	}
	
	/*
	 * AUTO-GENERATED CODE FOR INITIALIZING APP PANEL
	 */
	public void init(){
		System.err.println("AppPanel(Type:" + getComponentName() + ":" + getGUID() + ") is Initiated");
		$register_exception_handler$	
$if(user_code_free)$
		initComponent();
	}

	/** USER DEFINED INITIALIZATION. */
	protected abstract void initComponent();
$else$
		/*
		 * USER DEFINED GUI INTIALIZATION SHOULD BE PLACED HERE
		 * */
	}
$endif$

	/*
	 * AUTO-GENERATED CODE FOR PUBLISHER CHANNEL ASSIGNMENT
	 * */
	public void processPublisherChannelAssignment(PubChannelAssignmentMsg pubAssign){
		$pub_assign_lines$
	}
	
	/*
	 * AUTO-GENERATED CODE FOR SUBSCRIBER CHANNEL ASSIGNMENT
	 * */
	public void processSubscriberChannelAssignment(SubChannelAssignmentMsg subAssign){
		$sub_assign_lines$
	}
	
	$listener_defns$

	$sender_method_defns$
	
	$exception_handler_defns$
}