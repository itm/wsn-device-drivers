package viewer;


import model.Scenario;
import model.Setup;
import model.Trace;
import model.wiseml;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.load.Persister;

import java.io.File;


/**
 * Information from the Store Hashmap is retrieved. According to this information
 * xml is generated.
 */
public class CreateXML {


    /**
     * This function creates an XML Graph Entity.
     *
     * @param fileName
     * @param nodeList
     * @param edgeList
     */

    /**
     * public final void writeXML(final String fileName, final List nodeList, final List edgeList) {
     * final Serializer serializer = new Persister();
     * final Graph example = new Graph("G", "undirected", nodeList, edgeList);
     * final File result = new File(fileName);
     * try {
     * serializer.write(example, result);
     * }
     * catch (Exception e) {
     * e.printStackTrace();
     * }
     * }
     * <p/>
     * <p/>
     * }
     */

    public final void writeXML(final String filename, final Setup setup, final Scenario scene,
                               final Trace trace) {

        final Serializer serializer = new Persister();
        final wiseml example = new wiseml("1.0", "http://wisebed.eu/ns/wiseml/1.0", setup, scene, trace);

        final File result = new File(filename);
        try {
            serializer.write(example, result);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

     }

}