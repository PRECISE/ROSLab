/*
 * AUTO-GENERATED MESSAGE HANDLER
 */
class $name$Listener implements IMdcfMessageListener{
	@Override
	public void onMessage(MdcfMessage message){
		/*
		 * AUTO-GENERATED DATA CONVERTER
		 **/
		$port_data_type$ $name$Data$data_init$;
		try{
			$name$Data = $receive_data_converter$;
		} catch (NumberFormatException e) {
			System.err.println("$name$Listener:NumberFormatException:" + message.getTextMsg());
			e.printStackTrace();
		}		
$if(user_code_free)$
		$name$ListenerOnMessage(message, $name$Data);
	}
}

protected abstract void $name$ListenerOnMessage(MdcfMessage msg, $port_data_type$ $name$Data);
$else$
		
		/*
		 * USER DEFINED HANDLER CODE
		 **/
	}
}
$endif$