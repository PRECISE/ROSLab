package roslab.types.builtins;

public class TaskDescriptor {
	private String name;
	private enum taskTypeEnum {PERIODIC, SPORADIC};
	private taskTypeEnum type;
	private int period;
	private int deadline;
	private int wcet;
	private String trigPortName;
	
	public TaskDescriptor(String name, String type, int period,
			int deadline, int wcet, String trigPortName) {
		this.name = name;
		setType(type);
		this.period = period;
		this.deadline = deadline;
		this.wcet = wcet;
		this.trigPortName = trigPortName;
	}
	public String getTrigPortName() {
		return trigPortName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public boolean isPeriodic(){
		if(this.type == taskTypeEnum.PERIODIC)
			return true;
		else
			return false;
	}
	public String getType() {
		if(this.type == taskTypeEnum.PERIODIC)
			return "Periodic";
		else if(this.type == taskTypeEnum.SPORADIC)
			return "Sporadic";
		return null;
	}
	public void setType(String type) {
		if(type.equalsIgnoreCase("Periodic"))
			this.type = taskTypeEnum.PERIODIC;
		else if(type.equalsIgnoreCase("Sporadic"))
			this.type = taskTypeEnum.SPORADIC;
	}
	public int getPeriod() {
		return period;
	}
	public void setPeriod(int period) {
		this.period = period;
	}
	public int getDeadline() {
		return deadline;
	}
	public void setDeadline(int deadline) {
		this.deadline = deadline;
	}
	public int getWcet() {
		return wcet;
	}
	public void setWcet(int wcet) {
		this.wcet = wcet;
	}
}
