/**
 *
 */
package roslab.model.electronics;

/**
 * @author Peter Gebhard
 */
public class PinService implements Cloneable {

    String name;
    int number = -1;
    char one_to_many = '#';  // Default to 1-to-1 connection type
    String io = "#";
    String superServiceName = "#";
    int superServiceNumber = -1;
    int af = -1;

    /**
     * @param name
     * @param number
     * @param io
     */
    public PinService(String name) {
        this.name = name;
    }

    /**
     * @param name
     * @param number
     * @param io
     */
    public PinService(String name, int number, String io) {
        this.name = name;
        this.number = number;
        this.io = io;
    }

    /**
     * @param name
     * @param number
     * @param one_to_many
     * @param io
     */
    public PinService(String name, int number, char one_to_many, String io) {
        this.name = name;
        this.number = number;
        this.one_to_many = one_to_many;
        this.io = io;
    }

    /**
     * @param name
     * @param number
     * @param one_to_many
     * @param io
     * @param superServiceName
     * @param superServiceNumber
     */
    public PinService(String name, int number, char one_to_many, String io, int af) {
        this.name = name;
        this.number = number;
        this.one_to_many = one_to_many;
        this.io = io;
        this.af = af;
    }

    /**
     * @param name
     * @param number
     * @param one_to_many
     * @param io
     * @param superServiceName
     * @param superServiceNumber
     */
    public PinService(String name, int number, char one_to_many, String io, String superServiceName, int superServiceNumber) {
        this.name = name;
        this.number = number;
        this.one_to_many = one_to_many;
        this.io = io;
        this.superServiceName = superServiceName;
        this.superServiceNumber = superServiceNumber;
    }

    /**
     * @param name
     * @param number
     * @param one_to_many
     * @param io
     * @param superServiceName
     * @param superServiceNumber
     * @param af
     */
    public PinService(String name, int number, char one_to_many, String io, String superServiceName, int superServiceNumber, int af) {
        this.name = name;
        this.number = number;
        this.one_to_many = one_to_many;
        this.io = io;
        this.superServiceName = superServiceName;
        this.superServiceNumber = superServiceNumber;
        this.af = af;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the number
     */
    public int getNumber() {
        return number;
    }

    /**
     * @return the one_to_many
     */
    public char getOne_to_many() {
        return one_to_many;
    }

    /**
     * @return the io
     */
    public String getIo() {
        return io;
    }

    /**
     * @return the superServiceName
     */
    public String getSuperServiceName() {
        return superServiceName;
    }

    /**
     * @return the superServiceNumber
     */
    public int getSuperServiceNumber() {
        return superServiceNumber;
    }

    /**
     * @return the af
     */
    public int getAf() {
        return af;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    protected PinService clone() {
        return new PinService(name, number, one_to_many, io, superServiceName, superServiceNumber, af);
    }

}
