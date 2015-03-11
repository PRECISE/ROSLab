/**
 *
 */
package roslab.model.general;

import roslab.model.ui.UILink;

/**
 * @author Peter Gebhard
 */
public class Link {

    protected Endpoint src;
    protected Endpoint dest;
    protected UILink uiLink;

    /**
     * @param src
     * @param dest
     */
    public Link(Endpoint src, Endpoint dest) {
        if (src.canConnect(dest)) {
            this.src = src;
            this.dest = dest;
        }
        else {
            throw new IllegalArgumentException("Cannot connect incompatible endpoints.");
        }
    }

    /**
     * @return the name
     */
    public String getName() {
        return this.src.getName() + " -- " + this.dest.getName();
    }

    /**
     * @return the src
     */
    public Endpoint getSrc() {
        return src;
    }

    /**
     * @param src
     *            the src to set
     */
    public void setSrc(Endpoint src) {
        this.src = src;
    }

    /**
     * @return the dest
     */
    public Endpoint getDest() {
        return dest;
    }

    /**
     * @param dest
     *            the dest to set
     */
    public void setDest(Endpoint dest) {
        this.dest = dest;
    }

    /**
     * @return the uiLink
     */
    public UILink getUILink() {
        return uiLink;
    }

    /**
     * @param uiLink
     *            the uiLink to set
     */
    public void setUILink(UILink uiLink) {
        this.uiLink = uiLink;
    }

    public void destroy() {
        uiLink.disconnect();
        src.disconnect(this);
        dest.disconnect(this);
        uiLink = null;
    }

}
