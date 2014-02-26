package roslab.artifacts;

public class ROSSourceDependency {
	
	public final String include;

	public ROSSourceDependency(String include) {
		this.include = include;
	}
	
	public int hashCode(){
		return include.hashCode();
	}

}
