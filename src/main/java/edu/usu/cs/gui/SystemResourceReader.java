package edu.usu.cs.gui;
 
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Properties;

/**
 * A SystemResourceReader is initialized with a Resource (String).
 * It acquires a resource with the String's name within the
 * classpath.  It returns the contents of the resource in the form
 * requested by available methods.
 *
 * @author Randy K. Secrist (randy@secristfamily.com)
 * @deprecated Use regular resourcing methods instead of this.
 */
public class SystemResourceReader {
    /**
     * Denotes a protocol using the CLASSPATH.
     */
    private static final int CLASSPATH =  1 << 1;

    /**
     * Denotes a protocol using the filesystem.
     */
    private static final int FILE =  1 << 0;

    /**
     * The URL to use to access the resource.
     */
    private String url;

    /**
     * The requested protocol to use to lookup the resource.
     */
    private int type;

    /**
     * Constructs a new SystemResourceReader on the provided Resource
     * The URL should be prefixed by one of the following <code><br/></code>
     * file:///path/to file<code><br/></code>
     * classpath://com/secrist/path<code><br/></code>
     * http(s)://www.secristfamily.com/sfn<code><br/></code>
     * @param url The url used to lookup the resource.
     */
    public SystemResourceReader(String url) {
        if (url == null)
            return;

        // Verify URL String
        url = url.trim();
        if (url.regionMatches(true, 0, "file://", 0, "file://".length()))
            type = FILE;
        else if (url.regionMatches(true, 0, "classpath://", 0, "classpath://".length()))
            type = CLASSPATH;
        else
            throw new IllegalArgumentException("Specified url (" + url + ") does not match a given protocol!");

        // Set URL
        this.url = url;

        // TEST URL
        try {
            if (type != CLASSPATH)
                this.getURL();
        }
        catch(ResourceFailure e) {
            throw new IllegalArgumentException(e.getRelevantMessage());
        }
    }

    /**
     * Returns the offset (the length the protocol string) takes up.
     * @return The start parameter to use in a String#substring call.
     */
    private int getOffset() {
        switch (type) {
            case FILE: {
                return 7;
            }
            case CLASSPATH: {
                return 12;
            }
            default : {
                return -1;
            }
        }
    }

    /**
     * Attempts to read a resource from various possible sources depending
     * on the protocol include in the URL.
     * @return A Reader which can be used to parse the resource.
     * @throws ResourceFailure if the resource can not be located.
     */
    public Reader getReader() throws ResourceFailure {
        String path;
        int offset = getOffset();
        path = url.substring(offset, url.length());

        if (type == FILE) {
            if(path != null) {
                Reader reader = null;
                try {
                     reader = new FileReader(path);
                }
                catch (FileNotFoundException e) {
                    throw new ResourceFailure(e);
                }
                return reader;
            }
            else
                throw new ResourceFailure("Error locating resource within the filesystem identified by url: " + url);
        }
        else if (type == CLASSPATH)
            return new BufferedReader(new InputStreamReader(this.getInputStream(), Charset.forName("UTF-8")));
        else
            throw new ResourceFailure("HTTP protocols not yet supported!");
    }

    /**
     * Returns the InputStream to a resource.
     * @throws ResourceFailure if the resource can not be located.
     */
    public InputStream getInputStream() throws ResourceFailure {
        String path;
        int offset = getOffset();
        path = url.substring(offset, url.length());

        if (type == CLASSPATH) {
            InputStream anInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);

            if (anInputStream != null)
                return (anInputStream);
            else
                throw new ResourceFailure("Error locating resource within the classpath identified by url: " + url);
        }
        else if (type == FILE) {
            try {
                FileChannel channel = new FileInputStream(path).getChannel();
                final ByteBuffer buf = channel.map(FileChannel.MapMode.READ_ONLY, 0, channel.size());
                return new InputStream() {
                    public synchronized int read() throws IOException {
                        if (!buf.hasRemaining()) {
                            return -1;
                        }
                        return buf.get();
                    }
                    public synchronized int read(byte[] bytes, int off, int len) throws IOException {
                        // Read only what's left
                        len = Math.min(len, buf.remaining());
                        buf.get(bytes, off, len);
                        return len;
                    }
                };
            }
            catch (IOException e) {
                throw new ResourceFailure("Failure to create InputStream from File: " + path);
            }
        }
        else
            throw new ResourceFailure("HTTP protocols not yet supported!");
    }

    /**
     * Returns a URL to the specified resource.
     * @return <code>a URL</code> which is the URL to the named resource.
     * @throws ResourceFailure if the resource can not be located.
     */
    public URL getURL() throws ResourceFailure {
        String path;
        int offset = getOffset();
        path = url.substring(offset, url.length());
        if (type == CLASSPATH) {
            // Back out of package structure before processing the next call:
            URL aUrl = getClass().getResource("../../../../" + path);
            if (aUrl == null)
                throw new ResourceFailure("Error locating resource identified by url: " + url);
            if (aUrl.toExternalForm().indexOf(path) == -1)
                throw new ResourceFailure("Resource name was not contained in url: " + url);
            return (aUrl);
        }
        else {
            try {
                URL aUrl = new URL(url);
                return aUrl;
            }
            catch(MalformedURLException e) {
                throw new ResourceFailure(e);
            }
        }
    }

    /**
     * Translates the named resource
     * @return <code>aProperties</code> which is the Properties contained in the named resource
     * @throws ResourceFailure if the resource can not be located.
     */
    public Properties translateToProperties() throws ResourceFailure {
        InputStream anInputStream = this.getInputStream();
        Properties prop = new Properties();
        try {
            prop.load(anInputStream);
        }
        catch (IOException e) {
            throw new ResourceFailure("Error translating to properties the URL: " + url);
        }
        return(prop);
    }

    /**
     * Translates the named resource
     * @return <code>aString</code> which is the String contained in the named resource
     * @throws ResourceFailure if the resource can not be located.
     */
    public String translateToString() throws ResourceFailure {
        InputStream anInputStream = this.getInputStream();

        // We assume the whole file is available.
        byte[] byteArray = null;
        try {
            byteArray = new byte[anInputStream.available()];
            anInputStream.read(byteArray);
            anInputStream.close();
        }
        catch (IOException e) {
            throw new ResourceFailure("Error reading url identified by url: " + url);
        }
        return(new String(byteArray));
    }
}