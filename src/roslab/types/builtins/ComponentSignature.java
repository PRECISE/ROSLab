package roslab.types.builtins;

import java.util.LinkedList;
import java.util.List;

public class ComponentSignature {
	private String type; //component type name
	private LinkedList<PortName> sendPortNames = new LinkedList<PortName>();
	private LinkedList<PortName> recvPortNames = new LinkedList<PortName>();
	private LinkedList<PortType> sendPortTypes = new LinkedList<PortType>();
	private LinkedList<PortType> recvPortTypes = new LinkedList<PortType>();
	private LinkedList<TaskDescriptor> taskDescriptors = new LinkedList<TaskDescriptor>();
	private PortName exceptionPortName = null;
	private boolean shareable;
	/**
	 * @param type The DE type of the component
	 * @param sendPortNames the list of the publish ports this component possesses
	 * @param recvPortNames the list of the subscribe ports this component possesses
	 * @param sendPortTypes a list of each publish port's type.  The order of the elements
	 * in this list corresponds to the order of elements in the sendPortNames list
	 * @param recvPortTypes a list of each subscribe port's type.  The order of the elements
	 * in this list corresponds to the order of elements in the recvPortNames list
	 */
	public ComponentSignature(String type, List<PortName> sendPortNames, List<PortName> recvPortNames,
			List<PortType> sendPortTypes, List<PortType> recvPortTypes, List<TaskDescriptor> taskDescriptors){
		this.sendPortNames.addAll(sendPortNames);
		this.recvPortNames.addAll(recvPortNames);
		this.sendPortTypes.addAll(sendPortTypes);
		this.recvPortTypes.addAll(recvPortTypes);
		this.taskDescriptors.addAll(taskDescriptors);
		this.type = type;
	}
	
	public void addTaskDescriptor(TaskDescriptor task){
		if(taskDescriptors == null)
			taskDescriptors = new LinkedList<TaskDescriptor>();
		taskDescriptors.add(task);
	}
	
	/**
	 * This searches the task descriptor list (linearly) for a task with the
	 * given name.
	 * @param name The name of the desired task descriptor
	 * @return The task descriptor with the given name, or null if no such task
	 * exists
	 */
	public TaskDescriptor getTaskDescriptorByName(String name){
		for(TaskDescriptor t : taskDescriptors)
			if(t.getName().equals(name))
				return t;
		return null;
	}
	
	/**
	 * This searches the task descriptor list (linearly) for tasks with the
	 * given name and deletes any it finds.
	 * @param name The name of the task descriptor to delete
	 */
	public void removeTaskByName(String name) {
		LinkedList<TaskDescriptor> toDelete = new LinkedList<TaskDescriptor>();
		for(TaskDescriptor t : taskDescriptors)
			if(t.getName().equals(name))
				toDelete.add(t);
		taskDescriptors.removeAll(toDelete);
	}
	
	public LinkedList<TaskDescriptor> getTaskDescriptors() {
		return taskDescriptors;
	}

	public void setTaskDescriptors(LinkedList<TaskDescriptor> taskDescriptors) {
		this.taskDescriptors = taskDescriptors;
	}

	public List<PortName> getSendPortNames(){
		return sendPortNames;
	}
	
	public List<PortName> getRecvPortNames(){
		return recvPortNames;
	}
	
	public List<PortType> getSendPortTypes(){
		return sendPortTypes;
	}
	
	public List<PortType> getRecvPortTypes(){
		return recvPortTypes;
	}

	public void setExceptionPortName(PortName portName){
		exceptionPortName = portName;
	}
	
	public PortName getExceptionPortName(){
		return exceptionPortName;
	}
	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public boolean isShareable() {
		return shareable;
	}
}
